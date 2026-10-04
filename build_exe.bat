@echo off
echo ===================================================
echo   Generating Windows EXE with JDK 21 jpackage
echo ===================================================

cd /d "%~dp0"

where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "C:\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin" (
        set "PATH=%PATH%;C:\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin"
    )
)
where jpackage >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-21\bin" (
        set "PATH=%PATH%;C:\Program Files\Java\jdk-21\bin"
    )
)

if not exist "target\PetCare-1.0-SNAPSHOT.jar" (
    echo Fat JAR not found. Building package first...
    call mvn clean package -DskipTests
)

echo Packaging native Windows EXE into dist\ directory...
if not exist "dist" mkdir dist

jpackage --input target --name "PetCare" --main-jar "PetCare-1.0-SNAPSHOT.jar" --main-class "com.petcare.PetCare" --type app-image --dest dist --win-shortcut --win-menu

if %ERRORLEVEL% equ 0 (
    echo.
    echo ===================================================
    echo   NATIVE WINDOWS APP CREATED SUCCESSFULLY!
    echo   Executable located at: dist\PetCare\PetCare.exe
    echo ===================================================
) else (
    echo.
    echo [NOTE] If jpackage is not in your PATH, you can use Launch4j or Inno Setup with target\PetCare-1.0-SNAPSHOT.jar
)
pause
