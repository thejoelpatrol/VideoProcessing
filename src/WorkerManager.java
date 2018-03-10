import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class
WorkerManager extends Thread {
    private BlockingQueue<Image> work;
    private BlockingQueue<BufferedImage> output;
    private int workers;
    private VideoEncoder consumer;

    public WorkerManager(BlockingQueue<Image> work, int workers, BlockingQueue<BufferedImage> output, VideoEncoder consumer) {
        this.work = work;
        this.output = output;
        this.workers = workers;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        boolean done = false;

        while (work.peek() == null) { /* basically spinlock, it won't be long */ }
        Image check = work.peek();

        //while (check.height != 0 && !done) {
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
                workerThreads[i] = new BitShifter(locks[i], image, 3); // TODO how to create different classes here later?
                workerThreads[i].start();
            }
            i--;
            for (int j = 0; j < i; j++) {

               // System.out.println("acquiring please .......... " + locks[i].availablePermits());
                locks[j].acquireUninterruptibly();
                //System.out.println("we acquired one of them " + locks[i].availablePermits());
                putUninterruptibly(output, workerThreads[j].getfinishedImage());
            }
            System.out.println("output queue size " + output.size());

            //check = work.peek();
            //while (check == null) {
                /* basically spinlock, it won't be long */
            //    check = work.peek();
            //}
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
    private void putUninterruptibly(BlockingQueue queue, BufferedImage image) {
        while (true) {
            try {
                queue.put(image);
                return;
            } catch (InterruptedException e) {}
        }
    }


}
