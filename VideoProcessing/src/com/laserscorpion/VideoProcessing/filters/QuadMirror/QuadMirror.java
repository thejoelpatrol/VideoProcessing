package com.laserscorpion.VideoProcessing.filters.QuadMirror;

import com.laserscorpion.VideoProcessing.ByteMemoryAllocator;
import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class QuadMirror implements ImageFilter{
    boolean downsample;
    ByteMemoryAllocator allocator;


    public QuadMirror(boolean downsample) {
        this.downsample = downsample;
        allocator = ByteMemoryAllocator.getInstance();
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        Image toProcess;
        if (downsample) {
            ColorDownsampler sampler = new ColorDownsampler(image);
            toProcess = sampler.downsample(4096);
        } else
            toProcess = image;

        byte[] rgbResult = allocator.malloc(3 * toProcess.width * toProcess.height);
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y * toProcess.width);
                rgbResult[i] = 0;
                rgbResult[i+1] = 0;
                rgbResult[i+2] = 0;
            }
        }
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y*toProcess.width);
                Pixel pixel = toProcess.pixels[y][x];
                rgbResult[i] += (byte)pixel.r;
                rgbResult[i+1] += (byte)pixel.g;
                rgbResult[i+2] += (byte)pixel.b;

                if (y - 1 >= 0) {
                    i = 3 * (x + (y-1)*toProcess.width);
                    pixel = toProcess.pixels[y-1][x];
                    rgbResult[i] += (byte)pixel.r;
                    rgbResult[i+1] += (byte)pixel.g;
                    rgbResult[i+2] += (byte)pixel.b;
                }
                if (y + 1 < toProcess.height) {
                    i = 3 * (x + (y+1)*toProcess.width);
                    pixel = toProcess.pixels[y+1][x];
                    rgbResult[i] += (byte)pixel.r;
                    rgbResult[i+1] += (byte)pixel.g;
                    rgbResult[i+2] += (byte)pixel.b;
                }
                if (x - 1 >= 0) {
                    i = 3 * ((x-1) + y*toProcess.width);
                    pixel = toProcess.pixels[y][x-1];
                    rgbResult[i] += (byte)pixel.r;
                    rgbResult[i+1] += (byte)pixel.g;
                    rgbResult[i+2] += (byte)pixel.b;
                }
                if (x + 1 < toProcess.width) {
                    i = 3 * ((x+1) + y*toProcess.width);
                    pixel = toProcess.pixels[y][x+1];
                    rgbResult[i] += (byte)pixel.r;
                    rgbResult[i+1] += (byte)pixel.g;
                    rgbResult[i+2] += (byte)pixel.b;
                }
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
