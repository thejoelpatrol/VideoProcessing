package com.laserscorpion.VideoProcessing.filters.BitShifter;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class BitShifterFactory implements ImageFilterFactory {
    private boolean downsample;
    private int shift;

    public BitShifterFactory(int shift, boolean downsample) {
        this.downsample = downsample;
        this.shift = shift;
    }

    @Override
    public ImageFilter create() {
        return new BitShifter(shift, downsample);
    }
}
