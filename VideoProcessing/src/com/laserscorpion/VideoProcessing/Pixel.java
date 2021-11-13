package com.laserscorpion.VideoProcessing;

public class Pixel {
    public short r;
    public short g;
    public short b;

    public Pixel() { }

    public Pixel(short r, short g, short b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Pixel(boolean transparent) {
        if (transparent) {
            r = (short)0xFFFF;
            g = (short)0xFFFF;
            b = (short)0xFFFF;
        }
    }

    public boolean is_transparent() {
        return r == (short)0xFFFF && g == (short)0xFFFF && b == (short)0xFFFF;
    }

    public int toInt() {
        return ((r << 16) & 0xFF0000) | ((g << 8) & 0x00FF00) | (b & 0xFF);
    }

    public Pixel copyOf() {
        return new Pixel(r, g, b);
    }
}
