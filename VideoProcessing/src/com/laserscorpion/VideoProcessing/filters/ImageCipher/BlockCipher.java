package com.laserscorpion.VideoProcessing.filters.ImageCipher;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class BlockCipher {
    public enum cipherChoice {
        AES, DES
    }

    private Cipher cipher;
    private SecretKey key;

    public BlockCipher(cipherChoice choice, byte[] keymaterial) {
        try {
            if (choice == cipherChoice.AES) {
                if (keymaterial.length != 16)
                    throw new InvalidKeyException("AES keys are 16 bytes, silly!");
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                key = new SecretKeySpec(keymaterial, "AES");
            } else if (choice == cipherChoice.DES) {
                if (keymaterial.length != 8)
                    throw new InvalidKeyException("DES keys are 8 bytes, silly!");
                cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                key = new SecretKeySpec(keymaterial, "DES");
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public byte[] encrypt(byte[] input) {
        try {
            byte[] output = cipher.doFinal(input);
            return output;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
