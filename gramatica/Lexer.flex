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

// Literales básicos
Identifier = [a-zA-Z_][a-zA-Z0-9_]*
Number = [0-9]+
String = \"([^\"\\]|\\[\"\\nrt])*\"
Character = \'([^\'\\]|\\[\'\\nrt0])?\'

%%

<YYINITIAL> {
    // ORDEN CRÍTICO: Patrones más específicos PRIMERO
    
    // Etiquetas XML (más largas primero)
    "</AFD>"               { return symbol(sym.AFD_FIN); }
    "</AP>"                { return symbol(sym.AP_FIN); }
    "<AFD"                 { return symbol(sym.AFD_INI); }
    "<AP"                  { return symbol(sym.AP_INI); }
    
    // Palabras reservadas (más largas primero)
    "verAutomatas"         { return symbol(sym.VER_AUTOMATAS); }
    "Transiciones"         { return symbol(sym.TRANSICIONES); }
    "Nombre"               { return symbol(sym.NOMBRE); }
    "desc"                 { return symbol(sym.DESC); }
    
    // CRUCIAL: Flecha ANTES de cualquier símbolo individual
    "->"                   { return symbol(sym.FLECHA); }
    
    // Letras individuales (después de palabras completas)
    "N"                    { return symbol(sym.N); }
    "T"                    { return symbol(sym.T); }
    "P"                    { return symbol(sym.P); }
    "I"                    { return symbol(sym.I); }
    "A"                    { return symbol(sym.A); }
    
    // Símbolos de puntuación
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
        if (content.length() >= 2) {
            content = content.substring(1, content.length()-1);
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
        char c = ' ';
        
        if (charText.length() >= 2) {
            if (charText.length() == 3) {
                c = charText.charAt(1);
            } else if (charText.length() == 4 && charText.charAt(1) == '\\') {
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
                c = ' ';
            } else {
                c = charText.charAt(1);
            }
        }
        
        return symbol(sym.CARACTER, c); 
    }
    
    // Identificadores alfanuméricos
    {Identifier}           { return symbol(sym.IDENTIFICADOR, yytext()); }
    
    // Números como identificadores (para símbolos como 0, 1)
    {Number}               { return symbol(sym.IDENTIFICADOR, yytext()); }
    
    // Espacios en blanco y comentarios
    {WhiteSpace}           { /* Ignorar */ }
    {Comment}              { /* Ignorar */ }
    
    // Caracteres especiales específicos (NO incluir - ni > para preservar ->)
    "#"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "@"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "%"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "&"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "*"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "+"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "/"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "?"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "~"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "^"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "_"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
    "!"                    { return symbol(sym.IDENTIFICADOR, yytext()); }
}

// Manejo de errores léxicos
[^] { 
    String mensaje = "El carácter '" + yytext() + "' no pertenece al lenguaje";
    System.err.println("Error léxico: " + mensaje + " en línea " + (yyline+1) + ", columna " + (yycolumn+1));
    return symbol(sym.error, yytext());
}