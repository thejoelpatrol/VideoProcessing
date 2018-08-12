import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageWorkerThread;
import com.laserscorpion.VideoProcessing.PPMFile;
import com.laserscorpion.VideoProcessing.Pixel;

import java.util.concurrent.Semaphore;

public class BitShifter extends ImageWorkerThread {
    int shift;
    boolean downsample;

    public BitShifter(Semaphore callWhenDone, PPMFile image, int shift, boolean downsample) {
        super(callWhenDone, image);
        this.shift = shift;
        this.downsample = downsample;
    }

    @Override
    public Image processImage() {
        Image image;
        if (downsample) {
            ColorDownsampler sampler = new ColorDownsampler(this.image);
            image = sampler.downsample(16);
        } else
            image = this.image;

        byte[] rgbResult = new byte[3 * image.width * image.height];
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int i = 3 * (x + y*image.width);
                Pixel pixel = image.pixels[y][x];
                rgbResult[i] = rotateRight(pixel.r, shift);
                rgbResult[i+1] =  rotateRight(pixel.g, shift);
                rgbResult[i+2] =  rotateRight(pixel.b, shift);
            }
        }
        return new Image(rgbResult, image.height, image.width);
    }

    private byte rotateRight(short orig, int digits) {
        short result = (short) ((orig >> digits) & 0x00FF);
        result |= (short)((orig << Byte.SIZE - digits) & 0x00FF);
        byte b = (byte)(0xFF & result);
        return b;
    }
}
