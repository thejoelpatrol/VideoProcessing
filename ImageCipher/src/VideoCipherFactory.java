import com.laserscorpion.VideoProcessing.ImageWorkerFactory;
import com.laserscorpion.VideoProcessing.ImageWorkerThread;
import com.laserscorpion.VideoProcessing.PPMFile;

import java.util.concurrent.Semaphore;

public class VideoCipherFactory implements ImageWorkerFactory {
    private boolean downsample;

    public VideoCipherFactory(boolean downsample) {
        this.downsample = downsample;
    }

    @Override
    public ImageWorkerThread create(Semaphore lock, PPMFile image) {
        return new VideoCipher(lock, image, downsample);
    }
}
