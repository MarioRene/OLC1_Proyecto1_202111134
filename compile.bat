@echo off
echo Compilando AutomataLab...

echo Creando directorios...
mkdir build\classes 2>nul
mkdir build\jar 2>nul
mkdir src\analizadores 2>nul

echo Generando analizadores...
java -jar lib\jflex-full-1.7.0.jar -d src\analizadores gramatica\Lexer.flex
java -jar lib\java-cup-11b.jar -destdir src\analizadores -parser Parser gramatica\Parser.cup

echo Compilando modelos...
javac -cp "lib\*" -d build\classes src\modelos\*.java

echo Compilando analizadores...
javac -cp "lib\*;build\classes" -d build\classes src\analizadores\*.java

echo Compilando reportes...
javac -cp "lib\*;build\classes" -d build\classes src\reportes\*.java

echo Compilando interfaz...
javac -cp "lib\*;build\classes" -d build\classes src\interfaz\*.java

echo Compilando main...
javac -cp "lib\*;build\classes" -d build\classes src\main\*.java

echo Creando JAR...
echo Main-Class: main.Main > build\MANIFEST.MF
jar cfm build\jar\AutomataLab.jar build\MANIFEST.MF -C build\classes .

echo Compilacion completada!
echo Para ejecutar: java -jar build\jar\AutomataLab.jar

pause