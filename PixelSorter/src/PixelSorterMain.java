import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class PixelSorterMain {

    public static void main(String[] args) {
        if (args.length != 4)
            printUsageAndExit();

        String infile = args[0];
        int workers = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);
        boolean hsv = Boolean.parseBoolean(args[3]);

        PixelSorterFactory factory = new PixelSorterFactory(hsv);
        ImageFilterFactory[] factories = { factory };
        VideoProcessor processor = new VideoProcessor(infile, factories, workers, scale2x);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar PixelSorterMain.jar video-filepath threads-int scale2x-boolean hsv-boolean");
        System.exit(1);
    }
}
