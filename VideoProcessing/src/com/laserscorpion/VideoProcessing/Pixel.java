package com.laserscorpion.VideoProcessing;

public class Pixel {
    public short r;
    public short g;
    public short b;

    public int toInt() {
        return ((r << 16) & 0xFF0000) | ((g << 8) & 0x00FF00) | (b & 0xFF);
    }
}
