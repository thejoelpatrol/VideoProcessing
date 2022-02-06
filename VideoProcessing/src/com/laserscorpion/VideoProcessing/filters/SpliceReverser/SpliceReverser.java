package com.laserscorpion.VideoProcessing.filters.SpliceReverser;

import com.laserscorpion.VideoProcessing.ByteMemoryAllocator;
import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

import java.util.Arrays;

public class SpliceReverser implements ImageFilter {
    ByteMemoryAllocator allocator;
    boolean red, blue, green;

    public SpliceReverser(boolean red, boolean green, boolean blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        allocator = ByteMemoryAllocator.getInstance();
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        Image toProcess = image;
        byte[] redpx = null, greenpx = null, bluepx = null;

        int len = toProcess.width * toProcess.height;
        byte[] rgbResult = allocator.malloc(3 * len);
        if (this.blue)
            bluepx = allocator.malloc(len);
        if (this.red)
            redpx = allocator.malloc(len);
        if (this.blue)
            greenpx = allocator.malloc(len);
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y*toProcess.width);
                Pixel pixel = toProcess.pixels[y][x];
                if (this.red)
                    redpx[x + y*toProcess.width] = (byte)pixel.r;
                else
                    rgbResult[i] =  (byte)pixel.r;
                if (this.green)
                    greenpx[x + y*toProcess.width] = (byte)pixel.g;
                else
                    rgbResult[i+1] =  (byte)pixel.g;
                if (this.blue)
                    bluepx[x + y*toProcess.width] = (byte)pixel.b;
                else
                    rgbResult[i+2] =  (byte)pixel.b;
            }
        }

        int j = len;
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y*toProcess.width);
                j--;
                if (this.red)
                    rgbResult[i] = redpx[j];
                if (this.green)
                    rgbResult[i+1] = greenpx[j];
                if (this.blue)
                    rgbResult[i+2] = bluepx[j];
            }
        }

        image.replaceRGB(rgbResult);
        if (this.red)
            allocator.free(redpx);
        if (this.green)
            allocator.free(greenpx);
        if (this.blue)
            allocator.free(bluepx);
        allocator.free(rgbResult);
        return image;
    }
}
