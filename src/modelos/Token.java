package modelos;

public class Token {
    private int numero;
    private String lexema;
    private String tipo;
    private int linea;
    private int columna;
    
    public Token(int numero, String lexema, String tipo, int linea, int columna) {
        this.numero = numero;
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }
    
    // Getters
    public int getNumero() { return numero; }
    public String getLexema() { return lexema; }
    public String getTipo() { return tipo; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    
    @Override
    public String toString() {
        return numero + " | " + lexema + " | " + tipo + " | " + linea + " | " + columna;
    }
}