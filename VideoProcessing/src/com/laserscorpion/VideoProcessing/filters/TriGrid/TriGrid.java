package com.laserscorpion.VideoProcessing.filters.TriGrid;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;
import com.laserscorpion.VideoProcessing.drawing.Graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TriGrid implements ImageFilter {
    private static final double TRIANGLE_PROB = 0.5;
    private static final int[] _COLOR_RATIOS = {8, 2, 1};
    private static final int SEED = 25;
    private static final double CHANGE_THRESHOLD = 0.99;
    private static final int MIN_FRAME_HOLD = 30;
    private static final int MAX_FRAME_HOLD = 900;
    private int rows;
    private int cols;
    private int entrySize;
    private long seed;
    private ArrayList<Image.BlendMode> modes;
    private Box[][] grid;
    private Random random;
    private ArrayList<Pixel> boxColors;
    private ArrayList<Pixel> triColors;
    private boolean[] triangleProb;
    private int[] COLOR_RATIOS;


    public TriGrid(int rows, int cols, long seed, int[] color_ratios, double triangleProb) {
        this.rows = rows;
        this.cols = cols;
        this.seed = seed;
        this.random = new Random(seed);
        this.COLOR_RATIOS = color_ratios;
        grid = new Box[rows][];
        modes = new ArrayList<>();
        for (Image.BlendMode mode : Image.BlendMode.values()) {
            if (mode != Image.BlendMode.FLATTEN)
                modes.add(mode);
        }
        for (int i = 0; i < rows; i++) {
            grid[i] = new Box[cols];
            for (int j = 0; j < cols; j++) {
                int everyNthFrame = MIN_FRAME_HOLD + random.nextInt(MAX_FRAME_HOLD);
                grid[i][j] = new Box(everyNthFrame, random.nextInt());
            }
        }
        boxColors = new ArrayList<>();
        triColors = new ArrayList<>();
        this.triangleProb = new boolean[100];
        for (int i = 0; i < triangleProb * 100; i++) {
                this.triangleProb[i] = true;
        }

        Pixel t = new Pixel(true);
        for (int i = 0; i < COLOR_RATIOS[0]; i++) {
            boxColors.add(t);
            triColors.add(t);
        }
        for (int i = 1; i < COLOR_RATIOS.length; i++) {
            short r = (short)random.nextInt(256);
            short g = (short)random.nextInt(256);
            short b = (short)random.nextInt(256);
            for (int j = 0; j < COLOR_RATIOS[i]; j++) {
                boxColors.add(new Pixel(r, g, b));
                triColors.add(new Pixel((short)(255 - r), (short)(255 - g), (short)(255 - b)));
            }
        }
        /*short r = (short)random.nextInt(256);
        short g = (short)random.nextInt(256);
        short b = (short)random.nextInt(256);
        boxColors.add(new Pixel(r, g, b));
        triColors.add(new Pixel((short)(255 - r), (short)(255 - g), (short)(255 - b)));
        boxColors.add(new Pixel(true));
        triColors.add(new Pixel(true));*/
    }

    public TriGrid(int rows, int cols) {
        this(rows, cols, SEED, _COLOR_RATIOS, TRIANGLE_PROB);
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        HashMap<Image.BlendMode, Graphics> blendLayers = new HashMap<>();
        for (Image.BlendMode mode : Image.BlendMode.values()) {
            blendLayers.put(mode, new Graphics(image.height, image.width));
        }
        int height = image.height / rows;
        int width = image.width / cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Box box = grid[i][j];
                Graphics item = box.draw(frameNo, height, width);
                Graphics layer = blendLayers.get(box.blendMode);
                layer.place(item, j*width, i*height);
            }
        }

        for (Image.BlendMode mode : Image.BlendMode.values()) {
            Graphics layer = blendLayers.get(mode);
            image.blend(layer.getImage(), mode);
        }

        return image;
    }

    private class Box {
        private int x;
        private int y;
        private int everyNthFrame;
        private long seed;
        public Image.BlendMode blendMode;

        public Box(int everyNthFrame, long seed) {
            this.everyNthFrame = everyNthFrame;
            this.seed = seed;
        }

        public Graphics draw(int frameNo, int height, int width) {

            long s = seed + frameNo - (frameNo % everyNthFrame);
            random = new Random(s);
            //blendMode = modes.get(random.nextInt(modes.size()));
            blendMode = Image.BlendMode.DIFFERENCE;

            int i = random.nextInt(boxColors.size());
            Graphics box = new Graphics(height, width);
            box.rectangle(0, 0, width, height, boxColors.get(i));

            boolean triangle = triangleProb[random.nextInt(100)];
            if (triangle) {
                box.triangle(0, 0, width - 1/*width/2*/, height/2, 0, height - 1, triColors.get(i));
            }

            return box;
        }


    }
}

