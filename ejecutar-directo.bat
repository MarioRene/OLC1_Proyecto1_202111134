@echo off
echo =====================================
echo    AUTOMATALAB - Ejecución Directa
echo =====================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java no encontrado
    pause
    exit /b 1
)

echo [INFO] Java encontrado

REM Verificar archivos compilados
if not exist "build\classes\main\Main.class" (
    echo ERROR: Clases no compiladas
    echo.
    echo Soluciones:
    echo 1. Ejecutar: compile-simple.bat
    echo 2. O ejecutar: compile-mejorado.bat
    echo.
    pause
    exit /b 1
)

echo [INFO] Clases compiladas encontradas

REM Verificar runtime de CUP
if not exist "lib\java-cup-11b-runtime.jar" (
    echo ERROR: Runtime de CUP no encontrado
    echo Necesita: lib\java-cup-11b-runtime.jar
    pause
    exit /b 1
)

echo [INFO] Runtime de CUP encontrado

REM Ejecutar directamente con classpath completo
echo [EJECUTANDO] AutomataLab...
echo.
echo =====================================

java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main

echo.
echo =====================================
echo [FINALIZADO] AutomataLab terminado
pause