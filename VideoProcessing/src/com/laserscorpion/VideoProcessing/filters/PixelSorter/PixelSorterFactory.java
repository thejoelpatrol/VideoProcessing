package com.laserscorpion.VideoProcessing.filters.PixelSorter;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class PixelSorterFactory implements ImageFilterFactory {
    private boolean hsv;

    public PixelSorterFactory(boolean hsv) {
        this.hsv = hsv;
    }

    @Override
    public ImageFilter create() {
        return new PixelSorter(hsv);
    }
}
