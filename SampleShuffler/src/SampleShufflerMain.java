import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

public class SampleShufflerMain {
    public static void main(String[] args) {
        if (args.length != 6)
            printUsageAndExit();

        String infile = args[0];
        int WORKERS = Integer.parseInt(args[1]);
        int samples = Integer.parseInt(args[2]);
        boolean snap = Boolean.parseBoolean(args[3]);
        int maxSampleHeight = Integer.parseInt(args[4]);
        double glitchProbability = Double.parseDouble(args[5]);
        SampleShufflerFactory factory = new SampleShufflerFactory(samples, snap, maxSampleHeight, glitchProbability);

        ImageFilterFactory[] factories = { factory };
        VideoProcessor processor = new VideoProcessor(infile, factories, WORKERS);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar SampleShifter.jar infile-path threads-int samples-int snap-boolean maxSampleSize-int glitchProbability-float");
        System.exit(1);
    }
}
