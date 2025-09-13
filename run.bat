@echo off
setlocal enabledelayedexpansion

echo ========================================
echo         EJECUTOR AUTOMATALAB v1.0
echo ========================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado
    echo         Instala Java desde: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Opción 1: Ejecutar desde JAR (preferido)
if exist "build\jar\AutomataLab.jar" (
    if exist "lib\java-cup-11b-runtime.jar" (
        echo [INFO] Ejecutando desde JAR...
        echo.
        java -jar build\jar\AutomataLab.jar
        goto :end
    )
)

REM Opción 2: Ejecutar desde clases compiladas
if exist "build\classes\main\Main.class" (
    if exist "lib\java-cup-11b-runtime.jar" (
        echo [INFO] Ejecutando desde clases compiladas...
        echo.
        java -cp "build\classes;lib\*" main.Main
        goto :end
    )
)

REM Si llegamos aquí, no se puede ejecutar
echo [ERROR] No se puede ejecutar el proyecto
echo.
echo Posibles causas:
echo   1. El proyecto no esta compilado
echo   2. Faltan librerias en lib\
echo   3. Error en la compilacion
echo.
echo Soluciones:
echo   1. Ejecuta compile.bat para compilar
echo   2. Verifica que existan los archivos:
echo      - lib\java-cup-11b-runtime.jar
echo      - build\classes\main\Main.class
echo      O build\jar\AutomataLab.jar
echo.

:end
if errorlevel 1 (
    echo.
    echo [ERROR] La aplicacion termino con errores
)
echo.
echo Presiona cualquier tecla para continuar...
pause >nul