package modelos;

import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

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
        
        // Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del autómata no puede estar vacío");
        }
        
        this.nombre = nombre.trim();
        this.estados = new HashSet<>(estados);
        this.alfabeto = new HashSet<>(alfabeto);
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
        this.transiciones = new HashMap<>();
        
        // Validar que no haya duplicados
        if (this.estados.size() != estados.size()) {
            throw new RuntimeException("Hay estados duplicados en la definición");
        }
        
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
        
        System.out.println("✅ AFD '" + this.nombre + "' creado exitosamente:");
        System.out.println("   - Estados: " + this.estados.size());
        System.out.println("   - Alfabeto: " + this.alfabeto.size() + " símbolos");
        System.out.println("   - Transiciones: " + contarTransiciones());
    }
    
    private void procesarTransiciones(List<Transicion> transicionesList) {
        for (Transicion trans : transicionesList) {
            if (!(trans instanceof TransicionAFD)) {
                throw new RuntimeException("Tipo de transición inválido para AFD");
            }
            
            TransicionAFD transAFD = (TransicionAFD) trans;
            
            // Validar estados
            if (!estados.contains(transAFD.getOrigen())) {
                throw new RuntimeException("Estado origen '" + transAFD.getOrigen() + "' no está definido");
            }
            
            if (!estados.contains(transAFD.getDestino())) {
                throw new RuntimeException("Estado destino '" + transAFD.getDestino() + "' no está definido");
            }
            
            // Validar símbolo
            if (!alfabeto.contains(transAFD.getSimbolo()) && transAFD.getSimbolo() != '$') {
                throw new RuntimeException("Símbolo '" + transAFD.getSimbolo() + "' no pertenece al alfabeto");
            }
            
            // Validación de determinismo
            Map<Character, String> transEstado = this.transiciones.get(transAFD.getOrigen());
            if (transEstado.containsKey(transAFD.getSimbolo())) {
                String existente = transEstado.get(transAFD.getSimbolo());
                throw new RuntimeException("Error: AFD no determinista detectado. " +
                        "Ya existe δ(" + transAFD.getOrigen() + ", " + transAFD.getSimbolo() + ") = " + existente);
            }
            
            // Agregar transición
            transEstado.put(transAFD.getSimbolo(), transAFD.getDestino());
        }
    }
    
    private int contarTransiciones() {
        return transiciones.values().stream().mapToInt(Map::size).sum();
    }
    
    // MÉTODO DE VALIDACIÓN MEJORADO (basado en tu referencia)
    public boolean validarCadena(String w) {
        if (w == null) {
            System.out.println("❌ Error: Cadena no puede ser nula");
            return false;
        }
        
        System.out.println("\n🔍 VALIDANDO CADENA EN AFD '" + nombre + "'");
        System.out.println("═".repeat(50));
        System.out.println("Cadena de entrada: \"" + w + "\"");
        
        String q = estadoInicial; // Estado actual (como en tu referencia)
        System.out.println("Estado inicial: " + q);
        
        // Procesar cada símbolo (igual que en tu AutomataAFD.java)
        for (char c : w.toCharArray()) {
            String simbolo = String.valueOf(c); // Convertir char a String
            System.out.printf("Procesando: '%c' en estado: %s\n", c, q);
            
            // Buscar transición (igual que en tu referencia)
            Map<Character, String> fila = transiciones.get(q);
            if (fila == null || !fila.containsKey(c)) {
                System.out.println("❌ Error: δ(" + q + ", '" + c + "') no está definida");
                return false;
            }
            
            // Actualizar estado (como en tu referencia)
            q = fila.get(c);
            System.out.println("   δ(" + q + ", '" + c + "') → " + q);
        }
        
        // Verificar estado final (como en tu referencia)
        boolean esAceptado = estadosAceptacion.contains(q);
        System.out.println("\nEstado final: " + q);
        System.out.println("¿Es estado de aceptación? " + (esAceptado ? "✅ SÍ" : "❌ NO"));
        
        if (esAceptado) {
            System.out.println("✅ Resultado: Cadena ACEPTADA");
        } else {
            System.out.println("❌ Resultado: Cadena RECHAZADA");
        }
        System.out.println("═".repeat(50));
        
        return esAceptado;
    }
    
    // MÉTODO DE AGRUPACIÓN DE ARISTAS (basado en tu juntarAristas())
    private Map<String, Map<String, List<String>>> juntarAristas() {
        Map<String, Map<String, List<String>>> aristas = new LinkedHashMap<>();

        for (var entrada : transiciones.entrySet()) {
            String origen = entrada.getKey();
            Map<Character, String> destinos = entrada.getValue();

            if (!aristas.containsKey(origen)) {
                aristas.put(origen, new LinkedHashMap<>());
            }

            Map<String, List<String>> destinosOrigen = aristas.get(origen);

            for (var entradaSimbolo : destinos.entrySet()) {
                Character simbolo = entradaSimbolo.getKey();
                String destino = entradaSimbolo.getValue();

                if (!destinosOrigen.containsKey(destino)) {
                    destinosOrigen.put(destino, new ArrayList<>());
                }

                // Agregar símbolo a la lista de etiquetas
                destinosOrigen.get(destino).add(simbolo.toString());
            }
        }
        return aristas;
    }
    
    // MÉTODO GENERARDOT MEJORADO (basado en tu ConvertirADot())
    public String generarDot() {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado (igual que en tu referencia)
        sb.append("digraph ").append(nombre.replaceAll("[^a-zA-Z0-9_]", "_")).append(" {\n");
        sb.append("  rankdir=LR;\n");
        sb.append("  node [shape=circle, fontname=\"Arial\", fontsize=14];\n");
        sb.append("  edge [fontname=\"Arial\", fontsize=12];\n");
        sb.append("  bgcolor=white;\n");
        sb.append("\n");
        
        // Título
        sb.append("  label=\"AFD: ").append(nombre).append("\";\n");
        sb.append("  labelloc=top;\n");
        sb.append("  fontsize=16;\n");
        sb.append("\n");
        
        // Estado inicial (igual que en tu referencia)
        sb.append("  _ini [shape=point, label=\"\"];\n");
        sb.append("  _ini -> \"").append(estadoInicial).append("\" [color=blue, penwidth=2];\n");
        sb.append("\n");
        
        // Estados de aceptación (igual que en tu referencia)
        for (String q : estadosAceptacion) {
            sb.append("  \"").append(q).append("\" [shape=doublecircle");
            if (q.equals(estadoInicial)) {
                sb.append(", style=filled, fillcolor=lightgreen, color=blue, penwidth=2");
            } else {
                sb.append(", style=filled, fillcolor=lightgreen");
            }
            sb.append("];\n");
        }
        
        // Estados normales
        for (String estado : estados) {
            if (!estadosAceptacion.contains(estado)) {
                if (estado.equals(estadoInicial)) {
                    sb.append("  \"").append(estado).append("\" [style=filled, fillcolor=lightblue, color=blue, penwidth=2];\n");
                } else {
                    sb.append("  \"").append(estado).append("\" [shape=circle];\n");
                }
            }
        }
        sb.append("\n");
        
        // Aristas agrupadas (usando tu lógica)
        var edges = juntarAristas();
        
        for (var origen : edges.keySet()) {
            for (var destino : edges.get(origen).keySet()) {
                List<String> simbolos = edges.get(origen).get(destino);
                String label = String.join(", ", simbolos);
                
                sb.append("  \"").append(origen).append("\" -> \"").append(destino)
                  .append("\" [label=\"").append(label).append("\"");
                
                // Estilo especial para auto-transiciones
                if (origen.equals(destino)) {
                    sb.append(", color=red, style=bold, headport=n, tailport=n");
                }
                
                sb.append("];\n");
            }
        }
        
        sb.append("}\n");
        return sb.toString();
    }
    
    // MÉTODO PARA EXPORTAR GRÁFICA (basado en tu referencia)
    public Path generarArchivoDot(Path dotPath) throws IOException {
        Files.createDirectories(dotPath.getParent() == null ? Paths.get(".") : dotPath.getParent());
        Files.writeString(dotPath, generarDot(), StandardCharsets.UTF_8);
        return dotPath;
    }
    
    public void exportarGrafica(Path outputWithoutExt) throws IOException, InterruptedException {
        Path dot = generarArchivoDot(outputWithoutExt.resolveSibling(outputWithoutExt.getFileName() + ".dot"));
        Process p = new ProcessBuilder("dot", "-Tpng", dot.toString(), "-o",
                outputWithoutExt.resolveSibling(outputWithoutExt.getFileName() + ".png").toString())
                .redirectErrorStream(true)
                .start();
        int code = p.waitFor();
        if (code != 0) {
            System.err.println("Graphviz 'dot' falló con código " + code);
        } else {
            System.out.println("✅ Gráfico generado: " + outputWithoutExt + ".png");
        }
    }
    
    // Métodos adicionales útiles
    public void descripcion() {
        System.out.println("\n📊 DESCRIPCIÓN DEL AFD");
        System.out.println("═".repeat(40));
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata Finito Determinista (AFD)");
        System.out.println("Estados: " + estados);
        System.out.println("Alfabeto: " + alfabeto);
        System.out.println("Estado inicial: " + estadoInicial);
        System.out.println("Estados de aceptación: " + estadosAceptacion);
        System.out.println("Total de transiciones: " + contarTransiciones());
        System.out.println("Completo: " + (esCompleto() ? "✅ Sí" : "❌ No"));
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