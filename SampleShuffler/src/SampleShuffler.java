import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;
import java.util.Random;

public class SampleShuffler implements ImageFilter {
    //private static final long SEED = 25;
    private Random random;
    private int samples;
    private boolean snap;
    private int maxSampleHeight;
    private double glitchProbability;
    private Image image;

    public SampleShuffler(int samples, boolean snap, int maxSampleHeight, double glitchProbability) {
        //random = new Random(SEED);
        random = new Random();
        this.samples = samples;
        this.snap = snap;
        this.maxSampleHeight = maxSampleHeight;
        this.glitchProbability = glitchProbability;
    }

    @Override
    public Image processImage(Image image) {
        this.image = image;
        if (glitchFrame())
            return selectAndPlaceSamples();
        return image;
    }

    private boolean glitchFrame() {
        return random.nextDouble() <= glitchProbability;
    }

    private Image selectAndPlaceSamples() {
        Image result = new Image(image);
        int uniformHeight = 0;
        if (snap) uniformHeight = maxSampleHeight;

        for (int i = 1; i <= samples; i++) {
            int selectionHeight, selectionWidth;
            if (snap) {
                selectionHeight = uniformHeight;
                selectionWidth = selectionHeight;
            }
            else {
                selectionHeight = randomHeight();
                selectionWidth = randomWidth();
            }

            Image tempImage = selectSample(selectionWidth, selectionHeight);
            int new_x = randomXCoord(selectionWidth, snap);
            int new_y = randomYCoord(selectionHeight, snap);
            result.replaceSample(tempImage, new_x, new_y);
        }
        return result;
    }

    private int randomWidth() {
        int selectionWidth = random.nextInt(maxSampleHeight);
        if (selectionWidth > image.width)
            return image.width;
        return selectionWidth;
    }

    private int randomHeight() {
        int selection_height = random.nextInt(maxSampleHeight);
        if (selection_height > image.height)
            return image.height;
        return selection_height;
    }

    private Image selectSample(int selection_width, int selection_height) {
        Pixel[] pixels = new Pixel[selection_width * selection_height];

        int start_x = random.nextInt(image.width - selection_width);
        int start_y = random.nextInt(image.height - selection_height);
        int temp_image_loc = 0;

        for (int y = start_y; y < (start_y + selection_height); y++) {
            for (int x = start_x; x < (start_x + selection_width); x++) {
                //int loc = x + y * image.width;
                pixels[temp_image_loc] = image.pixels[y][x];
                temp_image_loc++;
            }
        }
        return new Image(pixels, selection_height, selection_width);
    }

    /**
     * Precondition: sampleWidth > 0
     * @param sampleWidth
     * @return
     */
    private int randomXCoord(int sampleWidth, boolean snap) {
        int x;
        if (snap) {
            x = random.nextInt(image.width);
            x -= x % sampleWidth;
        } else x = random.nextInt(image.width) - sampleWidth/2;
        if (x < 0)
            return 0;
        return x;
    }

    /**
     * Precondition: sampleHeight >= 0
     * @param sampleHeight
     * @return
     */
    private int randomYCoord(int sampleHeight, boolean snap) {
        int y;
        if (snap) {
            y = random.nextInt(image.height);
            y -= y % sampleHeight;
        } else
            y = random.nextInt(image.height) - sampleHeight/2;
        if (y < 0)
            return 0;
        return y;
    }

}
