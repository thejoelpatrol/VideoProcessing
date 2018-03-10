import java.lang.management.BufferPoolMXBean;
import java.util.Arrays;
import java.awt.image.BufferedImage;

public class Image {
    public Pixel[][] pixels;
    public int height;
    public int width;

    public Image(byte[] rgbVals, int height, int width) {
        this.height = height;
        this.width = width;
        pixels = new Pixel[height][];
        for (int i = 0; i < height; i++) {
            pixels[i] = new Pixel[width];
            for (int j = 0; j < width; j++) {
                int k = 3*(j + i*width);
                Pixel p = new Pixel();
                p.r = promote(rgbVals[k]);
                p.g = promote(rgbVals[k+1]);
                p.b = promote(rgbVals[k+2]);
                pixels[i][j] = p;
            }
        }
    }

    public BufferedImage toBufferedImage() {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        byte[] rgbVals = new byte[3 * width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = 0;
                color |= ((pixels[i][j].r << 16) & 0x00FF0000);
                color |= ((pixels[i][j].g << 8) & 0x0000FF00);
                color |= ((pixels[i][j].b) & 0x000000FF);
                result.setRGB(j, i, color);
            }
        }
        return result;
    }

    private short promote(byte b) {
        return (short)((short)0x00FF & (short)b);
    }

    public class Pixel {
        short r;
        short g;
        short b;
    }
}
