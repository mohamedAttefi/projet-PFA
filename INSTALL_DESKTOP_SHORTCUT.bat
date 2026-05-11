@echo off
title WorkOrders Management System - Desktop Shortcut Installer

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║           WorkOrders Management System - Shortcut Creator          ║
echo ║              Canal Informatique - Gestion des Interventions        ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

echo 🚀 Creating desktop shortcut...
echo.

REM Check if we're in the right directory
if not exist "distribution\start.bat" (
    echo ❌ Error: distribution folder not found!
    echo    Please run this script from the main project folder
    echo    (the folder containing the 'distribution' folder)
    echo.
    pause
    exit /b 1
)

REM Create VBS script for shortcut creation
echo Set oWS = WScript.CreateObject("WScript.Shell") > "%TEMP%\WorkOrdersShortcut.vbs"
echo sLinkFile = oWS.SpecialFolders("Desktop") ^& "\WorkOrders Management System.lnk" >> "%TEMP%\WorkOrdersShortcut.vbs"
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> "%TEMP%\WorkOrdersShortcut.vbs"
echo oLink.TargetPath = "%~dp0distribution\start.bat" >> "%TEMP%\WorkOrdersShortcut.vbs"
echo oLink.WorkingDirectory = "%~dp0distribution" >> "%TEMP%\WorkOrdersShortcut.vbs"
echo oLink.Description = "Canal Informatique - Gestion des Interventions" >> "%TEMP%\WorkOrdersShortcut.vbs"
echo oLink.Save >> "%TEMP%\WorkOrdersShortcut.vbs"

REM Execute VBS script
cscript //nologo "%TEMP%\WorkOrdersShortcut.vbs" >nul 2>&1

REM Clean up
del "%TEMP%\WorkOrdersShortcut.vbs" 2>nul

echo.
echo ✅ SUCCESS! Desktop shortcut created!
echo.
echo 📋 Shortcut Details:
echo    📍 Location: Desktop
echo    🎯 Name: WorkOrders Management System
echo    🚀 Launches: Application with all dependencies
echo.
echo 🎉 You can now launch the application from your desktop!
echo.
echo 💡 To remove the shortcut later, just delete it from your desktop.
echo.

REM Ask if user wants to test the shortcut
choice /C YN /M "Do you want to test the shortcut now?"
if errorlevel 2 goto :end
if errorlevel 1 (
    echo.
    echo 🚀 Launching application from desktop shortcut...
    start "" "%USERPROFILE%\Desktop\WorkOrders Management System.lnk"
)

:end
echo.
echo Installation complete! Enjoy your WorkOrders Management System!
echo.
timeout /t 3 >nul
