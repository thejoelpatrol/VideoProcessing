import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;


public class SampleShufflerFactory implements ImageFilterFactory {
    private int samples;
    private boolean snap;
    private int maxSampleHeight;
    private double glitchProbability;

    public SampleShufflerFactory(int samples, boolean snap, int maxSampleHeight, double glitchProbability) {
        this.samples = samples;
        this.snap = snap;
        this.maxSampleHeight = maxSampleHeight;
        this.glitchProbability = glitchProbability;
    }

    @Override
    public ImageFilter create() {
        return new SampleShuffler(samples, snap, maxSampleHeight, glitchProbability);
    }
}
