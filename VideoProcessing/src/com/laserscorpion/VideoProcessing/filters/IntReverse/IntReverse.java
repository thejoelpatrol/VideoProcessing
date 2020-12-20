package com.laserscorpion.VideoProcessing.filters.IntReverse;

import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class IntReverse implements ImageFilter {
    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = image.toRGBArray();

        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int i = 3 * (x + y*image.width);
                Pixel pixel = image.pixels[y][x];
                int orig = pixel.toInt();
                int reversed = Integer.reverse(orig);

                rgbResult[i] = (byte)(reversed >> 24);
                rgbResult[i+1] = (byte)(reversed >> 16 & 0xFF);
                rgbResult[i+2] = (byte)(reversed >> 8 & 0xFF);
            }
        }
        image.replaceRGB(rgbResult);
        return image;
    }
}
