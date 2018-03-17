import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class PPMWriter extends Thread {
    private static final String IMAGE_FORMAT = "JPG";
    private BlockingQueue<Image> images;
    private boolean keepWaiting = true;
//    private String outputDir;
    private OutputStream output;
    byte[] magic = new String("P6\n").getBytes(StandardCharsets.US_ASCII);
    byte[] widthHeight;
    byte[] maxVal = new String("255\n").getBytes(StandardCharsets.US_ASCII);
    private int frame = 0;


    public PPMWriter(BlockingQueue<Image> images, OutputStream output) {
        this.images = images;
        this.output = output;
        /*this.outputDir = outputDir + '/' + new Date().getTime() + '/';
        File f = new File(this.outputDir);
        f.mkdir();*/
    }

    public synchronized void run() {
        while (images.size() > 0 || keepWaiting) {
            Image image = takeAlmostUninterruptibly();
            if (image == null)
                break;
            saveImage(image);
            frame++;
            //System.exit(0);
            if (frame % 200 == 0)
                System.err.println("queue size " + images.size());
        }
        System.err.println("done encoding");
    }

    private void saveImage(Image image) {
        //if (frame % 500 == 0)
        //    System.err.println(new Date().toString() + ": saving frame " + frame);

        if (widthHeight == null)
            widthHeight = new String(image.width + " " + image.height + '\n').getBytes(StandardCharsets.US_ASCII);

        try {
            output.write(magic);
            output.write(widthHeight);
            output.write(maxVal);
            for (Image.Pixel[] row : image.pixels) {
                for (Image.Pixel pixel : row) {
                    output.write(pixel.r & 0xFF);
                    output.write(pixel.g & 0xFF);
                    output.write(pixel.b & 0xFF);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*String filename = outputDir + "frame_" + frame + '.' + IMAGE_FORMAT;
        if (filename != null) {
            try {
                ImageIO.write(image, IMAGE_FORMAT, new File(filename));
            } catch (IOException io) {
                System.err.println("couldn't save frame " + frame);
            }
        }*/
    }

    public void done() {
        keepWaiting = false;
        interrupt();
    }

    private Image takeAlmostUninterruptibly() {
        while (keepWaiting) {
            try {
                return images.take();
            } catch (InterruptedException e) {}
        }
        return null;
    }

}
