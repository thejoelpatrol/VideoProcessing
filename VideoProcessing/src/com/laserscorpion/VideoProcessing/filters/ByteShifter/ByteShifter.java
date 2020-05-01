package com.laserscorpion.VideoProcessing.filters.ByteShifter;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class ByteShifter implements ImageFilter {
    private static final float CHANNEL_MAX = 255.0F;

    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = image.toRGBArray();

        /*for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int i = 3 * (x + y*image.width);
                Pixel pixel = image.pixels[y][x];
                rgbResult[i] = (byte)pixel.r;
                rgbResult[i+1] = (byte)pixel.g;
                rgbResult[i+2] = (byte)pixel.b;
            }
        }*/
        System.arraycopy(rgbResult, 0, rgbResult, 1, rgbResult.length - 1);

        rgbResult[0] = (byte)0xFF; // why?
        image.replaceRGB(rgbResult);
        return image;
    }
}
