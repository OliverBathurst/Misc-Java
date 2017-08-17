/**
 * 
 */
package easyEncrypt;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.iharder.dnd.FileDrop;

public class Main {
  static javax.swing.JTextArea textb = new javax.swing.JTextArea();
  static Cipher AesCipher;
  static SecretKey SecKey = null;
  static int AESpasses = 10,zeropasses = 10, randpasses = 10;

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
    	textb.append("\n"+"Error making keys");
    }
  }
public static void encrypt(byte[] text, String filepathtoencrypt) {
	    try{   	    	
	    	Thread t1 = new Thread();
	        t1.start();  
	 
	        textb.append("\n"+"AES encryption passes started");
	        
	        AesCipher.init(Cipher.ENCRYPT_MODE, SecKey);
	        
	        for (int i=0;i<AESpasses;i++){	      
	        	byte[] encryptbytes = (Files.readAllBytes(Paths.get(filepathtoencrypt)));
	        	byte[] byteCipherText = AesCipher.doFinal(encryptbytes);
	        	Files.write(Paths.get(filepathtoencrypt), byteCipherText);	
	        	textb.append("\n" + "[" + i +"]"+ " AES pass completed");
	        }
	        
	        try{
	        	SecKey = null;
	        }catch(Exception e){
	        	textb.append("\n"+ "Key destruction failed");
	        }
	        
	        textb.append("\n"+"AES encryption passes finished");

	        zeroFill(filepathtoencrypt);
	        
            Thread.currentThread().interrupt();
     }catch (Exception e) {
    	 textb.append("\n"+"Error writing bytes to file");
    }
  }
private static void zeroFill(String filepathtoencrypt) {
	try {
		byte[] byteText = (Files.readAllBytes(Paths.get(filepathtoencrypt)));
		byte[] bytestowrite = new byte[byteText.length];
		Arrays.fill(bytestowrite, (byte) 0);
		
		textb.append("\n"+"Zero passes started");
		for(int i=0;i<zeropasses;i++){
			Files.write(Paths.get(filepathtoencrypt), bytestowrite);
			textb.append("\n" + "[" + i +"]"+ " Zero pass completed");
		}		
		textb.append("\n"+"Zero passes finished");
		
		doFinal(filepathtoencrypt);
	} catch (IOException e) {
		textb.append("\n"+"Error writing bytes to file");
	}
}
@SuppressWarnings("resource")
private static void doFinal(String filepathtoencrypt) {	
	try {
		FileChannel outChan = new FileOutputStream(filepathtoencrypt, true).getChannel();
	    outChan.truncate(0);
	    outChan.close();
	    textb.append("\n"+ "Trunicating complete");   
	    Path path = Paths.get(filepathtoencrypt);
	    setDate(path);	    	    
	} catch (IOException e) {
		textb.append("\n"+ "Error trunicating/deleting");
	}
	
}
private static void setDate(Path path) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	String newLastModifiedString = "31/01/1970";
	Date newLastModifiedDate = null;
	File a = path.toFile();
	try {
		newLastModifiedDate = dateFormat.parse(newLastModifiedString);
		a.setLastModified(newLastModifiedDate.getTime());
		textb.append("\n"+ "Date spoofing complete");
		try {
			Files.delete(path);
			textb.append("\n"+ "Deletion complete");
		} catch (IOException e) {
		}
	} catch (ParseException e) {
		textb.append("\n"+ "Error spoofing file date");
		try {
			Files.delete(path);
			textb.append("\n"+ "Deletion complete");
		} catch (IOException e1) {
		}
	}
}
private static void randFill(byte[] text, String filepathtoencrypt) {
	try {		
		byte[] bytestowrite = new byte[text.length];
		SecureRandom random = new SecureRandom();	//secure rng
			
		for(int i=0;i<randpasses;i++){
			random.nextBytes(bytestowrite);
			Files.write(Paths.get(filepathtoencrypt), bytestowrite);	
			textb.append("\n" + "[" + i +"]"+ " Random pass completed");
		}		
		
		byte[] encryptbytes = (Files.readAllBytes(Paths.get(filepathtoencrypt)));
		encrypt(encryptbytes, filepathtoencrypt);
		
	} catch (IOException e) {
		textb.append("\n"+"Error writing bytes to file");
	}	
}


public static void main(String[] args) {	
	generateKey();	
	javax.swing.JFrame frame = new javax.swing.JFrame("File Shredder");
    
    frame.getContentPane().add( 
        new javax.swing.JScrollPane(textb), 
        java.awt.BorderLayout.CENTER);
    JMenuBar bar = new JMenuBar();

	JMenu file = new JMenu("File");
	JMenuItem AES = new JMenuItem("Number of AES passes");
	JMenuItem rand = new JMenuItem("Number of random passes");
	JMenuItem zero = new JMenuItem("Number of zero passes");
	Button clear = new Button();
	clear.setLabel("Clear Output Box");
	file.add(AES);
	file.add(rand);
	file.add(zero);	
	bar.add(file);
	bar.add(clear);
	frame.setJMenuBar(bar);
	AES.addActionListener(new ActionListener() {     	
		@Override
		public void actionPerformed(java.awt.event.ActionEvent arg0) {	
			int a = 0;
			String aes = JOptionPane.showInputDialog("How many AES encryption passes? ");
			try{
				a = Integer.parseInt(aes);
				if (a>=0){
					AESpasses = a;
				}else{
					textb.append("\n" + "Cannot set integer");
				}
			}catch(Exception e){
				textb.append("\n" + "Cannot parse to integer");
			}	
		}
    });
	rand.addActionListener(new ActionListener() {     
		@Override
		public void actionPerformed(java.awt.event.ActionEvent arg0) {
			int a = 0;
			String rand = JOptionPane.showInputDialog("How many random encryption passes? ");
			try{
				a = Integer.parseInt(rand);
				if (a>=0){
					randpasses = a;
				}else{
					textb.append("\n" + "Cannot set integer");
				}
			}catch(Exception e){
				textb.append("\n" + "Cannot parse to integer");
			}	
		}
    });
	zero.addActionListener(new ActionListener() {     
		@Override
		public void actionPerformed(java.awt.event.ActionEvent arg0) {
			int a = 0;
			String aes = JOptionPane.showInputDialog("How many zero byte passes? ");
			try{
				a = Integer.parseInt(aes);
				if (a>=0){
					zeropasses = a;
				}else{
					textb.append("\n" + "Cannot set integer");
				}
			}catch(Exception e){
				textb.append("\n" + "Cannot parse to integer");
			}	
		}
    });		
	clear.addActionListener(new ActionListener() {     
		@Override
		public void actionPerformed(java.awt.event.ActionEvent arg0) {
				textb.setText("");
		}
    });		
    
    new FileDrop( System.out, textb, /*dragBorder,*/ new FileDrop.Listener()
    {   public void filesDropped( java.io.File[] files )
        {   
    	int n = JOptionPane.showConfirmDialog(null,"Are you sure you want to delete?","WARNING",
                JOptionPane.YES_NO_OPTION);
    	
    	if (n == JOptionPane.NO_OPTION || n==JOptionPane.CANCEL_OPTION) {
            
        }else{
    		for( int i = 0; i < files.length; i++ )
    		{  
    			try
                {  
            		textb.append( "\n"+ files[i].getCanonicalPath());
            		byte[] byteText = (Files.readAllBytes(Paths.get(files[i].getCanonicalPath())));
            		randFill(byteText, files[i].getCanonicalPath());
                }  
                catch( java.io.IOException e ) {}
            } 
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