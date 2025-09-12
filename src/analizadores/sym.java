package analizadores;

public class sym {
    public static final int EOF = 0;
    public static final int error = 1;
    
    // Etiquetas de autómatas
    public static final int AFD_INI = 2;
    public static final int AFD_FIN = 3;
    public static final int AP_INI = 4;
    public static final int AP_FIN = 5;
    
    // Palabras reservadas
    public static final int NOMBRE = 6;
    public static final int N = 7;
    public static final int T = 8;
    public static final int P = 9;
    public static final int I = 10;
    public static final int A = 11;
    public static final int TRANSICIONES = 12;
    public static final int VER_AUTOMATAS = 13;
    public static final int DESC = 14;
    
    // Símbolos
    public static final int IGUAL = 15;
    public static final int LLAVE_IZQ = 16;
    public static final int LLAVE_DER = 17;
    public static final int PAREN_IZQ = 18;
    public static final int PAREN_DER = 19;
    public static final int FLECHA = 20;
    public static final int OR = 21;
    public static final int PUNTO_COMA = 22;
    public static final int DOS_PUNTOS = 23;
    public static final int COMA = 24;
    public static final int DOLAR = 25;
    
    // Literales
    public static final int IDENTIFICADOR = 26;
    public static final int CADENA = 27;
    public static final int CARACTER = 28;
}