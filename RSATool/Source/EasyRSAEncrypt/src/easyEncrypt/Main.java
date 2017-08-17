/**
 * 
 */
package easyEncrypt;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main {

  public static final String ALGORITHM = "RSA";
  public static PrivateKey privKey = null;
  public static PublicKey pubKey = null;
  static Cipher AesCipher;
  static SecretKey SecKey = null;

  public static void generateKey() {
    try {
      Thread t1 = new Thread();
      t1.start(); 

      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(4096);
      KeyPair key = keyGen.generateKeyPair();
      pubKey = key.getPublic();
      privKey = key.getPrivate();
           
      KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
      KeyGen.init(128);
      SecKey = KeyGen.generateKey();
      AesCipher = Cipher.getInstance("AES");
      
      Thread.currentThread().interrupt();
    } catch (Exception e) {
    	e.printStackTrace();
      Interface.append("Error making keys");
    }
  }
public static void encrypt(byte[] text, String filepathtoencrypt) {
	    try{   	    	
	    	Thread t1 = new Thread();
	        t1.start(); 
	        
	        Interface.append("Encryption started");  
	        
	        AesCipher.init(Cipher.ENCRYPT_MODE, SecKey);
	        byte[] byteCipherText = AesCipher.doFinal(text);
	        Files.write(Paths.get(filepathtoencrypt), byteCipherText);	        	        
	        
	        Interface.append("Encryption finished!");	        
	        
	        
	        Cipher cipher = Cipher.getInstance(ALGORITHM);
	        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
	        byte[] rsakey = cipher.doFinal(SecKey.getEncoded());
	        
	        FileOutputStream fos = new FileOutputStream("DoubleENCkey.txt");
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(rsakey);	
			oos.close();
			
			FileOutputStream fos1 = new FileOutputStream("RSAkeyforDoubleENC.txt");
	        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
			oos1.writeObject(privKey);	
			oos1.close();
	        
        Thread.currentThread().interrupt();
     }catch (Exception e) {
    	Interface.append("Error with encryption, possibly due to an incorrect key");
    }
  }
  public static void decrypt(byte[] text, String filepathtoencrypt) {	 
	    try{   	    	
	    	Thread t1 = new Thread();
	        t1.start(); 
	        	        
	        ObjectInputStream inputStream = null;			
			inputStream = new ObjectInputStream(new FileInputStream("DoubleENCkey.txt"));
			byte[] Key = (byte[]) inputStream.readObject();
	        inputStream.close(); //get double encrypted key
	        
	        ObjectInputStream inputStream2 = null;			
			inputStream2 = new ObjectInputStream(new FileInputStream("RSAkeyforDoubleENC.txt"));
			PrivateKey pKey = (PrivateKey) inputStream2.readObject();
	        inputStream2.close();//get rsa private key
	  
	        Cipher cipher = Cipher.getInstance(ALGORITHM);
	        cipher.init(Cipher.DECRYPT_MODE, pKey);
	        byte[] aeskey = cipher.doFinal(Key); //decrypt double key with rsa private
	        SecretKey secKey = new SecretKeySpec(aeskey, "AES");
	        
	        
	        Interface.append("Decryption started");        
	        
	        AesCipher = Cipher.getInstance("AES");
	        AesCipher.init(Cipher.DECRYPT_MODE, secKey);
	        byte[] byteCipherText = AesCipher.doFinal(text);
	        Files.write(Paths.get(filepathtoencrypt), byteCipherText);	  
	 
	        Interface.append("Decryption finished!");
	        	        	       	        
		 
		 Thread.currentThread().interrupt();
	 }catch(Exception e) {
    	Interface.append("Error with decryption, possibly due to an incorrect key");
    }
  } 
  
public static void doEncrypt(String filepathtoencrypt){	
		try {	
				byte[] byteText = (Files.readAllBytes(Paths.get(filepathtoencrypt)));
				encrypt(byteText, filepathtoencrypt);
		} catch (IOException e) {
			Interface.append("Error reading bytes");
		}
	  }	  
public static void doDecrypt(String filepathtoencrypt){
		try {
				byte[] cipherText = Files.readAllBytes(Paths.get(filepathtoencrypt));
				decrypt(cipherText,filepathtoencrypt);			
		} catch (IOException e) {
			Interface.append("Error reading bytes");
		}	
	  }  
  }