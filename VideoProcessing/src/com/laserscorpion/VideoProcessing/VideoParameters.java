package com.laserscorpion.VideoProcessing;

public class VideoParameters {
    boolean scale2x;
    boolean ffplay;
    boolean x264;
    boolean nvenc;
    int x264crf;
    int nvencMaxrate;

    public VideoParameters(boolean scale2x, boolean ffplay, boolean x264, boolean nvenc, int x264crf, int nvencMaxrate) {
        this.scale2x = scale2x;
        this.ffplay = ffplay;
        this.x264 = x264;
        this.nvenc = nvenc;
        this.x264crf = x264crf;
        this.nvencMaxrate = nvencMaxrate;
    }
}
