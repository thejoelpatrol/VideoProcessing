import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class
WorkerManager extends Thread {
    private BlockingQueue<Image> work;
    private BlockingQueue<Image> output;
    private int workers;
    private PPMWriter consumer;

    public WorkerManager(BlockingQueue<Image> work, int workers, BlockingQueue<Image> output, PPMWriter consumer) {
        this.work = work;
        this.output = output;
        this.workers = workers;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        boolean done = false;

        while (work.peek() == null) { /* basically spinlock, it won't be long */ }

        while (!done) {
            ImageWorkerThread workerThreads[] = new ImageWorkerThread[workers];
            Semaphore locks[] = new Semaphore[workers];
            int i;
            for (i = 0; i < workers; i++) {
                Image image = takeUninterruptibly();
                if (image.height == 0) {
                    done = true;
                    System.out.println("we better be done");
                    break;
                }

                locks[i] = new Semaphore(1);
                workerThreads[i] = new BitShifter(locks[i], image, Main.intParam); // TODO how to create different classes here later?
                workerThreads[i].start();
            }
            i--;
            for (int j = 0; j < i; j++) {

                locks[j].acquireUninterruptibly();
                putUninterruptibly(output, workerThreads[j].getfinishedImage());
            }
        }

        System.out.println("Telling the encoder we're done");
        consumer.done();
    }

    private Image takeUninterruptibly() {
        while (true) {
            try {
                return work.take();
            } catch (InterruptedException e) {}
        }
    }
    private void putUninterruptibly(BlockingQueue queue, Image image) {
        while (true) {
            try {
                queue.put(image);
                return;
            } catch (InterruptedException e) {}
        }
    }


}
