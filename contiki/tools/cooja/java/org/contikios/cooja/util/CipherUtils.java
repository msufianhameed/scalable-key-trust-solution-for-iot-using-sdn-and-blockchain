package org.contikios.cooja.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CipherUtils {
	private static Cipher createCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("RSA/ECB/PKCS1Padding");
	}
	
	public static PublicKey getPublic(byte[] keyBytes) {
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] decrypt(byte[] msg, PublicKey key) {
		try {
			Cipher cipher = createCipher();
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(msg);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}