import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;

import java.util.ArrayList;

public class VideoMultiProcessor {

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length < 5)
            printUsageAndExit();

        String infile = args[0];
        int workers = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);
        ArrayList<ImageFilterFactory> factories = new ArrayList<>();
        for (int i = 3; i < args.length; i += 2) {
            String filterName = args[i];
            if (!filterName.substring(0, 2).equals("--"))
                printUsageAndExit();
            filterName = filterName.substring(2);
            if (filterName.equals("help")) {
                printUsageAndExit();
            } else if (filterName.equals("SampleShuffler")) {
                String filterArgs[] = args[i + 1].split(" ");
                int samples = Integer.parseInt(filterArgs[0]);
                boolean snap = Boolean.parseBoolean(filterArgs[1]);
                int maxSampleHeight = Integer.parseInt(filterArgs[2]);
                double glitchProbability = Double.parseDouble(filterArgs[3]);
                SampleShufflerFactory factory = new SampleShufflerFactory(samples, snap, maxSampleHeight, glitchProbability);
                factories.add(factory);
            } else if (filterName.equals("PixelSorter")) {
                String filterArgs[] = args[i + 1].split(" ");
                boolean hsv = Boolean.parseBoolean(filterArgs[0]);
                PixelSorterFactory factory = new PixelSorterFactory(hsv);
                factories.add(factory);
            } else if (filterName.equals("RGBHSV")) {
                factories.add(new HSVFactory());
            } else if (filterName.equals("BitShifter")) {
                String filterArgs[] = args[i + 1].split(" ");
                int shift = Integer.parseInt(filterArgs[0]);
                BitShifterFactory factory = new BitShifterFactory(shift, false);
                factories.add(factory);
            } else if (filterName.equals("ImageCipher")) {
                String filterArgs[] = args[i + 1].split(" ");
                boolean downsample = Boolean.parseBoolean(filterArgs[0]);
                factories.add(new VideoCipherFactory(downsample));
            } else if (filterName.equals("ByteShifter")) {
                factories.add(new ByteShiftFactory());
            } else if (filterName.equals("OtherByteShifter")) {
                String filterArgs[] = args[i + 1].split(" ");
                int offsetPerFrame = Integer.parseInt(filterArgs[0]);
                factories.add(new OtherByteShiftFactory(offsetPerFrame));
            } else {
                throw new ClassNotFoundException("Just what filter do you think you're trying to use? " + filterName + "?");
            }
        }

        ImageFilterFactory factoriesArray[] = new ImageFilterFactory[factories.size()];
        for (int i = 0; i < factories.size(); i++) {
            factoriesArray[i] = factories.get(i);
        }
        VideoProcessor processor = new VideoProcessor(infile, factoriesArray, workers, scale2x);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar VideoMultiProcessor.jar video-filepath threads-int scale2x-boolean --Filter \"filter args\" [--MoreFilters \"filter args\"]");
        System.err.println("Available filters:");
        System.err.println("--SampleShuffler \"samples-int snap-boolean max-sample-height-int glitch-probability-double\"");
        System.err.println("--BitShifter shift-int");
        System.err.println("--ImageCipher downsample-boolean");
        System.err.println("--OtherByteShifter offset-per-frame-int");
        System.err.println("--RGBHSV");
        System.err.println("--PixelSorter hsv-boolean");
        System.exit(1);
    }
}
