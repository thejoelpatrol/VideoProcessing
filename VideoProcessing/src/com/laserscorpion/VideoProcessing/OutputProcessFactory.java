package com.laserscorpion.VideoProcessing;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OutputProcessFactory {
    private String inputPath;
    private String args;
    private boolean ffplay;
    private boolean x264;
    private boolean nvenc;
    private boolean tcp;
    private boolean udp;
    private int x264crf;
    private int nvencMaxrate;
    private int tcpPort;
    private int udpPort;

    public OutputProcessFactory(String inputPath, String args, boolean ffplay, boolean x264,
                                boolean nvenc, boolean tcp, boolean udp, int x264crf, int nvencMaxrate, 
                                int tcpPort, int udpPort) {
        this.inputPath = inputPath;
        this.args = args;
        this.ffplay = ffplay;
        this.x264 = x264;
        this.nvenc = nvenc;
        this.x264crf = x264crf;
        this.nvencMaxrate = nvencMaxrate;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.udp = udp;
        this.tcp = tcp;
    }

    public List<ProcessBuilder> createProcesses() {
        ArrayList<ProcessBuilder> outputs = new ArrayList<>();
        if (ffplay) {
            String[] ffplayArgs = {"ffplay", "-i", "pipe:0"};
            ProcessBuilder builder = new ProcessBuilder(ffplayArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(ffplayArgs));
        }
        if (x264) {
            String outfileName = inputPath + "_" + new Date().getTime() + "_" +
                    args + "_x264-" + x264crf + ".mp4";
            String[] x264Args = {"ffmpeg","-framerate", "30", "-i", "pipe:0", "-c:v", "libx264",
                    "-r", "30", "-crf", Integer.toString(x264crf), "-pix_fmt", "yuv420p",
                    outfileName};
            ProcessBuilder builder = new ProcessBuilder(x264Args);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(x264Args));
        }
        if (nvenc) {
            String outfileName = inputPath + "_" + new Date().getTime() + "_" +
                    args + "_nvenc-" + nvencMaxrate + "M" + ".mp4";
            String[] nvencArgs = {"ffmpeg","-framerate", "30", "-i", "pipe:0", "-c:v", "h264_nvenc",
                    "-rc:v", "vbr_hq", "-cq:v", "19",
                    "-maxrate:v", Integer.toString(nvencMaxrate) + "M",
                    "-profile:v", "2", "-r", "30", "-pix_fmt", "yuv420p", outfileName };
            ProcessBuilder builder = new ProcessBuilder(nvencArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(nvencArgs));
        }
        if (tcp) {
            String[] tcpArgs = {"ffmpeg","-r", "30", "-i", "pipe:0", "-c:v", "libx264",
                    "-framerate", "30", "-crf", "15", "-pix_fmt", "yuv420p",
                    "-f", "mpegts", "tcp://localhost:" + tcpPort};
            ProcessBuilder builder = new ProcessBuilder(tcpArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(tcpArgs));
        }
        if (udp) {
            String[] udpArgs = {"ffmpeg","-framerate", "30", "-i", "pipe:0", "-c:v", "copy",
                    "-f", "mpegts", "udp://localhost:" + udpPort};
            ProcessBuilder builder = new ProcessBuilder(udpArgs);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            outputs.add(builder);
            System.out.println("Building process with args: " + Arrays.toString(udpArgs));
        }
        return outputs;
    }
}
