package modelos;

public abstract class Transicion {
    protected String origen;
    protected String destino;
    
    public Transicion(String origen, String destino) {
        this.origen = origen;
        this.destino = destino;
    }
    
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    
    @Override
    public abstract String toString();
}