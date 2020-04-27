package com.laserscorpion.VideoProcessing.filters.ReverseAdder;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class ReverseAdderFactory implements ImageFilterFactory {
    public ReverseAdderFactory() {
    }

    @Override
    public ImageFilter create() {
        return new ReverseAdder();
    }
}
