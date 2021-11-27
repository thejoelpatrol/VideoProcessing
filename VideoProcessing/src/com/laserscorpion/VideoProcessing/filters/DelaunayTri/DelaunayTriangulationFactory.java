package com.laserscorpion.VideoProcessing.filters.DelaunayTri;

import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class DelaunayTriangulationFactory implements ImageFilterFactory {
    private long seed;
    private int[] color_ratios;
    private int numVerts;

    public DelaunayTriangulationFactory(long seed, int numVerts, int[] color_ratios) {
        this.seed = seed;
        this.color_ratios = color_ratios;
        this.numVerts = numVerts;
    }

    @Override
    public ImageFilter create() {
        return new DelaunayTriangulation(seed, numVerts, color_ratios);
    }
}
