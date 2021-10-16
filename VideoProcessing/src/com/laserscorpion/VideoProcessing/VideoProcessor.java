package com.laserscorpion.VideoProcessing;

import java.io.IOException;
import java.io.OutputStream;
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
    private ProcessBuilder outputs[];
    Process input;
    Process outputProcesses[];

    public VideoProcessor(String inputFilepath, ImageFilterFactory[] factories, String argString,
                          int workers, VideoParameters encodeParams) {
        this.workers = workers;
        this.factories = factories;

        images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        scratchFiles = new ConcurrentLinkedQueue<>();

        outputs = new ProcessBuilder[(encodeParams.ffplay ? 1 : 0) +
                (encodeParams.x264 ? 1 : 0) +
                (encodeParams.nvenc ? 1 : 0)];
        outputProcesses = new Process[outputs.length];

        ArrayList<String> inArgsList = new ArrayList<>();
        inArgsList.add("ffmpeg");
        inArgsList.add("-i");
        inArgsList.add(inputFilepath);
        if (encodeParams.scale2x)
            inArgsList.addAll(Arrays.asList(ffmpegScale.split(" ")));
        inArgsList.addAll(Arrays.asList(ffmpegArgs.split(" ")));

        String[] inArgs = new String[inArgsList.size()];
        inArgs = inArgsList.toArray(inArgs);
        System.out.println("Going to run " + Arrays.toString(inArgs));
        inputFfmpeg = new ProcessBuilder(inArgs);
        inputFfmpeg.redirectError(ProcessBuilder.Redirect.INHERIT);

        int nOutput = 0;
        if (encodeParams.ffplay) {
            String[] ffplayArgs = {"ffplay", "-i", "pipe:0"};
            outputs[nOutput] = new ProcessBuilder(ffplayArgs);
            outputs[nOutput].redirectError(ProcessBuilder.Redirect.INHERIT);
            System.out.println("Going to run " + Arrays.toString(ffplayArgs));
            nOutput++;
        }
        if (encodeParams.x264) {
            String outfileName = inputFilepath + "_" + new Date().getTime() + "_" +
                    argString + "_x264-" + encodeParams.x264crf + ".mp4";
            String[] x264Args = {"ffmpeg","-framerate", "30", "-i", "pipe:0", "-c:v", "libx264",
                    "-r", "30", "-crf", Integer.toString(encodeParams.x264crf), "-pix_fmt", "yuv420p",
                    outfileName};
            outputs[nOutput] = new ProcessBuilder(x264Args);
            outputs[nOutput].redirectError(ProcessBuilder.Redirect.INHERIT);
            System.out.println("Going to run " + Arrays.toString(x264Args));
            nOutput++;
        }
        if (encodeParams.nvenc) {
            String outfileName = inputFilepath + "_" + new Date().getTime() + "_" +
                    argString + "_nvenc-" + encodeParams.nvenc + "M" + ".mp4";
            String[] nvencArgs = {"ffmpeg","-framerate", "30", "-i", "pipe:0", "-c:v", "h264_nvenc",
                    "-rc:v", "vbr_hq", "-cq:v", "19",
                    "-maxrate:v", Integer.toString(encodeParams.nvencMaxrate) + "M",
                    "-profile:v", "2", "-r", "30", "-pix_fmt", "yuv420p", outfileName };
            outputs[nOutput] = new ProcessBuilder(nvencArgs);
            outputs[nOutput].redirectError(ProcessBuilder.Redirect.INHERIT);
            System.out.println("Going to run " + Arrays.toString(nvencArgs));
            nOutput++;
        }
    }

    public void start() {
        ArrayList<OutputStream> outputStreams = new ArrayList<>();
        try {
            input = inputFfmpeg.start();
            for (int i = 0; i < outputs.length; i++) {
                outputProcesses[i] = outputs[i].start();
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
