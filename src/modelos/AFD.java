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
        
        // Validar nombre no vacío
        if (nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del autómata no puede estar vacío");
        }
        
        this.nombre = nombre.trim();
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
        
        // Validar que haya al menos un estado
        if (this.estados.isEmpty()) {
            throw new RuntimeException("Debe definirse al menos un estado");
        }
        
        // Validar que haya al menos un símbolo en el alfabeto
        if (this.alfabeto.isEmpty()) {
            throw new RuntimeException("Debe definirse al menos un símbolo en el alfabeto");
        }
        
        // Validaciones de pertenencia
        if (!this.estados.contains(estadoInicial)) {
            throw new RuntimeException("Estado inicial '" + estadoInicial + "' no pertenece al conjunto de estados N = " + this.estados);
        }
        
        for (String estadoAceptacion : estadosAceptacion) {
            if (!this.estados.contains(estadoAceptacion)) {
                throw new RuntimeException("Estado de aceptación '" + estadoAceptacion + "' no pertenece al conjunto de estados N = " + this.estados);
            }
        }
        
        // Inicializar estructura de transiciones
        for (String estado : this.estados) {
            this.transiciones.put(estado, new HashMap<>());
        }
        
        // Procesar transiciones
        procesarTransiciones(transicionesList);
        
        System.out.println("AFD '" + this.nombre + "' creado exitosamente:");
        System.out.println("- Estados: " + this.estados.size());
        System.out.println("- Alfabeto: " + this.alfabeto.size() + " símbolos");
        System.out.println("- Transiciones: " + contarTransiciones());
    }
    
    private void procesarTransiciones(List<Transicion> transicionesList) {
        for (Transicion trans : transicionesList) {
            if (!(trans instanceof TransicionAFD)) {
                throw new RuntimeException("Tipo de transición inválido para AFD");
            }
            
            TransicionAFD transAFD = (TransicionAFD) trans;
            
            // Validar estados
            if (!estados.contains(transAFD.getOrigen())) {
                throw new RuntimeException("Estado origen '" + transAFD.getOrigen() + "' no está definido en N = " + estados);
            }
            
            if (!estados.contains(transAFD.getDestino())) {
                throw new RuntimeException("Estado destino '" + transAFD.getDestino() + "' no está definido en N = " + estados);
            }
            
            // Validar símbolo (permitir $ para lambda/épsilon)
            if (!alfabeto.contains(transAFD.getSimbolo()) && transAFD.getSimbolo() != '$') {
                throw new RuntimeException("Símbolo '" + transAFD.getSimbolo() + "' no pertenece al alfabeto T = " + alfabeto);
            }
            
            // VALIDACIÓN CRÍTICA DE DETERMINISMO (según especificación)
            Map<Character, String> transEstado = this.transiciones.get(transAFD.getOrigen());
            if (transEstado.containsKey(transAFD.getSimbolo())) {
                String existente = transEstado.get(transAFD.getSimbolo());
                throw new RuntimeException("Error: AFD no determinista detectado. " +
                        "Ya existe δ(" + transAFD.getOrigen() + ", " + transAFD.getSimbolo() + ") = " + existente + 
                        ". No se puede definir δ(" + transAFD.getOrigen() + ", " + 
                        transAFD.getSimbolo() + ") = " + transAFD.getDestino() + 
                        ". Un AFD debe tener máximo una transición por estado y símbolo.");
            }
            
            // Agregar transición
            transEstado.put(transAFD.getSimbolo(), transAFD.getDestino());
        }
    }
    
    private int contarTransiciones() {
        return transiciones.values().stream().mapToInt(Map::size).sum();
    }
    
    public boolean validarCadena(String cadena) {
        if (cadena == null) {
            System.out.println("❌ Error: Cadena no puede ser nula");
            return false;
        }
        
        System.out.println("\n🔍 VALIDANDO CADENA EN AFD '" + nombre + "'");
        System.out.println("═".repeat(50));
        System.out.println("Cadena de entrada: \"" + cadena + "\"");
        System.out.println("Longitud: " + cadena.length());
        
        String estadoActual = estadoInicial;
        System.out.println("Estado inicial: " + estadoActual);
        System.out.println();
        
        // Procesar cada símbolo de la cadena
        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);
            String restoCadena = cadena.substring(i + 1);
            
            System.out.printf("Paso %d: Configuración (q=%s, w=\"%s%s\")\n", 
                            i + 1, estadoActual, simbolo, restoCadena);
            
            // Verificar que el símbolo pertenezca al alfabeto
            if (!alfabeto.contains(simbolo)) {
                System.out.println("❌ Error: Símbolo '" + simbolo + "' no pertenece al alfabeto Σ = " + alfabeto);
                return false;
            }
            
            // Buscar transición definida
            Map<Character, String> transEstado = transiciones.get(estadoActual);
            if (transEstado == null || !transEstado.containsKey(simbolo)) {
                System.out.println("❌ Error: δ(" + estadoActual + ", '" + simbolo + "') no está definida");
                System.out.println("   Transiciones disponibles desde " + estadoActual + ": " + 
                                 (transEstado != null ? transEstado.keySet() : "ninguna"));
                return false;
            }
            
            String nuevoEstado = transEstado.get(simbolo);
            System.out.println("   δ(" + estadoActual + ", '" + simbolo + "') = " + nuevoEstado);
            estadoActual = nuevoEstado;
        }
        
        // Verificar estado final
        boolean esAceptado = estadosAceptacion.contains(estadoActual);
        System.out.println();
        System.out.println("Configuración final: (q=" + estadoActual + ", w=ε)");
        System.out.println("Estado final " + estadoActual + 
                          (esAceptado ? " ∈ F" : " ∉ F") + 
                          " (F = " + estadosAceptacion + ")");
        
        if (esAceptado) {
            System.out.println("✅ Resultado: Cadena ACEPTADA");
        } else {
            System.out.println("❌ Resultado: Cadena RECHAZADA");
        }
        System.out.println("═".repeat(50));
        
        return esAceptado;
    }
    
    public void descripcion() {
        System.out.println("\n📊 DESCRIPCIÓN DEL AFD");
        System.out.println("═".repeat(40));
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata Finito Determinista (AFD)");
        System.out.println();
        
        System.out.println("Componentes formales:");
        System.out.println("Q (Estados): " + estados);
        System.out.println("  |Q| = " + estados.size());
        
        StringBuilder alfabetoStr = new StringBuilder();
        boolean first = true;
        for (Character c : alfabeto) {
            if (!first) alfabetoStr.append(", ");
            alfabetoStr.append("'").append(c).append("'");
            first = false;
        }
        System.out.println("Σ (Alfabeto): {" + alfabetoStr.toString() + "}");
        System.out.println("  |Σ| = " + alfabeto.size());
        
        System.out.println("q₀ (Estado inicial): " + estadoInicial);
        System.out.println("F (Estados finales): " + estadosAceptacion);
        System.out.println("  |F| = " + estadosAceptacion.size());
        
        System.out.println();
        System.out.println("δ (Función de transición):");
        
        boolean hayTransiciones = false;
        for (String estado : new TreeSet<>(estados)) {
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
        } else {
            System.out.println("  Total de transiciones: " + contarTransiciones());
        }
        
        // Análisis del autómata
        System.out.println();
        System.out.println("Análisis:");
        System.out.println("- Determinista: ✅ (por definición de AFD)");
        System.out.println("- Completo: " + (esCompleto() ? "✅" : "❌"));
        System.out.println("- Estados alcanzables: " + getEstadosAlcanzables().size() + "/" + estados.size());
        System.out.println("═".repeat(40));
    }
    
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
    
    public Set<String> getEstadosAlcanzables() {
        Set<String> alcanzables = new HashSet<>();
        Queue<String> porProcesar = new LinkedList<>();
        
        // Comenzar desde el estado inicial
        alcanzables.add(estadoInicial);
        porProcesar.offer(estadoInicial);
        
        while (!porProcesar.isEmpty()) {
            String estadoActual = porProcesar.poll();
            Map<Character, String> trans = transiciones.get(estadoActual);
            
            if (trans != null) {
                for (String destino : trans.values()) {
                    if (!alcanzables.contains(destino)) {
                        alcanzables.add(destino);
                        porProcesar.offer(destino);
                    }
                }
            }
        }
        
        return alcanzables;
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
    
    // Método para generar código DOT para Graphviz - VERSIÓN ÚNICA Y MEJORADA
    public String generarDot() {
        StringBuilder dot = new StringBuilder();
        String nombreLimpio = nombre.replaceAll("[^a-zA-Z0-9_]", "_");
        
        dot.append("digraph ").append(nombreLimpio).append(" {\n");
        dot.append("  // Configuración general\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  size=\"10,8\";\n");
        dot.append("  node [shape=circle, style=filled, fillcolor=white, fontname=\"Arial\", fontsize=14];\n");
        dot.append("  edge [fontname=\"Arial\", fontsize=12, color=black];\n");
        dot.append("  bgcolor=white;\n");
        dot.append("  \n");
        
        // Título del autómata
        dot.append("  // Título\n");
        dot.append("  label=\"AFD: ").append(nombre).append("\";\n");
        dot.append("  labelloc=top;\n");
        dot.append("  labeljust=center;\n");
        dot.append("  fontsize=18;\n");
        dot.append("  fontname=\"Arial Bold\";\n");
        dot.append("  fontcolor=blue;\n");
        dot.append("  \n");
        
        // Nodo invisible para flecha inicial
        dot.append("  // Punto de inicio\n");
        dot.append("  __start [shape=point, style=invis, width=0, height=0];\n");
        dot.append("  __start -> \"").append(estadoInicial).append("\" [label=\"inicio\", color=blue, penwidth=2, fontcolor=blue];\n");
        dot.append("  \n");
        
        // Definir estilos de estados
        dot.append("  // Estados\n");
        for (String estado : estados) {
            if (estadosAceptacion.contains(estado)) {
                if (estado.equals(estadoInicial)) {
                    // Estado inicial Y de aceptación
                    dot.append("  \"").append(estado).append("\" [shape=doublecircle, style=filled, fillcolor=\"lightgreen\", penwidth=3, color=blue];\n");
                } else {
                    // Solo estado de aceptación
                    dot.append("  \"").append(estado).append("\" [shape=doublecircle, style=filled, fillcolor=\"lightgreen\", penwidth=2];\n");
                }
            } else if (estado.equals(estadoInicial)) {
                // Solo estado inicial
                dot.append("  \"").append(estado).append("\" [shape=circle, style=filled, fillcolor=\"lightblue\", penwidth=3, color=blue];\n");
            } else {
                // Estado normal
                dot.append("  \"").append(estado).append("\" [shape=circle, style=filled, fillcolor=\"white\", penwidth=1];\n");
            }
        }
        
        dot.append("  \n");
        
        // Agrupar transiciones por origen-destino para mejor visualización
        dot.append("  // Transiciones\n");
        Map<String, List<Character>> transicionesAgrupadas = new HashMap<>();
        
        for (String estadoOrigen : transiciones.keySet()) {
            Map<Character, String> trans = transiciones.get(estadoOrigen);
            for (Map.Entry<Character, String> entry : trans.entrySet()) {
                String clave = estadoOrigen + "->" + entry.getValue();
                transicionesAgrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(entry.getKey());
            }
        }
        
        for (Map.Entry<String, List<Character>> entry : transicionesAgrupadas.entrySet()) {
            String[] partes = entry.getKey().split("->");
            String origen = partes[0];
            String destino = partes[1];
            
            List<Character> simbolos = entry.getValue();
            Collections.sort(simbolos);
            
            StringBuilder etiqueta = new StringBuilder();
            for (int i = 0; i < simbolos.size(); i++) {
                if (i > 0) etiqueta.append(", ");
                etiqueta.append(simbolos.get(i));
            }
            
            // Estilo especial para auto-transiciones (loops)
            if (origen.equals(destino)) {
                dot.append("  \"").append(origen).append("\" -> \"").append(destino)
                   .append("\" [label=\"").append(etiqueta).append("\", color=red, style=bold")
                   .append(", headport=n, tailport=n, penwidth=2];\n");
            } else {
                dot.append("  \"").append(origen).append("\" -> \"").append(destino)
                   .append("\" [label=\"").append(etiqueta).append("\", penwidth=1.5];\n");
            }
        }
        
        dot.append("  \n");
        
        // Información del autómata en un subgrafo
        dot.append("  // Información\n");
        dot.append("  subgraph cluster_legend {\n");
        dot.append("    style=filled;\n");
        dot.append("    color=lightgrey;\n");
        dot.append("    fontsize=12;\n");
        dot.append("    label=\"Información del AFD\";\n");
        dot.append("    \n");
        dot.append("    legend [shape=plaintext, label=\"");
        dot.append("Estados: ").append(estados.size()).append("\\n");
        dot.append("Alfabeto: ").append(alfabeto).append("\\n");
        dot.append("Transiciones: ").append(contarTransiciones()).append("\\n");
        dot.append("Completo: ").append(esCompleto() ? "Sí" : "No").append("\\n");
        dot.append("Determinista: Sí");
        dot.append("\", fontsize=10, fontname=\"Arial\"];\n");
        dot.append("  }\n");
        
        dot.append("}\n");
        return dot.toString();
    }
    
    @Override
    public String toString() {
        return String.format("AFD{nombre='%s', |Q|=%d, |Σ|=%d, |δ|=%d, completo=%s}", 
                           nombre, estados.size(), alfabeto.size(), contarTransiciones(), esCompleto());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AFD)) return false;
        AFD other = (AFD) obj;
        return nombre.equals(other.nombre);
    }
    
    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}