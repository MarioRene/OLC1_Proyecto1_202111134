@echo off
echo ========================================
echo    VERIFICADOR DE DEPENDENCIAS
echo ========================================
echo.

set ALL_OK=1

echo [1] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no instalado
    echo         Descarga: https://www.oracle.com/java/technologies/downloads/
    set ALL_OK=0
) else (
    echo [OK] Java encontrado
    java -version 2>&1 | findstr "version"
)

echo.
echo [2] Verificando javac...
javac -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] javac no disponible - necesitas JDK, no solo JRE
    set ALL_OK=0
) else (
    echo [OK] javac encontrado
    javac -version
)

echo.
echo [3] Verificando librerias...

if exist "lib\jflex-full-1.7.0.jar" (
    echo [OK] lib\jflex-full-1.7.0.jar
) else (
    echo [ERROR] Falta lib\jflex-full-1.7.0.jar
    echo         Descarga: https://jflex.de/download.html
    set ALL_OK=0
)

if exist "lib\java-cup-11b.jar" (
    echo [OK] lib\java-cup-11b.jar
) else (
    echo [ERROR] Falta lib\java-cup-11b.jar
    echo         Descarga: http://www2.cs.tum.edu/projects/cup/
    set ALL_OK=0
)

if exist "lib\java-cup-11b-runtime.jar" (
    echo [OK] lib\java-cup-11b-runtime.jar
) else (
    echo [ERROR] Falta lib\java-cup-11b-runtime.jar
    echo         Descarga: http://www2.cs.tum.edu/projects/cup/
    set ALL_OK=0
)

echo.
echo [4] Verificando archivos fuente...

if exist "gramatica\Lexer.flex" (
    echo [OK] gramatica\Lexer.flex
) else (
    echo [ERROR] Falta gramatica\Lexer.flex
    set ALL_OK=0
)

if exist "gramatica\Parser.cup" (
    echo [OK] gramatica\Parser.cup
) else (
    echo [ERROR] Falta gramatica\Parser.cup
    set ALL_OK=0
)

if exist "src\main\Main.java" (
    echo [OK] src\main\Main.java
) else (
    echo [ERROR] Falta src\main\Main.java
    set ALL_OK=0
)

echo.
echo [5] Verificando estructura de carpetas...

if exist "src" (
    echo [OK] Carpeta src
) else (
    echo [ERROR] Falta carpeta src
    set ALL_OK=0
)

if exist "lib" (
    echo [OK] Carpeta lib
) else (
    echo [ERROR] Falta carpeta lib
    set ALL_OK=0
)

if exist "gramatica" (
    echo [OK] Carpeta gramatica
) else (
    echo [ERROR] Falta carpeta gramatica
    set ALL_OK=0
)

echo.
echo ========================================
if %ALL_OK%==1 (
    echo    CONFIGURACION CORRECTA
    echo.
    echo Todo listo para compilar!
    echo Ejecuta: compile.bat
) else (
    echo    CONFIGURACION INCOMPLETA
    echo.
    echo Corrige los errores marcados arriba
    echo antes de compilar el proyecto.
)
echo ========================================
echo.
pause