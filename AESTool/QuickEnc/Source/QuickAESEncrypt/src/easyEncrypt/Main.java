/**
 * 
 */
package easyEncrypt;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JFrame;

import net.iharder.dnd.FileDrop;

public class Main {
  static javax.swing.JTextArea textb = new javax.swing.JTextArea();
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
      
      FileOutputStream fos = new FileOutputStream("AESKey.txt");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
	  oos.writeObject(SecKey);	
	  oos.close();
      
      
      Thread.currentThread().interrupt();
    } catch (Exception e) {
    	textb.append("\n"+"Error making keys");
    }
  }
public static void encrypt(byte[] text, String filepathtoencrypt) {
	    try{   	    	
	    	Thread t1 = new Thread();
	        t1.start();  
	 
	        textb.append("\n"+"Encryption started");
	        AesCipher.init(Cipher.ENCRYPT_MODE, SecKey);
	        byte[] byteCipherText = AesCipher.doFinal(text);
	        Files.write(Paths.get(filepathtoencrypt), byteCipherText);	        	        
	        textb.append("\n"+"Encryption finished");

	        
            Thread.currentThread().interrupt();
     }catch (Exception e) {
    	 textb.append("\n"+"Error writing");
    }
  }
public static void main(String[] args) {	
	generateKey();
	
	javax.swing.JFrame frame = new javax.swing.JFrame("Drop files here - Quick AES Encrypt");
    
    frame.getContentPane().add( 
        new javax.swing.JScrollPane(textb), 
        java.awt.BorderLayout.CENTER );
    
    new FileDrop( System.out, textb, /*dragBorder,*/ new FileDrop.Listener()
    {   public void filesDropped( java.io.File[] files )
        {   for( int i = 0; i < files.length; i++ )
            {   try
                {  
            		textb.append( "\n"+ files[i].getCanonicalPath());
            		byte[] byteText = (Files.readAllBytes(Paths.get(files[i].getCanonicalPath())));
            		encrypt(byteText, files[i].getCanonicalPath());
                }  
                catch( java.io.IOException e ) {}
            }  
        }   
    }); 
    textb.append("\n"+"Drop files here");
    frame.setBounds( 100, 100, 300, 400 );
    frame.setSize(1500,1000);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
	
}
}