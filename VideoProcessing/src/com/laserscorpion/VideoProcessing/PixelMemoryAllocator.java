package com.laserscorpion.VideoProcessing;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PixelMemoryAllocator {
    private static final int MAX_POOL_SIZE = 30;
    /**
     * Extremely simplified pool of exact-sized byte arrays. Not a super smart heap implementation.
     * But managing these same-sized arrays ourselves instead of relying on the JVM
     * heap to constantly create and destroy them can save GC overhead. This only makes sense
     * to use in a case where we know we'd create excessive garbage otherwise.
     */
    private static PixelMemoryAllocator singleton;
    private Map<Integer, List<Pixel[]>> pool;

    private PixelMemoryAllocator() {
        pool = new HashMap<>();
    }

    public static PixelMemoryAllocator getInstance() {
        if (singleton == null) {
            singleton = new PixelMemoryAllocator();
        }
        return singleton;
    }

    public synchronized Pixel[] malloc (int size) {
        return new Pixel[size];

        /*List<Pixel[]> blocks = pool.get(size);
        if (blocks == null || blocks.size() == 0)
            return new Pixel[size];
        Pixel[] block = blocks.remove(0);
        return block;*/
    }

    public synchronized void free(Pixel[] block) {
        return;

        /*List<Pixel[]> blocks = pool.get(block.length);
        if (blocks == null) {
            blocks = new LinkedList<Pixel[]>();
            pool.put(block.length, blocks);
        }
        if (blocks.size() > MAX_POOL_SIZE)
            return;
        blocks.add(block);*/
    }

    public synchronized void flush() {
        pool = new HashMap<>();
    }
}
