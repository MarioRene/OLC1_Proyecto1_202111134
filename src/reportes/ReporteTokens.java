package reportes;

import analizadores.Lexer;
import java_cup.runtime.Symbol;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import modelos.Token;

public class ReporteTokens {
    
    private static final Map<Integer, String> TOKEN_NAMES = new HashMap<>();
    
    static {
        // Mapeo de tokens usando los valores numéricos directamente
        // Estos valores deben coincidir con los definidos en sym.java
        TOKEN_NAMES.put(0, "EOF");
        TOKEN_NAMES.put(1, "ERROR");
        TOKEN_NAMES.put(2, "CADENA");
        TOKEN_NAMES.put(3, "IDENTIFICADOR");
        TOKEN_NAMES.put(4, "CARACTER");
        TOKEN_NAMES.put(5, "AFD_INICIO");
        TOKEN_NAMES.put(6, "AFD_FIN");
        TOKEN_NAMES.put(7, "AP_INICIO");
        TOKEN_NAMES.put(8, "AP_FIN");
        TOKEN_NAMES.put(9, "NOMBRE");
        TOKEN_NAMES.put(10, "N");
        TOKEN_NAMES.put(11, "T");
        TOKEN_NAMES.put(12, "P");
        TOKEN_NAMES.put(13, "I");
        TOKEN_NAMES.put(14, "A");
        TOKEN_NAMES.put(15, "TRANSICIONES");
        TOKEN_NAMES.put(16, "VER_AUTOMATAS");
        TOKEN_NAMES.put(17, "DESC");
        TOKEN_NAMES.put(18, "IGUAL");
        TOKEN_NAMES.put(19, "LLAVE_IZQUIERDA");
        TOKEN_NAMES.put(20, "LLAVE_DERECHA");
        TOKEN_NAMES.put(21, "PARENTESIS_IZQUIERDO");
        TOKEN_NAMES.put(22, "PARENTESIS_DERECHO");
        TOKEN_NAMES.put(23, "FLECHA");
        TOKEN_NAMES.put(24, "OR");
        TOKEN_NAMES.put(25, "PUNTO_COMA");
        TOKEN_NAMES.put(26, "DOS_PUNTOS");
        TOKEN_NAMES.put(27, "COMA");
        TOKEN_NAMES.put(28, "LAMBDA");
    }
    
    public static List<Token> analizarTokens(String input) {
        List<Token> tokens = new ArrayList<>();
        Map<String, Integer> estadisticas = new HashMap<>();
        
        try {
            Lexer lexer = new Lexer(new StringReader(input));
            Symbol symbol;
            int count = 1;
            
            System.out.println("INICIANDO ANALISIS LEXICO");
            System.out.println("========================================");
            
            while ((symbol = lexer.next_token()) != null) {
                if (symbol.sym == 0) { // EOF
                    tokens.add(new Token(count++, "EOF", "EOF", symbol.left + 1, symbol.right + 1));
                    break;
                }
                
                String tipo = getNombreToken(symbol.sym);
                String lexema = symbol.value != null ? symbol.value.toString() : getSymbolRepresentation(symbol.sym);
                
                if (lexema == null || lexema.trim().isEmpty()) {
                    lexema = tipo;
                }
                
                Token token = new Token(count++, lexema, tipo, symbol.left + 1, symbol.right + 1);
                tokens.add(token);
                
                estadisticas.merge(tipo, 1, Integer::sum);
                
                System.out.printf("%3d | %-20s | %-18s | %3d | %3d\n", 
                    token.getNumero(), 
                    truncarTexto(token.getLexema(), 20), 
                    token.getTipo(),
                    token.getLinea(), 
                    token.getColumna());
            }
            
            System.out.println("========================================");
            System.out.println("ANALISIS LEXICO COMPLETADO");
            System.out.println("Total de tokens: " + (tokens.size() - 1));
            System.out.println("Tipos diferentes: " + estadisticas.size());
            
        } catch (Exception e) {
            System.err.println("Error en analisis lexico: " + e.getMessage());
            tokens.add(new Token(tokens.size() + 1, e.getMessage(), "ERROR", 0, 0));
        }
        
        return tokens;
    }
    
    private static String truncarTexto(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    private static String getNombreToken(int symCode) {
        return TOKEN_NAMES.getOrDefault(symCode, "DESCONOCIDO_" + symCode);
    }
    
    private static String getSymbolRepresentation(int symCode) {
        switch (symCode) {
            case 18: return "=";
            case 19: return "{";
            case 20: return "}";
            case 21: return "(";
            case 22: return ")";
            case 23: return "->";
            case 24: return "|";
            case 25: return ";";
            case 26: return ":";
            case 27: return ",";
            case 28: return "$";
            default: return getNombreToken(symCode);
        }
    }
    
    public static void generarReporteHTML(List<Token> tokens, String archivoSalida) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<title>Reporte de Tokens</title>\n")
                .append("<meta charset='UTF-8'>\n")
                .append("<style>\n")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("table { border-collapse: collapse; width: 100%; }\n")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                .append("th { background-color: #f2f2f2; }\n")
                .append("</style>\n</head>\n<body>\n");
            
            html.append("<h1>Reporte de Tokens - AutomataLab</h1>\n");
            html.append("<p>Total de tokens: ").append(tokens.size()).append("</p>\n");
            
            html.append("<table>\n<tr>\n")
                .append("<th>#</th><th>Lexema</th><th>Tipo</th><th>Linea</th><th>Columna</th>\n</tr>\n");
            
            for (Token token : tokens) {
                html.append("<tr>\n")
                    .append("<td>").append(token.getNumero()).append("</td>\n")
                    .append("<td>").append(escapeHtml(token.getLexema())).append("</td>\n")
                    .append("<td>").append(token.getTipo()).append("</td>\n")
                    .append("<td>").append(token.getLinea()).append("</td>\n")
                    .append("<td>").append(token.getColumna()).append("</td>\n")
                    .append("</tr>\n");
            }
            
            html.append("</table>\n</body>\n</html>");
            
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