@echo off
echo =======================================
echo      AUTOMATALAB - Ejecutor Simple
echo =======================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java no encontrado
    echo Instale Java y agregelo al PATH
    pause
    exit /b 1
)

echo [OK] Java encontrado

REM Verificar estructura del proyecto
if not exist "src\main\Main.java" (
    echo ERROR: Archivo Main.java no encontrado
    echo Ejecute desde la carpeta raiz del proyecto
    pause
    exit /b 1
)

echo [OK] Estructura del proyecto correcta

REM Metodo 1: Intentar ejecutar JAR si existe
if exist "build\jar\AutomataLab.jar" (
    echo [EJECUTANDO] Intentando JAR compilado...
    java -jar build\jar\AutomataLab.jar
    if not errorlevel 1 goto end
    echo [INFO] JAR falló, intentando método alternativo...
)

REM Metodo 2: Ejecutar desde clases compiladas (más confiable)
if exist "build\classes\main\Main.class" (
    echo [EJECUTANDO] Usando clases compiladas con classpath explícito...
    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main
    goto end
)

REM Metodo 3: Compilar automaticamente si no existe
echo [INFO] Proyecto no compilado, compilando...
if exist "compile-mejorado.bat" (
    call compile-mejorado.bat
    if exist "build\jar\AutomataLab.jar" (
        echo [EJECUTANDO] Usando JAR recien compilado...
        java -jar build\jar\AutomataLab.jar
        goto end
    )
)

REM Si nada funciona
echo ERROR: No se pudo ejecutar el programa
echo.
echo Posibles soluciones:
echo 1. Ejecutar: compile-mejorado.bat
echo 2. Verificar que las librerias esten en lib\
echo 3. Ejecutar: ant jar
echo.

:end
echo.
echo Programa finalizado
pause