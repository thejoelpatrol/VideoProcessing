import java.util.concurrent.Semaphore;

public class BitShifterFactory implements ImageWorkerFactory{
    private boolean downsample;
    private int shift;

    public BitShifterFactory(int shift, boolean downsample) {
        this.downsample = downsample;
        this.shift = shift;
    }

    @Override
    public ImageWorkerThread create(Semaphore lock, PPMFile image) {
        return new BitShifter(lock, image, shift, downsample);
    }
}
