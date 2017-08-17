/**
 * 
 */
package easyEncrypt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Main {

  public static final String ALGORITHM = "RSA";
  static Cipher AesCipher;
  static SecretKey SecKey = null;

  public static void generateKey() {
    try {
      Thread t1 = new Thread();
      t1.start(); 
      
      KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
      KeyGen.init(128);
      SecKey = KeyGen.generateKey();
      AesCipher = Cipher.getInstance("AES");
           
      Thread.currentThread().interrupt();
    } catch (Exception e) {
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
        
        Thread.currentThread().interrupt();
     }catch (Exception e) {
    	 Interface.append("Error with encryption, possibly due to an incorrect key");
    }
  }
  public static void decrypt(byte[] text, String filepathtoencrypt) {	  
	 try{
		 Thread t1 = new Thread();
	     t1.start(); 
	     
	     Interface.append("Decryption started");
		 AesCipher.init(Cipher.DECRYPT_MODE, SecKey);
		 byte[] bytePlainText = AesCipher.doFinal(text);
		 Files.write(Paths.get(filepathtoencrypt), bytePlainText);    
		 Interface.append("Decryption finished!");
		 
		 Thread.currentThread().interrupt();
	 }catch(Exception e) {
    	Interface.append("Error with decryption, possibly due to an incorrect key");
    }
  } 
  
public static void doEncrypt(String filepathtoencrypt){	
		try {	
			if (SecKey == null){
				Interface.append("No key present");
			}else{
				byte[] byteText = (Files.readAllBytes(Paths.get(filepathtoencrypt)));
				encrypt(byteText, filepathtoencrypt);
			}
		} catch (IOException e) {
			Interface.append("Error reading bytes");
		}
	  }	  
public static void doDecrypt(String filepathtoencrypt){
		try {
			if (SecKey == null){
				Interface.append("No key present");
			}else{
				byte[] cipherText = Files.readAllBytes(Paths.get(filepathtoencrypt));
				decrypt(cipherText,filepathtoencrypt);
			}
		} catch (IOException e) {
			Interface.append("Error reading bytes");
		}	
	  }  
  }