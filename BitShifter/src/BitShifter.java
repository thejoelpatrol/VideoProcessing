import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

public class BitShifter implements ImageFilter{
    int shift;
    boolean downsample;

    public BitShifter(int shift, boolean downsample) {
        this.shift = shift;
        this.downsample = downsample;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        Image toProcess;
        if (downsample) {
            ColorDownsampler sampler = new ColorDownsampler(image);
            toProcess = sampler.downsample(16);
        } else
            toProcess = image;

        byte[] rgbResult = new byte[3 * toProcess.width * toProcess.height];
        for (int y = 0; y < toProcess.height; y++) {
            for (int x = 0; x < toProcess.width; x++) {
                int i = 3 * (x + y*toProcess.width);
                Pixel pixel = toProcess.pixels[y][x];
                rgbResult[i] = rotateRight(pixel.r, shift);
                rgbResult[i+1] =  rotateRight(pixel.g, shift);
                rgbResult[i+2] =  rotateRight(pixel.b, shift);
            }
        }
        return new Image(rgbResult, toProcess.height, toProcess.width);
    }

    private byte rotateRight(short orig, int digits) {
        short result = (short) ((orig >> digits) & 0x00FF);
        result |= (short)((orig << Byte.SIZE - digits) & 0x00FF);
        byte b = (byte)(0xFF & result);
        return b;
    }
}
