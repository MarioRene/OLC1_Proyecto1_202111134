package analizadores;

import java_cup.runtime.*;
import modelos.Token;

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

Comment = "//" [^\r\n]* | "/*" [^*] ~"*/" | "/*" "*"+ "/"

Identifier = [a-zA-Z_][a-zA-Z0-9_]*
String = \"([^\"\\]|\\.)*\"
Character = '[^']'

%%

<YYINITIAL> {
    // Palabras reservadas
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
        String content = yytext().substring(1, yytext().length()-1);
        // Escapar caracteres especiales
        content = content.replace("\\\"", "\"").replace("\\\\", "\\");
        return symbol(sym.CADENA, content); 
    }
    {Character}            { 
        char c = yytext().charAt(1);
        if (c == '\\') {
            // Manejar caracteres escapados
            if (yytext().length() > 2) {
                char escaped = yytext().charAt(2);
                switch (escaped) {
                    case 'n': c = '\n'; break;
                    case 't': c = '\t'; break;
                    case 'r': c = '\r'; break;
                    default: c = escaped;
                }
            }
        }
        return symbol(sym.CARACTER, c); 
    }
    
    // Espacios y comentarios
    {WhiteSpace}           { /* Ignorar */ }
    {Comment}              { /* Ignorar */ }
}

[^] { 
    System.err.println("Error léxico: Carácter no válido '" + yytext() + "' en línea " + (yyline+1) + ", columna " + (yycolumn+1));
    return symbol(sym.error, yytext());
}