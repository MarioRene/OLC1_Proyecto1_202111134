@echo off
echo =======================================================
echo        RECOMPILACION FORZADA - SOLUCION DE FLECHA
echo =======================================================
echo.

echo [DIAGNÓSTICO] Verificando problema específico...

REM Verificar si la flecha está en Lexer.flex
if exist "gramatica\Lexer.flex" (
    findstr /C:"\"->" "gramatica\Lexer.flex" >nul 2>&1
    if errorlevel 1 (
        echo ❌ PROBLEMA CONFIRMADO: Patrón de flecha no encontrado
        echo    💡 El archivo Lexer.flex NO tiene el patrón corregido
    ) else (
        echo ✅ Patrón de flecha encontrado en Lexer.flex
    )
) else (
    echo ❌ ERROR: gramatica\Lexer.flex no existe
    pause
    exit /b 1
)

echo.
echo [SOLUCIÓN] Aplicando recompilación forzada...

REM === PASO 1: LIMPIEZA AGRESIVA ===
echo.
echo [1/9] Limpieza agresiva completa...
echo - Eliminando build completo...
if exist "build" rmdir /s /q "build" 2>nul

echo - Eliminando archivos generados...
if exist "src\analizadores\Lexer.java" del /f /q "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del /f /q "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del /f /q "src\analizadores\sym.java" 2>nul

echo - Eliminando clases compiladas...
if exist "src\analizadores\*.class" del /f /q "src\analizadores\*.class" 2>nul

echo ✅ Limpieza completada

REM === PASO 2: RECREAR ESTRUCTURA ===
echo.
echo [2/9] Recreando estructura...
mkdir "build\classes" 2>nul
mkdir "build\jar" 2>nul
mkdir "src\analizadores" 2>nul
echo ✅ Estructura recreada

REM === PASO 3: VERIFICAR LEXER.FLEX ===
echo.
echo [3/9] Verificando Lexer.flex corregido...
findstr /C:"\"->" "gramatica\Lexer.flex" >nul 2>&1
if errorlevel 1 (
    echo ❌ CRÍTICO: Lexer.flex NO tiene el patrón de flecha corregido
    echo.
    echo 🔧 ACCIÓN REQUERIDA:
    echo 1. Reemplace el contenido de gramatica\Lexer.flex 
    echo 2. Con el código corregido proporcionado
    echo 3. Asegúrese que contenga: \"-^>\" { return symbol(sym.FLECHA); }
    echo.
    pause
    exit /b 1
)
echo ✅ Lexer.flex verificado - patrón de flecha encontrado

REM === PASO 4: GENERAR LEXER ===
echo.
echo [4/9] Generando analizador léxico (FORZADO)...
echo Comando: java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex

java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 (
    echo ❌ ERROR: Fallo en generación de Lexer
    echo.
    echo Posibles causas:
    echo - JFlex no encontrado en lib\
    echo - Errores de sintaxis en Lexer.flex
    echo - Permisos de archivo
    pause
    exit /b 1
)

if not exist "src\analizadores\Lexer.java" (
    echo ❌ CRÍTICO: Lexer.java no fue generado
    pause
    exit /b 1
)
echo ✅ Lexer.java generado exitosamente

REM === PASO 5: VERIFICAR LEXER GENERADO ===
echo.
echo [5/9] Verificando Lexer generado...
findstr /C:"FLECHA" "src\analizadores\Lexer.java" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  ADVERTENCIA: sym.FLECHA no encontrado en Lexer generado
) else (
    echo ✅ sym.FLECHA encontrado en Lexer generado
)

REM === PASO 6: GENERAR PARSER ===
echo.
echo [6/9] Generando analizador sintáctico...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 (
    echo ❌ ERROR: Fallo en generación de Parser
    pause
    exit /b 1
)

if not exist "src\analizadores\Parser.java" (
    echo ❌ ERROR: Parser.java no generado
    pause
    exit /b 1
)
echo ✅ Parser.java generado

REM === PASO 7: COMPILACIÓN SECUENCIAL ===
echo.
echo [7/9] Compilando en orden secuencial...

echo - [7a] Compilando modelos...
javac -encoding UTF-8 -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 goto :error_compilacion

echo - [7b] Compilando analizadores...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 goto :error_compilacion

echo - [7c] Compilando reportes...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 goto :error_compilacion

echo - [7d] Compilando interfaz...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 goto :error_compilacion

echo - [7e] Compilando main...
javac -encoding UTF-8 -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 goto :error_compilacion

echo ✅ Compilación exitosa

REM === PASO 8: INTEGRAR RUNTIME ===
echo.
echo [8/9] Integrando runtime de CUP...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar 2>nul
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..
echo ✅ Runtime integrado

REM === PASO 9: CREAR JAR ===
echo.
echo [9/9] Creando JAR final...
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 goto :error_jar

echo ✅ JAR creado: build\jar\AutomataLab.jar

REM === VERIFICACIÓN FINAL ===
echo.
echo =======================================================
echo              RECOMPILACIÓN COMPLETADA
echo =======================================================

echo 🔍 VERIFICACIÓN FINAL:
if exist "build\jar\AutomataLab.jar" (
    for %%F in ("build\jar\AutomataLab.jar") do echo ✅ JAR: %%~zF bytes
) else (
    echo ❌ JAR no creado
)

echo.
echo 🎯 PRUEBA DEL PROBLEMA ESPECÍFICO:
echo Use este texto de prueba en la aplicación:
echo.
echo ^<AFD Nombre="Test"^>
echo   N = {A}; T = {a}; I = {A}; A = {A};
echo   Transiciones: A -^> a, A ;
echo ^</AFD^>
echo.

echo 🚀 EJECUTAR:
echo java -jar build\jar\AutomataLab.jar
echo.
echo Si el problema persiste, el error está en Parser.cup o sym.java
pause
exit /b 0

:error_compilacion
echo.
echo ❌ ERROR DE COMPILACIÓN
echo.
echo Revise los errores mostrados arriba.
echo Los errores más comunes:
echo - Codificación de caracteres
echo - Dependencias faltantes  
echo - Errores de sintaxis Java
echo.
pause
exit /b 1

:error_jar
echo.
echo ❌ ERROR CREANDO JAR
echo.
echo Posibles causas:
echo - Permisos de archivo
echo - Clases no compiladas correctamente
echo - Problemas con MANIFEST.MF
echo.
pause
exit /b 1