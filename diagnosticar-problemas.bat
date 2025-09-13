@echo off
echo ====================================================
echo        DIAGNOSTICO DE PROBLEMAS - AutomataLab
echo ====================================================
echo.

set PROBLEMAS=0

echo [DIAGNOSTICO 1] Sistema base...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java no instalado
    set /a PROBLEMAS+=1
) else (
    echo ✅ Java instalado:
    java -version 2>&1 | findstr "version"
)

javac -version >nul 2>&1
if errorlevel 1 (
    echo ❌ JDK no disponible (solo JRE instalado)
    echo    💡 Necesita JDK completo para compilar
    set /a PROBLEMAS+=1
) else (
    echo ✅ JDK disponible:
    javac -version
)

echo.
echo [DIAGNOSTICO 2] Estructura del proyecto...
if exist "src\main\Main.java" (
    echo ✅ Archivo principal encontrado
) else (
    echo ❌ src\main\Main.java no encontrado
    set /a PROBLEMAS+=1
)

if exist "gramatica\Lexer.flex" (
    echo ✅ Lexer.flex encontrado
) else (
    echo ❌ gramatica\Lexer.flex no encontrado
    set /a PROBLEMAS+=1
)

if exist "gramatica\Parser.cup" (
    echo ✅ Parser.cup encontrado
) else (
    echo ❌ gramatica\Parser.cup no encontrado
    set /a PROBLEMAS+=1
)

echo.
echo [DIAGNOSTICO 3] Dependencias...
if exist "lib\jflex-full-1.7.0.jar" (
    echo ✅ JFlex disponible
) else (
    echo ❌ JFlex no encontrado
    echo    📥 Descargar de: https://jflex.de/download.html
    set /a PROBLEMAS+=1
)

if exist "lib\java-cup-11b.jar" (
    echo ✅ CUP disponible  
) else (
    echo ❌ Java CUP no encontrado
    echo    📥 Descargar de: http://www2.cs.tum.edu/projects/cup/
    set /a PROBLEMAS+=1
)

if exist "lib\java-cup-11b-runtime.jar" (
    echo ✅ CUP Runtime disponible
) else (
    echo ❌ CUP Runtime no encontrado
    echo    📥 Descargar de: http://www2.cs.tum.edu/projects/cup/
    set /a PROBLEMAS+=1
)

echo.
echo [DIAGNOSTICO 4] Estado de compilacion...
if exist "build\jar\AutomataLab.jar" (
    echo ✅ JAR compilado existe
    for %%F in ("build\jar\AutomataLab.jar") do echo    📊 Tamaño: %%~zF bytes
) else (
    echo ⚠️  JAR no compilado
)

if exist "src\analizadores\Lexer.java" (
    echo ✅ Lexer generado
) else (
    echo ⚠️  Lexer no generado
)

if exist "src\analizadores\Parser.java" (
    echo ✅ Parser generado
) else (
    echo ⚠️  Parser no generado
)

echo.
echo [DIAGNOSTICO 5] Problemas especificos detectados...

REM Verificar problema de flecha en Lexer.flex
if exist "gramatica\Lexer.flex" (
    findstr /C:"\"->" "gramatica\Lexer.flex" >nul 2>&1
    if errorlevel 1 (
        echo ❌ PROBLEMA: Flecha '->' no definida correctamente en Lexer.flex
        echo    💡 La flecha debe definirse ANTES que simbolos individuales
        set /a PROBLEMAS+=1
    ) else (
        echo ✅ Patron de flecha encontrado en Lexer.flex
    )
)

REM Verificar codificacion de archivos
if exist "ejemplos\ejemplo.atm" (
    findstr /C:"â€™" "ejemplos\ejemplo.atm" >nul 2>&1
    if not errorlevel 1 (
        echo ⚠️  PROBLEMA: Caracteres de codificacion incorrecta en archivo .atm
        echo    💡 Limpie caracteres especiales como â€™, Ã³
        set /a PROBLEMAS+=1
    )
)

echo.
echo ====================================================
echo                   RESULTADO
echo ====================================================

if %PROBLEMAS%==0 (
    echo 🎉 SISTEMA CORRECTO
    echo    Todo parece estar en orden
    echo    Puede ejecutar: run-simple.bat
) else (
    echo 🔧 %PROBLEMAS% PROBLEMAS DETECTADOS
    echo.
    echo 📋 PASOS RECOMENDADOS:
    
    if not exist "lib\jflex-full-1.7.0.jar" (
        echo 1. Descargar JFlex 1.7.0 y colocarlo en lib\
    )
    if not exist "lib\java-cup-11b.jar" (
        echo 2. Descargar Java CUP y colocarlo en lib\
    )
    if not exist "lib\java-cup-11b-runtime.jar" (
        echo 3. Descargar CUP Runtime y colocarlo en lib\
    )
    
    echo 4. Reemplazar Lexer.flex con la version corregida
    echo 5. Usar archivo .atm sin caracteres especiales  
    echo 6. Ejecutar: recompilar-completo.bat
    echo.
    echo 🔗 LINKS DE DESCARGA:
    echo    JFlex: https://github.com/jflex-de/jflex/releases
    echo    CUP: http://www2.cs.tum.edu/projects/cup/releases.html
)

echo ====================================================
echo.
pause