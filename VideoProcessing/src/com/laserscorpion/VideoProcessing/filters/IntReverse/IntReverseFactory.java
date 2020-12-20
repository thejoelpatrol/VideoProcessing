package com.laserscorpion.VideoProcessing.filters.IntReverse;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class IntReverseFactory implements ImageFilterFactory {
    public IntReverseFactory() {
    }

    @Override
    public ImageFilter create() {
        return new IntReverse();
    }
}
