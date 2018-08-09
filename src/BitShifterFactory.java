import java.util.concurrent.Semaphore;

public class BitShifterFactory implements ImageWorkerFactory{
    private boolean downsample;

    public BitShifterFactory(boolean downsample) {
        this.downsample = downsample;
    }

    @Override
    public ImageWorkerThread create(Semaphore lock, PPMFile image, int param) {
        return new BitShifter(lock, image, param, downsample);
    }
}
