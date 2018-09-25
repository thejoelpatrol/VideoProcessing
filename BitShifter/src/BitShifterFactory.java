import com.laserscorpion.VideoProcessing.ImageWorkerFactory;
import com.laserscorpion.VideoProcessing.ImageWorkerThread;
import com.laserscorpion.VideoProcessing.PPMFile;

import java.util.concurrent.Semaphore;

public class BitShifterFactory implements ImageWorkerFactory {
    private boolean downsample;
    private int shift;

    public BitShifterFactory(int shift, boolean downsample) {
        this.downsample = downsample;
        this.shift = shift;
    }

    @Override
    public ImageWorkerThread create(Semaphore lock) {
        return new BitShifter(lock, shift, downsample);
    }
}
