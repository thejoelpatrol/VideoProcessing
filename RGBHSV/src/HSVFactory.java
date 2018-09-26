import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.ImageFilterFactory;

public class HSVFactory implements ImageFilterFactory {
    public HSVFactory() {
    }

    @Override
    public ImageFilter create() {
        return new RGBHSV();
    }
}
