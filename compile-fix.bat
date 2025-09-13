@echo off
echo =============================================
echo    COMPILACION COMPLETA CON CORRECCION
echo =============================================
echo.

REM Limpiar completamente
echo [PASO 1] Limpieza completa...
if exist "build" rmdir /s /q "build" 2>nul
if exist "src\analizadores\Lexer.java" del "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del "src\analizadores\sym.java" 2>nul
echo [OK] Limpieza completada

REM Crear directorios
echo.
echo [PASO 2] Creando estructura...
mkdir build\classes 2>nul
mkdir build\jar 2>nul
mkdir src\analizadores 2>nul
echo [OK] Directorios creados

REM Verificar archivos de gramática
echo.
echo [PASO 3] Verificando archivos de gramática...
if not exist "gramatica\Lexer.flex" (
    echo [ERROR] Archivo Lexer.flex no encontrado
    echo Por favor, asegúrese de que el archivo esté en gramatica\Lexer.flex
    pause
    exit /b 1
)
if not exist "gramatica\Parser.cup" (
    echo [ERROR] Archivo Parser.cup no encontrado
    echo Por favor, asegúrese de que el archivo esté en gramatica\Parser.cup
    pause
    exit /b 1
)
echo [OK] Archivos de gramática encontrados

REM Generar analizador léxico
echo.
echo [PASO 4] Generando analizador léxico...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 (
    echo [ERROR] Error al generar el analizador léxico
    echo Verifique el archivo Lexer.flex
    pause
    exit /b 1
)
echo [OK] Analizador léxico generado

REM Generar analizador sintáctico
echo.
echo [PASO 5] Generando analizador sintáctico...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 (
    echo [ERROR] Error al generar el analizador sintáctico
    echo Verifique el archivo Parser.cup
    pause
    exit /b 1
)
echo [OK] Analizador sintáctico generado

REM Compilar modelos
echo.
echo [PASO 6] Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 (
    echo [ERROR] Error al compilar modelos
    pause
    exit /b 1
)
echo [OK] Modelos compilados

REM Compilar analizadores
echo.
echo [PASO 7] Compilando analizadores...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 (
    echo [ERROR] Error al compilar analizadores
    echo Verifique que los archivos .java se generaron correctamente
    pause
    exit /b 1
)
echo [OK] Analizadores compilados

REM Compilar reportes
echo.
echo [PASO 8] Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 (
    echo [ERROR] Error al compilar reportes
    pause
    exit /b 1
)
echo [OK] Reportes compilados

REM Compilar interfaz
echo.
echo [PASO 9] Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 (
    echo [ERROR] Error al compilar interfaz
    pause
    exit /b 1
)
echo [OK] Interfaz compilada

REM Compilar main
echo.
echo [PASO 10] Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 (
    echo [ERROR] Error al compilar main
    pause
    exit /b 1
)
echo [OK] Main compilado

REM Incluir runtime de CUP
echo.
echo [PASO 11] Incluyendo runtime de CUP...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..
echo [OK] Runtime incluido

REM Crear JAR
echo.
echo [PASO 12] Creando JAR...
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 (
    echo [ERROR] Error al crear JAR
    pause
    exit /b 1
)
echo [OK] JAR creado

REM Probar ejecución
echo.
echo [PASO 13] Probando ejecución...
echo Probando JAR...
java -jar build\jar\AutomataLab.jar --version 2>nul
if errorlevel 1 (
    echo [WARNING] JAR puede tener problemas, probando ejecución directa...
    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main --test 2>nul
    if errorlevel 1 (
        echo [WARNING] Ejecución directa también falló, pero la compilación está completa
    ) else (
        echo [OK] Ejecución directa funciona
    )
) else (
    echo [OK] JAR funciona correctamente
)

echo.
echo =============================================
echo           COMPILACION COMPLETA
echo =============================================
echo.
echo El proyecto ha sido compilado exitosamente.
echo.
echo COMO EJECUTAR:
echo Opción 1: java -jar build\jar\AutomataLab.jar
echo Opción 2: java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main
echo.
echo Si encuentra errores de sintaxis, revise:
echo 1. Que el archivo tenga la sintaxis correcta
echo 2. Que use -> (flecha) en las transiciones
echo 3. Que los símbolos estén bien definidos
echo.
pause