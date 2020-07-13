package com.laserscorpion.VideoProcessing.filters.PNGEncoder;

import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.filters.PNGEncoder.hjgawt.PngReaderBI;
import com.laserscorpion.VideoProcessing.filters.PNGEncoder.hjgawt.PngWriterBI;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.IImageLineSet;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;

public class PNGEncoder implements ImageFilter {
    private static final float CHANNEL_MAX = 255.0F;

    public PNGEncoder() {

    }

    @Override
    public Image processImage(Image image, int frameNo) {
        ColorDownsampler ds = new ColorDownsampler(image);
        Image downsampled = ds.downsample(32);
        //Image downsampled = image;

        try {
            File tmpPng = encodePNG(downsampled);
            String[] args = new String[4];
            args[0] = "python3";
            args[1] = "glitch_in_place.py";
            args[2] = tmpPng.getAbsolutePath();
            args[3] = "100";
            ProcessBuilder pythonGlitchBuilder = new ProcessBuilder(args);
            Process pythonGlitch = pythonGlitchBuilder.start();
            waitForPython(pythonGlitch);
            PngReaderBI pngr = new PngReaderBI(tmpPng);
            BufferedImage bi = pngr.readAll();
            tmpPng.delete();
            return new Image(bi);
        } catch (IOException e) {
            e.printStackTrace();
            return image;
        }
    }

    private void waitForPython(Process pythonGlitch) {
        while (true) {
            try {
                pythonGlitch.waitFor();
                return;
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    private File encodePNG(Image image) throws IOException {
        BufferedImage bi = image.toBufferedImage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PngWriterBI pngw = PngWriterBI.createInstance(bi, os);
        pngw.writeAll();
        pngw.end();
        byte[] initialPngBytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(initialPngBytes);
        PngReader pngr = new PngReader(is);
        File tempPng = File.createTempFile("tmppng-", ".png");
        PngWriter pngw2 = new PngWriter(tempPng, pngr.imgInfo);
        pngw2.copyChunksFrom(pngr.getChunksList());
        IImageLineSet<? extends IImageLine> lines = pngr.readRows();
        for (int row = 0; row < pngr.imgInfo.rows; row++) {
            /*if (row % 10 < 6)
                pngw2.setFilterType(FilterType.FILTER_PAETH);
            else
                pngw2.setFilterType(FilterType.getByVal(row % 3 + 2));*/
            pngw2.setFilterType(FilterType.FILTER_PAETH);
            pngw2.writeRow(lines.getImageLine(row));
        }
        pngr.end();
        //long crc0 = PngHelperInternal.getDigest(pngr);
        pngw2.end();

        return tempPng;
    }

}
