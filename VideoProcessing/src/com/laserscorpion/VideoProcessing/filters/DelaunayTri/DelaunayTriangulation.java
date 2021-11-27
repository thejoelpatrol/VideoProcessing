package com.laserscorpion.VideoProcessing.filters.DelaunayTri;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;
import com.laserscorpion.VideoProcessing.drawing.Graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class DelaunayTriangulation implements ImageFilter {
    private static final int[] _COLOR_RATIOS = {8, 2, 1};
    private static final int SEED = 25;
    private static final double CHANGE_THRESHOLD = 0.99;
    private static final int MIN_FRAME_HOLD = 30;
    private static final int MAX_FRAME_HOLD = 900;
    private long seed;
    private ArrayList<Image.BlendMode> modes;
    private Random random;
    private ArrayList<Pixel> triColors;
    private int[] COLOR_RATIOS;
    private DelaunayTriangulator tri;
    private int numVerts;
    List<Vector2D> pointSet;

    public DelaunayTriangulation(long seed, int numVerts, int[] color_ratios) {
        this.seed = seed;
        this.random = new Random(seed);
        this.numVerts = numVerts;
        this.COLOR_RATIOS = color_ratios;
        modes = new ArrayList<>();
        for (Image.BlendMode mode : Image.BlendMode.values()) {
            if (mode != Image.BlendMode.FLATTEN)
                modes.add(mode);
        }
        triColors = new ArrayList<>();

        Pixel t = new Pixel(true);
        for (int i = 0; i < COLOR_RATIOS[0]; i++) {
            triColors.add(t);
        }
        for (int i = 1; i < COLOR_RATIOS.length; i++) {
            short r = (short)random.nextInt(256);
            short g = (short)random.nextInt(256);
            short b = (short)random.nextInt(256);
            for (int j = 0; j < COLOR_RATIOS[i]; j++) {
                triColors.add(new Pixel((short)(255 - r), (short)(255 - g), (short)(255 - b)));
            }
        }
    }

    /*public DelaunayTriangulation(int rows, int cols) {
        this(rows, cols, SEED, _COLOR_RATIOS, TRIANGLE_PROB);
    }*/

    @Override
    public Image processImage(Image image, int frameNo) {
        /*HashMap<Image.BlendMode, Graphics> blendLayers = new HashMap<>();
        for (Image.BlendMode mode : Image.BlendMode.values()) {
            blendLayers.put(mode, new Graphics(image.height, image.width));
        }*/
        random = new Random(seed);
        Graphics layer = new Graphics(image.height, image.width);
        try {
            List<Vector2D> pointSet = new ArrayList<>();
            pointSet.add(new Vector2D(0,0));
            pointSet.add(new Vector2D(0,image.height - 1));
            pointSet.add(new Vector2D(image.width - 1,0));
            pointSet.add(new Vector2D(image.width - 1,image.height - 1));
            for (int i = 0; i < numVerts; i++) {
                pointSet.add(new Vector2D(random.nextInt(image.width - 1),
                        random.nextInt(image.height - 1)));
            }
            tri = new DelaunayTriangulator(pointSet);
            tri.triangulate();

            List<Triangle2D> triangleSoup = tri.getTriangles();
            for (Triangle2D triangle : triangleSoup) {
                Pixel color = triColors.get(random.nextInt(triColors.size()));
                layer.triangle((int)triangle.a.x, (int)triangle.a.y,
                        (int)triangle.b.x, (int)triangle.b.y,
                        (int)triangle.c.x, (int)triangle.c.y,
                        color);
            }

        } catch (NotEnoughPointsException e) {
        }


        /*for (Image.BlendMode mode : Image.BlendMode.values()) {
            Graphics layer = blendLayers.get(mode);
            image.blend(layer.getImage(), mode);
        }*/
        image.blend(layer.getImage(), Image.BlendMode.DIFFERENCE);

        return image;
    }

}

