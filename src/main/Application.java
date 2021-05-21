package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Application {
	//AES/CBC/NoPadding
	public final static String ALG = "AES";
	public final static int ITERATIONS = 10000;
	public final static int KEY_LENGTH = 128;

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Seleccione el archivo a cifrar o descifrar");
        File archivo = seleccionarArchivo();
		
		System.out.println("Seleccione la opción que desea realizar:");
        System.out.println("1. Cifrar archivo");
        System.out.println("2. Descifrar archivo");
        int opcion = obtenerOpcion(scanner);
        
        System.out.println("Digite la contraseña del archivo");
        String contrasena = obtenerContrasena(scanner);
        
        byte[] salt = new byte[16];
        SecretKey sk = obtenerClave(contrasena.toCharArray(), salt);
        
        byte[] sha1 = generarHashSha1(archivo);
        
        switch (opcion) {
		case 1: 
			try {
				cifrarArchivo(archivo, sk, sha1);
				System.out.println("El archivo ha sido cifrado correctamente");
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
					| IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			
			break;
		default:
			System.out.println("La opción seleccionada no es valida");
		}
	}
	
	public static File seleccionarArchivo() {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        try {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();                                        
            File f = new File(ruta);
            return f;
        } catch (NullPointerException e) {
            System.out.println("No se ha seleccionado ningún fichero");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
		return null;
	}
	
	public static int obtenerOpcion(Scanner scanner) {
		String opcion;
        opcion = scanner.nextLine();
        try {
			int n = Integer.parseInt(opcion);
			return n;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        return 0;
	}
	
	public static String obtenerContrasena(Scanner scanner) {
		String contrasena;
		contrasena = scanner.nextLine();
		return contrasena;
	}
	
	public static void cifrarArchivo(File archivo, SecretKey sk, byte[] sha1) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(ALG);
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		
		File encryptedFile = new File("./Archivo_cifrado.txt");
		FileInputStream inputStream = new FileInputStream(archivo);
		FileOutputStream outputStream = new FileOutputStream(encryptedFile);		
		
		byte[] buffer = new byte[64];
		int bytesRead;
		
		while ((bytesRead = inputStream.read(buffer)) != -1) {
	        byte[] output = cipher.update(buffer, 0, bytesRead);
	        if (output != null) {
	            outputStream.write(output);
	        }
	    }
		
		byte[] cifrado = cipher.doFinal();
		if (cifrado != null) {
	        outputStream.write(cifrado);
	    }
		
		outputStream.write(sha1);
		
		inputStream.close();
	    outputStream.close();
	}
	
	public static SecretKey obtenerClave(char[] password, byte[] salt)
	        throws NoSuchAlgorithmException, InvalidKeySpecException {

	        SecretKeyFactory factory =    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
	        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALG);
	        return secret;

	    }
	
	public static byte[] generarHashSha1(File archivo) throws NoSuchAlgorithmException, IOException {
		
		InputStream fis = new FileInputStream(archivo);
		byte[] buffer = new byte[1024];
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		int numRead;
		
		do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
            	digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
		fis.close();
		
		return digest.digest();
	}
	
	public void descifrarArchivo() {
		
	}

}
