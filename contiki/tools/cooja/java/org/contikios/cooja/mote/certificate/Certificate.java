package org.contikios.cooja.mote.certificate;

import java.io.File;
import java.security.*;
import org.contikios.cooja.util.ArrayUtils;

public class Certificate {
	private PrivateKey privateKey512 = null;
	private PublicKey publicKey512 = null;
	private PrivateKey privateKey2048 = null;
	private PublicKey publicKey2048 = null;
	
	public Certificate(int moteID) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen512 = KeyPairGenerator.getInstance("RSA");
		keyGen512.initialize(512);
		
		KeyPair keyPair512 = keyGen512.generateKeyPair();
		privateKey512 = keyPair512.getPrivate();
        publicKey512 = keyPair512.getPublic();
        
        KeyPairGenerator keyGen2048 = KeyPairGenerator.getInstance("RSA");
		keyGen512.initialize(2048);
        
        KeyPair keyPair2048 = keyGen2048.generateKeyPair();
		privateKey2048 = keyPair2048.getPrivate();
        publicKey2048 = keyPair2048.getPublic();
        
        File dir = new File("keys");
        if (!dir.exists()) {
          dir.mkdirs();
        }
        
        File keyFile512 = new File("keys/" + moteID + "-512.key");
        File pubFile512 = new File("keys/" + moteID + "-512.pub");
        File keyFile2048 = new File("keys/" + moteID + "-2048.key");
        File pubFile2048 = new File("keys/" + moteID + "-2048.pub");
        ArrayUtils.writeToFile(keyFile512, privateKey512.getEncoded());
        ArrayUtils.writeToFile(pubFile512, publicKey512.getEncoded());
        ArrayUtils.writeToFile(keyFile2048, privateKey2048.getEncoded());
        ArrayUtils.writeToFile(pubFile2048, publicKey2048.getEncoded());
	}
	
	public PrivateKey getPrivateKey512() {
		return privateKey512;
	}
	
	public PublicKey getPublicKey512() {
		return publicKey512;
	}
	
	public PrivateKey getPrivateKey2048() {
		return privateKey2048;
	}
	
	public PublicKey getPublicKey2048() {
		return publicKey2048;
	}
}
