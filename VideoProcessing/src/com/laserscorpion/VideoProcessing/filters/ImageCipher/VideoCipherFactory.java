package com.laserscorpion.VideoProcessing.filters.ImageCipher;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class VideoCipherFactory implements ImageFilterFactory {
    private boolean downsample;

    public VideoCipherFactory(boolean downsample) {
        this.downsample = downsample;
    }

    @Override
    public ImageFilter create() {
        return new VideoCipher(downsample);
    }
}
