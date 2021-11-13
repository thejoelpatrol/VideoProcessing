package com.laserscorpion.VideoProcessing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoProcessor {
    private static String ffmpegArgs = "-f image2pipe -vcodec ppm pipe:1";
    private static final String ffmpegScale = "-vf scale=2*iw:2*ih";

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
    private List<ProcessBuilder> outputs;
    Process input;
    Process outputProcesses[];

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories,
                          int workers, boolean scale2x, OutputProcessFactory outputProcessFactory) {
        this.workers = workers;
        this.factories = factories;

        images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        scratchFiles = new ConcurrentLinkedQueue<>();

        outputs = outputProcessFactory.createProcesses();
        outputProcesses = new Process[outputs.size()];

        ArrayList<String> inArgsList = new ArrayList<>();
        inArgsList.add("ffmpeg");
        inArgsList.add("-i");
        inArgsList.add(inputFilepath);
        if (scale2x)
            inArgsList.addAll(Arrays.asList(ffmpegScale.split(" ")));
        inArgsList.addAll(Arrays.asList(ffmpegArgs.split(" ")));

        String[] inArgs = new String[inArgsList.size()];
        inArgs = inArgsList.toArray(inArgs);
        System.out.println("Going to run " + Arrays.toString(inArgs));
        inputFfmpeg = new ProcessBuilder(inArgs);
        inputFfmpeg.redirectError(ProcessBuilder.Redirect.INHERIT);


    }

    public void start() {
        ArrayList<OutputStream> outputStreams = new ArrayList<>();
        try {
            input = inputFfmpeg.start();
            for (int i = 0; i < outputs.size(); i++) {
                outputProcesses[i] = outputs.get(i).start();
                outputStreams.add(outputProcesses[i].getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        reader = new PPMReader(input.getInputStream(), images, scratchFiles);
        encoder = new PPMWriter(outputImages, outputStreams, scratchFiles);
        manager = new WorkerManager(images, workers, factories, outputImages, scratchFiles, encoder);

        reader.start();
        manager.start();
        encoder.start();
        waitForChildren();
    }

    private void waitForChildren() {
        while (true) {
            try {
                for (Process p : outputProcesses) {
                    p.waitFor();
                }
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
