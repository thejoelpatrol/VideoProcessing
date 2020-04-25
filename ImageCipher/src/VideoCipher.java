import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;


public class VideoCipher implements ImageFilter {
    private static final int COLORS = 16;

    boolean downsample;

    public VideoCipher(boolean downsample) {
        this.downsample = downsample;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        Image toProcess = image;
        if (downsample) {
            ColorDownsampler sampler = new ColorDownsampler(image);
            toProcess = sampler.downsample(COLORS);
        }
        ImageCipher cipher = new ImageCipher();
        return cipher.encryptImageData(toProcess);
    }
}
