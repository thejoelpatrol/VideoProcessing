package com.laserscorpion.VideoProcessing;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoProcessor {
    private static String ffmpegArgs = " -f image2pipe -vcodec ppm pipe:1 ";
    private static final String ffmpegScale = " -vf scale=2*iw:2*ih ";
    private static final String ffmpegOutputArgs = "-framerate 30 -i pipe:0 -c:v libx264 -r 30 -crf 25 -pix_fmt yuv420p ";
    protected int QUEUE_SIZE = 100;

    private BlockingQueue<PPMFile> images;
    private BlockingQueue<PPMFile> outputImages;
    private ImageFilterFactory[] factories;
    private int workers;

    private PPMReader reader;
    private PPMWriter encoder;

    private WorkerManager manager;
    private ProcessBuilder inputFfmpeg;
    private ProcessBuilder outputFfmpeg;
    Process input;
    Process output;

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories, int workers) {
        this(inputFilepath, factories, workers, false);
    }

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories, int workers, boolean scale2x) {
        this.workers = workers;
        this.factories = factories;

        images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);

        String inputCommand = "ffmpeg -i " + inputFilepath + (scale2x ? ffmpegScale : "") +  ffmpegArgs;
        String outputCommand = "ffmpeg " + ffmpegOutputArgs + inputFilepath + "_" + new Date().getTime() + ".mp4";
        inputCommand = inputCommand.replace("  ", " ");
        outputCommand = outputCommand.replace("  ", " ");
        System.out.println("Going to run " + inputCommand);
        System.out.println("Going to run " + outputCommand);
        String[] inArgs = inputCommand.split(" ");
        String[] outArgs = outputCommand.split(" ");
        inputFfmpeg = new ProcessBuilder(inArgs);
        inputFfmpeg.redirectError(ProcessBuilder.Redirect.INHERIT);
        outputFfmpeg = new ProcessBuilder(outArgs);
        outputFfmpeg.redirectError(ProcessBuilder.Redirect.INHERIT);
    }

    public void start() {
        try {
            input = inputFfmpeg.start();
            output = outputFfmpeg.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        reader = new PPMReader(input.getInputStream(), images);
        encoder = new PPMWriter(outputImages, output.getOutputStream());
        manager = new WorkerManager(images, workers, factories, outputImages, encoder);

        reader.start();
        manager.start();
        encoder.start();
        waitForChild();
    }

    private void waitForChild() {
        while (true) {
            try {
                output.waitFor();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
