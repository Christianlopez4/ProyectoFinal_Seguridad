package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Application {
	public final static String ALGORITMO_AES = "AES";
	public final static String ALGORITMO_PBK = "PBKDF2WithHmacSHA256";
	public final static int ITERATIONS = 10000;
	public final static int KEY_LENGTH = 128;
	public final static String HASH_SHA = "SHA-1";

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Seleccione el archivo a cifrar o descifrar");
        File archivo = seleccionarArchivo();
		
        System.out.println("Digite la contraseña del archivo");
        String contrasena = obtenerContrasena(scanner);
        SecretKey sk = obtenerClave(contrasena);
        
		System.out.println("Seleccione la opción que desea realizar:");
        System.out.println("1. Cifrar archivo");
        System.out.println("2. Descifrar archivo");
        int opcion = obtenerOpcion(scanner);
        
        switch (opcion) {
		case 1: 
			cifrarArchivo(archivo, sk);
			System.out.println("Cifrado realizado exitosamente. Se ha creado el archivo con el hash SHA-1");
			break;
		case 2:
			descifrarArchivo(archivo, sk);
			System.out.println("Descifrado realizado");
			
			boolean isShaOk = validarSHA();
			if (isShaOk) {
				System.out.println("El SHA-1 del archivo cifrado y descifrado coinciden");
			} else {
				System.out.println("El SHA-1 del archivo cifrado y descifrado NO coinciden");
			}
			
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
	
	public static void cifrarArchivo(File archivo, SecretKey sk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(ALGORITMO_AES);
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		
		File encryptedFile = new File("./resources/files/Archivo_cifrado.txt");
		File shaFile = new File("./resources/files/sha1.txt");
		
		FileInputStream inputStream = new FileInputStream(archivo);
		FileOutputStream outputStream = new FileOutputStream(encryptedFile);
		FileOutputStream shaStream = new FileOutputStream(shaFile);
		
		byte[] buffer = new byte[128];
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
		
		byte[] sha1 = generarHashSha1(archivo);
		shaStream.write(sha1);
		
		inputStream.close();
	    outputStream.close();
	    shaStream.close();
	}

	public static SecretKey obtenerClave(String password)
	        throws NoSuchAlgorithmException, InvalidKeySpecException {
			byte[] salt = new byte[16];
	        SecretKeyFactory factory =    SecretKeyFactory.getInstance(ALGORITMO_PBK);
	        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
	        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITMO_AES);
	        return secret;
	    }
	
	public static byte[] generarHashSha1(File archivo) throws NoSuchAlgorithmException, IOException {
		InputStream fis = new FileInputStream(archivo);
		byte[] buffer = new byte[1024];
		MessageDigest digest = MessageDigest.getInstance(HASH_SHA);
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
  
	public static void descifrarArchivo(File archivo, SecretKey sk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ALGORITMO_AES);
		cipher.init(Cipher.DECRYPT_MODE, sk);
		
		File decryptedFile = new File("./resources/files/Archivo_descifrado.txt");
		File shaFile = new File("./resources/files/sha2.txt");
		
		FileInputStream inputStream = new FileInputStream(archivo);
		FileOutputStream outputStream = new FileOutputStream(decryptedFile);
		FileOutputStream shaStream = new FileOutputStream(shaFile);
		
		byte[] buffer = new byte[128];
		int bytesRead;
		
		while ((bytesRead = inputStream.read(buffer)) != -1) {
	        byte[] output = cipher.update(buffer, 0, bytesRead);
	        if (output != null) {
	            outputStream.write(output);
	        }
	    }
		
		byte[] descifrado = cipher.doFinal();
		if (descifrado != null) {
	        outputStream.write(descifrado);
	    }
		
		byte[] sha1 = generarHashSha1(decryptedFile);
		shaStream.write(sha1);
		
		inputStream.close();
	    outputStream.close();
	    shaStream.close();
	}
	
	public static boolean validarSHA() throws IOException {
		File shaOriginal = new File("./resources/files/sha1.txt");
		File shaDecrypted = new File("./resources/files/sha2.txt");
		
		String sha1 = getSha(shaOriginal);
		String sha2 = getSha(shaDecrypted);
		
		return sha1.equals(sha2);
	}
	
	public static String getSha(File archivo) throws IOException {
		String result = "";

	    DataInputStream reader = new DataInputStream(new FileInputStream(archivo));
	    int nBytesToRead = reader.available();
	    if(nBytesToRead > 0) {
	        byte[] bytes = new byte[nBytesToRead];
	        reader.read(bytes);
	        result = new String(bytes);
	    }
	    reader.close();
		return result;
	}

}
