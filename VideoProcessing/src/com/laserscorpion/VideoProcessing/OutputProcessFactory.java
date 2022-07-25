package com.laserscorpion.VideoProcessing;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputProcessFactory {
    private static final String FRAMERATE_REGEX = ".*Stream #0.*[0-9]+ kb\\/s, ([0-9]+\\.[0-9]+) fps, [0-9\\.]+ tbr, .*";
    private String inputPath;
    private String args;
    private boolean ffplay;
    private boolean x264;
    private boolean nvenc;
    private boolean tcp;
    private boolean udp;
    private int x264crf;
    private int nvencMaxrate;
    String hostname;
    private int tcpPort;
    private int udpPort;
    private String frameRate;

    public OutputProcessFactory(String inputPath, String args, boolean ffplay, boolean x264,
                                boolean nvenc, boolean tcp, boolean udp, int x264crf, int nvencMaxrate, 
                                String hostname, int tcpPort, int udpPort) {
        this.inputPath = inputPath;
        this.args = args;
        this.ffplay = ffplay;
        this.x264 = x264;
        this.nvenc = nvenc;
        this.x264crf = x264crf;
        this.nvencMaxrate = nvencMaxrate;
        this.hostname = hostname;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.udp = udp;
        this.tcp = tcp;
        frameRate = getFrameRate();
    }

    private String getFrameRate() {
        String[] ffmpegArgs = {"ffmpeg", "-i", inputPath};
        ProcessBuilder builder = new ProcessBuilder(ffmpegArgs);
        //builder.redirectError();
        Process proc = null;
        try {
            System.out.println("running: ffmpeg -i " + inputPath);
            proc = builder.start();
            int rc = proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }
        InputStream ffmpegOut = proc.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegOut));
        ArrayList<String> lines = new ArrayList<>();
        String s = null;
        try {
            while ((s = reader.readLine()) != null) {
                System.out.println("ffmpeg: " + s);
                lines.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Pattern p = Pattern.compile(FRAMERATE_REGEX);
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
        }
        System.err.println("Couldn't find framerate in ffmpeg output: " + lines);
        System.exit(1);
        return "";
    }

    public List<ProcessBuilder> createProcesses() {
        ArrayList<ProcessBuilder> outputs = new ArrayList<>();
        if (ffplay) {
            String[] ffplayArgs = {"ffplay", "-i", "pipe:0", "-framerate", frameRate};
            ProcessBuilder builder = new ProcessBuilder(ffplayArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(ffplayArgs));
        }
        if (x264) {
            String outfileName = inputPath + "_" + new Date().getTime() + "_" +
                    args + "_x264-" + x264crf + ".mp4";
            String[] x264Args = {"ffmpeg","-framerate", frameRate, "-i", "pipe:0", "-c:v", "libx264",
                    "-r", frameRate, "-crf", Integer.toString(x264crf), "-pix_fmt", "yuv420p",
                    outfileName};
            ProcessBuilder builder = new ProcessBuilder(x264Args);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(x264Args));
        }
        if (nvenc) {
            String outfileName = inputPath + "_" + new Date().getTime() + "_" +
                    args + "_nvenc-" + nvencMaxrate + "M" + ".mp4";
            String[] nvencArgs = {"ffmpeg","-framerate", frameRate, "-i", "pipe:0", "-c:v", "h264_nvenc",
                    "-rc:v", "vbr_hq", "-cq:v", "19",
                    "-maxrate:v", Integer.toString(nvencMaxrate) + "M",
                    "-profile:v", "2", "-r", frameRate, "-pix_fmt", "yuv420p", outfileName };
            ProcessBuilder builder = new ProcessBuilder(nvencArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(nvencArgs));
        }
        if (tcp) {
            String[] tcpArgs = {"ffmpeg","-framerate", frameRate, "-i", "pipe:0", "-c:v", "libx264",
                    "-r", frameRate, "-crf", "15", "-pix_fmt", "yuv420p",
                    "-f", "mpegts", "tcp://" + hostname + ":" + tcpPort};
            ProcessBuilder builder = new ProcessBuilder(tcpArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(tcpArgs));
        }
        if (udp) {
            String[] udpArgs = {"ffmpeg", "-framerate", frameRate, "-i", "pipe:0", "-c:v", "libx264",
                    "-r", frameRate, "-crf", "15", "-pix_fmt", "yuv420p",
                    "-f", "mpegts", "udp://" + hostname + ":" + udpPort};
            ProcessBuilder builder = new ProcessBuilder(udpArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(udpArgs));
        }
        return outputs;
    }
}
