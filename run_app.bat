@echo off
echo ===================================================
echo   Starting PetCare Veterinary Management System
echo ===================================================

cd /d "%~dp0"

where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "C:\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin" (
        set "PATH=%PATH%;C:\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin"
    )
)

if exist "target\PetCare-1.0-SNAPSHOT.jar" (
    java -jar "target\PetCare-1.0-SNAPSHOT.jar"
) else (
    echo Compiling and running via Maven...
    call mvn compile exec:java
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application stopped or error occurred.
    pause
)
