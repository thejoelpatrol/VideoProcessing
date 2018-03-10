import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class VideoEncoder extends Thread {
    private static final String IMAGE_FORMAT = "PNG";
    private BlockingQueue<BufferedImage> images;
    private boolean keepWaiting = true;
    private String outputDir;
    private int frame = 0;


    public VideoEncoder(BlockingQueue<BufferedImage> images, String outputDir) {
        this.images = images;
        this.outputDir = outputDir;
    }

    public synchronized void run() {
        while (images.size() > 0 || keepWaiting) {
            BufferedImage image = takeAlmostUninterruptibly();
            if (image == null)
                break;
            saveImage(image);
            frame++;
        }
        System.out.println("done encoding");
    }

    private void saveImage(BufferedImage image) {
        String filename = outputDir + "/frame_" + frame + '.' + IMAGE_FORMAT;
        if (filename != null) {
            try {
                ImageIO.write(image, IMAGE_FORMAT, new File(filename));
            } catch (IOException io) {
                System.err.println("couldn't save frame " + frame);
            }
        }
    }

    public void done() {
        keepWaiting = false;
        interrupt();
    }

    private BufferedImage takeAlmostUninterruptibly() {
        while (keepWaiting) {
            try {
                return images.take();
            } catch (InterruptedException e) {}
        }
        return null;
    }

}
