package reportes;

import analizadores.Parser;
import java.util.List;
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
                .append("        * {\n")
                .append("            margin: 0;\n")
                .append("            padding: 0;\n")
                .append("            box-sizing: border-box;\n")
                .append("        }\n")
                .append("        body {\n")
                .append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n")
                .append("            background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);\n")
                .append("            min-height: 100vh;\n")
                .append("            padding: 20px;\n")
                .append("        }\n")
                .append("        .container {\n")
                .append("            max-width: 1000px;\n")
                .append("            margin: 0 auto;\n")
                .append("            background: white;\n")
                .append("            border-radius: 15px;\n")
                .append("            box-shadow: 0 20px 40px rgba(0,0,0,0.1);\n")
                .append("            overflow: hidden;\n")
                .append("        }\n")
                .append("        .header {\n")
                .append("            background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);\n")
                .append("            color: white;\n")
                .append("            padding: 30px;\n")
                .append("            text-align: center;\n")
                .append("        }\n")
                .append("        .header h1 {\n")
                .append("            font-size: 2.5em;\n")
                .append("            margin-bottom: 10px;\n")
                .append("            font-weight: 300;\n")
                .append("        }\n")
                .append("        .header .subtitle {\n")
                .append("            opacity: 0.9;\n")
                .append("            font-size: 1.1em;\n")
                .append("        }\n")
                .append("        .content {\n")
                .append("            padding: 30px;\n")
                .append("        }\n")
                .append("        .no-errors {\n")
                .append("            text-align: center;\n")
                .append("            color: #4caf50;\n")
                .append("            font-size: 1.2em;\n")
                .append("            padding: 40px;\n")
                .append("        }\n")
                .append("        .error-list {\n")
                .append("            list-style: none;\n")
                .append("            padding: 0;\n")
                .append("        }\n")
                .append("        .error-item {\n")
                .append("            background: #fff3e0;\n")
                .append("            border-left: 5px solid #f44336;\n")
                .append("            margin: 15px 0;\n")
                .append("            padding: 20px;\n")
                .append("            border-radius: 0 10px 10px 0;\n")
                .append("            box-shadow: 0 2px 10px rgba(244, 67, 54, 0.1);\n")
                .append("        }\n")
                .append("        .error-number {\n")
                .append("            font-weight: bold;\n")
                .append("            color: #f44336;\n")
                .append("            font-size: 1.1em;\n")
                .append("            margin-bottom: 8px;\n")
                .append("        }\n")
                .append("        .error-message {\n")
                .append("            color: #333;\n")
                .append("            line-height: 1.6;\n")
                .append("        }\n")
                .append("        .stats {\n")
                .append("            background: #f5f5f5;\n")
                .append("            padding: 20px;\n")
                .append("            text-align: center;\n")
                .append("            border-top: 1px solid #e0e0e0;\n")
                .append("        }\n")
                .append("        .footer {\n")
                .append("            background: #343a40;\n")
                .append("            color: white;\n")
                .append("            padding: 20px 30px;\n")
                .append("            text-align: center;\n")
                .append("            font-size: 0.9em;\n")
                .append("        }\n")
                .append("        .timestamp {\n")
                .append("            opacity: 0.8;\n")
                .append("        }\n")
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
                html.append("            <div class=\"stats\">\n")
                    .append("                <strong>Total de errores encontrados: ").append(Parser.errores.size()).append("</strong>\n")
                    .append("            </div>\n")
                    .append("            <ul class=\"error-list\">\n");
                
                for (int i = 0; i < Parser.errores.size(); i++) {
                    html.append("                <li class=\"error-item\">\n")
                        .append("                    <div class=\"error-number\">Error #").append(i + 1).append("</div>\n")
                        .append("                    <div class=\"error-message\">").append(escapeHtml(Parser.errores.get(i))).append("</div>\n")
                        .append("                </li>\n");
                }
                
                html.append("            </ul>\n");
            }
            
            html.append("        </div>\n");
            
            html.append("        <div class=\"footer\">\n")
                .append("            <div class=\"timestamp\">\n")
                .append("                Generado el ").append(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm:ss"))).append("\n")
                .append("            </div>\n")
                .append("            <div style=\"margin-top: 10px; opacity: 0.7;\">\n")
                .append("                AutómataLab - Organización de Lenguajes y Compiladores 1\n")
                .append("            </div>\n")
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