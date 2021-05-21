package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.swing.JFileChooser;

public class Application {

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
	
	public void cifrarArchivo(File archivo, SecretKey llave) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, llave);
		
		FileInputStream inputStream = new FileInputStream(archivo);
		byte[] cifrado = cipher.doFinal();
	    Encoder encoder = Base64.getEncoder();
		String byteToString = encoder.encodeToString(cifrado);
	}
	
	public static SecretKey generarLlave(int n) {
		 KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(n);
		    SecretKey key = keyGenerator.generateKey();
		    return key;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void descifrarArchivo() {
		
	}

}
