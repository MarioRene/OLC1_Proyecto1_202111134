package modelos;

public class TransicionAFD extends Transicion {
    private char simbolo;
    
    public TransicionAFD(String origen, char simbolo, String destino) {
        super(origen, destino);
        this.simbolo = simbolo;
    }
    
    public char getSimbolo() { return simbolo; }
    
    @Override
    public String toString() {
        return origen + " -> '" + simbolo + "', " + destino;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TransicionAFD)) return false;
        TransicionAFD other = (TransicionAFD) obj;
        return origen.equals(other.origen) && 
               simbolo == other.simbolo && 
               destino.equals(other.destino);
    }
    
    @Override
    public int hashCode() {
        return origen.hashCode() + simbolo + destino.hashCode();
    }
}