package modelos;

import java.util.*;
import reportes.Graphviz;

public class AFD {
    private String nombre;
    private Set<String> estados;
    private Set<Character> alfabeto;
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    private Map<String, Map<Character, String>> transiciones;
    
    public AFD(String nombre, List<String> estados, List<Character> alfabeto, 
               String estadoInicial, List<String> estadosAceptacion, 
               List<Transicion> transicionesList) {
        this.nombre = nombre;
        this.estados = new HashSet<>(estados);
        this.alfabeto = new HashSet<>(alfabeto);
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
        this.transiciones = new HashMap<>();
        
        // Validaciones
        if (!estados.contains(estadoInicial)) {
            throw new RuntimeException("Estado inicial '" + estadoInicial + "' no pertenece al conjunto de estados");
        }
        
        for (String estadoAceptacion : estadosAceptacion) {
            if (!estados.contains(estadoAceptacion)) {
                throw new RuntimeException("Estado de aceptación '" + estadoAceptacion + "' no pertenece al conjunto de estados");
            }
        }
        
        // Inicializar estructura de transiciones
        for (String estado : estados) {
            this.transiciones.put(estado, new HashMap<>());
        }
        
        // Llenar transiciones
        for (Transicion trans : transicionesList) {
            if (trans instanceof TransicionAFD) {
                TransicionAFD transAFD = (TransicionAFD) trans;
                
                if (!estados.contains(transAFD.getOrigen())) {
                    throw new RuntimeException("Estado origen '" + transAFD.getOrigen() + "' no definido");
                }
                
                if (!estados.contains(transAFD.getDestino())) {
                    throw new RuntimeException("Estado destino '" + transAFD.getDestino() + "' no definido");
                }
                
                if (!alfabeto.contains(transAFD.getSimbolo()) && transAFD.getSimbolo() != '$') {
                    throw new RuntimeException("Símbolo '" + transAFD.getSimbolo() + "' no pertenece al alfabeto");
                }
                
                Map<Character, String> transEstado = this.transiciones.get(transAFD.getOrigen());
                if (transEstado.containsKey(transAFD.getSimbolo())) {
                    throw new RuntimeException("AFD no determinista: Múltiples transiciones para estado " + 
                            transAFD.getOrigen() + " con símbolo " + transAFD.getSimbolo());
                }
                transEstado.put(transAFD.getSimbolo(), transAFD.getDestino());
            }
        }
    }
    
    public boolean validarCadena(String cadena) {
        String estadoActual = estadoInicial;
        System.out.println("Validando cadena: " + cadena);
        System.out.println("Estado inicial: " + estadoActual);
        
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            
            if (!alfabeto.contains(simbolo)) {
                System.out.println("Error: Símbolo '" + simbolo + "' no pertenece al alfabeto");
                return false;
            }
            
            Map<Character, String> transEstado = transiciones.get(estadoActual);
            if (transEstado == null || !transEstado.containsKey(simbolo)) {
                System.out.println("Error: No hay transición definida desde estado '" + 
                                 estadoActual + "' con símbolo '" + simbolo + "'");
                return false;
            }
            
            String nuevoEstado = transEstado.get(simbolo);
            System.out.println("Transición: " + estadoActual + " -> '" + simbolo + "' -> " + nuevoEstado);
            estadoActual = nuevoEstado;
        }
        
        boolean esAceptado = estadosAceptacion.contains(estadoActual);
        System.out.println("Estado final: " + estadoActual);
        System.out.println("Cadena " + (esAceptado ? "VÁLIDA" : "NO VÁLIDA"));
        
        return esAceptado;
    }
    
    public List<String> validarCadenaConPasos(String cadena) {
        List<String> pasos = new ArrayList<>();
        String estadoActual = estadoInicial;
        pasos.add("Inicio: Estado = " + estadoActual + ", Cadena = " + cadena);
        
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            
            if (!alfabeto.contains(simbolo)) {
                pasos.add("ERROR: Símbolo '" + simbolo + "' no pertenece al alfabeto");
                return pasos;
            }
            
            Map<Character, String> transEstado = transiciones.get(estadoActual);
            if (transEstado == null || !transEstado.containsKey(simbolo)) {
                pasos.add("ERROR: No hay transición definida desde estado '" + 
                         estadoActual + "' con símbolo '" + simbolo + "'");
                return pasos;
            }
            
            String nuevoEstado = transEstado.get(simbolo);
            pasos.add("Transición: " + estadoActual + " -> '" + simbolo + "' -> " + nuevoEstado);
            estadoActual = nuevoEstado;
        }
        
        boolean esAceptado = estadosAceptacion.contains(estadoActual);
        pasos.add("Estado final: " + estadoActual);
        pasos.add("Resultado: Cadena " + (esAceptado ? "VÁLIDA" : "NO VÁLIDA"));
        
        return pasos;
    }
    
    public void descripcion() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata Finito Determinista");
        System.out.println("Estados: " + String.join(", ", estados));
        System.out.println("Alfabeto: " + alfabeto.toString().replaceAll("[\\[\\]]", ""));
        System.out.println("Estado Inicial: " + estadoInicial);
        System.out.println("Estados de Aceptación: " + String.join(", ", estadosAceptacion));
        System.out.println("Transiciones:");
        for (String estado : transiciones.keySet()) {
            Map<Character, String> trans = transiciones.get(estado);
            for (Map.Entry<Character, String> entry : trans.entrySet()) {
                System.out.println("  " + estado + " -> " + entry.getKey() + ", " + entry.getValue());
            }
        }
    }
    
    public String getDescripcionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Tipo: Autómata Finito Determinista\n");
        sb.append("Estados: ").append(String.join(", ", estados)).append("\n");
        sb.append("Alfabeto: ").append(alfabeto.toString().replaceAll("[\\[\\]]", "")).append("\n");
        sb.append("Estado Inicial: ").append(estadoInicial).append("\n");
        sb.append("Estados de Aceptación: ").append(String.join(", ", estadosAceptacion)).append("\n");
        sb.append("Transiciones:\n");
        for (String estado : transiciones.keySet()) {
            Map<Character, String> trans = transiciones.get(estado);
            for (Map.Entry<Character, String> entry : trans.entrySet()) {
                sb.append("  ").append(estado).append(" -> ").append(entry.getKey())
                  .append(", ").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosAceptacion() { return estadosAceptacion; }
    public Map<String, Map<Character, String>> getTransiciones() { return transiciones; }
    
    public String generarDot() {
        return Graphviz.generarDotAFD(this);
    }
}