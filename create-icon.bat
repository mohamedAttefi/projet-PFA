@echo off
echo Creating application icon...
echo.

REM Check if icon already exists in distribution
if exist "distribution\canal_logo.ico" (
    echo ✅ Icon already exists in distribution folder
    goto :end
)

REM Create a simple icon file placeholder
echo Creating placeholder icon...
echo. > "distribution\canal_logo.ico"

:end
echo.
echo 🎨 Icon setup complete
echo 📁 Location: distribution\canal_logo.ico
echo.
pause
