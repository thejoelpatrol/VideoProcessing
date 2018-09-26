import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class HSVMain {

    public static void main(String[] args) {
        if (args.length != 3)
            printUsageAndExit();

        String infile = args[0];
        int workers = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);

        HSVFactory factory = new HSVFactory();
        ImageFilterFactory[] factories = { factory };
        VideoProcessor processor = new VideoProcessor(infile, factories, workers, scale2x);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar HSVRGB.jar video-filepath threads-int scale2x-boolean");
        System.exit(1);
    }
}
