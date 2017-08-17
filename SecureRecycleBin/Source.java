package main;

import java.io.File;
import javax.security.auth.DestroyFailedException;

public class Main {
	public static void main(String[] args) {
		AESInstance newInstance = new AESInstance();
		try {
			newInstance.generateKey(128); //only 128 bit AES allowed by Java
		} catch (Exception e) {
			System.out.println("Key creation failed");
			System.exit(0); //key creation failed, exit
		}
		
		for(int i=0; i< args.length; i++) {
			checkFiles(args[i], newInstance); //loop through files 
		}
		
		try {
			newInstance.destroyKey();//finally destroy the key
		} catch (DestroyFailedException e) {
			newInstance.nullifyKey(); // if failure, nullify key for GC
		}	
	}
	private static void checkFiles(String filepath, AESInstance newInstance) {
		try {
			if(!newInstance.fileFromPath(filepath).isDirectory()) {
				newInstance.encrypt(filepath); //if a file, encrypt and delete
				System.out.println("Deleting: " + filepath);
				newInstance.deleteFile(filepath);
			}else{
				directoryErase(filepath, newInstance); //if it's a directory, get tricky			
			}
		} catch (Exception ignored) {}
	}
	private static void directoryErase(String directorypath, AESInstance newInstance) throws Exception {
		File[] listOfFiles = newInstance.fileFromPath(directorypath).listFiles(); //get all files from dir
		if (listOfFiles.length == 0) {
			newInstance.fileFromPath(directorypath).delete(); //if no files are in the directory
			System.out.println("Deleted dir: " + directorypath);
		}
		for (File i: listOfFiles) { //loop through files
			if(!i.isDirectory()) { //if it's a file
				newInstance.encrypt(i.getAbsolutePath()); //encrypt, then delete
				System.out.println("Deleting: " + i.getAbsolutePath());
				newInstance.deleteFile(i.getAbsolutePath());
			}else{
				directoryErase(i.getAbsolutePath(), newInstance); //if it's a directory, call it again
			}
		}
		newInstance.fileFromPath(directorypath).delete(); //delete folder when done
		System.out.println("Deleted dir: " + directorypath);
	}
}
