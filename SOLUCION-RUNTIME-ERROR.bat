@echo off
chcp 65001 >nul
echo =============================================
echo    AUTOMATALAB - COMPILACION Y EJECUCION
echo    Proyecto OLC1 - Análisis de Autómatas
echo =============================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no está instalado o no está en PATH
    echo Instale Java JDK 8 o superior
    pause
    exit /b 1
)

echo [INFO] Verificando estructura del proyecto...

REM Verificar librerías
if not exist "lib\jflex-full-1.7.0.jar" (
    echo [ERROR] JFlex no encontrado: lib\jflex-full-1.7.0.jar
    echo Descargue desde: https://jflex.de/download.html
    pause
    exit /b 1
)

if not exist "lib\java-cup-11b.jar" (
    echo [ERROR] Java CUP no encontrado: lib\java-cup-11b.jar
    echo Descargue desde: http://www2.cs.tum.edu/projects/cup/
    pause
    exit /b 1
)

if not exist "lib\java-cup-11b-runtime.jar" (
    echo [ERROR] CUP Runtime no encontrado: lib\java-cup-11b-runtime.jar
    echo Descargue desde: http://www2.cs.tum.edu/projects/cup/
    pause
    exit /b 1
)

REM Verificar archivos de gramática
if not exist "gramatica\Lexer.flex" (
    echo [ERROR] Archivo de lexer no encontrado: gramatica\Lexer.flex
    pause
    exit /b 1
)

if not exist "gramatica\Parser.cup" (
    echo [ERROR] Archivo de parser no encontrado: gramatica\Parser.cup
    pause
    exit /b 1
)

echo [OK] Todas las dependencias encontradas
echo.

REM Limpiar compilación anterior
echo [PASO 1] Limpiando compilación anterior...
if exist "build" rmdir /s /q "build" 2>nul
if exist "src\analizadores\Lexer.java" del "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del "src\analizadores\sym.java" 2>nul
echo [OK] Limpieza completada

REM Crear directorios
echo.
echo [PASO 2] Creando estructura de directorios...
mkdir "build\classes" 2>nul
mkdir "build\jar" 2>nul
mkdir "src\analizadores" 2>nul
echo [OK] Directorios creados

REM Generar analizador léxico
echo.
echo [PASO 3] Generando analizador léxico con JFlex...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 (
    echo [ERROR] Fallo en generación del lexer
    pause
    exit /b 1
)
if not exist "src\analizadores\Lexer.java" (
    echo [ERROR] Lexer.java no fue generado
    pause
    exit /b 1
)
echo [OK] Analizador léxico generado

REM Generar analizador sintáctico
echo.
echo [PASO 4] Generando analizador sintáctico con CUP...
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 (
    echo [ERROR] Fallo en generación del parser
    pause
    exit /b 1
)
if not exist "src\analizadores\Parser.java" (
    echo [ERROR] Parser.java no fue generado
    pause
    exit /b 1
)
if not exist "src\analizadores\sym.java" (
    echo [ERROR] sym.java no fue generado
    pause
    exit /b 1
)
echo [OK] Analizador sintáctico generado

REM Compilar código Java
echo.
echo [PASO 5] Compilando código fuente...
echo   - Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 goto :error_compilacion

echo   - Compilando analizadores...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 goto :error_compilacion

echo   - Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 goto :error_compilacion

echo   - Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 goto :error_compilacion

echo   - Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 goto :error_compilacion

echo [OK] Código compilado exitosamente

REM Crear JAR con runtime incluido
echo.
echo [PASO 6] Creando JAR ejecutable...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..

echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 (
    echo [ERROR] Fallo en creación del JAR
    pause
    exit /b 1
)
echo [OK] JAR creado: build\jar\AutomataLab.jar

REM Crear archivo de prueba si no existe
if not exist "ejemplos" mkdir "ejemplos"
if not exist "ejemplos\ejemplo.atm" (
    echo [INFO] Creando archivo de ejemplo...
    echo // Ejemplo de prueba > "ejemplos\ejemplo.atm"
    echo ^<AFD Nombre="AFD_Simple"^> >> "ejemplos\ejemplo.atm"
    echo     N = {S, F}; >> "ejemplos\ejemplo.atm"
    echo     T = {0, 1}; >> "ejemplos\ejemplo.atm"
    echo     I = {S}; >> "ejemplos\ejemplo.atm"
    echo     A = {F}; >> "ejemplos\ejemplo.atm"
    echo Transiciones: >> "ejemplos\ejemplo.atm"
    echo     S -^> 0, F ^| 1, S ; >> "ejemplos\ejemplo.atm"
    echo     F -^> 0, F ^| 1, S ; >> "ejemplos\ejemplo.atm"
    echo ^</AFD^> >> "ejemplos\ejemplo.atm"
    echo. >> "ejemplos\ejemplo.atm"
    echo verAutomatas(); >> "ejemplos\ejemplo.atm"
    echo desc(AFD_Simple); >> "ejemplos\ejemplo.atm"
    echo AFD_Simple("1010"); >> "ejemplos\ejemplo.atm"
)

echo.
echo =============================================
echo            COMPILACIÓN EXITOSA
echo =============================================
echo.
echo El proyecto se ha compilado correctamente.
echo.
echo OPCIONES DE EJECUCIÓN:
echo.
echo 1. Interfaz gráfica:
echo    java -jar build\jar\AutomataLab.jar
echo.
echo 2. Desde línea de comandos:
echo    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main
echo.
echo 3. Con archivo específico:
echo    java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main ejemplos\ejemplo.atm
echo.
echo ¿Desea ejecutar la interfaz gráfica ahora? (S/N)
set /p respuesta=
if /i "%respuesta%"=="S" (
    echo.
    echo [EJECUTANDO] Iniciando AutómataLab...
    java -jar build\jar\AutomataLab.jar
) else (
    echo.
    echo Para ejecutar manualmente use los comandos mostrados arriba.
)
echo.
pause
exit /b 0

:error_compilacion
echo.
echo [ERROR] Error durante la compilación
echo Verifique:
echo 1. Que todos los archivos fuente estén presentes
echo 2. Que no haya errores de sintaxis en el código
echo 3. Que Java JDK esté correctamente instalado
echo 4. Que las librerías sean compatibles
echo.
pause
exit /b 1