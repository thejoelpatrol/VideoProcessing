package com.laserscorpion.VideoProcessing.filters.ChillerShuffler;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;


public class ChillerShufflerFactory implements ImageFilterFactory {
    private int samples;
    private boolean snap;
    private int maxSampleHeight;
    private double glitchProbability;
    private int everyNthFrame;

    public ChillerShufflerFactory(int samples, boolean snap, int maxSampleHeight, double glitchProbability, int everyNthFrame) {
        this.samples = samples;
        this.snap = snap;
        this.maxSampleHeight = maxSampleHeight;
        this.glitchProbability = glitchProbability;
        this.everyNthFrame = everyNthFrame;
    }

    @Override
    public ImageFilter create() {
        return new ChillerShuffler(samples, snap, maxSampleHeight, glitchProbability, everyNthFrame);
    }
}
