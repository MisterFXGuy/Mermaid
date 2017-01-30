package net.jlxip.mermaid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

public class GenerateHexTrojan {
    public static String generate(File uncompiledTrojan){ // FUNCI�N PARA CONVERTIR EL TROYANO A HEXADECIMAL
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
        
        final StringBuilder builder = new StringBuilder();
        for(byte b : array) {
            builder.append(String.format("0x%02x, ", b));
        }
        
        String hexed = builder.toString();   // Creamos un nuevo String con la salida del m�todo bytesToHex pas�ndole el array de bytes como argumento
        return hexed;   // Devolvemos la variable hexed
    }
}
