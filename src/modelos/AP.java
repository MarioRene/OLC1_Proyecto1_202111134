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
    
    public AP(String nombre, List<String> estados, List<Character> alfabeto, 
              List<Character> simbolosPila, String estadoInicial, 
              List<String> estadosAceptacion, List<Transicion> transicionesList) {
        this.nombre = nombre;
        this.estados = new HashSet<>(estados);
        this.alfabeto = new HashSet<>(alfabeto);
        this.simbolosPila = new HashSet<>(simbolosPila);
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = new HashSet<>(estadosAceptacion);
        this.transiciones = new ArrayList<>();
        
        for (Transicion trans : transicionesList) {
            if (trans instanceof TransicionAP) {
                this.transiciones.add((TransicionAP) trans);
            }
        }
    }
    
    public boolean validarCadena(String cadena) {
        // Implementación de validación para autómata de pila
        // Usando el algoritmo estándar con configuración (estado, cadena, pila)
        return validarRecursivo(estadoInicial, cadena, new Stack<Character>());
    }
    
    private boolean validarRecursivo(String estadoActual, String cadena, Stack<Character> pila) {
        // Caso base: cadena vacía y estado de aceptación
        if (cadena.isEmpty() && estadosAceptacion.contains(estadoActual) && pila.isEmpty()) {
            return true;
        }
        
        // Buscar transiciones aplicables
        for (TransicionAP trans : transiciones) {
            if (trans.getOrigen().equals(estadoActual)) {
                // Verificar si la transición es aplicable
                char siguienteChar = cadena.isEmpty() ? '$' : cadena.charAt(0);
                
                if (trans.getEntrada() == '$' || trans.getEntrada() == siguienteChar) {
                    // Verificar condición de pila
                    if (trans.getExtrae() == '$' || (!pila.isEmpty() && pila.peek() == trans.getExtrae())) {
                        // Aplicar transición
                        String nuevoEstado = trans.getDestino();
                        String nuevaCadena = (trans.getEntrada() == '$') ? cadena : cadena.substring(1);
                        
                        Stack<Character> nuevaPila = (Stack<Character>) pila.clone();
                        if (trans.getExtrae() != '$') {
                            nuevaPila.pop();
                        }
                        if (trans.getInserta() != '$') {
                            nuevaPila.push(trans.getInserta());
                        }
                        
                        if (validarRecursivo(nuevoEstado, nuevaCadena, nuevaPila)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public void descripcion() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Tipo: Autómata de Pila");
        System.out.println("Estados: " + String.join(", ", estados));
        System.out.println("Alfabeto: " + alfabeto.toString().replaceAll("[\\[\\]]", ""));
        System.out.println("Símbolos de Pila: " + simbolosPila.toString().replaceAll("[\\[\\]]", ""));
        System.out.println("Estado Inicial: " + estadoInicial);
        System.out.println("Estados de Aceptación: " + String.join(", ", estadosAceptacion));
        System.out.println("Transiciones:");
        for (TransicionAP trans : transiciones) {
            System.out.println("  " + trans.getOrigen() + " (" + trans.getEntrada() + ") -> (" + 
                    trans.getExtrae() + "), " + trans.getDestino() + " : (" + trans.getInserta() + ")");
        }
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Set<Character> getSimbolosPila() { return simbolosPila; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosAceptacion() { return estadosAceptacion; }
    public List<TransicionAP> getTransiciones() { return transiciones; }
}