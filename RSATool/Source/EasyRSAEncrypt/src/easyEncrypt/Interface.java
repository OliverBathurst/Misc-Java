/**
 * 
 */
package easyEncrypt;

import java.awt.Button;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * @author Oliver
 *
 */
public class Interface {

	public static JTextArea t = new JTextArea();
	static String towrite = null;
	
	public static void main(String[] args) {			
		JFrame frame = new JFrame("RSATool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(1500,1000);
		frame.setLocationRelativeTo(null);
		JMenuBar bar = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem openFi = new JMenuItem("Open File");
		JMenuItem genKeys = new JMenuItem("Generate key pair");
		JMenuItem printKey = new JMenuItem("Print key");
		JMenuItem exportKey = new JMenuItem("Export private key");
		
		file.add(openFi);
		file.add(genKeys);
		file.add(printKey);
		file.add(exportKey);
		file.add(exit);		
	    frame.getContentPane().add(t);
	    Button encrypt = new Button();
		encrypt.setLabel("Encrypt");
		Button decrypt = new Button();
		decrypt.setLabel("Decrypt");		
		Button clear = new Button();
		clear.setLabel("Clear output box");
	      
		exit.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				System.exit(0);			
			}
        });
		openFi.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				try{
				JFileChooser a = new JFileChooser();
				int result = a.showOpenDialog(a);
				if (result == JFileChooser.APPROVE_OPTION) {					
					File selectedFile = a.getSelectedFile();
					t.append("\n"+"Selected file: " + selectedFile.getAbsolutePath() );
					
					towrite = selectedFile.getAbsolutePath();
				}
				}catch(Exception e){
					t.append("\n"+"Error");
				}
			}
        });					
		encrypt.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				if (towrite==null){
					t.append("\n"+"No file selected");
				}else{
					Main.doEncrypt(towrite);				
				}
			}
        });
		decrypt.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				if (towrite==null){
					t.append("\n"+"No file selected");
				}else{
					Main.doDecrypt(towrite);				
				}
			}
        });
		genKeys.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				try{
					Main.generateKey();					
					t.append("\n" + "Keys created");
				}catch(Exception e){
					t.append("\n" + "Error");
				}
			}
        });
		printKey.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {	
				try {
					t.append("\n" + Main.privKey.toString());
				}catch(Exception e){
					t.append("\n"+"Error finding key");
				}
			}
        });
		exportKey.addActionListener(new ActionListener() {     	
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {	
				exportkey();
			}
        });
		exit.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				System.exit(0);			
			}
        });
		clear.addActionListener(new ActionListener() {     
			@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				t.setText("");		
			}
        });		
		
		bar.add(file);
		bar.add(encrypt);
		bar.add(decrypt);
		bar.add(clear);
		frame.setJMenuBar(bar);
		frame.setVisible(true);
	}
	public static void append(String a){
		t.append("\n" + a);		
	}
	@SuppressWarnings("resource")
	protected static void tryopenkey() {
		try{
			JFileChooser a = new JFileChooser();
			int result = a.showOpenDialog(a);
			if (result == JFileChooser.APPROVE_OPTION) {					
				File selectedFile = a.getSelectedFile();
				ObjectInputStream inputStream = null;
				t.append("\n"+"Selected file: " + selectedFile.getAbsolutePath());
				inputStream = new ObjectInputStream(new FileInputStream(selectedFile.getAbsolutePath()));
			    PrivateKey Key = (PrivateKey) inputStream.readObject();
			    Main.privKey = Key; 	
			    t.append("\n"+"Loaded key from: "+ selectedFile.getAbsolutePath());
			}
		}catch(Exception e){
				t.append("\n"+"Error");
		}		
	}
	public static void exportkey(){
		JFileChooser a = new JFileChooser();
		int returnVal = a.showSaveDialog(a);
		FileFilter ft = new FileNameExtensionFilter("Text Files","txt");
		a.addChoosableFileFilter(ft);
		if (returnVal == JFileChooser.APPROVE_OPTION) {					
			File selectedFile = a.getSelectedFile();
			try {		
				ObjectOutputStream publicKeyOS = new ObjectOutputStream(
						new FileOutputStream(selectedFile.getAbsolutePath()));
				publicKeyOS.writeObject(Main.privKey);	
				publicKeyOS.close();
				t.append("\n" + "Saved key to file: " + selectedFile.getAbsolutePath());
			} catch (IOException e) {				
				t.append("\n"+"Error exporting key");
			}
		}	
	}
}
