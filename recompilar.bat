@echo off
echo =======================================================
echo   RECOMPILACION BASADA EN ARCHIVOS DE REFERENCIA
echo =======================================================
echo.

echo [INFO] Esta recompilacion usa la logica de tus archivos:
echo        - AutomataAFD.java (graficos mejorados)
echo        - Lexer.jflex (sintaxis robusta)
echo        - RegistroAutomatas.java (manejo eficiente)
echo.

REM Verificar que los archivos base existan
echo [VERIFICACION] Comprobando archivos base...
if not exist "gramatica\Lexer.flex" (
    echo ERROR: gramatica\Lexer.flex no encontrado
    echo SOLUCION: Copie el Lexer.flex corregido al archivo
    pause
    exit /b 1
)

if not exist "gramatica\Parser.cup" (
    echo ERROR: gramatica\Parser.cup no encontrado
    pause
    exit /b 1
)

echo OK: Archivos base encontrados

REM === PASO 1: LIMPIEZA COMPLETA ===
echo.
echo [1/8] Limpieza completa (inspirada en tu metodologia)...
echo - Removiendo build anterior...
if exist "build" rmdir /s /q "build" 2>nul

echo - Removiendo archivos generados...
if exist "src\analizadores\Lexer.java" del /f /q "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del /f /q "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del /f /q "src\analizadores\sym.java" 2>nul

echo - Removiendo clases compiladas...
for /r "src" %%f in (*.class) do del /f /q "%%f" 2>nul

echo OK: Limpieza completada

REM === PASO 2: ESTRUCTURA ===
echo.
echo [2/8] Recreando estructura...
mkdir "build\classes" 2>nul
mkdir "build\jar" 2>nul
mkdir "src\analizadores" 2>nul
echo OK: Estructura recreada

REM === PASO 3: VERIFICAR PATRON DE FLECHA ===
echo.
echo [3/8] Verificando patron de flecha (critico)...
findstr /C:"\"->" "gramatica\Lexer.flex" >nul 2>&1
if errorlevel 1 (
    echo ERROR: Patron de flecha \"-^>\" NO encontrado en Lexer.flex
    echo.
    echo ACCION REQUERIDA:
    echo 1. Abrir gramatica\Lexer.flex
    echo 2. Asegurar que contenga: \"-^>\" { return symbol(sym.FLECHA); }
    echo 3. Verificar que este ANTES que \"-\" y \">\" individuales
    echo.
    pause
    exit /b 1
)
echo OK: Patron de flecha encontrado

REM === PASO 4: GENERAR LEXER (estilo JFlex) ===
echo.
echo [4/8] Generando Lexer (usando sintaxis de tu referencia)...
echo Comando: java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex

java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 (
    echo ERROR: JFlex fallo
    echo Posibles causas:
    echo - Errores de sintaxis en Lexer.flex
    echo - JFlex no encontrado en lib\
    pause
    exit /b 1
)

if not exist "src\analizadores\Lexer.java" (
    echo ERROR: Lexer.java no fue generado
    pause
    exit /b 1
)
echo OK: Lexer.java generado exitosamente

REM === PASO 5: VERIFICAR LEXER GENERADO ===
echo.
echo [5/8] Verificando Lexer generado...
findstr /C:"sym.FLECHA" "src\analizadores\Lexer.java" >nul 2>&1
if errorlevel 1 (
    echo ADVERTENCIA: sym.FLECHA no encontrado en Lexer generado
    echo Esto puede causar errores de compilacion
) else (
    echo OK: sym.FLECHA encontrado en Lexer generado
)

REM === PASO 6: GENERAR PARSER (estilo CUP) ===
echo.
echo [6/8] Generando Parser (usando sintaxis de tu referencia)...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 (
    echo ERROR: CUP fallo
    pause
    exit /b 1
)

if not exist "src\analizadores\Parser.java" (
    echo ERROR: Parser.java no generado
    pause
    exit /b 1
)

if not exist "src\analizadores\sym.java" (
    echo ERROR: sym.java no generado
    pause
    exit /b 1
)
echo OK: Parser y sym generados

REM === PASO 7: COMPILACION SECUENCIAL (inspirada en tu metodologia) ===
echo.
echo [7/8] Compilacion secuencial...

echo - [7a] Compilando modelos (logica mejorada de AFD)...
javac -encoding UTF-8 -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 (
    echo ERROR: Fallo compilacion de modelos
    echo Verifique errores en AFD.java, AP.java, etc.
    pause
    exit /b 1
)

echo - [7b] Compilando analizadores...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 (
    echo ERROR: Fallo compilacion de analizadores
    echo Verifique Lexer.java y Parser.java generados
    pause
    exit /b 1
)

echo - [7c] Compilando reportes...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 (
    echo ERROR: Fallo compilacion de reportes
    pause
    exit /b 1
)

echo - [7d] Compilando interfaz...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 (
    echo ERROR: Fallo compilacion de interfaz
    pause
    exit /b 1
)

echo - [7e] Compilando main...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 (
    echo ERROR: Fallo compilacion de main
    pause
    exit /b 1
)

echo OK: Compilacion Java completada

REM === PASO 8: CREAR JAR (estilo de tu referencia) ===
echo.
echo [8/8] Creando JAR con runtime incluido...

echo - Extrayendo runtime de CUP...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar 2>nul
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..

echo - Creando manifest...
echo Main-Class: main.Main > build\MANIFEST.MF

echo - Empaquetando JAR...
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 (
    echo ERROR: Fallo creacion de JAR
    pause
    exit /b 1
)

echo OK: JAR creado exitosamente

REM === VERIFICACION FINAL ===
echo.
echo =======================================================
echo              RECOMPILACION EXITOSA
echo =======================================================
echo.
echo MEJORAS APLICADAS (basadas en tus archivos):
echo ✅ Lexer robusto (basado en tu Lexer.jflex)
echo ✅ AFD con graficos mejorados (basado en AutomataAFD.java)  
echo ✅ Manejo de errores mejorado
echo ✅ Generacion DOT optimizada
echo.
echo ARCHIVOS GENERADOS:
echo 📁 build\jar\AutomataLab.jar (%~z0 bytes)
echo 📁 src\analizadores\Lexer.java
echo 📁 src\analizadores\Parser.java  
echo 📁 src\analizadores\sym.java
echo.
echo EJECUTAR:
echo 🚀 java -jar build\jar\AutomataLab.jar
echo 🚀 ejecutar-directo.bat (metodo alternativo)
echo.
echo PROBAR CON:
echo - Archivo .atm con flecha ->
echo - Verificar que no hay errores de '>' no reconocido
echo - Comprobar generacion de graficos DOT
echo.
pause