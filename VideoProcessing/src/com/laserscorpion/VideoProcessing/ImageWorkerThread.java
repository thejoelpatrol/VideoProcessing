package com.laserscorpion.VideoProcessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

public class ImageWorkerThread extends Thread {
    private Semaphore imageReady;
    private boolean keepWaiting = true;
    protected PPMFile ppm;
    protected Image image;
    //protected Image processedImage;
    protected PPMFile finishedImage;
    protected BlockingQueue<PPMFile> queue; // this queue only ever has max one thing in it, but it's good for synchronization
    protected Queue<PPMFile> scratchImages;
    protected ImageFilter[] filters;
    protected ByteMemoryAllocator byteMemoryAllocator;
    protected int currentFrameNo;

    /**
     * Call super() in your subclass, I insist.
     * @param outputReady
     */
    public ImageWorkerThread(ImageFilter[] filters, Semaphore outputReady, Queue<PPMFile> scratchImages) {
        this.imageReady = outputReady;
        queue = new ArrayBlockingQueue<PPMFile>(1);
        this.filters = filters;
        this.scratchImages = scratchImages;
        this.byteMemoryAllocator = ByteMemoryAllocator.getInstance();
    }

    @Override
    public void run() {
        while (keepWaiting) {
            ppm = takeAlmostUninterruptibly();
            if (ppm == null)
                return;

            if (image == null || !(image.width == ppm.width && image.height == ppm.height))
                image = new Image(ppm.data, ppm.height, ppm.width);
            else
                image.replaceRGB(ppm.data);

            for (int i = 0; i < filters.length; i++) {

                image = filters[i].processImage(image, currentFrameNo);
            }
            finishImage();
            //scratchImages.add(ppm); // don't reenable this
            ppm = null;
            imageReady.release();
        }
    }

    private PPMFile takeAlmostUninterruptibly() {
        while (keepWaiting) {
            try {
                return queue.take();
            } catch (InterruptedException e) {}
        }
        return null;
    }

    public synchronized void finishRunning() {
        keepWaiting = false;
        interrupt();
    }

    private void finishImage() {
        finishedImage = new PPMFile();
        finishedImage.width = image.width;
        finishedImage.height = image.height;
        finishedImage.maxVal = 255;
        finishedImage.data = byteMemoryAllocator.malloc(finishedImage.width * finishedImage.height * 3);//new byte[finishedImage.width * finishedImage.height * 3];
        for (int i = 0; i < finishedImage.height; i++) {
            for (int j = 0; j < finishedImage.width; j++) {
                Pixel pixel = image.pixels[i][j];
                int index = 3 * (j + i*finishedImage.width);
                finishedImage.data[index] = (byte)(pixel.r & 0xFF);
                finishedImage.data[index+1] = (byte)(pixel.g & 0xFF);
                finishedImage.data[index+2] = (byte)(pixel.b & 0xFF);
            }
        }
    }

    public void setImage(PPMFile image, int frameNo) {
        finishedImage = null;
        imageReady.acquireUninterruptibly();
        queue.add(image);
        currentFrameNo = frameNo;
    }

    public PPMFile getfinishedImage() {
        //if (processedImage == null)
        //    throw new IllegalStateException("not done processing!!1!");
        while (finishedImage == null) {
            // if you call this before you know we're ready due to the semaphore, having to spinlock is your own fault
        }

        return finishedImage;
    }

    public Image getImage() {
        return new Image(image);
    }
}
