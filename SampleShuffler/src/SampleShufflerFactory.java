import com.laserscorpion.VideoProcessing.ImageWorkerFactory;
import com.laserscorpion.VideoProcessing.ImageWorkerThread;
import com.laserscorpion.VideoProcessing.PPMFile;

import java.util.concurrent.Semaphore;


public class SampleShufflerFactory implements ImageWorkerFactory {
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
    public ImageWorkerThread create(Semaphore lock) {
        return new SampleShuffler(lock, samples, snap, maxSampleHeight, glitchProbability);
    }
}
