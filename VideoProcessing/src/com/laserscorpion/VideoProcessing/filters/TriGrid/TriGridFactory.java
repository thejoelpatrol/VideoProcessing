package com.laserscorpion.VideoProcessing.filters.TriGrid;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class TriGridFactory implements ImageFilterFactory {
    private int rows;
    private int cols;
    private long seed;
    private int[] color_ratios;
    private double triangleProb;

    public TriGridFactory(int rows, int cols, long seed, int[] color_ratios, double triangleProb) {
        this.rows = rows;
        this.cols = cols;
        this.seed = seed;
        this.color_ratios = color_ratios;
        this.triangleProb = triangleProb;
    }

    @Override
    public ImageFilter create() {
        return new TriGrid(rows, cols, seed, color_ratios, triangleProb);
    }
}
