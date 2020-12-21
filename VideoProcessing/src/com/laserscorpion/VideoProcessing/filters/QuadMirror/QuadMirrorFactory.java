package com.laserscorpion.VideoProcessing.filters.QuadMirror;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class QuadMirrorFactory implements ImageFilterFactory {

    @Override
    public ImageFilter create() {
        return new QuadMirror();
    }
}
