package modelos;

import java.util.*;

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
        
        // Validar parámetros no nulos
        if (nombre == null || estados == null || alfabeto == null || 
            estadoInicial == null || estadosAceptacion == null || transicionesList == null) {
            throw new RuntimeException("Parámetros no pueden ser nulos");
        }
        
        this.nombre = nombre;
        this.estados = new HashSet<>(estados);
        this.alfabeto = new HashSet<>(alfabeto);
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
        this.transiciones = new HashMap<>();
        
        // Validar que no haya estados duplicados
        if (this.estados.size() != estados.size()) {
            throw new RuntimeException("Hay estados duplicados en la definición");
        }
        
        // Validar que no haya símbolos duplicados en el alfabeto
        if (this.alfabeto.size() != alfabeto.size()) {
            throw new RuntimeException("Hay símbolos duplicados en el alfabeto");
        }
        
        // Validaciones de pertenencia
        if (!this.estados.contains(estadoInicial)) {
            throw new RuntimeException("Estado inicial '" + estadoInicial + "' no pertenece al conjunto de estados");
        }
        
        for (String estadoAceptacion : estadosAceptacion) {
            if (!this.estados.contains(estadoAceptacion)) {
                throw new RuntimeException("Estado de aceptación '" + estadoAceptacion + "' no pertenece al conjunto de estados");
            }
        }
        
        // Inicializar estructura de transiciones
        for (String estado : this.estados) {
            this.transiciones.put(estado, new HashMap<>());
        }
        
        // Procesar transiciones
        procesarTransiciones(transicionesList);
    }
    
    private void procesarTransiciones(List<Transicion> transicionesList) {
        for (Transicion trans : transicionesList) {
            if (!(trans instanceof TransicionAFD)) {
                throw new RuntimeException("Tipo de transición inválido para AFD");
            }
            
            TransicionAFD transAFD = (TransicionAFD) trans;
            
            // Validar estados
            if (!estados.contains(transAFD.getOrigen())) {
                throw new RuntimeException("Estado origen '" + transAFD.getOrigen() + "' no está definido en N");
            }
            
            if (!estados.contains(transAFD.getDestino())) {
                throw new RuntimeException("Estado destino '" + transAFD.getDestino() + "' no está definido en N");
            }
            
            // Validar símbolo (permitir $ para lambda)
            if (!alfabeto.contains(transAFD.getSimbolo()) && transAFD.getSimbolo() != '$') {
                throw new RuntimeException("Símbolo '" + transAFD.getSimbolo() + "' no pertenece al alfabeto T");
            }
            
            // Verificar determinismo
            Map<Character, String> transEstado = this.transiciones.get(transAFD.getOrigen());
            if (transEstado.containsKey(transAFD.getSimbolo())) {
                throw new RuntimeException("AFD no determinista: Ya existe transición desde '" + 
                        transAFD.getOrigen() + "' con símbolo '" + transAFD.getSimbolo() + "'");
            }
            
            // Agregar transición
            transEstado.put(transAFD.getSimbolo(), transAFD.getDestino());
        }
    }
    
    public boolean validarCadena(String cadena) {
        if (cadena == null) {
            System.out.println("Error: Cadena no puede ser nula");
            return false;
        }
        
        String estadoActual = estadoInicial;
        System.out.println("Validando cadena: \"" + cadena + "\"");
        System.out.println("Estado inicial: " + estadoActual);
        
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            
            if (!alfabeto.contains(simbolo)) {
                System.out.println("Error: Símbolo '" + simbolo + "' no pertenece al alfabeto");
                return false;
            }
            
            Map<Character, String> transEstado = transiciones.get(estadoActual);
            if (transEstado == null || !transEstado.containsKey(simbolo)) {
                System.out.println("Error: No hay transición definida desde '" + 
                                 estadoActual + "' con símbolo '" + simbolo + "'");
                return false;
            }
            
            String nuevoEstado = transEstado.get(simbolo);
            System.out.println("δ(" + estadoActual + ", '" + simbolo + "') = " + nuevoEstado);
            estadoActual = nuevoEstado;
        }
        
        boolean esAceptado = estadosAceptacion.contains(estadoActual);
        System.out.println("Estado final: " + estadoActual);
        System.out.println("Resultado: " + (esAceptado ? "ACEPTADA" : "RECHAZADA"));
        
        return esAceptado;
    }
    
    public List<String> validarCadenaConPasos(String cadena) {
        List<String> pasos = new ArrayList<>();
        
        if (cadena == null) {
            pasos.add("ERROR: Cadena no puede ser nula");
            return pasos;
        }
        
        String estadoActual = estadoInicial;
        pasos.add("Configuración inicial: q = " + estadoActual + ", w = \"" + cadena + "\"");
        
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            String restoCadena = cadena.substring(i + 1);
            
            if (!alfabeto.contains(simbolo)) {
                pasos.add("ERROR: Símbolo '" + simbolo + "' no pertenece al alfabeto Σ");
                return pasos;
            }
            
            Map<Character, String> transEstado = transiciones.get(estadoActual);
            if (transEstado == null || !transEstado.containsKey(simbolo)) {
                pasos.add("ERROR: δ(" + estadoActual + ", '" + simbolo + "') no está definida");
                return pasos;
            }
            
            String nuevoEstado = transEstado.get(simbolo);
            pasos.add("δ(" + estadoActual + ", '" + simbolo + "') = " + nuevoEstado + 
                     ", resto: \"" + restoCadena + "\"");
            estadoActual = nuevoEstado;
        }
        
        boolean esAceptado = estadosAceptacion.contains(estadoActual);
        pasos.add("Configuración final: q = " + estadoActual + ", w = ε");
        pasos.add("Estado " + estadoActual + (esAceptado ? " ∈ F" : " ∉ F"));
        pasos.add("Resultado: Cadena " + (esAceptado ? "ACEPTADA" : "RECHAZADA"));
        
        return pasos;
    }
    
    public void descripcion() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata Finito Determinista (AFD)");
        System.out.println("Conjunto de estados (Q): {" + String.join(", ", estados) + "}");
        
        StringBuilder alfabetoStr = new StringBuilder();
        boolean first = true;
        for (Character c : alfabeto) {
            if (!first) alfabetoStr.append(", ");
            alfabetoStr.append("'").append(c).append("'");
            first = false;
        }
        System.out.println("Alfabeto de entrada (Σ): {" + alfabetoStr.toString() + "}");
        
        System.out.println("Estado inicial (q₀): " + estadoInicial);
        System.out.println("Estados de aceptación (F): {" + String.join(", ", estadosAceptacion) + "}");
        System.out.println("Función de transición (δ):");
        
        boolean hayTransiciones = false;
        for (String estado : new TreeSet<>(estados)) { // Ordenar para mejor visualización
            Map<Character, String> trans = transiciones.get(estado);
            if (trans != null && !trans.isEmpty()) {
                hayTransiciones = true;
                for (Map.Entry<Character, String> entry : new TreeMap<>(trans).entrySet()) {
                    System.out.println("  δ(" + estado + ", '" + entry.getKey() + "') = " + entry.getValue());
                }
            }
        }
        
        if (!hayTransiciones) {
            System.out.println("  (No hay transiciones definidas)");
        }
    }
    
    public String getDescripcionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append("AFD: ").append(nombre).append("\n");
        sb.append("Q = {").append(String.join(", ", estados)).append("}\n");
        
        StringBuilder alfabetoStr = new StringBuilder();
        boolean first = true;
        for (Character c : alfabeto) {
            if (!first) alfabetoStr.append(", ");
            alfabetoStr.append("'").append(c).append("'");
            first = false;
        }
        sb.append("Σ = {").append(alfabetoStr.toString()).append("}\n");
        sb.append("q₀ = ").append(estadoInicial).append("\n");
        sb.append("F = {").append(String.join(", ", estadosAceptacion)).append("}\n");
        sb.append("δ:\n");
        
        for (String estado : new TreeSet<>(estados)) {
            Map<Character, String> trans = transiciones.get(estado);
            if (trans != null && !trans.isEmpty()) {
                for (Map.Entry<Character, String> entry : new TreeMap<>(trans).entrySet()) {
                    sb.append("  δ(").append(estado).append(", '").append(entry.getKey())
                      .append("') = ").append(entry.getValue()).append("\n");
                }
            }
        }
        
        return sb.toString();
    }
    
    // Método para verificar completitud del AFD
    public boolean esCompleto() {
        for (String estado : estados) {
            Map<Character, String> transEstado = transiciones.get(estado);
            for (Character simbolo : alfabeto) {
                if (transEstado == null || !transEstado.containsKey(simbolo)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public Set<String> getEstados() { return new HashSet<>(estados); }
    public Set<Character> getAlfabeto() { return new HashSet<>(alfabeto); }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosAceptacion() { return new HashSet<>(estadosAceptacion); }
    public Map<String, Map<Character, String>> getTransiciones() { 
        Map<String, Map<Character, String>> copia = new HashMap<>();
        for (Map.Entry<String, Map<Character, String>> entry : transiciones.entrySet()) {
            copia.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copia;
    }
    
    // Método para generar DOT (sin dependencia de Graphviz durante compilación)
    public String generarDot() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ").append(nombre.replaceAll("[^a-zA-Z0-9]", "_")).append(" {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle];\n");
        
        // Estado inicial
        dot.append("  __start [shape=point, style=invis];\n");
        dot.append("  __start -> \"").append(estadoInicial).append("\";\n");
        
        // Estados de aceptación
        for (String estado : estadosAceptacion) {
            dot.append("  \"").append(estado).append("\" [shape = doublecircle];\n");
        }
        
        // Estados normales
        for (String estado : estados) {
            if (!estadosAceptacion.contains(estado)) {
                dot.append("  \"").append(estado).append("\" [shape = circle];\n");
            }
        }
        
        // Transiciones
        for (String estadoOrigen : transiciones.keySet()) {
            Map<Character, String> trans = transiciones.get(estadoOrigen);
            for (Map.Entry<Character, String> entry : trans.entrySet()) {
                dot.append("  \"").append(estadoOrigen).append("\" -> \"")
                   .append(entry.getValue()).append("\" [label=\"")
                   .append(entry.getKey()).append("\"];\n");
            }
        }
        
        dot.append("}\n");
        return dot.toString();
    }
    
    @Override
    public String toString() {
        return "AFD{nombre='" + nombre + "', estados=" + estados.size() + 
               ", alfabeto=" + alfabeto.size() + ", transiciones=" + 
               transiciones.values().stream().mapToInt(Map::size).sum() + "}";
    }
}