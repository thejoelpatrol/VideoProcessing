package com.laserscorpion.VideoProcessing.filters.UnevenBitShifter;

import com.laserscorpion.VideoProcessing.ByteMemoryAllocator;
import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class UnevenBitShifter implements ImageFilter{
    int rshift;
    int gshift;
    int bshift;
    boolean downsample;
    ByteMemoryAllocator allocator;

    public UnevenBitShifter(int rshift, int gshift, int bshift, boolean downsample) {
        this.rshift = rshift;
        this.gshift = gshift;
        this.bshift = bshift;
        this.downsample = downsample;
        allocator = ByteMemoryAllocator.getInstance();
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        Image toProcess;
        if (downsample) {
            ColorDownsampler sampler = new ColorDownsampler(image);
            toProcess = sampler.downsample(16);
        } else
            toProcess = image;

        byte[] rgbResult = allocator.malloc(3 * toProcess.width * toProcess.height);// new byte[3 * toProcess.width * toProcess.height];
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y*toProcess.width);
                Pixel pixel = toProcess.pixels[y][x];
                rgbResult[i] = rotateRight(pixel.r, rshift);
                rgbResult[i+1] =  rotateRight(pixel.g, gshift);
                rgbResult[i+2] =  rotateRight(pixel.b, bshift);
            }
        }
        image.replaceRGB(rgbResult);
        allocator.free(rgbResult);
        return image;
    }

    private byte rotateRight(short orig, int digits) {
        short result = (short) ((orig >> digits) & 0x00FF);
        result |= (short)((orig << Byte.SIZE - digits) & 0x00FF);
        byte b = (byte)(0xFF & result);
        return b;
    }
}
