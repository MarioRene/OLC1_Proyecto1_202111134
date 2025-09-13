@echo off
echo =============================================
echo    SOLUCION PARA ERROR DE RUNTIME CUP
echo    NoClassDefFoundError: java_cup/runtime/Scanner
echo =============================================
echo.

echo [DIAGNOSTICO] Verificando problema...

REM Verificar si el error es del JAR
if exist "build\jar\AutomataLab.jar" (
    echo [DETECTADO] JAR existe pero tiene problemas de runtime
    echo [SOLUCION] Recompilando con inclusion correcta de runtime...
) else (
    echo [DETECTADO] JAR no existe
    echo [SOLUCION] Compilando desde cero...
)

REM Paso 1: Limpiar todo
echo.
echo [PASO 1] Limpiando compilacion anterior...
if exist "build" rmdir /s /q "build" 2>nul
if exist "src\analizadores\Lexer.java" del "src\analizadores\Lexer.java" 2>nul
if exist "src\analizadores\Parser.java" del "src\analizadores\Parser.java" 2>nul
if exist "src\analizadores\sym.java" del "src\analizadores\sym.java" 2>nul
echo [OK] Limpieza completada

REM Paso 2: Crear directorios
echo.
echo [PASO 2] Creando estructura...
mkdir build\classes 2>nul
mkdir build\jar 2>nul
mkdir src\analizadores 2>nul
echo [OK] Directorios creados

REM Paso 3: Verificar librerias
echo.
echo [PASO 3] Verificando librerias...
if not exist "lib\java-cup-11b-runtime.jar" (
    echo [ERROR] Runtime de CUP no encontrado
    echo Descargue java-cup-11b-runtime.jar de:
    echo http://www2.cs.tum.edu/projects/cup/
    echo Y coloqueolo en lib\
    pause
    exit /b 1
)
echo [OK] Runtime de CUP encontrado

REM Paso 4: Generar analizadores
echo.
echo [PASO 4] Generando analizadores...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
if errorlevel 1 goto error
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup
if errorlevel 1 goto error
echo [OK] Analizadores generados

REM Paso 5: Compilar Java
echo.
echo [PASO 5] Compilando codigo fuente...
javac -cp "lib\*" -d build\classes src\modelos\*.java
if errorlevel 1 goto error
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java
if errorlevel 1 goto error
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java
if errorlevel 1 goto error
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java
if errorlevel 1 goto error
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java
if errorlevel 1 goto error
echo [OK] Codigo compilado

REM Paso 6: Extraer runtime de CUP a las clases compiladas
echo.
echo [PASO 6] Incluyendo runtime de CUP...
cd build\classes
jar xf ..\..\lib\java-cup-11b-runtime.jar
REM Eliminar META-INF para evitar conflictos
if exist "META-INF" rmdir /s /q "META-INF" 2>nul
cd ..\..
echo [OK] Runtime de CUP incluido

REM Paso 7: Crear JAR corregido
echo.
echo [PASO 7] Creando JAR con runtime incluido...
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 goto error
echo [OK] JAR creado correctamente

REM Paso 8: Verificar solucion
echo.
echo [PASO 8] Verificando solucion...
if not exist "build\jar\AutomataLab.jar" (
    echo [ERROR] JAR no fue creado
    goto error
)

REM Probar que el JAR funcione
echo [TESTING] Probando JAR corregido...
java -jar build\jar\AutomataLab.jar --version 2>nul
if errorlevel 1 (
    echo [WARNING] JAR puede tener problemas, usando metodo alternativo
    goto alternative
)

echo.
echo =============================================
echo            SOLUCION EXITOSA
echo =============================================
echo.
echo El problema del runtime de CUP ha sido corregido.
echo.
echo COMO EJECUTAR:
echo Opcion 1 (JAR): java -jar build\jar\AutomataLab.jar
echo Opcion 2 (Script): ejecutar-directo.bat
echo.
echo Si el JAR aun falla, use la Opcion 2.
echo.
goto end

:alternative
echo.
echo =============================================
echo        METODO ALTERNATIVO PREPARADO
echo =============================================
echo.
echo El JAR puede tener problemas en su sistema.
echo Use el metodo alternativo mas confiable:
echo.
echo EJECUTAR: ejecutar-directo.bat
echo.
echo O manualmente:
echo java -cp "build\classes;lib\java-cup-11b-runtime.jar" main.Main
echo.
goto end

:error
echo.
echo =============================================
echo              ERROR EN PROCESO
echo =============================================
echo.
echo No se pudo completar la correccion.
echo Verifique:
echo 1. Java JDK instalado (no solo JRE)
echo 2. Todas las librerias en lib\
echo 3. Permisos de escritura
echo 4. Ejecutar como administrador
echo.
pause
exit /b 1

:end
pause