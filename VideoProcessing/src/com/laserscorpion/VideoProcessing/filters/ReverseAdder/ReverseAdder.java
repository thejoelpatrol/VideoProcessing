package com.laserscorpion.VideoProcessing.filters.ReverseAdder;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

import java.awt.Color;
import java.util.Arrays;

public class ReverseAdder implements ImageFilter {
    private static final float CHANNEL_MAX = 255.0F;

    public ReverseAdder() {

    }

    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = image.toRGBArray();

        for (int i = 0; i < rgbResult.length; i++) {
            int j = rgbResult.length - i - 1;
            rgbResult[i] += rgbResult[j];
        }
        image.replaceRGB(rgbResult);
        return image;
    }

}
