package com.laserscorpion.VideoProcessing;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IntMemoryAllocator {
    /**
     * Extremely simplified pool of exact-sized byte arrays. Not a super smart heap implementation.
     * But managing these same-sized arrays ourselves instead of relying on the JVM
     * heap to constantly create and destroy them can save GC overhead. This only makes sense
     * to use in a case where we know we'd create excessive garbage otherwise.
     */
    private static IntMemoryAllocator singleton;
    private Map<Integer, List<int[]>> pool;

    private IntMemoryAllocator() {
        pool = new HashMap<>();
    }

    public static IntMemoryAllocator getInstance() {
        if (singleton == null) {
            singleton = new IntMemoryAllocator();
        }
        return singleton;
    }

    public synchronized int[] malloc (int size) {
        List<int[]> blocks = pool.get(size);
        if (blocks == null || blocks.size() == 0)
            return new int[size];
        int[] block = blocks.remove(0);
        return block;
    }

    public synchronized void free(int[] block) {
        List<int[]> blocks = pool.get(block.length);
        if (blocks == null)
            blocks = new LinkedList<int[]>();
        blocks.add(block);
        pool.put(block.length, blocks);
    }
}
