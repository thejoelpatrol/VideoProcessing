import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public abstract class ImageWorkerThread extends Thread {
    private Semaphore done;
    protected Image image;
    protected Image processedImage;
    protected PPMFile finishedImage;

    /**
     * Call super() in your subclass, I insist.
     * @param releaseWhenDone
     * @param processThisPlease
     */
    public ImageWorkerThread(Semaphore releaseWhenDone, Image processThisPlease) {
        done = releaseWhenDone;
        image = processThisPlease;
        //System.out.println("acquiring so you can't");
        done.acquireUninterruptibly();
    }

    @Override
    public void run() {
        processImage();
        finishImage();
        done.release();
    }

    private void finishImage() {
        finishedImage = new PPMFile();
        finishedImage.width = processedImage.width;
        finishedImage.height = processedImage.height;
        finishedImage.maxVal = 255;
        finishedImage.data = new byte[finishedImage.width * finishedImage.height * 3];
        for (int i = 0; i < finishedImage.height; i++) {
            for (int j = 0; j < finishedImage.width; j++) {
                Image.Pixel pixel = processedImage.pixels[i][j];
                int index = 3 * (j + i*finishedImage.width);
                finishedImage.data[index] = (byte)(pixel.r & 0xFF);
                finishedImage.data[index+1] = (byte)(pixel.g & 0xFF);
                finishedImage.data[index+2] = (byte)(pixel.b & 0xFF);
            }
        }
    }


    public abstract void processImage();

    public PPMFile getfinishedImage() {
        //if (processedImage == null)
        //    throw new IllegalStateException("not done processing!!1!");
        while (finishedImage == null) {
            // if you call this before you know we're ready due to the semaphore, having to spinlock is your own fault
        }
        return finishedImage;
    }
}
