package com.laserscorpion.VideoProcessing.filters.OtherByteShifter;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class OtherByteShiftFactory implements ImageFilterFactory {
    private int offsetPerFrame;
    public OtherByteShiftFactory(int offsetPerFrame) {
        this.offsetPerFrame = offsetPerFrame;
    }

    @Override
    public ImageFilter create() {
        return new OtherByteShifter(offsetPerFrame);
    }
}
