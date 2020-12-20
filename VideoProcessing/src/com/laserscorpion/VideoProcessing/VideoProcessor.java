package com.laserscorpion.VideoProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoProcessor {
    private static String ffmpegArgs = "-f image2pipe -vcodec ppm pipe:1";
    private static final String ffmpegScale = "-vf scale=2*iw:2*ih";
    private static final String ffmpegOutputArgs = "-framerate 30 -i pipe:0 -c:v libx264 -r 30 -crf 25 -pix_fmt yuv420p";
    protected int QUEUE_SIZE = 100;

    private BlockingQueue<PPMFile> images;
    private BlockingQueue<PPMFile> outputImages;
    private Queue<PPMFile> scratchFiles;
    private ImageFilterFactory[] factories;
    private int workers;

    private PPMReader reader;
    private PPMWriter encoder;

    private WorkerManager manager;
    private ProcessBuilder inputFfmpeg;
    private ProcessBuilder outputFfmpeg;
    Process input;
    Process output;

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories, String argString, int workers) {
        this(inputFilepath, factories, argString, workers, false);
    }

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories, String argString, int workers, boolean scale2x) {
        this.workers = workers;
        this.factories = factories;

        images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        scratchFiles = new ConcurrentLinkedQueue<>();

        ArrayList<String> inArgsList = new ArrayList<>();
        inArgsList.add("ffmpeg");
        inArgsList.add("-i");
        inArgsList.add(inputFilepath);
        if (scale2x)
            inArgsList.addAll(Arrays.asList(ffmpegScale.split(" ")));
        inArgsList.addAll(Arrays.asList(ffmpegArgs.split(" ")));
        String[] inArgs = new String[inArgsList.size()];
        inArgs = inArgsList.toArray(inArgs);

        ArrayList<String> outArgsList = new ArrayList<>();
        outArgsList.add("ffmpeg");
        outArgsList.addAll(Arrays.asList(ffmpegOutputArgs.split(" ")));
        outArgsList.add(inputFilepath + "_" + new Date().getTime() + "_" + argString + ".mp4");
        String[] outArgs = new String[outArgsList.size()];
        outArgs = outArgsList.toArray(outArgs);

        System.out.println("Going to run " + Arrays.toString(inArgs));
        System.out.println("Going to run " + Arrays.toString(outArgs));

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

        reader = new PPMReader(input.getInputStream(), images, scratchFiles);
        encoder = new PPMWriter(outputImages, output.getOutputStream(), scratchFiles);
        manager = new WorkerManager(images, workers, factories, outputImages, scratchFiles, encoder);

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
