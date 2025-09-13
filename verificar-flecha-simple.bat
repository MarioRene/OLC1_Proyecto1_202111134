@echo off
echo =====================================================
echo        VERIFICADOR ESPECIFICO - PROBLEMA FLECHA
echo =====================================================
echo.

echo [1] Verificando Lexer.flex original...
if exist "gramatica\Lexer.flex" (
    findstr /C:"\"->" "gramatica\Lexer.flex" >nul
    if errorlevel 1 (
        echo ERROR: Patron de flecha NO encontrado en Lexer.flex
        echo SOLUCION: Debe agregar: \"-^>\" { return symbol(sym.FLECHA); }
    ) else (
        echo OK: Patron de flecha encontrado en Lexer.flex
        findstr /n /C:"\"->" "gramatica\Lexer.flex"
    )
) else (
    echo ERROR: Lexer.flex no existe
)

echo.
echo [2] Verificando Lexer.java generado...
if exist "src\analizadores\Lexer.java" (
    findstr /C:"sym.FLECHA" "src\analizadores\Lexer.java" >nul
    if errorlevel 1 (
        echo ERROR: sym.FLECHA no encontrado en Lexer.java generado
        echo SOLUCION: Necesita regenerar con JFlex
    ) else (
        echo OK: sym.FLECHA encontrado en Lexer.java
    )
) else (
    echo ERROR: Lexer.java no existe - no se ha generado
)

echo.
echo [3] Verificando sym.java...
if exist "src\analizadores\sym.java" (
    findstr /C:"FLECHA" "src\analizadores\sym.java" >nul
    if errorlevel 1 (
        echo ERROR: FLECHA no definida en sym.java
        echo SOLUCION: Necesita regenerar con CUP
    ) else (
        echo OK: FLECHA definida en sym.java
        findstr /C:"FLECHA" "src\analizadores\sym.java"
    )
) else (
    echo ERROR: sym.java no existe - no se ha generado
)

echo.
echo [4] Verificando archivos compilados...
if exist "build\classes\analizadores\Lexer.class" (
    echo OK: Lexer.class existe
) else (
    echo ERROR: Lexer.class no compilado
)

echo.
echo =====================================================
echo                   DIAGNOSTICO
echo =====================================================
echo.
echo PASOS SIGUIENTES:
echo 1. Reemplazar gramatica\Lexer.flex con version corregida
echo 2. Ejecutar desde CMD de Windows: recompilar.bat
echo 3. Probar con archivo simple de prueba
echo.
pause