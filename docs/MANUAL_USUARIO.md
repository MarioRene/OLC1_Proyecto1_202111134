# Manual de Usuario - AutómataLab

## Introducción

AutómataLab es una herramienta de software diseñada para la definición, validación y visualización de autómatas finitos deterministas (AFD) y autómatas de pila (AP). La aplicación permite a los usuarios crear autómatas mediante un lenguaje de definición específico y realizar pruebas de validación de cadenas.

## Requisitos del Sistema

### Software Requerido
- Java Runtime Environment (JRE) 8 o superior
- Sistema operativo: Windows, Linux o macOS
- Memoria RAM mínima: 512 MB
- Espacio en disco: 50 MB

### Software Opcional
- Graphviz (para generación de gráficos de autómatas)

## Instalación

1. Descargar el archivo `AutomataLab.jar` 
2. Colocar el archivo en el directorio deseado
3. Para ejecutar desde línea de comandos:
   ```bash
   java -jar AutomataLab.jar
   ```
4. Alternativamente, hacer doble clic en el archivo JAR si el sistema está configurado correctamente

## Interfaz de Usuario

### Ventana Principal

La interfaz principal está dividida en dos secciones:

1. **Panel Superior - Editor de Código**
   - Área de texto para escribir definiciones de autómatas
   - Numeración de líneas automática
   - Barra de estado con posición del cursor

2. **Panel Inferior - Consola de Salida**
   - Muestra resultados de ejecución
   - Mensajes de error y confirmación
   - Salida de funciones del lenguaje

### Barra de Menú

#### Menú Archivo
- **Nuevo (Ctrl+N)**: Crea un nuevo archivo
- **Abrir (Ctrl+O)**: Abre un archivo .atm existente
- **Guardar (Ctrl+S)**: Guarda el archivo actual
- **Guardar como (Ctrl+Shift+S)**: Guarda con nuevo nombre
- **Salir (Alt+F4)**: Cierra la aplicación

#### Menú Editar
- **Cortar (Ctrl+X)**: Corta texto seleccionado
- **Copiar (Ctrl+C)**: Copia texto seleccionado
- **Pegar (Ctrl+V)**: Pega texto del portapapeles
- **Buscar (Ctrl+F)**: Busca texto en el editor

#### Menú Ejecutar
- **Ejecutar (F5)**: Analiza y ejecuta el código
- **Limpiar Consola (Ctrl+L)**: Limpia la consola de salida

#### Menú Reportes
- **Tokens**: Muestra tabla de tokens reconocidos
- **Errores**: Lista errores encontrados en el análisis
- **Autómatas**: Muestra autómatas definidos
- **Generar Gráfico**: Crea representación visual de autómatas

#### Menú Ayuda
- **Manual de Usuario**: Abre este manual
- **Acerca de**: Información sobre la aplicación

## Lenguaje de Definición

### Estructura General

Los archivos de AutómataLab tienen extensión `.atm` y contienen definiciones de autómatas seguidas de comandos de ejecución.

### Comentarios

```
// Comentario de una línea

/* Comentario
   multilínea */
```

### Definición de AFD

Un Autómata Finito Determinista se define con la siguiente estructura:

```
<AFD Nombre="nombre_del_automata">
  N = {estado1, estado2, ...};       // Conjunto de estados
  T = {'a', 'b', ...};               // Alfabeto de entrada
  I = {estado_inicial};              // Estado inicial
  A = {estado_final1, ...};          // Estados de aceptación
  
  Transiciones:
    estado1 -> 'simbolo', estado2;
    estado2 -> 'simbolo', estado1;
    ...
</AFD>
```

#### Ejemplo de AFD

```
<AFD Nombre="AFD_Par">
  N = {q0, q1};
  T = {'0', '1'};
  I = {q0};
  A = {q0};
  
  Transiciones:
    q0 -> '0', q1;
    q0 -> '1', q0;
    q1 -> '0', q0;
    q1 -> '1', q1;
</AFD>
```

### Definición de AP

Un Autómata de Pila se define con estructura similar, agregando símbolos de pila:

```
<AP Nombre="nombre_del_automata">
  N = {estado1, estado2, ...};       // Conjunto de estados
  T = {'a', 'b', ...};               // Alfabeto de entrada
  P = {'A', 'B', 'Z'};               // Símbolos de pila
  I = {estado_inicial};              // Estado inicial
  A = {estado_final};                // Estados de aceptación
  
  Transiciones:
    estado1 ('a') -> ('Z'), estado2 : ('A');
    ...
</AP>
```

#### Formato de Transiciones en AP

```
estado_origen (símbolo_entrada) -> (símbolo_extrae_pila), estado_destino : (símbolo_inserta_pila);
```

- `$` representa la cadena vacía (lambda)
- Las transiciones pueden extraer e insertar símbolos de la pila

#### Ejemplo de AP

```
<AP Nombre="AP_AnBn">
  N = {q0, q1, q2};
  T = {'a', 'b'};
  P = {'A', 'Z'};
  I = {q0};
  A = {q2};
  
  Transiciones:
    q0 ('a') -> ('Z'), q1 : ('A');
    q1 ('a') -> ('A'), q1 : ('A');
    q1 ('b') -> ('A'), q2 : ('$');
    q2 ('b') -> ('A'), q2 : ('$');
</AP>
```

### Funciones del Lenguaje

#### Listar Autómatas

```
verAutomatas();
```

Muestra lista de todos los autómatas definidos con su tipo.

#### Descripción de Autómata

```
desc(nombre_automata);
```

Muestra información detallada del autómata especificado.

#### Validación de Cadenas

```
nombre_automata("cadena_a_validar");
```

Valida si la cadena es aceptada por el autómata.

### Ejemplo Completo

```
// Definición de AFD para números pares de ceros
<AFD Nombre="AFD_Par">
  N = {q0, q1};
  T = {'0', '1'};
  I = {q0};
  A = {q0};
  
  Transiciones:
    q0 -> '0', q1;
    q0 -> '1', q0;
    q1 -> '0', q0;
    q1 -> '1', q1;
</AFD>

// Definición de AP para a^n b^n
<AP Nombre="AP_AnBn">
  N = {q0, q1, q2};
  T = {'a', 'b'};
  P = {'A', 'Z'};
  I = {q0};
  A = {q2};
  
  Transiciones:
    q0 ('a') -> ('Z'), q1 : ('A');
    q1 ('a') -> ('A'), q1 : ('A');
    q1 ('b') -> ('A'), q2 : ('$');
    q2 ('b') -> ('A'), q2 : ('$');
</AP>

// Comandos de ejecución
verAutomatas();
desc(AFD_Par);
AFD_Par("1010");
AFD_Par("100");
AP_AnBn("aabb");
```

## Uso de la Aplicación

### Crear un Nuevo Proyecto

1. Abrir AutómataLab
2. Seleccionar **Archivo > Nuevo** o presionar `Ctrl+N`
3. Escribir las definiciones de autómatas en el editor
4. Guardar el archivo con extensión `.atm`

### Ejecutar Análisis

1. Escribir o cargar código en el editor
2. Presionar **F5** o seleccionar **Ejecutar > Ejecutar**
3. Observar resultados en la consola
4. Revisar errores si los hay

### Generar Reportes

#### Reporte de Tokens
1. Ejecutar análisis del código
2. Seleccionar **Reportes > Tokens**
3. Revisar tabla de tokens reconocidos

#### Reporte de Errores
1. Si hay errores en el análisis
2. Seleccionar **Reportes > Errores**
3. Revisar descripción detallada de errores

#### Gráfico de Autómatas
1. Definir al menos un AFD
2. Seleccionar **Reportes > Generar Gráfico**
3. Elegir autómata y formato de salida
4. Especificar ubicación del archivo

## Casos de Uso Comunes

### Validar Expresiones Regulares

Para validar patrones simples como números binarios:

```
<AFD Nombre="Binario">
  N = {inicio, valido};
  T = {'0', '1'};
  I = {inicio};
  A = {valido};
  
  Transiciones:
    inicio -> '0', valido;
    inicio -> '1', valido;
    valido -> '0', valido;
    valido -> '1', valido;
</AFD>

Binario("101010");
Binario("010011");
```

### Reconocer Lenguajes Context-Free

Para validar paréntesis balanceados:

```
<AP Nombre="Parentesis">
  N = {q0, q1};
  T = {'(', ')'};
  P = {'P', 'Z'};
  I = {q0};
  A = {q1};
  
  Transiciones:
    q0 ('(') -> ('Z'), q0 : ('P');
    q0 ('(') -> ('P'), q0 : ('P');
    q0 (')') -> ('P'), q0 : ('$');
    q0 ('$') -> ('Z'), q1 : ('$');
</AP>

Parentesis("(())");
Parentesis("((())");
```

## Solución de Problemas

### Errores Comunes

#### "Estado no definido"
- Verificar que todos los estados en transiciones estén declarados en N
- Revisar ortografía de nombres de estados

#### "Símbolo no pertenece al alfabeto"
- Asegurar que todos los símbolos en transiciones estén en T
- Verificar formato de caracteres (usar comillas simples)

#### "AFD no determinista"
- Revisar que no haya múltiples transiciones desde el mismo estado con el mismo símbolo
- Cada estado debe tener máximo una transición por símbolo

#### "Error léxico: Carácter no válido"
- Verificar sintaxis correcta
- Revisar que las comillas estén balanceadas
- Asegurar que los comentarios estén cerrados

### Limitaciones Conocidas

1. **Nombres de estados**: Deben ser identificadores válidos (sin espacios ni caracteres especiales)
2. **Símbolos del alfabeto**: Solo caracteres individuales
3. **Tamaño de autómatas**: Limitado por memoria disponible
4. **Generación de gráficos**: Requiere Graphviz instalado

### Obtener Ayuda

Para problemas técnicos o consultas:
1. Consultar este manual
2. Revisar mensajes de error en la consola
3. Verificar sintaxis con ejemplos proporcionados
4. Contactar al desarrollador si persisten problemas

## Consejos de Uso

### Mejores Prácticas

1. **Nombres descriptivos**: Usar nombres significativos para estados y autómatas
2. **Comentarios**: Documentar el propósito de cada autómata
3. **Pruebas graduales**: Comenzar con autómatas simples
4. **Validación incremental**: Probar con cadenas conocidas antes de casos complejos

### Optimización

1. **Minimizar estados**: Usar la menor cantidad de estados posible
2. **Organizar transiciones**: Agrupar transiciones relacionadas
3. **Documentar casos especiales**: Explicar transiciones complejas

Este manual proporciona la información esencial para utilizar AutómataLab efectivamente. Para información técnica detallada, consultar el Manual Técnico.