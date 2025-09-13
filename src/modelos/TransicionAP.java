package modelos;

public class TransicionAP extends Transicion {
    private char entrada;
    private char extrae;
    private char inserta;
    
    public TransicionAP(String origen, char entrada, char extrae, String destino, char inserta) {
        super(origen, destino);
        this.entrada = entrada;
        this.extrae = extrae;
        this.inserta = inserta;
    }
    
    public char getEntrada() { return entrada; }
    public char getExtrae() { return extrae; }
    public char getInserta() { return inserta; }
    
    @Override
    public String toString() {
        return origen + " (" + entrada + ") -> (" + extrae + "), " + destino + " : (" + inserta + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TransicionAP)) return false;
        TransicionAP other = (TransicionAP) obj;
        return origen.equals(other.origen) && 
               entrada == other.entrada && 
               extrae == other.extrae &&
               destino.equals(other.destino) &&
               inserta == other.inserta;
    }
    
    @Override
    public int hashCode() {
        return origen.hashCode() + entrada + extrae + destino.hashCode() + inserta;
    }
}