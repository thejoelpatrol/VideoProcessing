package com.laserscorpion.VideoProcessing;

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

        int frame = 0;

        while (!done) {
            int i;
            for (i = 0; i < workers; i++) {
                PPMFile image = takeUninterruptibly();
                //System.err.println("input length " + image.data.length);
                frame++;
                //if (frame % 50 == 0)
                //    System.err.println("input queue: " + work.size());
                if (image.height == 0) {
                    done = true;
                    System.err.println("we better be done");
                    i--;
                    break;
                }
                if (workerThreads[i] == null) {
                    locks[i] = new Semaphore(1);
                    workerThreads[i] = factory.create(locks[i], image);
                    workerThreads[i].start();
                } else {
                    workerThreads[i].setImage(image);
                }
            }
            for (int j = 0; j < i; j++) {
                locks[j].acquireUninterruptibly();
                locks[j].release();
                //System.err.println("queuing image " + frames2++);
                putUninterruptibly(output, workerThreads[j].getfinishedImage());
            }
        }

        for (int i = 0; i < workers; i++) {
            workerThreads[i].finishRunning();
        }

        System.err.println("Telling the encoder we're done");
        consumer.done();
    }

    private PPMFile takeUninterruptibly() {
        while (true) {
            try {
                return work.take();
            } catch (InterruptedException e) {}
        }
    }
    private void putUninterruptibly(BlockingQueue queue, PPMFile image) {
        while (true) {
            try {
                queue.put(image);
                //System.err.println("output length " + image.data.length);
                return;
            } catch (InterruptedException e) {}
        }
    }


}
