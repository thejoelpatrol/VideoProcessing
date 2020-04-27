package com.laserscorpion.VideoProcessing.filters.OtherByteShifter;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class OtherByteShifter implements ImageFilter {
    private int offsetPerFrame;

    public OtherByteShifter(int offsetPerFrame) {
        this.offsetPerFrame = offsetPerFrame;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = new byte[3 * image.width * image.height];
        byte[] temp = new byte[3 * image.width * image.height];

        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int i = 3 * (x + y*image.width);
                Pixel pixel = image.pixels[y][x];
                temp[i] = (byte)pixel.r;
                temp[i + 1] = (byte)pixel.g;
                temp[i + 2] = (byte)pixel.b;
            }
        }
        int cutoff = offsetPerFrame*frameNo % rgbResult.length;
        int rest = rgbResult.length - cutoff;

        System.arraycopy(temp, 0, rgbResult, cutoff, rest);
        System.arraycopy(temp, rest, rgbResult, 0, cutoff);

        rgbResult[0] = (byte)0xFF;

        return new Image(rgbResult, image.height, image.width);
    }
}
