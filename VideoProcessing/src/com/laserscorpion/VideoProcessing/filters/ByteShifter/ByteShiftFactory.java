package com.laserscorpion.VideoProcessing.filters.ByteShifter;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class ByteShiftFactory implements ImageFilterFactory {
    public ByteShiftFactory() {
    }

    @Override
    public ImageFilter create() {
        return new ByteShifter();
    }
}
