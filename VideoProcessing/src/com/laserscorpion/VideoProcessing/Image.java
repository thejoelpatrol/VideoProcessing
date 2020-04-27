package com.laserscorpion.VideoProcessing;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Image {
    public Pixel[][] pixels;
    public int height;
    public int width;

    public Image(byte[] rgbVals, int height, int width) {
        this.height = height;
        this.width = width;
        pixels = new Pixel[height][];
        for (int i = 0; i < height; i++) {
            pixels[i] = new Pixel[width];
            for (int j = 0; j < width; j++) {
                int k = 3*(j + i*width);
                Pixel p = new Pixel();
                p.r = promote(rgbVals[k]);
                p.g = promote(rgbVals[k+1]);
                p.b = promote(rgbVals[k+2]);
                pixels[i][j] = p;
            }
        }
    }

    public Image(Pixel[] pixels, int height, int width) {
        this.pixels = new Pixel[height][];
        for (int i = 0; i < height; i++) {
            this.pixels[i] = new Pixel[width];
            for (int j = 0; j < width; j++) {
                this.pixels[i][j] = pixels[j + i*width];
            }
        }
        this.height = height;
        this.width = width;
    }

    public Image(Image copy) {
        height = copy.height;
        width = copy.width;
        pixels = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            pixels[i] = copyOf(copy.pixels[i]);
        }
    }

    private Pixel[] copyOf(Pixel[] pixels) {
        Pixel[] result = new Pixel[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = pixels[i];
        }
        return result;
    }

    public void replaceSample(Image subImage, int xOffset, int yOffset) {
        for (int y = 0; y < subImage.height; y++) {
            for (int x = 0; x < subImage.width; x++) {
                int xLoc = x + xOffset;
                int yLoc = y + yOffset;
                if (xLoc < width && yLoc < height)
                    pixels[yLoc][xLoc] = subImage.pixels[y][x];
            }
        }
    }

    public Image(BufferedImage image) {
        throw new NotImplementedException(); // this is not a great thing to do...should probably just create a new exception type
        //Image(image.getRGB())
    }

    public BufferedImage toBufferedImage() {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = 0;
                color |= ((pixels[i][j].r << 16) & 0x00FF0000);
                color |= ((pixels[i][j].g << 8) & 0x0000FF00);
                color |= ((pixels[i][j].b) & 0x000000FF);
                result.setRGB(j, i, color);
            }
        }
        return result;
    }

    public byte[] toRGBArray() {
        byte[] rgbResult = new byte[3 * width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = pixels[y][x];
                int i = 3 * (x + y*width);
                rgbResult[i] = (byte)pixel.r;
                rgbResult[i+1] =  (byte)(pixel.g);
                rgbResult[i+2] =  (byte)(pixel.b);
            }
        }
        return rgbResult;
    }

    private short promote(byte b) {
        return (short)((short)0x00FF & (short)b);
    }

}
