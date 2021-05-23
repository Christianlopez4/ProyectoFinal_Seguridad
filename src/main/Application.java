package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public static void main(String[] args) {

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

	/**
	 * Método que permite al usuario seleccionar un archivo
	 * 
	 * @return el archivo seleccionado por el usuario
	 */
	public static File seleccionarArchivo() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(fileChooser);
		try {
			String ruta = fileChooser.getSelectedFile().getAbsolutePath();
			File f = new File(ruta);
			return f;
		} catch (NullPointerException e) {
			System.out.println("No se ha seleccionado ningún fichero");
		}
		return null;
	}

	/**
	 * Método que permite al usuario validar la opción que desea realizar
	 * 
	 * @param scanner clase que permite la obtener la entrada de la opción que desea
	 *                realizar el usuario
	 * @return entero que representa la opción que el usuario desea realizar
	 */
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

	/**
	 * @param scanner clase que permite obtener la contraseña del archivo a cifrar o
	 *                descifrar
	 * @return contraseña ingresada por el usuario
	 */
	public static String obtenerContrasena(Scanner scanner) {
		String contrasena;
		contrasena = scanner.nextLine();
		return contrasena;
	}

	/**
	 * Método para obtener la clave de 128 bits, utilizando el algoritmo PBKDF2
	 * 
	 * @param password es la contraseña del archivo a cifrar
	 * @return clave generada a partir de la contraseña
	 */
	public static SecretKey obtenerClave(String password) {
		byte[] salt = new byte[16];
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(ALGORITMO_PBK);
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITMO_AES);
			return secret;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("El algoritmo seleccionado no se encuentra disponible");
		} catch (InvalidKeySpecException e) {
			System.out.println("Clave no válida");
		}
		return null;
	}

	/**
	 * Método que permite cifrar un archivo
	 * 
	 * @param archivo es el archivo a cifrar
	 * @param sk      es la clave de 128 bits que se usará para el cifrado
	 */
	public static void cifrarArchivo(File archivo, SecretKey sk) {
		try {
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
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("El algoritmo seleccionado no se encuentra disponible");
		} catch (InvalidKeyException e) {
			System.out.println("La clave ingresada no es válida (codificación no válida, longitud incorrecta, etc)");
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("La longitud de los datos proporcionados a un cifrado de bloque es incorrecta");
		} catch (IOException e) {
			System.out.println("Se ha producido un error durante la escritura/lectura de archivos");
		}
	}

	/**
	 * Método para descifrar un archivo
	 * 
	 * @param archivo es el archivo a descifrar
	 * @param sk      es la clave de 128 bits que se usará para el descifrado
	 */
	public static void descifrarArchivo(File archivo, SecretKey sk) {
		try {
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
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("El algoritmo seleccionado no se encuentra disponible");
		} catch (InvalidKeyException e) {
			System.out.println("La clave ingresada no es válida (codificación no válida, longitud incorrecta, etc)");
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("La longitud de los datos proporcionados a un cifrado de bloque es incorrecta");
		} catch (IOException e) {
			System.out.println("Se ha producido un error durante la escritura/lectura de archivos");
		}

	}

	/**
	 * Método que permite generar el hash SHA-1 de un archivo
	 * 
	 * @param archivo es el archivo al que se le desea generar el hash SHA-1
	 * @return hash SHA-1 del archivo
	 */
	public static byte[] generarHashSha1(File archivo) {
		try {
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
		} catch (FileNotFoundException e) {
			System.out.println("No ha sido posible abrir el archivo indicado");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("El algoritmo seleccionado no se encuentra disponible");
		} catch (IOException e) {
			System.out.println("Se ha producido un error durante la escritura/lectura de archivos");
		}
		return null;
	}

	/**
	 * Método que permite validar si el hash SHA-1 del archivo cifrado y descifrado
	 * coinciden
	 * 
	 * @return un boolean que indica si los hash SHA-1 de los archivos cifrado y
	 *         descifrado coinciden
	 */
	public static boolean validarSHA() {
		File shaOriginal = new File("./resources/files/sha1.txt");
		File shaDecrypted = new File("./resources/files/sha2.txt");

		String sha1;
		sha1 = getSha(shaOriginal);
		String sha2 = getSha(shaDecrypted);
		return sha1.equals(sha2);
	}

	/**
	 * Método que permite obtener el hash SHA-1 escrito en un archivo
	 * 
	 * @param archivo es donde se encuentra escrito el hash SHA-1
	 * @return el hash SHA-1 del archivo
	 */
	public static String getSha(File archivo) {
		String result = "";

		try {
			DataInputStream reader = new DataInputStream(new FileInputStream(archivo));
			int nBytesToRead;
			nBytesToRead = reader.available();
			if (nBytesToRead > 0) {
				byte[] bytes = new byte[nBytesToRead];
				reader.read(bytes);
				result = new String(bytes);
			}
			reader.close();
			return result;
		} catch (IOException e) {
			System.out.println("Se ha producido un error durante la escritura/lectura de archivos");
		}
		return result;

	}

}
