import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class VideoEncoder extends Thread {
    private static final String IMAGE_FORMAT = "JPG";
    private BlockingQueue<BufferedImage> images;
    private boolean keepWaiting = true;
    private String outputDir;
    private int frame = 0;


    public VideoEncoder(BlockingQueue<BufferedImage> images, String outputDir) {
        this.images = images;
        this.outputDir = outputDir + '/' + new Date().getTime() + '/';
        File f = new File(this.outputDir);
        f.mkdir();
    }

    public synchronized void run() {
        while (images.size() > 0 || keepWaiting) {
            BufferedImage image = takeAlmostUninterruptibly();
            if (image == null)
                break;
            saveImage(image);
            frame++;
            //if (frame % 200 == 0)
              //  System.out.println("queue size " + images.size());
        }
        System.out.println("done encoding");
    }

    private void saveImage(BufferedImage image) {
        if (frame % 500 == 0)
            System.out.println(new Date().toString() + ": saving frame " + frame);
        String filename = outputDir + "frame_" + frame + '.' + IMAGE_FORMAT;
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
