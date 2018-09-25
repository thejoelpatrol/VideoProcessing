package com.laserscorpion.VideoProcessing;

import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

class
WorkerManager extends Thread {
    private static final int DEFAULT_WORKERS = 3;
    private BlockingQueue<PPMFile> work;
    private BlockingQueue<PPMFile> output;
    private int workers;
    private PPMWriter consumer;
    private ImageWorkerFactory factory;

    public WorkerManager(BlockingQueue<PPMFile> work, ImageWorkerFactory factory, BlockingQueue<PPMFile> output, PPMWriter consumer) {
        this(work, DEFAULT_WORKERS, factory, output, consumer);
    }

    public WorkerManager(BlockingQueue<PPMFile> work, int workers, ImageWorkerFactory factory, BlockingQueue<PPMFile> output, PPMWriter consumer) {
        this.work = work;
        this.output = output;
        this.workers = workers;
        this.consumer = consumer;
        this.factory = factory;
    }

    @Override
    public void run() {
        boolean done = false;

        while (work.peek() == null) { /* basically spinlock, it won't be long */ }

        ImageWorkerThread workerThreads[] = new ImageWorkerThread[workers];
        Semaphore locks[] = new Semaphore[workers];

        while (!done) {
            int i;
            for (i = 0; i < workers; i++) {
                PPMFile image = takeUninterruptibly(work);
                if (image.height == 0) {
                    done = true;
                    System.err.println("we better be done");
                    i--;
                    break;
                }
                if (workerThreads[i] == null) {
                    locks[i] = new Semaphore(1);
                    workerThreads[i] = factory.create(locks[i]);
                    workerThreads[i].start();
                }
                workerThreads[i].setImage(image);
            }
            for (int j = 0; j < i; j++) {
                locks[j].acquireUninterruptibly();
                locks[j].release();
                putUninterruptibly(output, workerThreads[j].getfinishedImage());
            }
        }

        for (int i = 0; i < workers; i++) {
            workerThreads[i].finishRunning();
        }

        System.err.println("Telling the encoder we're done");
        consumer.done();
    }

    private PPMFile takeUninterruptibly(BlockingQueue<PPMFile> queue) {
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException e) {}
        }
    }
    private void putUninterruptibly(BlockingQueue queue, PPMFile image) {
        while (true) {
            try {
                queue.put(image);
                return;
            } catch (InterruptedException e) {}
        }
    }

    private class StageManager extends Thread {
        private BlockingQueue<PPMFile> in;
        private BlockingQueue<PPMFile> out;

        public StageManager(BlockingQueue<PPMFile> in, BlockingQueue<PPMFile> out) {

        }

        public void run() {

        }
    }
}
