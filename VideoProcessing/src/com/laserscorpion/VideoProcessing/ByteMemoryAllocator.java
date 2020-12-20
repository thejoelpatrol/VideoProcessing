package com.laserscorpion.VideoProcessing;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ByteMemoryAllocator {
    /**
     * Extremely simplified pool of exact-sized byte arrays. Not a super smart heap implementation.
     * But managing these same-sized arrays ourselves instead of relying on the JVM
     * heap to constantly create and destroy them can save GC overhead. This only makes sense
     * to use in a case where we know we'd create excessive garbage otherwise.
     */
    private static ByteMemoryAllocator singleton;
    private Map<Integer, List<byte[]>> pool;

    private ByteMemoryAllocator() {
        pool = new HashMap<>();
    }

    public static ByteMemoryAllocator getInstance() {
        if (singleton == null) {
            singleton = new ByteMemoryAllocator();
        }
        return singleton;
    }

    public synchronized byte[] malloc (int size) {
        List<byte[]> blocks = pool.get(size);
        if (blocks == null || blocks.size() == 0)
            return new byte[size];
        byte[] block = blocks.remove(0);
        return block;
    }

    public synchronized void free(byte[] block) {
        List<byte[]> blocks = pool.get(block.length);
        if (blocks == null)
            blocks = new LinkedList<byte[]>();
        blocks.add(block);
        pool.put(block.length, blocks);
    }
}
