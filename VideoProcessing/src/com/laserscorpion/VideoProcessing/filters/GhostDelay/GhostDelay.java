package com.laserscorpion.VideoProcessing.filters.GhostDelay;

import com.laserscorpion.VideoProcessing.FrameDelayService;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class GhostDelay implements ImageFilter {
    private static final short CHANNEL_MAX = 0xFF;
    private double alpha = 0.40;
    private FrameDelayService frameService;
    private int numFrames;
    private int delay;

    public GhostDelay(int numFrames, double alpha, int delay) {
        frameService = FrameDelayService.getInstance();
        this.numFrames = numFrames;
        this.alpha = alpha;
        this.delay = delay;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        //byte[] rgbResult = allocator.malloc(3 * image.width * image.height);
        Image[] pastFrames = new Image[numFrames];
        int availableFrames = numFrames;
        for (int i = 0; i < numFrames; i++) {
            pastFrames[i] = frameService.get(frameNo - i - 1 - delay);
            if (pastFrames[i] == null) {
                availableFrames = i;
                break;
            }
        }

        Pixel pixel = new Pixel();
        double opacity = 1 - alpha;
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                //int i = 3 * (x + y*image.width);
                pixel.r = 0;
                pixel.g = 0;
                pixel.b = 0;
                for (int i = availableFrames - 1; i >= 0; i--) {
                    Pixel newerPixel = pastFrames[i].pixels[y][x];
                    pixel.r = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.r + alpha*newerPixel.r));
                    pixel.g = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.g + alpha*newerPixel.g));
                    pixel.b = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.b + alpha*newerPixel.b));
                    //opacity *= alpha;
                }
                image.pixels[y][x].r = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.r + alpha*image.pixels[y][x].r));
                image.pixels[y][x].g = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.g + alpha*image.pixels[y][x].g));
                image.pixels[y][x].b = (short)Math.min(CHANNEL_MAX, (short)(opacity*pixel.b + alpha*image.pixels[y][x].b));
            }
        }
        return image;
    }
}
