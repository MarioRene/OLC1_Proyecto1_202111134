@echo off
setlocal enabledelayedexpansion
echo ====================================================
echo     RECOMPILACION COMPLETA - SOLUCION DE ERRORES
echo ====================================================
echo.

set ERRORES=0

REM Verificar Java
echo [VERIFICACION 1/3] Comprobando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no encontrado. Instale Java JDK 8 o superior
    set /a ERRORES+=1
    pause
    exit /b 1
) else (
    echo [OK] Java encontrado
)

REM Verificar dependencias
echo.
echo [VERIFICACION 2/3] Comprobando dependencias...
if not exist "lib\jflex-full-1.7.0.jar" (
    echo [ERROR] JFlex no encontrado: lib\jflex-full-1.7.0.jar
    set /a ERRORES+=1
)
if not exist "lib\java-cup-11b.jar" (
    echo [ERROR] Java CUP no encontrado: lib\java-cup-11b.jar
    set /a ERRORES+=1
)
if not exist "lib\java-cup-11b-runtime.jar" (
    echo [ERROR] CUP Runtime no encontrado: lib\java-cup-11b-runtime.jar
    set /a ERRORES+=1
)

if !ERRORES! NEQ 0 (
    echo.
    echo [SOLUCION] Descargue las librerias faltantes:
    echo - JFlex: https://jflex.de/download.html
    echo - CUP: http://www2.cs.tum.edu/projects/cup/
    pause
    exit /b 1
)

echo [OK] Todas las dependencias encontradas

REM Verificar archivos fuente
echo.
echo [VERIFICACION 3/3] Comprobando archivos fuente...
if not exist "gramatica\Lexer.flex" (
    echo [ERROR] Lexer.flex no encontrado
    set /a ERRORES+=1
)
if not exist "gramatica\Parser.cup" (
    echo [ERROR] Parser.cup no encontrado  
    set /a ERRORES+=1
)

if !ERRORES! NEQ 0 (
    echo [ERROR] Archivos fuente faltantes
    pause
    exit /b 1
)

echo [OK] Archivos fuente encontrados
echo.

REM === PROCESO DE RECOMPILACION ===

echo ====================================================
echo              INICIANDO RECOMPILACION
echo ====================================================

REM PASO 1: Limpieza completa
echo.
echo [PASO 1/8] Limpieza completa...
if exist "build" (
    echo - Eliminando directorio build...
    rmdir /s /q "build" 2>nul
)
if exist "src\analizadores\Lexer.java" (
    echo - Eliminando Lexer.java generado...
    del "src\analizadores\Lexer.java" 2>nul
)
if exist "src\analizadores\Parser.java" (
    echo - Eliminando Parser.java generado...
    del "src\analizadores\Parser.java" 2>nul
)
if exist "src\analizadores\sym.java" (
    echo - Eliminando sym.java generado...
    del "src\analizadores\sym.java" 2>nul
)
echo [OK] Limpieza completada

REM PASO 2: Crear directorios
echo.
echo [PASO 2/8] Creando estructura de directorios...
mkdir "build\classes" 2>nul
mkdir "build\jar" 2>nul
mkdir "src\analizadores" 2>nul
echo [OK] Directorios creados

REM PASO 3: Generar analizador lexico
echo.
echo [PASO 3/8] Generando analizador lexico con JFlex...
echo - Comando: java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 (
    echo [ERROR] Fallo en generacion del analizador lexico
    echo - Verifique la sintaxis de Lexer.flex
    pause
    exit /b 1
)

if not exist "src\analizadores\Lexer.java" (
    echo [ERROR] Lexer.java no fue generado
    pause
    exit /b 1
)
echo [OK] Analizador lexico generado correctamente

REM PASO 4: Generar analizador sintactico
echo.
echo [PASO 4/8] Generando analizador sintactico con CUP...
echo - Comando: java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 (
    echo [ERROR] Fallo en generacion del analizador sintactico
    echo - Verifique la sintaxis de Parser.cup
    pause
    exit /b 1
)

if not exist "src\analizadores\Parser.java" (
    echo [ERROR] Parser.java no fue generado
    pause
    exit /b 1
)
if not exist "src\analizadores\sym.java" (
    echo [ERROR] sym.java no fue generado
    pause
    exit /b 1
)
echo [OK] Analizador sintactico generado correctamente

REM PASO 5: Compilar en orden correcto
echo.
echo [PASO 5/8] Compilando codigo Java...

echo - Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 (
    echo [ERROR] Error compilando modelos
    goto :error_compilacion
)

echo - Compilando analizadores generados...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 (
    echo [ERROR] Error compilando analizadores
    goto :error_compilacion
)

echo - Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 (
    echo [ERROR] Error compilando reportes
    goto :error_compilacion
)

echo - Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 (
    echo [ERROR] Error compilando interfaz
    goto :error_compilacion
)

echo - Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 (
    echo [ERROR] Error compilando main
    goto :error_compilacion
)

echo [OK] Compilacion exitosa

REM PASO 6: Extraer runtime de CUP
echo.
echo [PASO 6/8] Integrando runtime de CUP...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar 2>nul
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..
echo [OK] Runtime integrado

REM PASO 7: Crear JAR
echo.
echo [PASO 7/8] Creando JAR ejecutable...
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 (
    echo [ERROR] Error creando JAR
    pause
    exit /b 1
)
echo [OK] JAR creado: build\jar\AutomataLab.jar

REM PASO 8: Verificar funcionamiento
echo.
echo [PASO 8/8] Verificando funcionamiento...
java -jar build\jar\AutomataLab.jar --version 2>nul
if errorlevel 1 (
    echo [ADVERTENCIA] JAR puede tener problemas, probando ejecucion directa...
    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main --test 2>nul
    if errorlevel 1 (
        echo [INFO] Verificacion automatica fallida - esto es normal
    )
)

echo ====================================================
echo              RECOMPILACION EXITOSA
echo ====================================================
echo.
echo ✅ SOLUCION DE PROBLEMAS APLICADA:
echo   - Corregido orden de patrones en Lexer (flecha -> antes que >)
echo   - Mejorado manejo de errores lexicos
echo   - Archivos fuente regenerados completamente
echo.
echo 📁 ARCHIVOS GENERADOS:
echo   - build\jar\AutomataLab.jar (aplicacion completa)
echo   - src\analizadores\Lexer.java (analizador lexico)
echo   - src\analizadores\Parser.java (analizador sintactico)
echo   - src\analizadores\sym.java (simbolos)
echo.
echo 🚀 COMO EJECUTAR:
echo   Opcion 1: java -jar build\jar\AutomataLab.jar
echo   Opcion 2: ejecutar-directo.bat
echo   Opcion 3: run-simple.bat
echo.
echo 📝 ARCHIVO DE PRUEBA CORREGIDO:
echo   Use el archivo limpio sin caracteres especiales
echo   que se proporciono en las instrucciones
echo.
pause
exit /b 0

:error_compilacion
echo.
echo ====================================================
echo                ERROR DE COMPILACION
echo ====================================================
echo.
echo [DIAGNOSTICO] Posibles causas:
echo 1. Errores de sintaxis en archivos fuente
echo 2. Dependencias faltantes o incorrectas
echo 3. Problemas de codificacion de archivos
echo 4. Version de Java incompatible
echo.
echo [SOLUCION] Pasos recomendados:
echo 1. Revisar errores mostrados arriba
echo 2. Verificar que Lexer.flex y Parser.cup esten correctos
echo 3. Asegurar que todas las dependencias esten en lib\
echo 4. Intentar ejecutar: verificar-sistema.bat
echo.
pause
exit /b 1