import com.laserscorpion.VideoProcessing.ImageFilterFactory;
import com.laserscorpion.VideoProcessing.OutputProcessFactory;
import com.laserscorpion.VideoProcessing.VideoProcessor;
import com.laserscorpion.VideoProcessing.filters.BitShifter.BitShifterFactory;
import com.laserscorpion.VideoProcessing.filters.BoxTest.BoxTestFactory;
import com.laserscorpion.VideoProcessing.filters.ByteShifter.ByteShiftFactory;
import com.laserscorpion.VideoProcessing.filters.ChillerShuffler.ChillerShufflerFactory;
import com.laserscorpion.VideoProcessing.filters.GhostDelay.GhostDelayFactory;
import com.laserscorpion.VideoProcessing.filters.ImageCipher.VideoCipherFactory;
import com.laserscorpion.VideoProcessing.filters.IntReverse.IntReverseFactory;
import com.laserscorpion.VideoProcessing.filters.OtherByteShifter.OtherByteShiftFactory;
import com.laserscorpion.VideoProcessing.filters.PNGEncoder.PNGEncoderFactory;
import com.laserscorpion.VideoProcessing.filters.PixelSorter.PixelSorterFactory;
import com.laserscorpion.VideoProcessing.filters.QuadMirror.QuadMirrorFactory;
import com.laserscorpion.VideoProcessing.filters.RGBHSV.HSVFactory;
import com.laserscorpion.VideoProcessing.filters.ReverseAdder.ReverseAdderFactory;
import com.laserscorpion.VideoProcessing.filters.SampleShuffler.SampleShufflerFactory;
import com.laserscorpion.VideoProcessing.filters.TriGrid.TriGridFactory;
import com.laserscorpion.VideoProcessing.filters.UnevenBitShifter.UnevenBitShifterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class VideoMultiProcessor {

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length < 5)
            printUsageAndExit();

        boolean ffplay = false, x264 = false, nvenc = false, tcp = false, udp = false;
        int x264crf = -1;
        int nvencMaxrate = -1;
        int tcpPort = -1;
        int udpPort = -1;
        String infile = args[0];
        int workers = Integer.parseInt(args[1]);
        boolean scale2x = Boolean.parseBoolean(args[2]);
        ArrayList<ImageFilterFactory> factories = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            String filterName = args[i];
            if (!filterName.substring(0, 2).equals("--"))
                printUsageAndExit();
            filterName = filterName.substring(2);
            if (filterName.equals("help")) {
                printUsageAndExit();
            } else if (filterName.equals("ffplay")) {
                ffplay = true;
            } else if (filterName.equals("x264")) {
                x264 = true;
                x264crf = Integer.parseInt(args[i + 1]);
                i++;
            } else if (filterName.equals("nvenc")) {
                nvenc = true;
                nvencMaxrate = Integer.parseInt(args[i + 1]);
                i++;
            } else if (filterName.equals("tcp")) {
                tcp = true;
                tcpPort = Integer.parseInt(args[i + 1]);
                i++;
            } else if (filterName.equals("udp")) {
                udp = true;
                udpPort = Integer.parseInt(args[i + 1]);
                i++;
            } else if (filterName.equals("SampleShuffler")) {
                String filterArgs[] = args[i + 1].split(" ");
                int samples = Integer.parseInt(filterArgs[0]);
                boolean snap = Boolean.parseBoolean(filterArgs[1]);
                int maxSampleHeight = Integer.parseInt(filterArgs[2]);
                double glitchProbability = Double.parseDouble(filterArgs[3]);
                SampleShufflerFactory factory = new SampleShufflerFactory(samples, snap, maxSampleHeight, glitchProbability);
                factories.add(factory);
                i++;
            } else if (filterName.equals("PixelSorter")) {
                String filterArgs[] = args[i + 1].split(" ");
                boolean hsv = Boolean.parseBoolean(filterArgs[0]);
                PixelSorterFactory factory = new PixelSorterFactory(hsv);
                factories.add(factory);
                i++;
            } else if (filterName.equals("RGBHSV")) {
                factories.add(new HSVFactory());
            } else if (filterName.equals("BitShifter")) {
                String filterArgs[] = args[i + 1].split(" ");
                int shift = Integer.parseInt(filterArgs[0]);
                BitShifterFactory factory = new BitShifterFactory(shift, false);
                factories.add(factory);
                i++;
            } else if (filterName.equals("UnevenBitShifter")) {
                String filterArgs[] = args[i + 1].split(" ");
                int rshift = Integer.parseInt(filterArgs[0]);
                int gshift = Integer.parseInt(filterArgs[1]);
                int bshift = Integer.parseInt(filterArgs[2]);
                UnevenBitShifterFactory factory = new UnevenBitShifterFactory(rshift, gshift, bshift, false);
                factories.add(factory);
                i++;
            } else if (filterName.equals("ImageCipher")) {
                String filterArgs[] = args[i + 1].split(" ");
                boolean downsample = Boolean.parseBoolean(filterArgs[0]);
                factories.add(new VideoCipherFactory(downsample));
                i++;
            } else if (filterName.equals("ByteShifter")) {
                factories.add(new ByteShiftFactory());
            } else if (filterName.equals("OtherByteShifter")) {
                String filterArgs[] = args[i + 1].split(" ");
                int offsetPerFrame = Integer.parseInt(filterArgs[0]);
                factories.add(new OtherByteShiftFactory(offsetPerFrame));
                i++;
            } else if (filterName.equals("ReverseAdder")) {
                factories.add(new ReverseAdderFactory());
            } else if (filterName.equals("ChillerShuffler")) {
                String filterArgs[] = args[i + 1].split(" ");
                int samples = Integer.parseInt(filterArgs[0]);
                boolean snap = Boolean.parseBoolean(filterArgs[1]);
                int maxSampleHeight = Integer.parseInt(filterArgs[2]);
                double glitchProbability = Double.parseDouble(filterArgs[3]);
                int everyNthFrame = Integer.parseInt(filterArgs[4]);
                ChillerShufflerFactory factory = new ChillerShufflerFactory(samples, snap, maxSampleHeight, glitchProbability, everyNthFrame);
                factories.add(factory);
                i++;
            } else if (filterName.equals("PNGEncoder")) {
                factories.add(new PNGEncoderFactory());
            } else if (filterName.equals("IntReverse")) {
                factories.add(new IntReverseFactory());
            } else if (filterName.equals("QuadMirror")) {
                String filterArgs[] = args[i + 1].split(" ");
                boolean downsample = Boolean.parseBoolean(filterArgs[0]);
                factories.add(new QuadMirrorFactory(downsample));
                i++;
            } else if (filterName.equals("GhostDelay")) {
                String filterArgs[] = args[i + 1].split(" ");
                int numFrames = Integer.parseInt(filterArgs[0]);
                double alpha = Double.parseDouble(filterArgs[1]);
                factories.add(new GhostDelayFactory(numFrames, alpha, workers));
                i++;
            } else if (filterName.equals("BoxTest")) {
                factories.add(new BoxTestFactory(100, 100, 100, 100));
            } else if (filterName.equals("TriGrid")) {
                //factories.add(new TriGridFactory(8, 12, 25));
                //factories.add(new TriGridFactory(4, 6, 25));
                //factories.add(new TriGridFactory(2, 3, new Date().getTime()));
                String filterArgs[] = args[i + 1].split(" ");
                int rows = Integer.parseInt(filterArgs[0]);
                int cols = Integer.parseInt(filterArgs[1]);
                long seed = Long.parseLong(filterArgs[2]);
                if (seed == 0)
                    seed = new Date().getTime();
                System.err.println("TriGrid seed " + seed);
                int[] colorShares = new int[4];
                colorShares[0] =  Integer.parseInt(filterArgs[3]);
                colorShares[1] =  Integer.parseInt(filterArgs[4]);
                colorShares[2]  =  Integer.parseInt(filterArgs[5]);
                colorShares[3] =  Integer.parseInt(filterArgs[6]);
                double triangleProb = Double.parseDouble(filterArgs[7]);
                factories.add(new TriGridFactory(rows, cols, seed, colorShares, triangleProb));
                i++;
            } else {
                System.err.println("Just what filter do you think you're trying to use? " + filterName + "?");
                printUsageAndExit();
            }
        }
        if (factories.size() == 0) {
            System.err.println("No filters specified");
            printUsageAndExit();
        }
        if (!(ffplay || x264 || nvenc || tcp || udp)) {
            System.err.println("No output specified; use at least one of {ffplay, x264, nvenc, tcp, udp}");
            printUsageAndExit();
        }

        ImageFilterFactory factoriesArray[] = new ImageFilterFactory[factories.size()];
        for (int i = 0; i < factories.size(); i++) {
            factoriesArray[i] = factories.get(i);
        }
        String argString = String.join("_", Arrays.copyOfRange(args, 1, args.length));
        OutputProcessFactory outputProcessFactory = new OutputProcessFactory(infile, argString, ffplay, x264, nvenc, tcp, udp, x264crf, nvencMaxrate, tcpPort, udpPort);

        VideoProcessor processor = new VideoProcessor(infile, factoriesArray, workers, scale2x, outputProcessFactory);
        processor.start();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: $ java -jar VideoMultiProcessor.jar video-filepath threads-int scale2x-boolean [--ffplay] [--x264 crf] [--nvenc maxrate] --Filter \"filter args\" [--MoreFilters \"filter args\"]");
        System.err.println("Available filters:");
        System.err.println("--SampleShuffler \"samples-int snap-boolean max-sample-height-int glitch-probability-double\"");
        System.err.println("--BitShifter shift-int");
        System.err.println("--UnevenBitShifter rshift-int gshift-int bshift-int");
        System.err.println("--ImageCipher downsample-boolean");
        System.err.println("--OtherByteShifter offset-per-frame-int");
        System.err.println("--RGBHSV");
        System.err.println("--PixelSorter hsv-boolean");
        System.err.println("--ChillerShuffler \"samples-int snap-boolean max-sample-height-int glitch-probability-double everyNthFrame\"");
        System.err.println("--PNGEncoder");
        System.err.println("--ReverseAdder");
        System.err.println("--IntReverse");
        System.err.println("--QuadMirror downsample-boolean");
        System.err.println("--GhostDelay numFrames-int alpha-double");
        System.exit(1);
    }
}
