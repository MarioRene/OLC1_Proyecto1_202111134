package modelos;

public abstract class Transicion {
    // Clase base para transiciones
}

class TransicionAFD extends Transicion {
    private String origen;
    private char simbolo;
    private String destino;
    
    public TransicionAFD(String origen, char simbolo, String destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino;
    }
    
    // Getters
    public String getOrigen() { return origen; }
    public char getSimbolo() { return simbolo; }
    public String getDestino() { return destino; }
}

class TransicionAP extends Transicion {
    private String origen;
    private char entrada;
    private char extrae;
    private String destino;
    private char inserta;
    
    public TransicionAP(String origen, char entrada, char extrae, String destino, char inserta) {
        this.origen = origen;
        this.entrada = entrada;
        this.extrae = extrae;
        this.destino = destino;
        this.inserta = inserta;
    }
    
    // Getters
    public String getOrigen() { return origen; }
    public char getEntrada() { return entrada; }
    public char getExtrae() { return extrae; }
    public String getDestino() { return destino; }
    public char getInserta() { return inserta; }
}