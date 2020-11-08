package com.github.sdnwiselab.sdnwise.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CipherUtils {
	private static Cipher createEncryptCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("RSA/ECB/PKCS1Padding");
	}
	
	private static Cipher createDecryptCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("RSA/ECB/PKCS1Padding");
	}
	
	private static MessageDigest createMessageDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-1");
	}
	
	public static PrivateKey getPrivateKey(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
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
	
	public static String encrypt(String msg, PrivateKey key) throws
			InvalidKeyException,
			IllegalBlockSizeException,
			BadPaddingException,
			UnsupportedEncodingException,
			NoSuchAlgorithmException,
			NoSuchPaddingException {
		Cipher cipher = createEncryptCipher();
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
	}
	
	public static byte[] decrypt(byte[] msg, PublicKey key) {
		try {
			Cipher cipher = createDecryptCipher();
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(msg);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String hash(String input) {
		try {
			MessageDigest messageDigest = createMessageDigest();
			byte[] hashBytes = messageDigest.digest(input.getBytes(Charset.forName("UTF-8")));
			StringBuffer hashStringBuffer = new StringBuffer();
			
			for (int i = 0; i < hashBytes.length; i++) {
			    String hex = Integer.toHexString(0xff & hashBytes[i]);
			    
			    if (hex.length() == 1) {
			    	hashStringBuffer.append('0');
			    }
		        
			    hashStringBuffer.append(hex);
			}
			
			return hashStringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}