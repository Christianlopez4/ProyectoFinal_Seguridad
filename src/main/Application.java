package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Application {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Seleccione el archivo a cifrar o descifrar");
        Scanner entrada = seleccionarArchivo();
		
		System.out.println("Seleccione la opción que desea realizar:");
        System.out.println("1. Cifrar archivo");
        System.out.println("2. Descifrar archivo");
        int opcion = obtenerOpcion(scanner);
        
        System.out.println("Digite la contraseña del archivo");
        String contrasena = obtenerContrasena(scanner);
        
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
	
	public static Scanner seleccionarArchivo() {
		Scanner entrada = null;
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        try {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();                                        
            File f = new File(ruta);
            entrada = new Scanner(f);
            return entrada;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("No se ha seleccionado ningún fichero");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (entrada != null) {
                entrada.close();
            }
        }
		return entrada;
	}
	
	public void cifrarArchivo() {
		
	}
	
	public void descifrarArchivo() {
		
	}

}
