package reportes;

import analizadores.Parser;
import java.util.List;

public class ReporteErrores {
    
    public static void mostrarErroresConsola() {
        System.out.println("=== REPORTE DE ERRORES ===");
        if (Parser.errores.isEmpty()) {
            System.out.println("No se encontraron errores.");
        } else {
            for (String error : Parser.errores) {
                System.out.println(error);
            }
        }
        System.out.println("=========================");
    }
    
    public static void generarReporteHTML(String archivoSalida) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <title>Reporte de Errores</title>\n")
                .append("    <style>\n")
                .append("        table { border-collapse: collapse; width: 100%; }\n")
                .append("        th, td { border: 1px solid black; padding: 8px; text-align: left; }\n")
                .append("        th { background-color: #f2f2f2; }\n")
                .append("        .error { color: red; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>Reporte de Errores</h1>\n");
            
            if (Parser.errores.isEmpty()) {
                html.append("    <p>No se encontraron errores.</p>\n");
            } else {
                html.append("    <table>\n")
                    .append("        <tr>\n")
                    .append("            <th>#</th>\n")
                    .append("            <th>Mensaje de Error</th>\n")
                    .append("        </tr>\n");
                
                int count = 1;
                for (String error : Parser.errores) {
                    html.append("        <tr>\n")
                        .append("            <td>").append(count++).append("</td>\n")
                        .append("            <td class=\"error\">").append(escapeHtml(error)).append("</td>\n")
                        .append("        </tr>\n");
                }
                
                html.append("    </table>\n");
            }
            
            html.append("</body>\n")
                .append("</html>");
            
            try (java.io.FileWriter writer = new java.io.FileWriter(archivoSalida)) {
                writer.write(html.toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error al generar reporte de errores: " + e.getMessage());
        }
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}