package com.laserscorpion.VideoProcessing;

import java.util.HashMap;

public class FrameDelayService {
    private HashMap<Integer, Image> cache;
    private static FrameDelayService singleton;
    private ByteMemoryAllocator allocator;

    public FrameDelayService() {
        cache = new HashMap<>();
        allocator = ByteMemoryAllocator.getInstance();
    }

    public static FrameDelayService getInstance() {
        if (singleton == null)
            singleton = new FrameDelayService();
        return singleton;
    }

    public void save(int frameNo, Image image) {
        cache.put(frameNo, image);
    }

    public Image get(int frameNo) {
        return cache.get(frameNo);
    }

    public void clear(int frameNo) {
        if (cache.containsKey(frameNo)) {
            //Image frame = cache.get(frameNo);
            //frame.dispose();
            cache.remove(frameNo);
        }

    }
}
