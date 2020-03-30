package cn.dmlab.crypto;

import cn.dmlab.utils.ByteUtil;
import cn.dmlab.utils.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

public class CipherUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * encrypt data use DES algorithm.
     *
     * @param content  need to be encrypted data
     * @param password password
     * @return encrypted data
     */
    @Deprecated
    public static byte[] encryptDES(byte[] content, String password) {
        if (password.length() > 16) {
            throw new RuntimeException();
        }
        if (password.length() % 8 != 0) {
            StringBuilder sb = new StringBuilder(password);
            for (int i = 0; i < (8 - (password.length() % 8)); i++) {
                sb.append("0");
            }
            password = sb.toString();
        }
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes(Utils.DEFAULT_CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            return cipher.doFinal(content);
        } catch (Throwable exception) {
            return null;
        }
    }

    /**
     * decrypt data by DES algorithm.
     *
     * @param content  origin data
     * @param password password
     * @return result bytes
     */
    @Deprecated
    public static byte[] decryptDES(byte[] content, String password) {
        if (password.length() > 16) {
            throw new RuntimeException();
        }
        if (password.length() % 8 != 0) {
            StringBuilder sb = new StringBuilder(password);
            for (int i = 0; i < (8 - (password.length() % 8)); i++) {
                sb.append("0");
            }
            password = sb.toString();
        }
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes(Utils.DEFAULT_CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            return cipher.doFinal(content);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * encrypt data by AES algorithm.
     *
     * @param content  need to be encrypted data
     * @param password password 128/192/256 bits | 16/24/32 bytes
     * @return result bytes
     */
    public static byte[] encryptAES(byte[] content, String password) {
        byte[] keyBytes = getPassword(password, 32);
        Key key = new SecretKeySpec(keyBytes, "AES");
        try {
            Cipher in = Cipher.getInstance("AES/CBC/PKCS5Padding");
            in.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(getIV(keyBytes, 16)));
            return in.doFinal(content);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * decrypt data by AES algorithm.
     *
     * @param content  origin data
     * @param password password 128/192/256 bits | 16/24/32 bytes
     * @return result bytes
     */
    public static byte[] decryptAES(byte[] content, String password) {
        byte[] keyBytes = getPassword(password, 32);
        Key key = new SecretKeySpec(keyBytes, "AES");
        try {
            Cipher out = Cipher.getInstance("AES/CBC/PKCS5Padding");
            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(getIV(keyBytes, 16)));
            return out.doFinal(content);
        } catch (Exception exception) {
            return ByteUtil.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * encrypt data by 3DES algorithm.
     *
     * @param content  need to be encrypted data
     * @param password password 24 bytes
     * @return result bytes
     */
    public static byte[] encrypt3DES(byte[] content, String password) {
        byte[] keyBytes = getPassword(password, 24);
        Key key = new SecretKeySpec(keyBytes, "DESede");
        try {
            Cipher in = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            in.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(getIV(keyBytes, 8)));
            return in.doFinal(content);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * decrypted data by 3DES algorithm.
     *
     * @param content  origin data
     * @param password password 24 bytes
     * @return result bytes
     */
    public static byte[] decrypt3DES(byte[] content, String password) {
        byte[] keyBytes = getPassword(password, 24);
        Key key = new SecretKeySpec(keyBytes, "DESede");
        try {
            Cipher out = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(getIV(keyBytes, 8)));
            return out.doFinal(content);
        } catch (Exception exception) {
            return ByteUtil.EMPTY_BYTE_ARRAY;
        }
    }

    private static byte[] getIV(byte[] bytes, int length) {
        byte[] result = new byte[length];
        System.arraycopy(bytes, 0, result, 0, length);
        return result;
    }

    private static byte[] getPassword(String password, int len) {
        int length = password.length();
        if (length < len) {
            password = append(password, len);
        } else if (length > len) {
            password = password.substring(0, len);
        }
        return password.getBytes(Utils.DEFAULT_CHARSET);
    }

    private static String append(String origin, int size) {
        StringBuilder sb = new StringBuilder(origin);
        for (int i = origin.length(); i < size; i++) {
            sb.append("@");
        }
        return sb.toString();
    }
}
