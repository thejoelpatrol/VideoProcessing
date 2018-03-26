import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public abstract class ImageWorkerThread extends Thread {
    private Semaphore imageReady;
    private boolean keepWaiting = true;
    protected Image image;
    protected Image processedImage;
    protected PPMFile finishedImage;
    protected BlockingQueue<Image> queue;

    /**
     * Call super() in your subclass, I insist.
     * @param outputReady
     * @param processThisPlease
     */
    public ImageWorkerThread(Semaphore outputReady, Image processThisPlease) {
        this.imageReady = outputReady;
        queue = new ArrayBlockingQueue<Image>(1);
        queue.add(processThisPlease);
        //image = processThisPlease;
        imageReady.acquireUninterruptibly();
    }

    @Override
    public void run() {
        while (keepWaiting) {
            image = takeAlmostUninterruptibly();
            if (image == null)
                return;
            processImage();
            finishImage();
            image = null;
            imageReady.release();
        }
    }

    private Image takeAlmostUninterruptibly() {
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

    public void setImage(Image image) {
        imageReady.acquireUninterruptibly();
        queue.add(image);
        finishedImage = null;
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
