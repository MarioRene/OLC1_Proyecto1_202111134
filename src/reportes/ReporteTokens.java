package reportes;

import analizadores.Lexer;
import analizadores.sym;
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
        TOKEN_NAMES.put(sym.EOF, "EOF");
        TOKEN_NAMES.put(sym.error, "ERROR");
        TOKEN_NAMES.put(sym.CADENA, "CADENA");
        TOKEN_NAMES.put(sym.IDENTIFICADOR, "IDENTIFICADOR");
        TOKEN_NAMES.put(sym.CARACTER, "CARACTER");
        TOKEN_NAMES.put(sym.AFD_INI, "AFD_INICIO");
        TOKEN_NAMES.put(sym.AFD_FIN, "AFD_FIN");
        TOKEN_NAMES.put(sym.AP_INI, "AP_INICIO");
        TOKEN_NAMES.put(sym.AP_FIN, "AP_FIN");
        TOKEN_NAMES.put(sym.NOMBRE, "NOMBRE");
        TOKEN_NAMES.put(sym.N, "N");
        TOKEN_NAMES.put(sym.T, "T");
        TOKEN_NAMES.put(sym.P, "P");
        TOKEN_NAMES.put(sym.I, "I");
        TOKEN_NAMES.put(sym.A, "A");
        TOKEN_NAMES.put(sym.TRANSICIONES, "TRANSICIONES");
        TOKEN_NAMES.put(sym.VER_AUTOMATAS, "VER_AUTOMATAS");
        TOKEN_NAMES.put(sym.DESC, "DESC");
        TOKEN_NAMES.put(sym.IGUAL, "IGUAL");
        TOKEN_NAMES.put(sym.LLAVE_IZQ, "LLAVE_IZQUIERDA");
        TOKEN_NAMES.put(sym.LLAVE_DER, "LLAVE_DERECHA");
        TOKEN_NAMES.put(sym.PAREN_IZQ, "PARENTESIS_IZQUIERDO");
        TOKEN_NAMES.put(sym.PAREN_DER, "PARENTESIS_DERECHO");
        TOKEN_NAMES.put(sym.FLECHA, "FLECHA");
        TOKEN_NAMES.put(sym.OR, "OR");
        TOKEN_NAMES.put(sym.PUNTO_COMA, "PUNTO_COMA");
        TOKEN_NAMES.put(sym.DOS_PUNTOS, "DOS_PUNTOS");
        TOKEN_NAMES.put(sym.COMA, "COMA");
        TOKEN_NAMES.put(sym.DOLAR, "LAMBDA");
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
                if (symbol.sym == sym.EOF) {
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
    
    private static String getNombreToken(int sym) {
        return TOKEN_NAMES.getOrDefault(sym, "DESCONOCIDO_" + sym);
    }
    
    private static String getSymbolRepresentation(int sym) {
        switch (sym) {
            case sym.IGUAL: return "=";
            case sym.LLAVE_IZQ: return "{";
            case sym.LLAVE_DER: return "}";
            case sym.PAREN_IZQ: return "(";
            case sym.PAREN_DER: return ")";
            case sym.FLECHA: return "->";
            case sym.OR: return "|";
            case sym.PUNTO_COMA: return ";";
            case sym.DOS_PUNTOS: return ":";
            case sym.COMA: return ",";
            case sym.DOLAR: return "$";
            default: return getNombreToken(sym);
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