import java.util.concurrent.Semaphore;

public class BitShifterFactory implements ImageWorkerFactory{

    @Override
    public ImageWorkerThread create(Semaphore lock, PPMFile image, int param) {
        return new BitShifter(lock, image, param);
    }
}
