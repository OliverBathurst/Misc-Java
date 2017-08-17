/**
 * 
 */
package easyEncrypt;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.swing.JFrame;

import net.iharder.dnd.FileDrop;

public class Main {
  static javax.swing.JTextArea textb = new javax.swing.JTextArea();
  static Cipher AesCipher;
  static SecretKey SecKey = null;

public static void readIn() {
    try {    
    	
    	ObjectInputStream inputStream2 = null;			
		inputStream2 = new ObjectInputStream(new FileInputStream("AESKey.txt"));
		SecretKey secKey = (SecretKey) inputStream2.readObject();
		SecKey = secKey;	
        inputStream2.close();
        
        textb.append("\n"+"Key loaded");
        
      Thread.currentThread().interrupt();
    } catch (Exception e) {
    	textb.append("\n"+"Error finding key");
    }
  }
public static void decrypt(byte[] text, String filepathtoencrypt) {
	    try{   	    	
	    	Thread t1 = new Thread();
	        t1.start();  
	        AesCipher = Cipher.getInstance("AES");
	        
	        textb.append("\n"+"Decryption started");
	        AesCipher.init(Cipher.DECRYPT_MODE, SecKey);
	        byte[] byteCipherText = AesCipher.doFinal(text);
	        Files.write(Paths.get(filepathtoencrypt), byteCipherText);	        	        
	        textb.append("\n"+"Decryption finished");
	        
            Thread.currentThread().interrupt();
     }catch (Exception e) {
    	 textb.append("\n"+"Error writing");
    }
  }
public static void main(String[] args) {	
	readIn();	
	javax.swing.JFrame frame = new javax.swing.JFrame("Drop files here - Quick AES Decrypt");   
    frame.getContentPane().add( 
        new javax.swing.JScrollPane(textb), 
        java.awt.BorderLayout.CENTER);
    
    new FileDrop( System.out, textb, /*dragBorder,*/ new FileDrop.Listener()
    {   public void filesDropped( java.io.File[] files )
        {   for( int i = 0; i < files.length; i++ )
            {   try
                {  
            		textb.append( "\n"+ files[i].getCanonicalPath());
            		byte[] byteText = (Files.readAllBytes(Paths.get(files[i].getCanonicalPath())));
            		decrypt(byteText, files[i].getCanonicalPath());
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