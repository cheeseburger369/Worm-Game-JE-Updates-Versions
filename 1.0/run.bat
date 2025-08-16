@echo off
cd /d "%~dp0"
if not exist TheWormGame.class (
    echo Compiling game...
    javac TheWormGame.java
)
echo Starting The Worm Game...
java TheWormGame
pause