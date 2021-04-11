package com.laserscorpion.VideoProcessing.filters.GhostDelay;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class GhostDelayFactory implements ImageFilterFactory {
    public static final boolean REQUIRES_FRAMECACHE = true;
    private int numFrames;
    private double alpha;
    private int delay;

    public GhostDelayFactory(int numFrames, double alpha, int delay) {
        this.numFrames = numFrames;
        this.alpha = alpha;
        this.delay = delay;
    }

    @Override
    public ImageFilter create() {
        return new GhostDelay(numFrames, alpha, delay);
    }
}
