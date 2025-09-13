package modelos;

import java.util.*;

public class AP {
    private String nombre;
    private Set<String> estados;
    private Set<Character> alfabeto;
    private Set<Character> simbolosPila;
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    private List<TransicionAP> transiciones;
    
    // Clase para representar configuraciones del AP
    private static class Configuracion {
        String estado;
        String cadenaRestante;
        Stack<Character> pila;
        
        public Configuracion(String estado, String cadenaRestante, Stack<Character> pila) {
            this.estado = estado;
            this.cadenaRestante = cadenaRestante;
            this.pila = (Stack<Character>) pila.clone();
        }
        
        @Override
        public String toString() {
            return "(" + estado + ", \"" + cadenaRestante + "\", " + pila + ")";
        }
    }
    
    public AP(String nombre, List<String> estados, List<Character> alfabeto, 
              List<Character> simbolosPila, String estadoInicial, 
              List<String> estadosAceptacion, List<Transicion> transicionesList) {
        
        // Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del AP no puede estar vacío");
        }
        
        if (estados == null || estados.isEmpty()) {
            throw new RuntimeException("Debe definirse al menos un estado");
        }
        
        if (alfabeto == null || alfabeto.isEmpty()) {
            throw new RuntimeException("Debe definirse al menos un símbolo en el alfabeto");
        }
        
        if (simbolosPila == null || simbolosPila.isEmpty()) {
            throw new RuntimeException("Debe definirse al menos un símbolo de pila");
        }
        
        this.nombre = nombre.trim();
        this.estados = new HashSet<>(estados);
        this.alfabeto = new HashSet<>(alfabeto);
        this.simbolosPila = new HashSet<>(simbolosPila);
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
        this.transiciones = new ArrayList<>();
        
        // Validar que no haya duplicados
        if (this.estados.size() != estados.size()) {
            throw new RuntimeException("Hay estados duplicados en la definición");
        }
        
        if (this.alfabeto.size() != alfabeto.size()) {
            throw new RuntimeException("Hay símbolos duplicados en el alfabeto");
        }
        
        if (this.simbolosPila.size() != simbolosPila.size()) {
            throw new RuntimeException("Hay símbolos duplicados en la pila");
        }
        
        // Validar pertenencia
        if (!this.estados.contains(estadoInicial)) {
            throw new RuntimeException("Estado inicial '" + estadoInicial + "' no pertenece al conjunto de estados");
        }
        
        for (String estadoAceptacion : estadosAceptacion) {
            if (!this.estados.contains(estadoAceptacion)) {
                throw new RuntimeException("Estado de aceptación '" + estadoAceptacion + "' no pertenece al conjunto de estados");
            }
        }
        
        // Procesar transiciones
        procesarTransiciones(transicionesList);
        
        System.out.println("AP '" + this.nombre + "' creado exitosamente:");
        System.out.println("- Estados: " + this.estados.size());
        System.out.println("- Alfabeto: " + this.alfabeto.size() + " símbolos");
        System.out.println("- Símbolos de pila: " + this.simbolosPila.size());
        System.out.println("- Transiciones: " + this.transiciones.size());
    }
    
    private void procesarTransiciones(List<Transicion> transicionesList) {
        for (Transicion trans : transicionesList) {
            if (!(trans instanceof TransicionAP)) {
                throw new RuntimeException("Tipo de transición inválido para AP");
            }
            
            TransicionAP transAP = (TransicionAP) trans;
            
            // Validar estados
            if (!estados.contains(transAP.getOrigen())) {
                throw new RuntimeException("Estado origen '" + transAP.getOrigen() + "' no está definido en N");
            }
            
            if (!estados.contains(transAP.getDestino())) {
                throw new RuntimeException("Estado destino '" + transAP.getDestino() + "' no está definido en N");
            }
            
            // Validar símbolo de entrada (permitir $ para lambda)
            if (transAP.getEntrada() != '$' && !alfabeto.contains(transAP.getEntrada())) {
                throw new RuntimeException("Símbolo de entrada '" + transAP.getEntrada() + "' no pertenece al alfabeto T");
            }
            
            // Validar símbolos de pila (permitir $ para lambda)
            if (transAP.getExtrae() != '$' && !simbolosPila.contains(transAP.getExtrae())) {
                throw new RuntimeException("Símbolo a extraer '" + transAP.getExtrae() + "' no pertenece a los símbolos de pila P");
            }
            
            if (transAP.getInserta() != '$' && !simbolosPila.contains(transAP.getInserta())) {
                throw new RuntimeException("Símbolo a insertar '" + transAP.getInserta() + "' no pertenece a los símbolos de pila P");
            }
            
            this.transiciones.add(transAP);
        }
    }
    
    public boolean validarCadena(String cadena) {
        if (cadena == null) {
            System.out.println("Error: Cadena no puede ser nula");
            return false;
        }
        
        System.out.println("\n🔍 VALIDANDO CADENA EN AP '" + nombre + "'");
        System.out.println("═".repeat(50));
        System.out.println("Cadena de entrada: \"" + cadena + "\"");
        
        // Inicializar pila con símbolo inicial (usualmente Z)
        Stack<Character> pilaInicial = new Stack<>();
        // Buscar un símbolo típico de fondo de pila (Z, #, etc.)
        Character simboloFondo = null;
        for (Character c : simbolosPila) {
            if (c == 'Z' || c == '#') {
                simboloFondo = c;
                break;
            }
        }
        if (simboloFondo == null && !simbolosPila.isEmpty()) {
            simboloFondo = simbolosPila.iterator().next(); // Tomar el primero disponible
        }
        if (simboloFondo != null) {
            pilaInicial.push(simboloFondo);
        }
        
        // Usar algoritmo de búsqueda en amplitud para explorar todas las configuraciones posibles
        Queue<Configuracion> configuraciones = new LinkedList<>();
        Set<String> visitadas = new HashSet<>();
        
        Configuracion inicial = new Configuracion(estadoInicial, cadena, pilaInicial);
        configuraciones.offer(inicial);
        
        System.out.println("Configuración inicial: " + inicial);
        
        int paso = 0;
        final int MAX_PASOS = 1000; // Evitar bucles infinitos
        
        while (!configuraciones.isEmpty() && paso < MAX_PASOS) {
            Configuracion actual = configuraciones.poll();
            paso++;
            
            // Crear clave única para evitar repetir configuraciones
            String clave = actual.estado + "|" + actual.cadenaRestante + "|" + actual.pila.toString();
            if (visitadas.contains(clave)) {
                continue;
            }
            visitadas.add(clave);
            
            // Verificar si hemos llegado a una configuración de aceptación
            if (actual.cadenaRestante.isEmpty() && estadosAceptacion.contains(actual.estado)) {
                System.out.println("Paso " + paso + ": " + actual + " - ACEPTACIÓN");
                System.out.println("✅ Resultado: Cadena ACEPTADA");
                System.out.println("═".repeat(50));
                return true;
            }
            
            // Buscar transiciones aplicables
            for (TransicionAP trans : transiciones) {
                if (!trans.getOrigen().equals(actual.estado)) {
                    continue;
                }
                
                // Verificar si la transición es aplicable
                boolean puedeAplicar = false;
                char simboloEntrada = actual.cadenaRestante.isEmpty() ? '$' : actual.cadenaRestante.charAt(0);
                
                // Verificar símbolo de entrada
                if (trans.getEntrada() == '$' || trans.getEntrada() == simboloEntrada) {
                    // Verificar condición de pila
                    if (trans.getExtrae() == '$' || 
                        (!actual.pila.isEmpty() && actual.pila.peek().equals(trans.getExtrae()))) {
                        puedeAplicar = true;
                    }
                }
                
                if (puedeAplicar) {
                    // Crear nueva configuración
                    String nuevaCadena = (trans.getEntrada() == '$') ? 
                        actual.cadenaRestante : 
                        actual.cadenaRestante.substring(1);
                    
                    Stack<Character> nuevaPila = (Stack<Character>) actual.pila.clone();
                    
                    // Extraer de la pila si es necesario
                    if (trans.getExtrae() != '$') {
                        if (!nuevaPila.isEmpty()) {
                            nuevaPila.pop();
                        }
                    }
                    
                    // Insertar en la pila si es necesario
                    if (trans.getInserta() != '$') {
                        nuevaPila.push(trans.getInserta());
                    }
                    
                    Configuracion nueva = new Configuracion(trans.getDestino(), nuevaCadena, nuevaPila);
                    configuraciones.offer(nueva);
                    
                    if (paso <= 10) { // Mostrar solo los primeros pasos para no saturar
                        System.out.println("Paso " + paso + ": " + actual + " → " + nueva + 
                                         " [δ(" + trans.getOrigen() + ", " + trans.getEntrada() + 
                                         ", " + trans.getExtrae() + ") = (" + trans.getDestino() + 
                                         ", " + trans.getInserta() + ")]");
                    }
                }
            }
        }
        
        if (paso >= MAX_PASOS) {
            System.out.println("⚠️ Límite de pasos alcanzado - posible bucle infinito");
        }
        
        System.out.println("❌ Resultado: Cadena RECHAZADA");
        System.out.println("═".repeat(50));
        return false;
    }
    
    public void descripcion() {
        System.out.println("\n📊 DESCRIPCIÓN DEL AP");
        System.out.println("═".repeat(40));
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata de Pila (AP)");
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
        
        StringBuilder pilaStr = new StringBuilder();
        first = true;
        for (Character c : simbolosPila) {
            if (!first) pilaStr.append(", ");
            pilaStr.append("'").append(c).append("'");
            first = false;
        }
        System.out.println("Γ (Símbolos de pila): {" + pilaStr.toString() + "}");
        System.out.println("  |Γ| = " + simbolosPila.size());
        
        System.out.println("q₀ (Estado inicial): " + estadoInicial);
        System.out.println("F (Estados finales): " + estadosAceptacion);
        System.out.println("  |F| = " + estadosAceptacion.size());
        
        System.out.println();
        System.out.println("δ (Función de transición):");
        
        if (transiciones.isEmpty()) {
            System.out.println("  (No hay transiciones definidas)");
        } else {
            for (TransicionAP trans : transiciones) {
                System.out.println("  δ(" + trans.getOrigen() + ", '" + trans.getEntrada() + 
                                 "', '" + trans.getExtrae() + "') = (" + trans.getDestino() + 
                                 ", '" + trans.getInserta() + "')");
            }
            System.out.println("  Total de transiciones: " + transiciones.size());
        }
        
        System.out.println("═".repeat(40));
    }
    
    public String getDescripcionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append("AP: ").append(nombre).append("\n");
        sb.append("Q = ").append(estados).append("\n");
        
        StringBuilder alfabetoStr = new StringBuilder();
        boolean first = true;
        for (Character c : alfabeto) {
            if (!first) alfabetoStr.append(", ");
            alfabetoStr.append("'").append(c).append("'");
            first = false;
        }
        sb.append("Σ = {").append(alfabetoStr.toString()).append("}\n");
        
        StringBuilder pilaStr = new StringBuilder();
        first = true;
        for (Character c : simbolosPila) {
            if (!first) pilaStr.append(", ");
            pilaStr.append("'").append(c).append("'");
            first = false;
        }
        sb.append("Γ = {").append(pilaStr.toString()).append("}\n");
        
        sb.append("q₀ = ").append(estadoInicial).append("\n");
        sb.append("F = ").append(estadosAceptacion).append("\n");
        sb.append("δ:\n");
        
        for (TransicionAP trans : transiciones) {
            sb.append("  δ(").append(trans.getOrigen()).append(", '").append(trans.getEntrada())
              .append("', '").append(trans.getExtrae()).append("') = (").append(trans.getDestino())
              .append(", '").append(trans.getInserta()).append("')\n");
        }
        
        sb.append("\nPropiedades:\n");
        sb.append("- Estados: ").append(estados.size()).append("\n");
        sb.append("- Símbolos alfabeto: ").append(alfabeto.size()).append("\n");
        sb.append("- Símbolos pila: ").append(simbolosPila.size()).append("\n");
        sb.append("- Transiciones: ").append(transiciones.size()).append("\n");
        
        return sb.toString();
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public Set<String> getEstados() { return new HashSet<>(estados); }
    public Set<Character> getAlfabeto() { return new HashSet<>(alfabeto); }
    public Set<Character> getSimbolosPila() { return new HashSet<>(simbolosPila); }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosAceptacion() { return new HashSet<>(estadosAceptacion); }
    public List<TransicionAP> getTransiciones() { return new ArrayList<>(transiciones); }
    
    @Override
    public String toString() {
        return String.format("AP{nombre='%s', |Q|=%d, |Σ|=%d, |Γ|=%d, |δ|=%d}", 
                           nombre, estados.size(), alfabeto.size(), simbolosPila.size(), transiciones.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AP)) return false;
        AP other = (AP) obj;
        return nombre.equals(other.nombre);
    }
    
    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}