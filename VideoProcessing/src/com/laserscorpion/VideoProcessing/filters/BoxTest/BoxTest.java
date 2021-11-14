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
    private Graphics differenceLayer;
    private Graphics differenceLayer2;
    private Graphics differenceLayer3;
    private Graphics lighterColorLayer;

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
            Pixel g = new Pixel((short)0, (short)255,(short)0);
            boxLayer.line(x, y, x+100, y+100, g);
            boxLayer.line(x, y+100, x+100, y, g);
            boxLayer.line(x, y+100, x+100, y+50, g);
            boxLayer.line(x, y+100, x+100, y+75, g);
            boxLayer.line(x, y+100, x+50, y, g);
            boxLayer.line(x, y+100, x+25, y, g);

            boxLayer.triangle(x + 200, y, x+300, y + 100, x + 200, y + 100, g);
        }
        if (differenceLayer == null) {
            differenceLayer = new Graphics(image.height, image.width);
            Pixel c = new Pixel((short)255, (short)255, (short)255);
            differenceLayer.rectangle(x + 100, y + 100, width, height, c);
        }
        if (differenceLayer2 == null) {
            differenceLayer2 = new Graphics(image.height, image.width);
            Pixel c = new Pixel((short)105, (short)105, (short)0);
            differenceLayer2.rectangle(x, y + 100, width, height, c);
        }
        if (differenceLayer3 == null) {
            differenceLayer3 = new Graphics(image.height, image.width);
            Pixel c = new Pixel((short)0, (short)255, (short)0);
            differenceLayer3.rectangle(x + 100, y, width, height, c);
        }
        if (lighterColorLayer == null) {
            lighterColorLayer = new Graphics(image.height, image.width);
            Pixel c = new Pixel((short)0, (short)255, (short)255);
            lighterColorLayer.triangle(x + 200, y, x+300, y, x + 300, y + 100, c);
        }


        Image box = boxLayer.getImage();
        image.blend(box, Image.BlendMode.FLATTEN);
        Image differenceBox = differenceLayer.getImage();
        image.blend(differenceBox, Image.BlendMode.DIFFERENCE);
        Image differenceBox2 = differenceLayer2.getImage();
        image.blend(differenceBox2, Image.BlendMode.DIFFERENCE);
        Image differenceBox3 = differenceLayer3.getImage();
        image.blend(differenceBox3, Image.BlendMode.DIFFERENCE);
        Image lightBox = lighterColorLayer.getImage();
        image.blend(lightBox, Image.BlendMode.LIGHTER_COLOR);


        return image;
    }
}

