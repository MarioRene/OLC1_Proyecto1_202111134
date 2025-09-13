@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo ═══════════════════════════════════════════════════════════════
echo                    AUTOMATALAB v2.0 LAUNCHER
echo                      Ejecutor Inteligente
echo ═══════════════════════════════════════════════════════════════
echo.

REM Configurar colores
for /F %%a in ('echo prompt $E^| cmd') do set "ESC=%%a"
set "GREEN=%ESC%[32m"
set "RED=%ESC%[31m"
set "YELLOW=%ESC%[33m"
set "BLUE=%ESC%[34m"
set "CYAN=%ESC%[36m"
set "WHITE=%ESC%[37m"
set "RESET=%ESC%[0m"

REM Variables de configuración
set "PROJECT_NAME=AutómataLab"
set "JAR_NAME=AutomataLab.jar"
set "MAIN_CLASS=main.Main"

echo %CYAN%[LAUNCHER]%RESET% Iniciando %PROJECT_NAME%...

REM ===== VERIFICAR JAVA =====
echo %BLUE%[CHECK]%RESET% Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo %RED%[ERROR]%RESET% Java no está instalado o no está en PATH
    echo.
    echo %YELLOW%Soluciones:%RESET%
    echo 1. Descargar Java desde: https://www.oracle.com/java/technologies/downloads/
    echo 2. Verificar que Java esté en PATH
    echo 3. Reiniciar la consola después de instalar
    echo.
    pause
    exit /b 1
) else (
    for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i version') do (
        echo %GREEN%[OK]%RESET% Java %%i encontrado
        goto java_ok
    )
    :java_ok
)

REM ===== DETECCIÓN AUTOMÁTICA DE MÉTODO DE EJECUCIÓN =====
echo %BLUE%[CHECK]%RESET% Buscando la mejor forma de ejecutar...

set "EXECUTION_METHOD="
set "EXECUTION_COMMAND="

REM Método 1: JAR compilado (preferido)
if exist "build\jar\%JAR_NAME%" (
    if exist "lib\java-cup-11b-runtime.jar" (
        set "EXECUTION_METHOD=JAR"
        set "EXECUTION_COMMAND=java -jar build\jar\%JAR_NAME%"
        echo %GREEN%[OK]%RESET% JAR ejecutable encontrado
        goto execute
    )
)

REM Método 2: Clases compiladas
if exist "build\classes\main\Main.class" (
    if exist "lib\java-cup-11b-runtime.jar" (
        set "EXECUTION_METHOD=CLASSES"
        set "EXECUTION_COMMAND=java -cp "build\classes;lib\*" %MAIN_CLASS%"
        echo %YELLOW%[OK]%RESET% Clases compiladas encontradas
        goto execute
    )
)

REM Método 3: Compilar automáticamente
if exist "gramatica\Lexer.flex" (
    if exist "gramatica\Parser.cup" (
        echo %YELLOW%[WARN]%RESET% Proyecto no compilado, compilando automáticamente...
        call compile-mejorado.bat
        if errorlevel 1 (
            echo %RED%[ERROR]%RESET% Error en compilación automática
            goto manual_compile
        )
        
        if exist "build\jar\%JAR_NAME%" (
            set "EXECUTION_METHOD=JAR"
            set "EXECUTION_COMMAND=java -jar build\jar\%JAR_NAME%"
            echo %GREEN%[OK]%RESET% Compilación automática exitosa
            goto execute
        )
    )
)

REM No se puede ejecutar
echo %RED%[ERROR]%RESET% No se puede ejecutar %PROJECT_NAME%
echo.
echo %YELLOW%Estado del proyecto:%RESET%
if not exist "build\jar\%JAR_NAME%" echo   ❌ JAR ejecutable no encontrado
if not exist "build\classes\main\Main.class" echo   ❌ Clases compiladas no encontradas
if not exist "lib\java-cup-11b-runtime.jar" echo   ❌ Runtime de CUP no encontrado
if not exist "gramatica\Lexer.flex" echo   ❌ Archivo Lexer.flex no encontrado
if not exist "gramatica\Parser.cup" echo   ❌ Archivo Parser.cup no encontrado

:manual_compile
echo.
echo %CYAN%Soluciones:%RESET%
echo 1. Ejecutar: compile-mejorado.bat
echo 2. Verificar que todas las librerías estén en lib\
echo 3. Verificar que los archivos fuente estén completos
echo.
echo %BLUE%¿Deseas intentar compilar ahora? (S/N):%RESET%
set /p COMPILE_NOW=""
if /i "%COMPILE_NOW%"=="S" (
    echo.
    call compile-mejorado.bat
    if not errorlevel 1 (
        echo.
        echo %GREEN%[SUCCESS]%RESET% Compilación exitosa, intentando ejecutar...
        call "%~f0"
        exit /b
    )
)
echo.
pause
exit /b 1

:execute
echo %BLUE%[LAUNCHER]%RESET% Método de ejecución: %EXECUTION_METHOD%
echo.

REM Mostrar información del sistema
echo %CYAN%Información del sistema:%RESET%
echo   OS: %OS%
echo   Procesador: %PROCESSOR_ARCHITECTURE%
echo   Usuario: %USERNAME%
echo   Directorio: %CD%
echo.

REM Verificar memoria disponible
for /f "skip=1" %%i in ('wmic OS get TotalVisibleMemorySize /value') do (
    for /f "tokens=2 delims==" %%j in ("%%i") do (
        set /a MEMORY_MB=%%j/1024
        if !MEMORY_MB! LSS 1024 (
            echo %YELLOW%[WARN]%RESET% Poca memoria disponible: !MEMORY_MB! MB
            echo          Se recomienda cerrar otras aplicaciones
        ) else (
            echo %GREEN%[OK]%RESET% Memoria disponible: !MEMORY_MB! MB
        )
        goto memory_ok
    )
)
:memory_ok

echo.
echo %GREEN%[STARTING]%RESET% Ejecutando %PROJECT_NAME%...
echo %BLUE%Comando:%RESET% %EXECUTION_COMMAND%
echo.
echo ═══════════════════════════════════════════════════════════════

REM Ejecutar con manejo de errores
%EXECUTION_COMMAND%
set "EXIT_CODE=%ERRORLEVEL%"

echo.
echo ═══════════════════════════════════════════════════════════════

if %EXIT_CODE% EQU 0 (
    echo %GREEN%[SUCCESS]%RESET% %PROJECT_NAME% finalizó correctamente
) else (
    echo %RED%[ERROR]%RESET% %PROJECT_NAME% finalizó con errores (código: %EXIT_CODE%)
    echo.
    echo %YELLOW%Posibles causas:%RESET%
    echo • Error en el código Java
    echo • Librerías faltantes o corruptas
    echo • Problemas de permisos
    echo • Memoria insuficiente
    echo.
    echo %BLUE%Para diagnosticar:%RESET%
    echo 1. Revise los mensajes de error anteriores
    echo 2. Ejecute compile-mejorado.bat para recompilar
    echo 3. Verifique que Java esté correctamente instalado
)

echo.
echo %CYAN%Estadísticas de ejecución:%RESET%
echo   Método usado: %EXECUTION_METHOD%
echo   Código de salida: %EXIT_CODE%
echo   Hora de finalización: %TIME%

if %EXIT_CODE% NEQ 0 (
    echo.
    echo %YELLOW%¿Deseas ver ayuda para solucionar problemas? (S/N):%RESET%
    set /p SHOW_HELP=""
    if /i "!SHOW_HELP!"=="S" (
        echo.
        call :show_troubleshooting
    )
)

echo.
echo ═══════════════════════════════════════════════════════════════
echo                 Gracias por usar %PROJECT_NAME%
echo ═══════════════════════════════════════════════════════════════
echo Presiona cualquier tecla para cerrar...
pause >nul
exit /b %EXIT_CODE%

:show_troubleshooting
echo %CYAN%═══ GUÍA DE SOLUCIÓN DE PROBLEMAS ═══%RESET%
echo.
echo %YELLOW%Problema:%RESET% "Error: Could not find or load main class"
echo %WHITE%Solución:%RESET% 
echo   • Recompilar: compile-mejorado.bat
echo   • Verificar CLASSPATH
echo.
echo %YELLOW%Problema:%RESET% "Exception in thread 'main'"
echo %WHITE%Solución:%RESET%
echo   • Revisar sintaxis en archivos .atm
echo   • Verificar librerías en lib\
echo.
echo %YELLOW%Problema:%RESET% "NoClassDefFoundError"
echo %WHITE%Solución:%RESET%
echo   • Instalar JDK completo (no solo JRE)
echo   • Verificar java-cup-11b-runtime.jar
echo.
echo %YELLOW%Problema:%RESET% Ventana no aparece
echo %WHITE%Solución:%RESET%
echo   • Verificar drivers gráficos
echo   • Ejecutar como administrador
echo   • Verificar resolución de pantalla
echo.
echo %BLUE%Recursos adicionales:%RESET%
echo   • Manual: docs\MANUAL_USUARIO.md
echo   • Ejemplos: ejemplos\*.atm
echo   • Logs: Revisar mensajes en consola
echo.
echo ═════════════════════════════════════════════
goto :eof