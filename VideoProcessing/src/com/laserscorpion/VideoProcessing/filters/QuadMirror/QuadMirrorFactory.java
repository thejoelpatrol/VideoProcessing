package com.laserscorpion.VideoProcessing.filters.QuadMirror;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class QuadMirrorFactory implements ImageFilterFactory {
    private boolean downsample;

    public QuadMirrorFactory(boolean downsample) {
        this.downsample = downsample;
    }

    @Override
    public ImageFilter create() {
        return new QuadMirror(downsample);
    }
}
