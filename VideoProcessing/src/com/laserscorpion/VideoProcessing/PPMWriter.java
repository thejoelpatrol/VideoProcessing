package com.laserscorpion.VideoProcessing;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

class PPMWriter extends Thread {
    private static final String IMAGE_FORMAT = "JPG";
    private BlockingQueue<PPMFile> images;
    private Queue<PPMFile> scratchImages;
    private boolean keepWaiting = true;
//    private String outputDir;
    private List<OutputStream> outputs;
    byte[] magic = new String("P6\n").getBytes(StandardCharsets.US_ASCII);
    byte[] widthHeight;
    byte[] maxVal = new String("255\n").getBytes(StandardCharsets.US_ASCII);
    private int frame = 0;

    public PPMWriter(BlockingQueue<PPMFile> images, List<OutputStream> outputs, Queue<PPMFile> scratchImages) {
        this.images = images;
        this.outputs = outputs;
        this.scratchImages = scratchImages;
        /*this.outputDir = outputDir + '/' + new Date().getTime() + '/';
        File f = new File(this.outputDir);
        f.mkdir();*/
    }

    public synchronized void run() {
        while (images.size() > 0 || keepWaiting) {
            PPMFile image = takeAlmostUninterruptibly();
            if (image == null)
                break;
            saveImage(image);
            scratchImages.add(image);
            frame++;
            //System.exit(0);
            if (frame % 200 == 0)
                System.err.println("output queue size " + images.size() + "   ");
        }
        System.err.println("done encoding");
        try {
            for (OutputStream output : outputs) {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("This is going to freeze.");
        }

    }

    private void saveImage(PPMFile image) {

        if (widthHeight == null)
            widthHeight = new String(image.width + " " + image.height + '\n').getBytes(StandardCharsets.US_ASCII);

        try {
            for (OutputStream output : outputs) {
                output.write(magic);
                output.write(widthHeight);
                output.write(maxVal);
                output.write(image.data);
            }
            if (frame % 500 == 0)
                System.err.println(new Date().toString() + ": saved frame " + frame);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void done() {
        keepWaiting = false;
        interrupt();
    }

    private PPMFile takeAlmostUninterruptibly() {
        while (keepWaiting || images.size() > 0) {
            try {
                return images.take();
            } catch (InterruptedException e) {}
        }
        return null;
    }

}
