@echo off
echo ============================================
echo   COMPILACION PASO A PASO - AUTOMATALAB
echo ============================================
echo.

echo [PASO 1] Limpiando archivos anteriores...
if exist "build" rmdir /s /q "build"
if exist "src\analizadores\*.java" del "src\analizadores\*.java"
echo [OK] Limpieza completada

echo.
echo [PASO 2] Creando directorios...
mkdir "build" 2>nul
mkdir "build\classes" 2>nul
mkdir "src\analizadores" 2>nul
echo [OK] Directorios creados

echo.
echo [PASO 3] Generando Lexer...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error en JFlex
    pause
    exit /b 1
)
echo [OK] Lexer generado

echo.
echo [PASO 4] Generando Parser...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error en CUP
    pause
    exit /b 1
)
echo [OK] Parser generado

echo.
echo [PASO 5] Compilando por módulos...

echo   5a. Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error compilando modelos
    pause
    exit /b 1
)

echo   5b. Compilando analizadores...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error compilando analizadores
    pause
    exit /b 1
)

echo   5c. Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error compilando reportes
    pause
    exit /b 1
)

echo   5d. Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error compilando interfaz
    pause
    exit /b 1
)

echo   5e. Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Error compilando main
    pause
    exit /b 1
)

echo [OK] Compilación completada

echo.
echo [PASO 6] Verificando archivos generados...
if exist "build\classes\main\Main.class" (
    echo [OK] Main.class generado
) else (
    echo [ERROR] Main.class no encontrado
    pause
    exit /b 1
)

echo.
echo ============================================
echo       COMPILACION EXITOSA!
echo ============================================
echo.
echo Para ejecutar directamente:
echo   java -cp "build\classes;lib\*" main.Main
echo.
echo ¿Ejecutar ahora? (S/N)
set /p choice="> "
if /i "%choice%"=="S" (
    echo.
    echo Ejecutando AutómataLab...
    java -cp "build\classes;lib\*" main.Main
)

echo.
pause