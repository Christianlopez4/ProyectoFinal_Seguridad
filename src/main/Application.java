package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

public class Application {
	
	public final static String ALG = "AES";

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Seleccione el archivo a cifrar o descifrar");
        File archivo = seleccionarArchivo();
		
		System.out.println("Seleccione la opción que desea realizar:");
        System.out.println("1. Cifrar archivo");
        System.out.println("2. Descifrar archivo");
        int opcion = obtenerOpcion(scanner);
        
        System.out.println("Digite la contraseña del archivo");
        String contrasena = obtenerContrasena(scanner);
        
        SecretKeySpec sk = generarClave(contrasena); 
        
        switch (opcion) {
		case 1: 
			try {
				cifrarArchivo(archivo, sk);
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
	
	public static void cifrarArchivo(File archivo, SecretKeySpec clave) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(ALG);
		cipher.init(Cipher.ENCRYPT_MODE, clave);
		
		FileInputStream inputStream = new FileInputStream(archivo);
		
		File encryptedFile = new File("./Archivo_cifrado.txt");
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
		
		inputStream.close();
	    outputStream.close();
	}
	
	public static SecretKeySpec generarClave(String contrasena) {
		SecretKeySpec sks = new SecretKeySpec(contrasena.getBytes(), ALG);
		return sks;
	}
	
	public void descifrarArchivo() {
		
	}

}
