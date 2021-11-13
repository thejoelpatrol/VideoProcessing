package com.laserscorpion.VideoProcessing.drawing;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.Pixel;

public class Graphics {
    private Image canvas;

    public Graphics(int height, int width) {
        Pixel transparent[] = new Pixel[width * height];
        Pixel p = new Pixel(true);

        for (int i = 0; i < transparent.length; i++) {
            transparent[i] = p;
        }
        canvas = new Image(transparent, height, width);
    }

    public Image getImage() {
        return canvas;
    }

    public void background(Pixel color) {
        Pixel bg = color.copyOf(); // intentionally using one pixel ptr for entire bg
        for (int i = 0; i < canvas.height; i++) {
            for (int j = 0; j < canvas.width; j++) {
                canvas.pixels[i][j] = bg;
            }
        }
    }

    public void rectangle(int x, int y, int width, int height, Pixel color) {
        Pixel c = color.copyOf(); // intentionally using one pixel ptr for entire shape
        for (int i = y; i < y + height; i++) {
            if (i >= canvas.height)
                break;
            for (int j = x; j < x + width; j++) {
                if (j >= canvas.width)
                    break;
                canvas.pixels[i][j] = c;
            }
        }
    }



}
