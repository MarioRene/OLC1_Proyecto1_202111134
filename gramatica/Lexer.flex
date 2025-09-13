package analizadores;

import java_cup.runtime.*;

%%

%public
%class Lexer
%cup
%char
%column
%line
%unicode

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

// Definiciones de patrones (basado en tu referencia)
ID = [A-Za-z_][A-Za-z0-9_]*
STRCH = [^"\\\n\r] | \\["\\/bfnrt] | \\u[0-9A-Fa-f]{4}
STRING = \"({STRCH})*\"
CHAR = \'([^\'\\]|\\[\'\\nrt0])?\'
NUMBER = [0-9]+

// Comentarios
COMMENT_LINE = "//"[^\r\n]*
COMMENT_BLOCK = "/*"([^*]|[\r\n]|("*"+([^*/]|[\r\n])))*"*"+"/"

%%

// ==========================================
// PATRONES EN ORDEN DE PRIORIDAD
// ==========================================

// 1. ETIQUETAS XML (más específicas primero)
"</AFD>"                            { return symbol(sym.AFD_FIN); }
"</AP>"                             { return symbol(sym.AP_FIN); }
"<AFD"                              { return symbol(sym.AFD_INI); }
"<AP"                               { return symbol(sym.AP_INI); }

// 2. PALABRAS RESERVADAS (más largas primero)
"verAutomatas"                      { return symbol(sym.VER_AUTOMATAS); }
"Transiciones"                      { return symbol(sym.TRANSICIONES); }
"Nombre"                            { return symbol(sym.NOMBRE); }
"desc"                              { return symbol(sym.DESC); }

// 3. FLECHA (CRÍTICO: antes que símbolos individuales)
"->"                                { return symbol(sym.FLECHA); }

// 4. SÍMBOLOS DE PUNTUACIÓN
"="                                 { return symbol(sym.IGUAL); }
"{"                                 { return symbol(sym.LLAVE_IZQ); }
"}"                                 { return symbol(sym.LLAVE_DER); }
"("                                 { return symbol(sym.PAREN_IZQ); }
")"                                 { return symbol(sym.PAREN_DER); }
"|"                                 { return symbol(sym.OR); }
";"                                 { return symbol(sym.PUNTO_COMA); }
":"                                 { return symbol(sym.DOS_PUNTOS); }
","                                 { return symbol(sym.COMA); }
"$"                                 { return symbol(sym.DOLAR); }

// 5. LETRAS INDIVIDUALES (después de palabras completas)
"N"                                 { return symbol(sym.N); }
"T"                                 { return symbol(sym.T); }
"P"                                 { return symbol(sym.P); }
"I"                                 { return symbol(sym.I); }
"A"                                 { return symbol(sym.A); }

// 6. LITERALES (basado en tu referencia)
{STRING}                            { 
    String s = yytext(); 
    return symbol(sym.CADENA, s.substring(1, s.length()-1)); 
}

{CHAR}                              { 
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
        }
    }
    return symbol(sym.CARACTER, c); 
}

// 7. IDENTIFICADORES Y NÚMEROS
{ID}                                { return symbol(sym.IDENTIFICADOR, yytext()); }
{NUMBER}                            { return symbol(sym.IDENTIFICADOR, yytext()); }

// 8. SÍMBOLOS ESPECIALES PERMITIDOS
"#"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"@"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"%"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"&"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"*"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"+"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"/"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"?"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"~"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"^"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"_"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }
"!"                                 { return symbol(sym.IDENTIFICADOR, yytext()); }

// 9. ESPACIOS EN BLANCO E IGNORADOS (basado en tu referencia)
[ \t\r\n\f ]                       { /* Espacios en blanco se ignoran */ }
{COMMENT_LINE}                      { /* Comentarios de línea se ignoran */ }
{COMMENT_BLOCK}                     { /* Comentarios de bloque se ignoran */ }

// 10. MANEJO DE ERRORES (mejorado basado en tu referencia)
.                                   { 
    System.out.println("Error Léxico: " + yytext() + " | Línea: " + (yyline+1) + " | Columna: " + (yycolumn+1));
    return symbol(sym.error, yytext());
}