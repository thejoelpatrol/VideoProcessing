package com.laserscorpion.VideoProcessing.filters.UnevenBitShifter;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class UnevenBitShifterFactory implements ImageFilterFactory {
    private boolean downsample;
    private int rshift;
    private int gshift;
    private int bshift;

    public UnevenBitShifterFactory(int rshift, int gshift, int bshift, boolean downsample) {
        this.downsample = downsample;
        this.rshift = rshift;
        this.bshift = bshift;
        this.gshift = gshift;
    }

    @Override
    public ImageFilter create() {
        return new UnevenBitShifter(rshift, gshift, bshift, downsample);
    }
}
