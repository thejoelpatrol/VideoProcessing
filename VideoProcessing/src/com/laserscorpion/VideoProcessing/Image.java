package com.laserscorpion.VideoProcessing;

import java.awt.Color;
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

    public Image(int height, int width) {
        this.height = height;
        this.width = width;
        this.pixels = new Pixel[height][];
        for (int i = 0; i < height; i++) {
            this.pixels[i] = new Pixel[width];
        }
    }

    private Pixel[] copyOf(Pixel[] pixels) {
        Pixel[] result =  new Pixel[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            result[i] = pixels[i].copyOf();
        }
        return result;
    }

    public void replaceSample(Image subImage, int xOffset, int yOffset) {
        for (int y = 0; y < subImage.height; y++) {
            for (int x = 0; x < subImage.width; x++) {
                int xLoc = x + xOffset;
                int yLoc = y + yOffset;
                if (xLoc < width && yLoc < height)
                    pixels[yLoc][xLoc] = subImage.pixels[y][x].copyOf();
            }
        }
    }

    public Image(BufferedImage image) {
        int rgb[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        this.height = image.getHeight();
        this.width = image.getWidth();
        pixels = new Pixel[height][];
        for (int i = 0; i < height; i++) {
            pixels[i] = new Pixel[width];
            for (int j = 0; j < width; j++) {
                int k = j + i*width;
                Pixel p = new Pixel();
                p.r = (short)((rgb[k] & 0xFF0000) >> 16);
                p.g = (short)((rgb[k] & 0x00FF00) >> 8);
                p.b = (short)(rgb[k] & 0x0000FF);
                pixels[i][j] = p;
            }
        }
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

    /**
     * For performance, you should ByteMemoryAllocator.free() the returned array
     * @return
     */
    public byte[] toRGBArray() {
        ByteMemoryAllocator allocator = ByteMemoryAllocator.getInstance();
        byte[] rgbResult = allocator.malloc(3 * width * height);
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

    public void replaceRGB(byte rgb[]) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel pixel = pixels[y][x];
                int i = 3 * (x + y*width);
                pixel.r = promote(rgb[i]);
                pixel.g = promote(rgb[i + 1]);
                pixel.b = promote(rgb[i + 2]);
            }
        }
    }

    private short promote(byte b) {
        return (short)((short)0x00FF & (short)b);
    }

    public void blend(Image top, BlendMode mode) {
        switch (mode) {
            case FLATTEN:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (!top.pixels[i][j].is_transparent()) {
                            pixels[i][j] = top.pixels[i][j].copyOf();
                        }
                    }
                }
                break;
            case ADD:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (top.pixels[i][j].is_transparent())
                            continue;
                        pixels[i][j].r = (short)((pixels[i][j].r + top.pixels[i][j].r) & 0x00FF);
                        pixels[i][j].g = (short)((pixels[i][j].g + top.pixels[i][j].g) & 0x00FF);
                        pixels[i][j].b = (short)((pixels[i][j].b + top.pixels[i][j].b) & 0x00FF);
                    }
                }
                break;
            case SUBTRACT:
            case DIFFERENCE:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (top.pixels[i][j].is_transparent())
                            continue;
                        //if (i == 190 && j==220)
                         //   System.out.println("hello");
                        if (top.pixels[i][j].r != 0)
                            pixels[i][j].r = (short)(((byte)top.pixels[i][j].r - (byte)pixels[i][j].r) & 0x00FF);
                        if (top.pixels[i][j].g != 0)
                            pixels[i][j].g = (short)(((byte)top.pixels[i][j].g - (byte)pixels[i][j].g) & 0x00FF);
                        if (top.pixels[i][j].b != 0)
                            pixels[i][j].b = (short)(((byte)top.pixels[i][j].b - (byte)pixels[i][j].b) & 0x00FF);
                    }
                }
                break;
            case LIGHTER_COLOR:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (top.pixels[i][j].is_transparent())
                            continue;
                        float hsvBottom[] = Color.RGBtoHSB(pixels[i][j].r, pixels[i][j].b, pixels[i][j].b, null);
                        float hsvTop[] = Color.RGBtoHSB(top.pixels[i][j].r, top.pixels[i][j].b, top.pixels[i][j].b, null);
                        if (hsvTop[2] > hsvBottom[2]) {
                            pixels[i][j] = top.pixels[i][j].copyOf();
                        }
                    }
                }
                break;
            case DARKER_COLOR:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (top.pixels[i][j].is_transparent())
                            continue;
                        float hsvBottom[] = Color.RGBtoHSB(pixels[i][j].r, pixels[i][j].b, pixels[i][j].b, null);
                        float hsvTop[] = Color.RGBtoHSB(top.pixels[i][j].r, top.pixels[i][j].b, top.pixels[i][j].b, null);
                        if (hsvTop[2] < hsvBottom[2]) {
                            pixels[i][j] = top.pixels[i][j].copyOf();
                        }
                    }
                }
                break;
        }
    }

    public enum BlendMode {
        FLATTEN,
        ADD,
        SUBTRACT,
        DIFFERENCE,
        LIGHTER_COLOR,
        DARKER_COLOR
    }
}
