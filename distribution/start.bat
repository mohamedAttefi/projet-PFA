@echo off
echo Starting WorkOrders Management System...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher and try again
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "workorders-1.0-SNAPSHOT.jar" (
    echo Error: workorders-1.0-SNAPSHOT.jar not found
    echo Please ensure you're running this script from the correct directory
    pause
    exit /b 1
)

REM Check if lib folder exists
if not exist "lib" (
    echo Error: lib folder not found
    echo Please ensure you're running this script from the correct directory
    pause
    exit /b 1
)

REM Start the application using JavaFX modules
echo Launching application...
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "workorders-1.0-SNAPSHOT.jar;lib/*" com.company.Main

if %errorlevel% neq 0 (
    echo.
    echo Application exited with error code %errorlevel%
    echo Trying alternative launch method...
    java -cp "workorders-1.0-SNAPSHOT.jar;lib/*" com.company.Main
    
    if %errorlevel% neq 0 (
        echo.
        echo Both launch methods failed. Please check:
        echo - Java 17+ is installed
        echo - All JAR files are present
        echo - Database is accessible
        pause
        exit /b 1
    )
)
