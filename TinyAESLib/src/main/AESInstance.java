package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

public class AESInstance {
	private SecretKey SecKey;
	private Cipher AesCipher;
	
	/**
	 * try to destroy the secret key
	 */
	void destroyKey() throws DestroyFailedException {
		SecKey.destroy();
	}
	/**
	 * in case the key destruction fails, nullify the key object
	 */
	void nullifyKey() {
		SecKey = null;
	}
	/**
	 * return the encoded version of the key
	 */
	String returnKey() {
		return SecKey.getEncoded().toString();
	}
	/**
	 * check if file is a directory
	 */
	Boolean isDir(File file) {
		return file.isDirectory();
	}
	/**
	 * resolve path to file
	 */
	File fileFromPath(String path) throws Exception {
		return new File(path);
	}	
	/**
	 * delete file given a path
	 */
	void deleteFile(String a) throws IOException {
		Files.delete(Paths.get(a));
	}
	void deleteFile(File a) throws IOException {
		Files.delete(Paths.get(a.getAbsolutePath()));
	}
	/**
	 * write key to file in root dir
	 */
	void exportKey() throws FileNotFoundException, IOException {
		ObjectOutputStream publicKeyOS = new ObjectOutputStream(
				new FileOutputStream("AESKey.txt"));
		publicKeyOS.writeObject(SecKey);	
		publicKeyOS.close();
	}
	/**
	 * try generating a key
	 */
	void generateKey(int keySize) throws Exception{
		if(keySize != 128 && keySize != 192 && keySize != 256) {
			throw new Exception("Illegal Key Size");
		}else{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(keySize);
			SecKey = keyGen.generateKey();
			AesCipher = Cipher.getInstance("AES");
		}
	}
	/**
	 * read in secret key from path
	 */
	void loadKeyFromFile(String filepath) throws ClassNotFoundException, IOException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filepath));
		SecKey = (SecretKeySpec) inputStream.readObject();
	    inputStream.close();	
	}
	/**
	 * write random bytes 
	 */
	void writeRandom(File file) throws Exception {
		FileOutputStream out = new FileOutputStream(file);
		InputStream in = new FileInputStream(file);		
		SecureRandom secRand = SecureRandom.getInstanceStrong();
		byte[] buffer = new byte[8192];
		secRand.nextBytes(buffer);
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	void writeRandom(String filepath) throws Exception {
		FileOutputStream out = new FileOutputStream(filepath);
		InputStream in = new FileInputStream(filepath);
		SecureRandom secRand = SecureRandom.getInstanceStrong();
		byte[] buffer = new byte[8192];
		secRand.nextBytes(buffer);
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	/**
	 * zero a file passed to it
	 */
	void zeroPass(File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		InputStream in = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	void zeroPass(String filepath) throws IOException {
		FileOutputStream out = new FileOutputStream(filepath);
		InputStream in = new FileInputStream(filepath);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	/**
	 * encrypt file
	 */
	void encrypt(File file) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		AesCipher.init(Cipher.ENCRYPT_MODE, SecKey);
		CipherOutputStream out = new CipherOutputStream(new FileOutputStream(file), AesCipher);
		InputStream in = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	void encrypt(String filepath) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		AesCipher.init(Cipher.ENCRYPT_MODE, SecKey);
		CipherOutputStream out = new CipherOutputStream(new FileOutputStream(filepath), AesCipher);
		InputStream in = new FileInputStream(filepath);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	/**
	 * decrypt file
	 */
	void decrypt(File file) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		AesCipher.init(Cipher.DECRYPT_MODE, SecKey);
		CipherOutputStream out = new CipherOutputStream(new FileOutputStream(file), AesCipher);
		InputStream in = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
	void decrypt(String filepath) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		AesCipher.init(Cipher.DECRYPT_MODE, SecKey);
		CipherOutputStream out = new CipherOutputStream(new FileOutputStream(filepath), AesCipher);
		InputStream in = new FileInputStream(filepath);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = in.read(buffer)) > 0)
		{
		    out.write(buffer, 0, count);
		}
		out.close();
		in.close();
	}
}
