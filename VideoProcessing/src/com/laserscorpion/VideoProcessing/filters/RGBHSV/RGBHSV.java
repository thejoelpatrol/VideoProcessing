package com.laserscorpion.VideoProcessing.filters.RGBHSV;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

import java.awt.Color;

public class RGBHSV implements ImageFilter {
    private static final float CHANNEL_MAX = 255.0F;

    /*public com.laserscorpion.VideoProcessing.filters.RGBHSV.RGBHSV(int shift, boolean downsample) {

    }*/

    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = new byte[3 * image.width * image.height];

        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int i = 3 * (x + y*image.width);
                Pixel pixel = image.pixels[y][x];
                float h = pixel.r / CHANNEL_MAX;
                float s = pixel.g / CHANNEL_MAX;
                float v = pixel.b / CHANNEL_MAX;
                int rgb = Color.HSBtoRGB(h, s, v);

                rgbResult[i] = (byte)((rgb & 0xFF0000) >> 16);
                rgbResult[i+1] = (byte)((rgb & 0x00FF00) >> 8);
                rgbResult[i+2] = (byte)(rgb & 0xFF);
            }
        }

        return new Image(rgbResult, image.height, image.width);
    }
}
