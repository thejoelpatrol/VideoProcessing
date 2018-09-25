import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class ChainMain {
    public static void main(String[] args) {
        /*
            Just a sample test of chaining filters together after refactoring the interface
         */

        String infile = args[0];
        int WORKERS = Integer.parseInt(args[1]);
        BitShifterFactory factory1 = new BitShifterFactory(5, false);
        SampleShufflerFactory factory2 = new SampleShufflerFactory(10, false, 600, 0.5);

        ImageFilterFactory[] factories = new ImageFilterFactory[2];
        factories[0] = factory1;
        factories[1] = factory2;
        VideoProcessor processor = new VideoProcessor(infile, factories, WORKERS, true);
        processor.start();
    }

    private static void printUsageAndExit() {
        //System.err.println("Usage: $ java -jar VideoCipher.jar video-filepath threads-int");
        System.exit(1);
    }

}
