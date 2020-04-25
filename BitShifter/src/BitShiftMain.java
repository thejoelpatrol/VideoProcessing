import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class BitShiftMain {
    //public static int intParam;

    public static void main(String[] args) {
	/*
	    $ ffmpeg -i /Volumes/OrinocoFlow/raw\ video/glrenderScreenSnapz001.mov -f image2pipe -vcodec ppm pipe:1 | java -jar out/artifacts/PipeShift_jar/PipeShift.jar threads# bit-shift#
	 */
        if (args.length != 4)
            printUsageAndExit();

        String infile = args[0];
        int WORKERS = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);
        int shift = Integer.parseInt(args[3]);
        BitShifterFactory factory = new BitShifterFactory(shift, false);

        ImageFilterFactory[] factories = { factory };
        VideoProcessor processor = new VideoProcessor(infile, factories, WORKERS, scale2x);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar PipeShift.jar video-filepath threads-int scale2x-boolean shift-amount-int");
        System.exit(1);
    }
}
