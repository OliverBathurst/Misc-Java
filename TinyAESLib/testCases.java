package main;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.security.auth.DestroyFailedException;

public class testCases {

	public static void main(String[] args) {
		boolean exit = false;
		System.out.println("Options: \n"
				+ "1. Encrypt \n2. Decrypt \n3. Export Key\n"
				+ "4. Zero Pass\n5. Return Key\n6. Destroy Key\n7. Delete File\n"
				+ "8. Export Key\n9. Generate Key\n10. Load Key from File\n"
				+ "11. Zero Pass\n12. Incorrect Key size\n13. Nullify Key\n14. Exit");
		AESInstance aes = new AESInstance();
		Scanner sc = new Scanner(System.in);
		while(exit != true) {
		int choice = sc.nextInt();
		
		switch (choice){
		case 1:
			try {
				aes.encrypt("test.txt");
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 2:
			try {
				aes.decrypt("test.txt");
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 3:
			try {
				aes.exportKey();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				aes.zeroPass("test.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 5:
			System.out.println(aes.returnKey());
			break;
		case 6:
			try {
				aes.destroyKey();
			} catch (DestroyFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 7:
			try {
				aes.deleteFile("test.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 8:
			try {
				aes.exportKey();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 9:
			try {
				aes.generateKey(128);
				System.out.println("Generated key");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 10:
			try {
				aes.loadKeyFromFile("AESKey.txt");
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 11:
			try {
				aes.zeroPass("test.txt");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 12:
			try {
				aes.generateKey(200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 13:
			aes.nullifyKey();
			break;
		case 14:
			exit = true;
			break;
		}
		}
		sc.close();
	}
}