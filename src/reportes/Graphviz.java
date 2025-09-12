package reportes;

import modelos.AFD;
import java.io.*;
import java.util.*;

public class Graphviz {
    
    public static String generarDotAFD(AFD afd) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ").append(afd.getNombre().replaceAll("[^a-zA-Z0-9]", "_")).append(" {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle];\n");
        
        // Estado inicial
        dot.append("  __start [shape=point, style=invis];\n");
        dot.append("  __start -> \"").append(afd.getEstadoInicial()).append("\";\n");
        
        // Estados de aceptación
        for (String estado : afd.getEstadosAceptacion()) {
            dot.append("  \"").append(estado).append("\" [shape = doublecircle];\n");
        }
        
        // Estados normales
        for (String estado : afd.getEstados()) {
            if (!afd.getEstadosAceptacion().contains(estado)) {
                dot.append("  \"").append(estado).append("\" [shape = circle];\n");
            }
        }
        
        // Transiciones
        for (String estadoOrigen : afd.getTransiciones().keySet()) {
            Map<Character, String> trans = afd.getTransiciones().get(estadoOrigen);
            for (Map.Entry<Character, String> entry : trans.entrySet()) {
                dot.append("  \"").append(estadoOrigen).append("\" -> \"")
                   .append(entry.getValue()).append("\" [label=\"")
                   .append(entry.getKey()).append("\"];\n");
            }
        }
        
        dot.append("}\n");
        return dot.toString();
    }
    
    public static boolean generarImagenAFD(AFD afd, String formato, String archivoSalida) {
        try {
            String dot = generarDotAFD(afd);
            
            // Guardar archivo DOT temporal
            File tempFile = File.createTempFile("automata", ".dot");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(dot);
            }
            
            // Ejecutar Graphviz
            ProcessBuilder pb = new ProcessBuilder("dot", "-T" + formato, tempFile.getAbsolutePath(), "-o", archivoSalida);
            Process process = pb.start();
            
            // Esperar a que termine
            int exitCode = process.waitFor();
            
            // Eliminar archivo temporal
            tempFile.delete();
            
            return exitCode == 0;
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar imagen: " + e.getMessage());
            return false;
        }
    }
    
    public static void mostrarGraficoAFD(AFD afd) {
        try {
            // Generar imagen temporal
            File tempImage = File.createTempFile("automata", ".png");
            if (generarImagenAFD(afd, "png", tempImage.getAbsolutePath())) {
                // Mostrar imagen
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(tempImage);
                } else {
                    System.out.println("Imagen generada en: " + tempImage.getAbsolutePath());
                }
            } else {
                System.out.println("No se pudo generar la imagen del autómata");
                System.out.println("Código DOT:\n" + generarDotAFD(afd));
            }
        } catch (IOException e) {
            System.err.println("Error al mostrar gráfico: " + e.getMessage());
        }
    }
}