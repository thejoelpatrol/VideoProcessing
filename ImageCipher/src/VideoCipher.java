import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageWorkerThread;
import com.laserscorpion.VideoProcessing.PPMFile;

import java.util.concurrent.Semaphore;


public class VideoCipher extends ImageWorkerThread {
    private static final int COLORS = 16;

    boolean downsample;

    public VideoCipher(Semaphore outputReady, PPMFile processThisPlease, boolean downsample) {
        super(outputReady, processThisPlease);
        this.downsample = downsample;
    }

    @Override
    public Image processImage() {
        ColorDownsampler sampler = new ColorDownsampler(image);
        Image downsampled = sampler.downsample(COLORS);
        ImageCipher cipher = new ImageCipher();
        return cipher.encryptImageData(downsampled);
    }
}
