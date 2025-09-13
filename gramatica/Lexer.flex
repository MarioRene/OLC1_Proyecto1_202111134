package analizadores;

import java_cup.runtime.*;

%%

%class Lexer
%unicode
%line
%column
%cup
%public

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

// Definiciones de patrones
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

// Comentarios
CommentLine = "//"[^\r\n]*
CommentBlock = "/*"([^*]|[\r\n]|("*"+([^*/]|[\r\n])))*"*"+"/"
Comment = {CommentLine} | {CommentBlock}

// Literales
Identifier = [a-zA-Z_][a-zA-Z0-9_]*
String = \"([^\"\\]|\\[\"\\nrt])*\"
Character = \'([^\'\\]|\\[\'\\nrt0])?\'

%%

<YYINITIAL> {
    // Palabras reservadas y etiquetas (orden importante - más específico primero)
    "<AFD"                 { return symbol(sym.AFD_INI); }
    "</AFD>"               { return symbol(sym.AFD_FIN); }
    "<AP"                  { return symbol(sym.AP_INI); }
    "</AP>"                { return symbol(sym.AP_FIN); }
    "Transiciones"         { return symbol(sym.TRANSICIONES); }
    "verAutomatas"         { return symbol(sym.VER_AUTOMATAS); }
    "Nombre"               { return symbol(sym.NOMBRE); }
    "desc"                 { return symbol(sym.DESC); }
    "N"                    { return symbol(sym.N); }
    "T"                    { return symbol(sym.T); }
    "P"                    { return symbol(sym.P); }
    "I"                    { return symbol(sym.I); }
    "A"                    { return symbol(sym.A); }
    
    // Símbolos (orden importante)
    "->"                   { return symbol(sym.FLECHA); }
    "="                    { return symbol(sym.IGUAL); }
    "{"                    { return symbol(sym.LLAVE_IZQ); }
    "}"                    { return symbol(sym.LLAVE_DER); }
    "("                    { return symbol(sym.PAREN_IZQ); }
    ")"                    { return symbol(sym.PAREN_DER); }
    "|"                    { return symbol(sym.OR); }
    ";"                    { return symbol(sym.PUNTO_COMA); }
    ":"                    { return symbol(sym.DOS_PUNTOS); }
    ","                    { return symbol(sym.COMA); }
    "$"                    { return symbol(sym.DOLAR); }
    
    // Literales
    {String}               { 
        String content = yytext();
        // Remover comillas del inicio y final
        if (content.length() >= 2) {
            content = content.substring(1, content.length()-1);
            // Procesar secuencias de escape básicas
            content = content.replace("\\\"", "\"")
                            .replace("\\\\", "\\")
                            .replace("\\n", "\n")
                            .replace("\\t", "\t")
                            .replace("\\r", "\r");
        }
        return symbol(sym.CADENA, content); 
    }
    
    {Character}            { 
        String charText = yytext();
        char c = ' '; // valor por defecto
        
        if (charText.length() >= 2) {
            if (charText.length() == 3) {
                // Caracter simple: 'a'
                c = charText.charAt(1);
            } else if (charText.length() == 4 && charText.charAt(1) == '\\') {
                // Caracter con escape: '\n'
                char escaped = charText.charAt(2);
                switch (escaped) {
                    case 'n': c = '\n'; break;
                    case 't': c = '\t'; break;
                    case 'r': c = '\r'; break;
                    case '\\': c = '\\'; break;
                    case '\'': c = '\''; break;
                    case '0': c = '\0'; break;
                    default: c = escaped; break;
                }
            } else if (charText.length() == 2) {
                // Caso especial para caracteres vacíos '' - usar espacio
                c = ' ';
            } else {
                // Fallback
                c = charText.charAt(1);
            }
        }
        
        return symbol(sym.CARACTER, c); 
    }
    
    {Identifier}           { return symbol(sym.IDENTIFICADOR, yytext()); }
    
    // Espacios en blanco y comentarios - ignorar
    {WhiteSpace}           { /* Ignorar espacios en blanco */ }
    {Comment}              { /* Ignorar comentarios */ }
}

// Manejo de errores léxicos - cualquier otro caracter
[^] { 
    String mensaje = "Caracter no valido '" + yytext() + "' en linea " + (yyline+1) + ", columna " + (yycolumn+1);
    System.err.println("Error lexico: " + mensaje);
    return symbol(sym.error, yytext());
}