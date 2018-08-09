import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    //public static int intParam;

    public static void main(String[] args) {
	/*
	    $ ffmpeg -i /Volumes/OrinocoFlow/raw\ video/glrenderScreenSnapz001.mov -f image2pipe -vcodec ppm pipe:1 | java -jar out/artifacts/PipeShift_jar/PipeShift.jar threads# bit-shift#
	 */
        if (args.length != 2)
            printUsageAndExit();

        int WORKERS = Integer.parseInt(args[0]);
        int shift = Integer.parseInt(args[1]);
        BitShifterFactory factory = new BitShifterFactory(shift, false);

        VideoProcessor processor = new VideoProcessor(factory, WORKERS);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar PipeShift.jar threads-int shift-amount-int");
        System.exit(1);
    }
}
