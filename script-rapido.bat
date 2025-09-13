@echo off
echo ====================================
echo   COMPILACION RAPIDA - AUTOMATALAB
echo ====================================
echo.

echo [1/10] Limpiando...
if exist "build" rmdir /s /q "build" 2>nul
if exist "src\analizadores\Lexer.java" del "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del "src\analizadores\sym.java" 2>nul
mkdir build\classes 2>nul
mkdir build\jar 2>nul

echo [2/10] Generando Lexer...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 goto error

echo [3/10] Generando Parser...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 goto error

echo [4/10] Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 goto error

echo [5/10] Compilando analizadores...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 goto error

echo [6/10] Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 goto error

echo [7/10] Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 goto error

echo [8/10] Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 goto error

echo [9/10] Creando JAR...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar 2>nul
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 goto error

echo [10/10] Ejecutando...
echo.
echo ====================================
echo       COMPILACION EXITOSA
echo ====================================
echo.
echo Iniciando AutómataLab...
echo.

java -jar build\jar\AutomataLab.jar
if errorlevel 1 (
    echo.
    echo JAR falló, intentando método alternativo...
    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main
)

echo.
echo ====================================
echo Compilación y ejecución completadas
echo ====================================
goto end

:error
echo.
echo [ERROR] La compilación falló en el paso anterior
echo Revise los mensajes de error
pause
exit /b 1

:end
echo.
pause