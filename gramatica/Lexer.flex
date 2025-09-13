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

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

Comment = "//"[^\r\n]* | "/*"([^*]|[\r\n]|("*"+([^*/]|[\r\n])))*"*"+"/"

Identifier = [a-zA-Z_][a-zA-Z0-9_]*
String = \"([^\"\\]|\\.)*\"
Character = \'([^\'\\]|\\.)*\'

%%

<YYINITIAL> {
    // Palabras reservadas y etiquetas
    "<AFD"                 { return symbol(sym.AFD_INI); }
    "</AFD>"               { return symbol(sym.AFD_FIN); }
    "<AP"                  { return symbol(sym.AP_INI); }
    "</AP>"                { return symbol(sym.AP_FIN); }
    "Nombre"               { return symbol(sym.NOMBRE); }
    "N"                    { return symbol(sym.N); }
    "T"                    { return symbol(sym.T); }
    "P"                    { return symbol(sym.P); }
    "I"                    { return symbol(sym.I); }
    "A"                    { return symbol(sym.A); }
    "Transiciones"         { return symbol(sym.TRANSICIONES); }
    "verAutomatas"         { return symbol(sym.VER_AUTOMATAS); }
    "desc"                 { return symbol(sym.DESC); }
    
    // Símbolos
    "="                    { return symbol(sym.IGUAL); }
    "{"                    { return symbol(sym.LLAVE_IZQ); }
    "}"                    { return symbol(sym.LLAVE_DER); }
    "("                    { return symbol(sym.PAREN_IZQ); }
    ")"                    { return symbol(sym.PAREN_DER); }
    "->"                   { return symbol(sym.FLECHA); }
    "|"                    { return symbol(sym.OR); }
    ";"                    { return symbol(sym.PUNTO_COMA); }
    ":"                    { return symbol(sym.DOS_PUNTOS); }
    ","                    { return symbol(sym.COMA); }
    "$"                    { return symbol(sym.DOLAR); }
    
    // Literales
    {Identifier}           { return symbol(sym.IDENTIFICADOR, yytext()); }
    {String}               { 
        String content = yytext();
        // Remover comillas del inicio y final
        content = content.substring(1, content.length()-1);
        // Procesar escape sequences
        content = content.replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                        .replace("\\n", "\n")
                        .replace("\\t", "\t")
                        .replace("\\r", "\r");
        return symbol(sym.CADENA, content); 
    }
    {Character}            { 
        String charText = yytext();
        char c;
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
        } else {
            // Fallback para casos especiales
            c = charText.charAt(1);
        }
        return symbol(sym.CARACTER, c); 
    }
    
    // Espacios y comentarios
    {WhiteSpace}           { /* Ignorar espacios en blanco */ }
    {Comment}              { /* Ignorar comentarios */ }
}

// Manejo de errores léxicos
[^] { 
    System.err.println("Error léxico: Carácter no válido '" + yytext() + 
                      "' en línea " + (yyline+1) + ", columna " + (yycolumn+1));
    return symbol(sym.error, yytext());
}