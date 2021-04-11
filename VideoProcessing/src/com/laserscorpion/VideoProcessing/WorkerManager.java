package com.laserscorpion.VideoProcessing;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

class
WorkerManager extends Thread {
    private static final int DEFAULT_WORKERS = 3;
    private static final int FRAME_CACHE_SIZE = 20;
    private BlockingQueue<PPMFile> work;
    private BlockingQueue<PPMFile> output;
    private int workers;
    private PPMWriter consumer;
    private ImageWorkerThread workerThreads[];
    private Semaphore locks[];
    private FrameDelayService frameService;
    private ByteMemoryAllocator byteAllocator;
    private PixelMemoryAllocator pixelAllocator;
    private IntMemoryAllocator intAllocator;
    private int frameNo = 0;

    public WorkerManager(BlockingQueue<PPMFile> work, ImageFilterFactory[] factories,
                         BlockingQueue<PPMFile> output, Queue<PPMFile> scratchImages, PPMWriter consumer) {
        this(work, DEFAULT_WORKERS, factories, output, scratchImages, consumer);
    }

    public WorkerManager(BlockingQueue<PPMFile> work, int workers, ImageFilterFactory[] factories,
                         BlockingQueue<PPMFile> output, Queue<PPMFile> scratchImages, PPMWriter consumer) {
        this.work = work;
        this.output = output;
        this.workers = workers;
        this.consumer = consumer;

        workerThreads = new ImageWorkerThread[workers];
        locks = new Semaphore[workers];
        for (int i = 0; i < workers; i++) {
            locks[i] = new Semaphore(1);
            ImageFilter[] filters = new ImageFilter[factories.length];
            for (int j = 0; j < filters.length; j++) {
                filters[j] = factories[j].create();
            }
            workerThreads[i] = new ImageWorkerThread(filters, locks[i], scratchImages);
        }

        boolean saveFrameCache = false;
        for (ImageFilterFactory factory : factories) {
            Class cls = factory.getClass();
            try {
                Field field = cls.getField("REQUIRES_FRAMECACHE");
                saveFrameCache = true;
            } catch (NoSuchFieldException e) {}
        }
        if (saveFrameCache)
            this.frameService = FrameDelayService.getInstance();

        byteAllocator = ByteMemoryAllocator.getInstance();
        pixelAllocator = PixelMemoryAllocator.getInstance();
        intAllocator = IntMemoryAllocator.getInstance();

    }

    @Override
    public void run() {
        boolean done = false;

        while (work.peek() == null) { /* basically spinlock, it won't be long */ }

        for (int i = 0; i < workers; i++) {
            workerThreads[i].start();
        }

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
                workerThreads[i].setImage(image, frameNo);
                frameNo++;

                if (frameNo % 10 == 0) {
                    //byteAllocator.flush();
                    //pixelAllocator.flush();
                    //intAllocator.flush()
                }
            }
            for (int j = 0; j < i; j++) {
                locks[j].acquireUninterruptibly();
                locks[j].release();
                putUninterruptibly(output, workerThreads[j].getfinishedImage());

                int workerFrame = frameNo - i + j;
                if (frameService != null) {
                    frameService.save(workerFrame, workerThreads[j].getImage());
                    frameService.clear(workerFrame - FRAME_CACHE_SIZE);
                }

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
}
