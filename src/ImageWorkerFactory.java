import java.util.concurrent.Semaphore;

public interface ImageWorkerFactory {
    ImageWorkerThread create(Semaphore lock, PPMFile image);
}
