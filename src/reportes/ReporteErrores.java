package reportes;

import analizadores.Parser;
import java.util.List;
import java.util.ArrayList; // <- AGREGADO: Import faltante
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReporteErrores {
    
    public static void mostrarErroresConsola() {
        System.out.println("=== REPORTE DE ERRORES ===");
        if (Parser.errores.isEmpty()) {
            System.out.println("No se encontraron errores.");
        } else {
            System.out.println("Total de errores: " + Parser.errores.size());
            System.out.println();
            
            for (int i = 0; i < Parser.errores.size(); i++) {
                System.out.println((i + 1) + ". " + Parser.errores.get(i));
            }
        }
        System.out.println("=========================");
    }
    
    public static void generarReporteHTML(String archivoSalida) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"es\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>Reporte de Errores - AutómataLab</title>\n")
                .append("    <style>\n")
                .append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n")
                .append("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%); min-height: 100vh; padding: 20px; }\n")
                .append("        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 15px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); overflow: hidden; }\n")
                .append("        .header { background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%); color: white; padding: 30px; text-align: center; }\n")
                .append("        .header h1 { font-size: 2.5em; margin-bottom: 10px; font-weight: 300; }\n")
                .append("        .content { padding: 30px; }\n")
                .append("        .no-errors { text-align: center; color: #4caf50; font-size: 1.2em; padding: 40px; }\n")
                .append("        .error-item { background: #fff3e0; border-left: 5px solid #f44336; margin: 15px 0; padding: 20px; border-radius: 0 10px 10px 0; }\n")
                .append("        .error-number { font-weight: bold; color: #f44336; font-size: 1.1em; margin-bottom: 8px; }\n")
                .append("        .footer { background: #343a40; color: white; padding: 20px 30px; text-align: center; font-size: 0.9em; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <div class=\"container\">\n")
                .append("        <div class=\"header\">\n")
                .append("            <h1>Reporte de Errores</h1>\n")
                .append("            <div class=\"subtitle\">Análisis de Errores - AutómataLab</div>\n")
                .append("        </div>\n");
            
            html.append("        <div class=\"content\">\n");
            
            if (Parser.errores.isEmpty()) {
                html.append("            <div class=\"no-errors\">\n")
                    .append("                ✅ ¡Excelente! No se encontraron errores en el análisis.\n")
                    .append("                <br><br>\n")
                    .append("                El código fue procesado correctamente sin problemas.\n")
                    .append("            </div>\n");
            } else {
                html.append("            <div style=\"text-align: center; margin-bottom: 20px;\">\n")
                    .append("                <strong>Total de errores encontrados: ").append(Parser.errores.size()).append("</strong>\n")
                    .append("            </div>\n");
                
                for (int i = 0; i < Parser.errores.size(); i++) {
                    html.append("            <div class=\"error-item\">\n")
                        .append("                <div class=\"error-number\">Error #").append(i + 1).append("</div>\n")
                        .append("                <div>").append(escapeHtml(Parser.errores.get(i))).append("</div>\n")
                        .append("            </div>\n");
                }
            }
            
            html.append("        </div>\n");
            html.append("        <div class=\"footer\">\n")
                .append("            Generado el ").append(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm:ss"))).append("\n")
                .append("            <br>AutómataLab - OLC1\n")
                .append("        </div>\n")
                .append("    </div>\n")
                .append("</body>\n")
                .append("</html>");
            
            try (FileWriter writer = new FileWriter(archivoSalida)) {
                writer.write(html.toString());
            }
            
            System.out.println("Reporte de errores HTML generado: " + archivoSalida);
            
        } catch (Exception e) {
            System.err.println("Error al generar reporte de errores HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;")
                  .replace("\n", "<br>")
                  .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
    
    public static void limpiarErrores() {
        Parser.errores.clear();
        System.out.println("Lista de errores limpiada.");
    }
    
    public static int contarErrores() {
        return Parser.errores.size();
    }
    
    public static List<String> obtenerErrores() {
        return new ArrayList<>(Parser.errores);
    }
    
    public static boolean hayErrores() {
        return !Parser.errores.isEmpty();
    }
    
    public static void agregarError(String error) {
        Parser.errores.add(error);
    }
}