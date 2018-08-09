import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final int QUEUE_SIZE = 100;
    public static int intParam;

    public static void main(String[] args) {
	/*
	    $ ffmpeg -i /Volumes/OrinocoFlow/raw\ video/glrenderScreenSnapz001.mov -f image2pipe -vcodec ppm pipe:1 | java -jar out/artifacts/PipeShift_jar/PipeShift.jar threads# bit-shift#
	 */

        if (args.length != 2) {
            printUsageAndExit();
        }

        int WORKERS = Integer.parseInt(args[0]);
        //System.err.println("workers " + WORKERS);

        intParam = Integer.parseInt(args[1]);

        BlockingQueue<PPMFile> images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        BlockingQueue<PPMFile> outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        PPMReader reader = new PPMReader(System.in, images, new Object() );
        PPMWriter encoder = new PPMWriter(outputImages, System.out);

        BitShifterFactory factory = new BitShifterFactory();

        WorkerManager manager = new WorkerManager(images, WORKERS, factory, outputImages, encoder);

        reader.start();
        manager.start();
        encoder.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar PipeShift.jar threads-int shift-amount-int");
        System.exit(1);
    }
}
