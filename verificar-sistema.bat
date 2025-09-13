@echo off
echo ==========================================
echo     VERIFICADOR DE SISTEMA - AutomataLab
echo ==========================================
echo.

set ERRORS=0

echo [1] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no instalado o no en PATH
    set /a ERRORS+=1
) else (
    echo [OK] Java instalado
    java -version 2>&1 | findstr "version"
)

echo.
echo [2] Verificando JDK (javac)...
javac -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] javac no disponible - necesita JDK
    set /a ERRORS+=1
) else (
    echo [OK] JDK disponible
    javac -version
)

echo.
echo [3] Verificando estructura de carpetas...
if not exist "src" (
    echo [ERROR] Carpeta src no encontrada
    set /a ERRORS+=1
) else (
    echo [OK] Carpeta src existe
)

if not exist "lib" (
    echo [ERROR] Carpeta lib no encontrada
    set /a ERRORS+=1
) else (
    echo [OK] Carpeta lib existe
)

if not exist "gramatica" (
    echo [ERROR] Carpeta gramatica no encontrada
    set /a ERRORS+=1
) else (
    echo [OK] Carpeta gramatica existe
)

echo.
echo [4] Verificando librerias necesarias...
if exist "lib\jflex-full-1.7.0.jar" (
    echo [OK] JFlex encontrado
) else (
    echo [ERROR] lib\jflex-full-1.7.0.jar no encontrado
    set /a ERRORS+=1
)

if exist "lib\java-cup-11b.jar" (
    echo [OK] Java CUP encontrado
) else (
    echo [ERROR] lib\java-cup-11b.jar no encontrado
    set /a ERRORS+=1
)

if exist "lib\java-cup-11b-runtime.jar" (
    echo [OK] CUP Runtime encontrado
) else (
    echo [ERROR] lib\java-cup-11b-runtime.jar no encontrado
    set /a ERRORS+=1
)

echo.
echo [5] Verificando archivos fuente principales...
if exist "src\main\Main.java" (
    echo [OK] Main.java encontrado
) else (
    echo [ERROR] src\main\Main.java no encontrado
    set /a ERRORS+=1
)

if exist "gramatica\Lexer.flex" (
    echo [OK] Lexer.flex encontrado
) else (
    echo [ERROR] gramatica\Lexer.flex no encontrado
    set /a ERRORS+=1
)

if exist "gramatica\Parser.cup" (
    echo [OK] Parser.cup encontrado
) else (
    echo [ERROR] gramatica\Parser.cup no encontrado
    set /a ERRORS+=1
)

echo.
echo [6] Estado de compilacion...
if exist "build\jar\AutomataLab.jar" (
    echo [OK] JAR compilado existe
) else (
    echo [INFO] JAR no compilado
)

if exist "build\classes\main\Main.class" (
    echo [OK] Clases compiladas existen
) else (
    echo [INFO] Clases no compiladas
)

echo.
echo ==========================================
if %ERRORS%==0 (
    echo RESULTADO: Sistema configurado correctamente
    echo Puede ejecutar: run-simple.bat
) else (
    echo RESULTADO: %ERRORS% errores encontrados
    echo.
    echo SOLUCIONES:
    echo 1. Instalar JDK completo (no solo JRE^)
    echo 2. Descargar librerias faltantes:
    echo    - JFlex: https://jflex.de/download.html
    echo    - CUP: http://www2.cs.tum.edu/projects/cup/
    echo 3. Verificar estructura de carpetas
)
echo ==========================================
echo.
pause