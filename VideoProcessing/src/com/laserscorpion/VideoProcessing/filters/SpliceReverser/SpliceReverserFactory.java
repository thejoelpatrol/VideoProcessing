package com.laserscorpion.VideoProcessing.filters.SpliceReverser;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class SpliceReverserFactory implements ImageFilterFactory {
    boolean red, blue, green;

    public SpliceReverserFactory(boolean red, boolean green, boolean blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public ImageFilter create() {
        return new SpliceReverser(red, green, blue);
    }
}
