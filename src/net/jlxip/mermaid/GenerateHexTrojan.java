package net.jlxip.mermaid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

public class GenerateHexTrojan {
    public String GenerateHexTrojan(File uncompiledTrojan){ // FUNCI�N PARA CONVERTIR EL TROYANO A HEXADECIMAL
        String trojanPath = uncompiledTrojan.getAbsolutePath(); // Creamos una variable con la ruta del archivo que recibimos por argumentos
        Pattern raw_dots_pattern = Pattern.compile(Pattern.quote(".")); // Creamos un patr�n para separar Strings mediante el punto
        String[] dots = raw_dots_pattern.split(trojanPath); // Separamos la variable con la ruta del archivo por puntos
        
        String trojanPathNOExtension = "";  // Creamos un String vac�o para almacenar la ruta del troyano SIN extensi�n
        for(int i=0;i<dots.length-1;i++){   // Recorremos el array de puntos hasta llegar a uno menos del final
            trojanPathNOExtension = trojanPathNOExtension + dots[i] + ".";  // A�adimos a la variable trojanPathNOExtension el String de la posici�n actual del array m�s un punto
        }
        
        String compiledTrojanPath = trojanPathNOExtension + "exe";  // Creamos un String para almacenar la ruta del troyano sin extensi�n m�s "exe", por lo que le a�adimos esta extensi�n
        
        byte[] array = null;    // Creamos un nuevo array de bytes
        try{
            FileInputStream fis = new FileInputStream(compiledTrojanPath);  // Abrimos el archivo
            BufferedInputStream bis = new BufferedInputStream(fis);         // para lectura binaria
            array = new byte[bis.available()];  // Creamos el tama�o del array con el tama�o de los bytes disponibles para lectura
            bis.read(array);    // Leemos el archivo y lo almacenamos en la array "array" (muy currado el nombre)
            bis.close();    // Cerramos...
            fis.close();    // "
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        String hexed = bytesToHex(array);   // Creamos un nuevo String con la salida del m�todo bytesToHex pas�ndole el array de bytes como argumento
        return hexed;   // Returnamos la variable hexed
    }
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray(); //Creamos un array de car�cteres con todos los car�cteres posibles en el lenguaje hexadecimal
    public static String bytesToHex(byte[] bytes) { // FUNCI�N PARA CONVERTIR UN ARRAY DE BYTES EN UNA CADENA HEXADECIMAL
        char[] hexChars = new char[bytes.length * 2];       // La verdad es que no s� muy bien c�mo funciona esto, lo admito
        for ( int j = 0; j < bytes.length; j++ ) {          // Lo encontr� en este StackOverflow:
            int v = bytes[j] & 0xFF;                        // http://goo.gl/ETe6us
            hexChars[j * 2] = hexArray[v >>> 4];            // 
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];       // Siento la desinformaci�n :S
        }
        return new String(hexChars);    // Returnamos un nuevo String con el array de car�cteres hexChars
    }
}
