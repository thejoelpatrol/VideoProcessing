import com.laserscorpion.VideoProcessing.Image;
import com.laserscorpion.VideoProcessing.ImageFilter;
import com.laserscorpion.VideoProcessing.Pixel;

import java.awt.Color;
import java.util.Arrays;

public class PixelSorter implements ImageFilter {
    private static final float CHANNEL_MAX = 255.0F;
    private boolean hsv;

    public PixelSorter(boolean hsv) {
        this.hsv = hsv;
    }

    @Override
    public Image processImage(Image image, int frameNo) {
        byte[] rgbResult = new byte[3 * image.width * image.height];

        for (int y = 0; y < image.height; y++) {
            Pixel pixelRow[] = image.pixels[y];
            int row[] = new int[pixelRow.length];
            for (int x = 0; x < pixelRow.length; x++) {
                Pixel pixel = pixelRow[x];
                if (hsv) {
                    float hsv[] = new float[3];
                    Color.RGBtoHSB(pixel.r, pixel.g, pixel.b, hsv);
                    int h = (int) (hsv[0] * CHANNEL_MAX) << 16;
                    int s = (int) (hsv[1] * CHANNEL_MAX) << 8;
                    int v = (int) (hsv[2] * CHANNEL_MAX);
                    row[x] = h | s | v;
                } else {
                    row[x] = (pixel.r << 16) | (pixel.g << 8) | pixel.b;
                }
            }
            Arrays.sort(row);
            for (int x = 0; x < pixelRow.length; x++) {
                if (hsv) {
                    float h = ((row[x] & 0xFF0000) >> 16) / CHANNEL_MAX;
                    float s = ((row[x] & 0x00FF00) >> 8) / CHANNEL_MAX;
                    float v = (row[x] & 0x0000FF) / CHANNEL_MAX;
                    row[x] = Color.HSBtoRGB(h, s, v);
                }
                int i = 3 * (x + y * image.width);
                rgbResult[i] = (byte) ((row[x] & 0xFF0000) >> 16);
                rgbResult[i + 1] = (byte) ((row[x] & 0x00FF00) >> 8);
                rgbResult[i + 2] = (byte) (row[x] & 0xFF);
            }
        }
        return new Image(rgbResult, image.height, image.width);
    }

}
