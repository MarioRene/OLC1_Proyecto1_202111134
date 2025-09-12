package reportes;

import analizadores.Lexer;
import java_cup.runtime.Symbol;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import modelos.Token;

public class ReporteTokens {
    
    public static List<Token> analizarTokens(String input) {
        List<Token> tokens = new ArrayList<>();
        
        try {
            Lexer lexer = new Lexer(new StringReader(input));
            Symbol symbol;
            int count = 1;
            
            while ((symbol = lexer.next_token()) != null) {
                if (symbol.sym == 0) break; // EOF
                
                String tipo = getNombreToken(symbol.sym);
                String lexema = symbol.value != null ? symbol.value.toString() : "";
                
                tokens.add(new Token(count++, lexema, tipo, symbol.left + 1, symbol.right + 1));
            }
            
        } catch (Exception e) {
            System.err.println("Error en análisis léxico: " + e.getMessage());
        }
        
        return tokens;
    }
    
    private static String getNombreToken(int sym) {
        // Mapear símbolos a nombres legibles
        switch (sym) {
            case 0: return "EOF";
            case 1: return "error";
            case 2: return "AFD_INI";
            case 3: return "AFD_FIN";
            case 4: return "AP_INI";
            case 5: return "AP_FIN";
            case 6: return "NOMBRE";
            case 7: return "N";
            case 8: return "T";
            case 9: return "P";
            case 10: return "I";
            case 11: return "A";
            case 12: return "TRANSICIONES";
            case 13: return "VER_AUTOMATAS";
            case 14: return "DESC";
            case 15: return "IGUAL";
            case 16: return "LLAVE_IZQ";
            case 17: return "LLAVE_DER";
            case 18: return "PAREN_IZQ";
            case 19: return "PAREN_DER";
            case 20: return "FLECHA";
            case 21: return "OR";
            case 22: return "PUNTO_COMA";
            case 23: return "DOS_PUNTOS";
            case 24: return "COMA";
            case 25: return "DOLAR";
            case 26: return "IDENTIFICADOR";
            case 27: return "CADENA";
            case 28: return "CARACTER";
            default: return "DESCONOCIDO";
        }
    }
    
    public static void generarReporteHTML(List<Token> tokens, String archivoSalida) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <title>Reporte de Tokens</title>\n")
                .append("    <style>\n")
                .append("        table { border-collapse: collapse; width: 100%; }\n")
                .append("        th, td { border: 1px solid black; padding: 8px; text-align: left; }\n")
                .append("        th { background-color: #f2f2f2; }\n")
                .append("        tr:nth-child(even) { background-color: #f9f9f9; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>Reporte de Tokens</h1>\n")
                .append("    <table>\n")
                .append("        <tr>\n")
                .append("            <th>#</th>\n")
                .append("            <th>Lexema</th>\n")
                .append("            <th>Tipo</th>\n")
                .append("            <th>Línea</th>\n")
                .append("            <th>Columna</th>\n")
                .append("        </tr>\n");
            
            for (Token token : tokens) {
                html.append("        <tr>\n")
                    .append("            <td>").append(token.getNumero()).append("</td>\n")
                    .append("            <td>").append(escapeHtml(token.getLexema())).append("</td>\n")
                    .append("            <td>").append(token.getTipo()).append("</td>\n")
                    .append("            <td>").append(token.getLinea()).append("</td>\n")
                    .append("            <td>").append(token.getColumna()).append("</td>\n")
                    .append("        </tr>\n");
            }
            
            html.append("    </table>\n")
                .append("</body>\n")
                .append("</html>");
            
            try (java.io.FileWriter writer = new java.io.FileWriter(archivoSalida)) {
                writer.write(html.toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error al generar reporte HTML: " + e.getMessage());
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