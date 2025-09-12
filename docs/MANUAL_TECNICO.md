# Manual Técnico - AutómataLab

## Información del Proyecto

**Nombre:** AutómataLab  
**Versión:** 1.0  
**Curso:** Organización de Lenguajes y Compiladores 1  
**Semestre:** Primer Semestre 2025  
**Lenguaje:** Java 8+  
**Herramientas:** JFlex, CUP, Swing  

## Arquitectura del Sistema

### Estructura de Directorios

```
src/
├── analizadores/          # Analizadores léxico y sintáctico
│   ├── Lexer.java        # Analizador léxico generado por JFlex
│   ├── Parser.java       # Analizador sintáctico generado por CUP
│   └── sym.java          # Símbolos del analizador
├── interfaz/             # Interfaz gráfica de usuario
│   ├── MainWindow.java   # Ventana principal
│   ├── EditorPanel.java  # Panel del editor de código
│   └── ConsolePanel.java # Panel de consola
├── modelos/              # Modelos de datos
│   ├── AFD.java          # Autómata Finito Determinista
│   ├── AP.java           # Autómata de Pila
│   ├── Token.java        # Representación de tokens
│   └── Transicion.java   # Transiciones de autómatas
├── reportes/             # Generación de reportes
│   ├── Graphviz.java     # Generación de gráficos
│   ├── ReporteTokens.java# Reporte de análisis léxico
│   └── ReporteErrores.java# Reporte de errores
└── main/
    └── Main.java         # Clase principal
```

### Gramática del Lenguaje

#### Analizador Léxico (JFlex)

El analizador léxico reconoce los siguientes tokens:

- **Palabras reservadas:** `<AFD>`, `</AFD>`, `<AP>`, `</AP>`, `N`, `T`, `P`, `I`, `A`, `Transiciones`, `verAutomatas`, `desc`
- **Símbolos:** `=`, `{`, `}`, `(`, `)`, `->`, `|`, `;`, `:`, `,`, `$`
- **Literales:** Identificadores, cadenas, caracteres
- **Comentarios:** Línea (`//`) y multilínea (`/* */`)

#### Analizador Sintáctico (CUP)

La gramática del lenguaje está definida en formato BNF:

```bnf
programa ::= lista_elementos

lista_elementos ::= lista_elementos elemento 
                  | elemento 
                  | ε

elemento ::= definicion_afd 
           | definicion_ap
           | llamada_funcion

definicion_afd ::= '<AFD' 'Nombre' '=' CADENA '>'
                   'N' '=' '{' lista_estados '}' ';'
                   'T' '=' '{' lista_simbolos '}' ';'
                   'I' '=' '{' IDENTIFICADOR '}' ';'
                   'A' '=' '{' lista_estados '}' ';'
                   'Transiciones' ':' lista_transiciones_afd
                   '</AFD>'

definicion_ap ::= '<AP' 'Nombre' '=' CADENA '>'
                  'N' '=' '{' lista_estados '}' ';'
                  'T' '=' '{' lista_simbolos '}' ';'
                  'P' '=' '{' lista_simbolos '}' ';'
                  'I' '=' '{' IDENTIFICADOR '}' ';'
                  'A' '=' '{' lista_estados '}' ';'
                  'Transiciones' ':' lista_transiciones_ap
                  '</AP>'
```

## Clases Principales

### 1. Modelos de Datos

#### AFD.java
Representa un Autómata Finito Determinista.

**Atributos principales:**
- `String nombre`: Nombre del autómata
- `Set<String> estados`: Conjunto de estados
- `Set<Character> alfabeto`: Alfabeto de entrada
- `String estadoInicial`: Estado inicial
- `Set<String> estadosAceptacion`: Estados de aceptación
- `Map<String, Map<Character, String>> transiciones`: Función de transición

**Métodos principales:**
- `boolean validarCadena(String cadena)`: Valida una cadena de entrada
- `void descripcion()`: Muestra la descripción del autómata
- `String generarDot()`: Genera código DOT para Graphviz

#### AP.java
Representa un Autómata de Pila.

**Atributos principales:**
- Similar a AFD con adición de `Set<Character> simbolosPila`
- `List<TransicionAP> transiciones`: Transiciones específicas para autómata de pila

#### Token.java
Representa un token reconocido por el analizador léxico.

**Atributos:**
- `int numero`: Número secuencial del token
- `String lexema`: Cadena reconocida
- `String tipo`: Tipo de token
- `int linea`, `int columna`: Posición en el código fuente

### 2. Analizadores

#### Lexer.java (Generado por JFlex)
Analizador léxico que tokeniza la entrada según las reglas definidas.

#### Parser.java (Generado por CUP)
Analizador sintáctico que construye la representación interna de los autómatas.

**Variables estáticas importantes:**
- `Map<String, Object> automatas`: Almacena los autómatas definidos
- `List<String> errores`: Lista de errores encontrados

### 3. Interfaz Gráfica

#### MainWindow.java
Ventana principal de la aplicación.

**Componentes principales:**
- `EditorPanel editorPanel`: Editor de código
- `ConsolePanel consolePanel`: Consola de salida
- `JMenuBar`: Barra de menú con funcionalidades

**Funcionalidades:**
- Gestión de archivos (nuevo, abrir, guardar)
- Ejecución del análisis
- Generación de reportes
- Manejo de eventos

#### EditorPanel.java
Panel de edición con características avanzadas.

**Características:**
- Numeración de líneas
- Resaltado de sintaxis básico
- Barra de estado con posición del cursor
- Funcionalidades de búsqueda

### 4. Reportes

#### Graphviz.java
Genera representaciones gráficas de los autómatas usando Graphviz.

**Métodos principales:**
- `String generarDotAFD(AFD afd)`: Genera código DOT
- `boolean generarImagenAFD(AFD afd, String formato, String archivo)`: Crea archivo de imagen

#### ReporteTokens.java
Genera reportes del análisis léxico.

#### ReporteErrores.java
Maneja y presenta los errores encontrados durante el análisis.

## Dependencias

### Librerías Requeridas

1. **JFlex** (`lib/iflex-full-1.7.0.jar`)
   - Generador de analizadores léxicos
   - Versión: 1.7.0

2. **CUP** (`lib/java-cup-11b.jar`, `lib/java-cup-11b-runtime.jar`)
   - Generador de analizadores sintácticos
   - Versión: 11b

3. **Swing** (Incluido en Java SE)
   - Framework para interfaz gráfica

### Software Externo

1. **Graphviz** (Opcional)
   - Requerido para generar gráficos de autómatas
   - Comando `dot` debe estar disponible en PATH

## Proceso de Compilación

### Usando Apache Ant

El proyecto incluye un archivo `build.xml` para automatizar la compilación:

```bash
# Limpiar archivos compilados
ant clean

# Generar analizadores y compilar
ant compile

# Crear archivo JAR ejecutable
ant jar

# Ejecutar la aplicación
ant run
```

### Pasos Manuales

1. **Generar analizador léxico:**
```bash
java -jar lib/iflex-full-1.7.0.jar -d src/analizadores gramatica/Lexer.flex
```

2. **Generar analizador sintáctico:**
```bash
java -jar lib/java-cup-11b.jar -destdir src/analizadores -parser Parser gramatica/Parser.cup
```

3. **Compilar código Java:**
```bash
javac -cp "lib/*" -d build/classes src/**/*.java
```

4. **Crear JAR ejecutable:**
```bash
jar cvfm AutomataLab.jar MANIFEST.MF -C build/classes .
```

## Algoritmos Implementados

### Validación de Cadenas en AFD

```java
public boolean validarCadena(String cadena) {
    String estadoActual = estadoInicial;
    
    for (char simbolo : cadena.toCharArray()) {
        if (!alfabeto.contains(simbolo)) return false;
        
        Map<Character, String> transEstado = transiciones.get(estadoActual);
        if (!transEstado.containsKey(simbolo)) return false;
        
        estadoActual = transEstado.get(simbolo);
    }
    
    return estadosAceptacion.contains(estadoActual);
}
```

### Validación de Cadenas en AP

Utiliza recursión con backtracking para explorar todas las configuraciones posibles del autómata de pila.

## Manejo de Errores

### Errores Léxicos
- Caracteres no reconocidos
- Cadenas mal formadas
- Comentarios sin cerrar

### Errores Sintácticos
- Estructuras incompletas
- Tokens inesperados
- Reglas no respetadas

### Errores Semánticos
- Estados no definidos en transiciones
- Símbolos no pertenecientes al alfabeto
- AFD no determinista (múltiples transiciones)

## Extensibilidad

### Agregar Nuevos Tipos de Autómatas

1. Crear nueva clase que extienda la clase base
2. Definir nuevas reglas en la gramática
3. Implementar analizador específico
4. Agregar soporte en la interfaz

### Modificar la Gramática

1. Actualizar archivo `Lexer.flex` para nuevos tokens
2. Modificar `Parser.cup` para nuevas reglas
3. Actualizar archivo `sym.java` con nuevos símbolos
4. Regenerar analizadores

## Problemas Conocidos

1. **Dependencia de Graphviz**: La generación de gráficos requiere instalación externa
2. **Manejo de memoria**: Autómatas muy grandes pueden causar problemas de rendimiento
3. **Validación limitada**: Algunas validaciones semánticas podrían ser más estrictas

## Mantenimiento

### Actualizaciones Recomendadas

1. Migrar a JavaFX para interfaz más moderna
2. Implementar sistema de plugins para extensibilidad
3. Agregar soporte para más formatos de exportación
4. Mejorar manejo de errores con recuperación

### Pruebas

Se recomienda implementar:
- Pruebas unitarias para cada modelo
- Pruebas de integración para analizadores
- Pruebas de interfaz automatizadas