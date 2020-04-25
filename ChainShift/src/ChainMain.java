import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class ChainMain {
    public static void main(String[] args) {
        /*
            A sample test of chaining filters together after refactoring the interface that turns out to be good
         */
        if (args.length != 4)
            printUsageAndExit();
        String infile = args[0];
        int WORKERS = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);
        int shift =  Integer.parseInt(args[3]);
        BitShifterFactory factory1 = new BitShifterFactory(shift, false);
        HSVFactory factory2 = new HSVFactory();

        ImageFilterFactory[] factories = new ImageFilterFactory[2];
        factories[0] = factory2;
        factories[1] = factory1;
        VideoProcessor processor = new VideoProcessor(infile, factories, WORKERS, scale2x);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar ChainShift.jar video-filepath threads-int scale2x-bool shift-int");
        System.exit(1);
    }

}