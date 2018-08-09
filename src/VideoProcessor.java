import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoProcessor {
    protected int QUEUE_SIZE = 100;

    public void start(ImageWorkerFactory factory, int workers) {
        BlockingQueue<PPMFile> images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        BlockingQueue<PPMFile> outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        PPMReader reader = new PPMReader(System.in, images, new Object() );
        PPMWriter encoder = new PPMWriter(outputImages, System.out);

        WorkerManager manager = new WorkerManager(images, workers, factory, outputImages, encoder);

        reader.start();
        manager.start();
        encoder.start();
    }

}
