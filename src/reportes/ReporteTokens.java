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

    /**
     * Escapa caracteres especiales de HTML en una cadena.
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
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
    
    public static void mostrarTokensConsola(List<Token> tokens) {
        System.out.println("=== TABLA DE TOKENS ===");
        System.out.printf("%-5s %-20s %-15s %-8s %-8s\n", "#", "Lexema", "Tipo", "Línea", "Columna");
        System.out.println("-".repeat(60));
        
        for (Token token : tokens) {
            System.out.printf("%-5d %-20s %-15s %-8d %-8d\n", 
                token.getNumero(),
                truncarTexto(token.getLexema(), 20),
                token.getTipo(),
                token.getLinea(),
                token.getColumna()
            );
        }
        System.out.println("=".repeat(60));
        System.out.println("Total de tokens: " + tokens.size());
    }
    
    public static void generarReporteHTML(List<Token> tokens, String archivoSalida) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("<title>Tabla de Tokens - AutómataLab</title>\n")
                .append("<style>\n")
                .append("* { margin: 0; padding: 0; box-sizing: border-box; }\n")
                .append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%); min-height: 100vh; padding: 20px; }\n")
                .append(".container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 15px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); overflow: hidden; }\n")
                .append(".header { background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%); color: white; padding: 30px; text-align: center; }\n")
                .append(".header h1 { font-size: 2.5em; margin-bottom: 10px; font-weight: 300; }\n")
                .append(".content { padding: 30px; }\n")
                .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n")
                .append("th, td { border: 1px solid #ddd; padding: 12px 8px; text-align: left; }\n")
                .append("th { background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%); font-weight: bold; color: #333; }\n")
                .append("tr:nth-child(even) { background-color: #f9f9f9; }\n")
                .append("tr:hover { background-color: #e3f2fd; }\n")
                .append(".token-number { text-align: center; font-weight: bold; color: #1976d2; }\n")
                .append(".token-lexema { font-family: 'Courier New', monospace; background: #f5f5f5; padding: 4px 8px; border-radius: 4px; }\n")
                .append(".token-tipo { color: #388e3c; font-weight: 500; }\n")
                .append(".footer { background: #343a40; color: white; padding: 20px 30px; text-align: center; font-size: 0.9em; }\n")
                .append("</style>\n</head>\n<body>\n");
            
            html.append("<div class=\"container\">\n")
                .append("<div class=\"header\">\n")
                .append("<h1>Tabla de Tokens</h1>\n")
                .append("<div class=\"subtitle\">Análisis Léxico - AutómataLab</div>\n")
                .append("</div>\n");
            
            html.append("<div class=\"content\">\n");
            html.append("<p style=\"text-align: center; margin-bottom: 20px; font-size: 1.1em;\">\n")
                .append("<strong>Total de tokens analizados: ").append(tokens.size()).append("</strong>\n")
                .append("</p>\n");
            
            // Tabla según especificación: #, Lexema, Tipo, Línea, Columna
            html.append("<table>\n<thead>\n<tr>\n")
                .append("<th class=\"token-number\">#</th>\n")
                .append("<th>Lexema</th>\n")
                .append("<th>Tipo</th>\n")
                .append("<th>Línea</th>\n")
                .append("<th>Columna</th>\n")
                .append("</tr>\n</thead>\n<tbody>\n");
            
            for (Token token : tokens) {
                html.append("<tr>\n")
                    .append("<td class=\"token-number\">").append(token.getNumero()).append("</td>\n")
                    .append("<td class=\"token-lexema\">").append(escapeHtml(token.getLexema())).append("</td>\n")
                    .append("<td class=\"token-tipo\">").append(token.getTipo()).append("</td>\n")
                    .append("<td>").append(token.getLinea()).append("</td>\n")
                    .append("<td>").append(token.getColumna()).append("</td>\n")
                    .append("</tr>\n");
            }
            
            html.append("</tbody>\n</table>\n</div>\n");
            
            html.append("<div class=\"footer\">\n")
                .append("Generado el ").append(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm:ss"))).append("\n")
                .append("<br>AutómataLab - OLC1 Proyecto 1\n")
                .append("</div>\n")
                .append("</div>\n</body>\n</html>");
            
            try (java.io.FileWriter writer = new java.io.FileWriter(archivoSalida)) {
                writer.write(html.toString());
            }
            
            System.out.println("Reporte de tokens HTML generado: " + archivoSalida);
            
        } catch (Exception e) {
            System.err.println("Error al generar reporte HTML de tokens: " + e.getMessage());
        }
    }
}