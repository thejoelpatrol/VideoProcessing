package com.laserscorpion.VideoProcessing.filters.BoxTest;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class BoxTestFactory implements ImageFilterFactory {
    private int x;
    private int y;
    private int height;
    private int width;

    public BoxTestFactory(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    @Override
    public ImageFilter create() {
        return new BoxTest(x, y, height, width);

    }
}
