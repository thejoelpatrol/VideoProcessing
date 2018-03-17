import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public abstract class ImageWorkerThread extends Thread {
    private Semaphore done;
    protected Image image;
    protected Image processedImage;

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
        done.release();
    }

    public abstract void processImage();

    public Image getfinishedImage() {
        //if (processedImage == null)
        //    throw new IllegalStateException("not done processing!!1!");
        while (processedImage == null) {
            // if you call this before you know we're ready due to the semaphore, having to spinlock is your own fault
        }
        return processedImage;
    }
}
