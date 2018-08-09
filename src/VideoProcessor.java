import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoProcessor {
    protected int QUEUE_SIZE = 100;

    private PPMReader reader;
    private PPMWriter encoder;
    private WorkerManager manager;

    public VideoProcessor(ImageWorkerFactory factory, int workers) {
        BlockingQueue<PPMFile> images = new LinkedBlockingQueue<>(QUEUE_SIZE);
        BlockingQueue<PPMFile> outputImages = new LinkedBlockingQueue<>(QUEUE_SIZE);
        reader = new PPMReader(System.in, images);
        encoder = new PPMWriter(outputImages, System.out);
        manager = new WorkerManager(images, workers, factory, outputImages, encoder);
    }

    public void start() {
        reader.start();
        manager.start();
        encoder.start();
    }

}
