# Gramática Formal - AutómataLab

## Descripción General

Esta gramática define la sintaxis del lenguaje AutómataLab para la definición de Autómatas Finitos Deterministas (AFD) y Autómatas de Pila (AP). La gramática está expresada en formato BNF (Backus-Naur Form) y es utilizada por el analizador sintáctico generado con CUP.

## Símbolos Terminales

### Palabras Reservadas
```
<AFD>           // Inicio de definición de AFD
</AFD>          // Fin de definición de AFD
<AP>            // Inicio de definición de AP
</AP>           // Fin de definición de AP
Nombre          // Atributo de nombre
N               // Conjunto de estados
T               // Alfabeto de entrada
P               // Símbolos de pila
I               // Estado inicial
A               // Estados de aceptación
Transiciones    // Sección de transiciones
verAutomatas    // Función para listar autómatas
desc            // Función para describir autómata
```

### Símbolos de Puntuación
```
=               // Asignación
{               // Llave izquierda
}               // Llave derecha
(               // Paréntesis izquierdo
)               // Paréntesis derecho
->              // Flecha de transición
|               // Disyunción (OR)
;               // Punto y coma
:               // Dos puntos
,               // Coma
$               // Símbolo lambda (cadena vacía)
```

### Literales
```
IDENTIFICADOR   // Secuencia alfanumérica que inicia con letra
CADENA          // Secuencia de caracteres entre comillas dobles
CARACTER        // Carácter individual entre comillas simples
```

## Gramática en Formato BNF

### Estructura Principal

```bnf
<programa> ::= <lista_elementos>

<lista_elementos> ::= <lista_elementos> <elemento>
                    | <elemento>
                    | ε

<elemento> ::= <definicion_afd>
             | <definicion_ap>
             | <llamada_funcion>
             | <comentario>
```

### Definición de AFD

```bnf
<definicion_afd> ::= '<AFD' 'Nombre' '=' CADENA '>'
                     <seccion_estados>
                     <seccion_alfabeto>
                     <seccion_inicial>
                     <seccion_aceptacion>
                     <seccion_transiciones_afd>
                     '</AFD>'

<seccion_estados> ::= 'N' '=' '{' <lista_estados> '}' ';'

<seccion_alfabeto> ::= 'T' '=' '{' <lista_simbolos> '}' ';'

<seccion_inicial> ::= 'I' '=' '{' IDENTIFICADOR '}' ';'

<seccion_aceptacion> ::= 'A' '=' '{' <lista_estados> '}' ';'

<seccion_transiciones_afd> ::= 'Transiciones' ':' <lista_transiciones_afd>
```

### Definición de AP

```bnf
<definicion_ap> ::= '<AP' 'Nombre' '=' CADENA '>'
                    <seccion_estados>
                    <seccion_alfabeto>
                    <seccion_pila>
                    <seccion_inicial>
                    <seccion_aceptacion>
                    <seccion_transiciones_ap>
                    '</AP>'

<seccion_pila> ::= 'P' '=' '{' <lista_simbolos> '}' ';'

<seccion_transiciones_ap> ::= 'Transiciones' ':' <lista_transiciones_ap>
```

### Listas de Elementos

```bnf
<lista_estados> ::= <lista_estados> ',' IDENTIFICADOR
                  | IDENTIFICADOR

<lista_simbolos> ::= <lista_simbolos> ',' CARACTER
                   | CARACTER
```

### Transiciones de AFD

```bnf
<lista_transiciones_afd> ::= <lista_transiciones_afd> <transicion_afd> ';'
                           | <transicion_afd> ';'

<transicion_afd> ::= <conjunto_transiciones_afd>

<conjunto_transiciones_afd> ::= <conjunto_transiciones_afd> '|' <transicion_simple_afd>
                              | <transicion_simple_afd>

<transicion_simple_afd> ::= IDENTIFICADOR '->' CARACTER ',' IDENTIFICADOR
```

### Transiciones de AP

```bnf
<lista_transiciones_ap> ::= <lista_transiciones_ap> <transicion_ap> ';'
                          | <transicion_ap> ';'

<transicion_ap> ::= <conjunto_transiciones_ap>

<conjunto_transiciones_ap> ::= <conjunto_transiciones_ap> '|' <transicion_simple_ap>
                             | <transicion_simple_ap>

<transicion_simple_ap> ::= IDENTIFICADOR '(' CARACTER ')' '->' '(' CARACTER ')' ',' IDENTIFICADOR ':' '(' CARACTER ')'
```

### Llamadas a Funciones

```bnf
<llamada_funcion> ::= <funcion_ver_automatas>
                    | <funcion_describir>
                    | <funcion_validar>

<funcion_ver_automatas> ::= 'verAutomatas' '(' ')' ';'

<funcion_describir> ::= 'desc' '(' IDENTIFICADOR ')' ';'

<funcion_validar> ::= IDENTIFICADOR '(' CADENA ')' ';'
```

### Comentarios

```bnf
<comentario> ::= <comentario_linea>
               | <comentario_bloque>

<comentario_linea> ::= '//' <contenido_hasta_fin_linea>

<comentario_bloque> ::= '/*' <contenido_hasta_cierre> '*/'
```

## Precedencia y Asociatividad

```
Operador    Precedencia    Asociatividad
|           1              Izquierda
->          2              Derecha
,           3              Izquierda
```

## Reglas Semánticas

### Validaciones de AFD

1. **Estados únicos**: Todos los estados en N deben ser únicos
2. **Estado inicial válido**: El estado inicial debe pertenecer a N
3. **Estados de aceptación válidos**: Todos los estados en A deben pertenecer a N
4. **Símbolos de transición válidos**: Todos los símbolos en transiciones deben pertenecer a T
5. **Determinismo**: No puede haber múltiples transiciones desde un estado con el mismo símbolo
6. **Estados de transición válidos**: Estados origen y destino deben pertenecer a N

### Validaciones de AP

1. **Cumple validaciones de AFD** (excepto determinismo)
2. **Símbolos de pila válidos**: Símbolos extraídos e insertados deben pertenecer a P
3. **Transiciones bien formadas**: Formato correcto para entrada, extracción e inserción

### Validaciones Generales

1. **Nombres únicos**: Cada autómata debe tener un nombre único
2. **Referencias válidas**: Las funciones solo pueden referenciar autómatas definidos
3. **Cadenas válidas**: Las cadenas de prueba solo pueden contener símbolos del alfabeto

## Ejemplos de Derivación

### Derivación para AFD Simple

```
Entrada: <AFD Nombre="Simple"> N = {q0}; T = {'a'}; I = {q0}; A = {q0}; Transiciones: q0 -> 'a', q0; </AFD>

Derivación:
<programa>
├── <lista_elementos>
    ├── <elemento>
        ├── <definicion_afd>
            ├── '<AFD' 'Nombre' '=' "Simple" '>'
            ├── <seccion_estados>
            │   ├── 'N' '=' '{' <lista_estados> '}' ';'
            │   └── <lista_estados> → q0
            ├── <seccion_alfabeto>
            │   ├── 'T' '=' '{' <lista_simbolos> '}' ';'
            │   └── <lista_simbolos> → 'a'
            ├── <seccion_inicial>
            │   └── 'I' '=' '{' q0 '}' ';'
            ├── <seccion_aceptacion>
            │   └── 'A' '=' '{' q0 '}' ';'
            ├── <seccion_transiciones_afd>
            │   ├── 'Transiciones' ':'
            │   └── <lista_transiciones_afd>
            │       └── <transicion_afd> → q0 '->' 'a' ',' q0 ';'
            └── '</AFD>'
```

### Derivación para Función

```
Entrada: verAutomatas();

Derivación:
<programa>
├── <lista_elementos>
    ├── <elemento>
        ├── <llamada_funcion>
            └── <funcion_ver_automatas>
                └── 'verAutomatas' '(' ')' ';'
```

## Notas de Implementación

### Manejo de Errores

1. **Errores de sintaxis**: Se reportan con línea y columna
2. **Recuperación de errores**: Se implementa en nivel de elementos
3. **Errores semánticos**: Se validan durante la construcción del AST

### Características Especiales

1. **Comentarios**: Se ignoran durante el análisis sintáctico
2. **Espacios en blanco**: Se consideran separadores de tokens
3. **Sensibilidad a mayúsculas**: El lenguaje es sensible a mayúsculas y minúsculas
4. **Símbolo lambda**: Se representa con '$' en lugar de λ

### Limitaciones

1. **Identificadores**: Deben seguir convenciones de Java
2. **Caracteres**: Solo se permiten caracteres ASCII imprimibles
3. **Cadenas**: No admiten caracteres de escape complejos
4. **Números**: No se admiten literales numéricos directamente

Esta gramática proporciona la base formal para el análisis sintáctico del lenguaje AutómataLab y sirve como referencia para futuras extensiones o modificaciones del lenguaje.