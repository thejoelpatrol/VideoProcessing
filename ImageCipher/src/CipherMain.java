import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class CipherMain {
    public static void main(String[] args) {
        if (args.length != 2)
            printUsageAndExit();

        String infile = args[0];
        int WORKERS = Integer.parseInt(args[1]);
        VideoCipherFactory factory = new VideoCipherFactory(true);

        ImageFilterFactory[] factories = { factory };
        VideoProcessor processor = new VideoProcessor(infile, factories, WORKERS, true);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar VideoCipher.jar video-filepath threads-int");
        System.exit(1);
    }

}
