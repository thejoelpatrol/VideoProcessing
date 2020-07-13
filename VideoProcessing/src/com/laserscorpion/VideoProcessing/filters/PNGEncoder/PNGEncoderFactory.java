package com.laserscorpion.VideoProcessing.filters.PNGEncoder;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.filters.ReverseAdder.ReverseAdder;

public class PNGEncoderFactory implements ImageFilterFactory {
    public PNGEncoderFactory() {
    }

    @Override
    public ImageFilter create() {
        return new PNGEncoder();
    }
}
