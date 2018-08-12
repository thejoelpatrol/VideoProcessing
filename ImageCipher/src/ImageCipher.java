import com.laserscorpion.VideoProcessing.ColorDownsampler;
import com.laserscorpion.VideoProcessing.Image;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCipher {
    /*private byte DESKey[] = {(byte)0x35, (byte)0x25, (byte)0x25, (byte)0x25, (byte)0x25, (byte)0x25, (byte)0x25, (byte)0x25};
    private byte DESKey[] = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};*/
    private byte DEFAULT_DESKey[] = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};

    private byte DEFAULT_AESKey[] = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};

    BlockCipher cipher;
    private byte DESKey[];
    private byte AESKey[];

    public ImageCipher() {
        DESKey = DEFAULT_DESKey;
        AESKey = DEFAULT_AESKey;
        cipher = new BlockCipher(BlockCipher.cipherChoice.DES, DESKey);
    }

    public ImageCipher(byte[] DESKey) {
        this.DESKey = DESKey;
        cipher = new BlockCipher(BlockCipher.cipherChoice.DES, this.DESKey);
    }

    public void encryptImageFile(String filename) throws IOException {
        File imageFile = new File(filename);
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            System.err.println("Couldn't read image file: " + filename);
            System.exit(1);
        }

        Image fromBufferedImage = new Image(image);
        Image encryptedImage = encryptImageData(fromBufferedImage);
        /*
        File outputFile = new File(filename + "_encrypted.png");
        ImageIO.write(encryptedImage, "PNG", outputFile);*/
    }

    public Image encryptImageData(Image clearText) {
        /*ColorDownsampler sampler = new ColorDownsampler(clearText);
        Image downsampled = sampler.downsample(COLORS);*/
        BufferedImage buffered = clearText.toBufferedImage();
        int[] cleartextRGB = buffered.getRGB(0, 0, clearText.width, clearText.height, null, 0, buffered.getWidth());
        byte[] cleartextBytes = convertToBytes(cleartextRGB);
        byte[] encryptedBytes = encryptBytes(cleartextBytes);

        /*int[] encryptedPixels = convertToInts(encryptedBytes);
        BufferedImage encryptedImage = new BufferedImage(clearText.width, clearText.height, BufferedImage.TYPE_INT_RGB);
        encryptedImage.setRGB(0, 0, encryptedImage.getWidth(), encryptedImage.getHeight(), encryptedPixels, 0, encryptedImage.getWidth());*/

        Image encryptedImage = new Image(encryptedBytes, clearText.height, clearText.width);
        return encryptedImage;
    }

    private byte[] convertToBytes(int[] ints) {
        byte[] bytes = new byte[ints.length * 3];
        for (int i = 0; i < ints.length; i++) {
            int pixel = ints[i];
            byte b = (byte)(pixel & 0xFF);
            byte g = (byte)((pixel >>> 8) & 0xFF);
            byte r = (byte)((pixel >>> 16) & 0xFF);
            int index = i * 3;
            bytes[index] = r;
            bytes[index + 1] = g;
            bytes[index + 2] = b;
        }
        return bytes;
    }

    private int[] convertToInts(byte[] bytes) {
        int len = bytes.length / 3 + (bytes.length % 3 == 0 ? 0 : 1);
        int[] ints = new int[len];
        for (int i = 0; i < len; i++) {
            int index = i * 3;
            int pixel = 0xFF000000;
            pixel |= ((int)bytes[index]) & 0x000000FF;
            try {
                pixel |= ((int)bytes[index + 1] << 8) & 0x0000FF00;
                pixel |= ((int)bytes[index + 2] << 16) & 0x00FF0000;
            } catch (IndexOutOfBoundsException e) {
                if (i != len-1) // padding means that bytes.length may not be divisible be 3, so going out of bounds on the last pixel is ok
                    throw e;
            }
            ints[i]= pixel;
        }
        return ints;
    }

    private byte[] encryptBytes(byte[] clearText) {
        //BlockCipher cipher = new BlockCipher(BlockCipher.cipherChoice.DES, DESKey);
        //BlockCipher cipher = new BlockCipher(BlockCipher.cipherChoice.AES, AESKey);
        byte[] encryptedBytes = cipher.encrypt(clearText);
        return encryptedBytes;
    }

}
