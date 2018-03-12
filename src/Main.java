import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final int WORKERS = 3;

    public static int intParam;

    public static void main(String[] args) {
	/*
	    $ ffmpeg -i /Volumes/OrinocoFlow/raw\ video/glrenderScreenSnapz001.mov -f image2pipe -vcodec ppm pipe:1 | java -jar out/artifacts/PipeShift_jar/PipeShift.jar output-dir/
	 */
        //boolean useParamWindow = (args.length == 1);

        if (args.length != 2) {
            printUsageAndExit();
        }

        intParam = Integer.parseInt(args[1]);

        BlockingQueue<Image> images = new LinkedBlockingQueue<>(30);
        BlockingQueue<BufferedImage> outputImages = new LinkedBlockingQueue<>(30);
        PPMReader reader = new PPMReader(System.in, images, new Object() );
        VideoEncoder encoder = new VideoEncoder(outputImages, args[0]);
        WorkerManager manager = new WorkerManager(images, WORKERS, outputImages, encoder);

        reader.start();
        manager.start();
        encoder.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar PipeShift.jar output-dir/ shift-amount-int");
        System.exit(1);
    }
}
