package com.laserscorpion.VideoProcessing.filters.BoxTest;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;
import com.laserscorpion.VideoProcessing.drawing.Graphics;

public class BoxTest implements ImageFilter {
    private int x;
    private int y;
    private int height;
    private int width;
    private Graphics boxLayer;

    public BoxTest(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        if (boxLayer == null) {
            boxLayer = new Graphics(image.height, image.width);
            Pixel c = new Pixel((short)255, (short)0, (short)0);
            boxLayer.rectangle(x, y, width, height, c);
        }

        Image box = boxLayer.getImage();
        image.blend(box, Image.BlendMode.FLATTEN);

        return image;
    }
}

