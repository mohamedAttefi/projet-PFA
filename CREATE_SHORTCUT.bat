@echo off
echo Creating Desktop Shortcut for WorkOrders Management System...
echo.

REM Check if distribution folder exists
if not exist "distribution\start.bat" (
    echo Error: distribution folder not found!
    echo Please run this script from the main project folder
    pause
    exit /b 1
)

REM Create VBS script with proper syntax
echo Set oWS = CreateObject("WScript.Shell") > "%TEMP%\shortcut.vbs"
echo sLinkFile = oWS.SpecialFolders("Desktop") ^& "\WorkOrders Management System.lnk" >> "%TEMP%\shortcut.vbs"
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> "%TEMP%\shortcut.vbs"
echo oLink.TargetPath = "%~dp0distribution\WorkOrdersLauncher.vbs" >> "%TEMP%\shortcut.vbs"
echo oLink.WorkingDirectory = "%~dp0distribution" >> "%TEMP%\shortcut.vbs"
echo oLink.Description = "Canal Informatique - Gestion des Interventions" >> "%TEMP%\shortcut.vbs"
echo oLink.IconLocation = "%~dp0distribution\workorders-icon.png" >> "%TEMP%\shortcut.vbs"
echo oLink.Save >> "%TEMP%\shortcut.vbs"

REM Execute VBS script
cscript //nologo "%TEMP%\shortcut.vbs"

REM Clean up
del "%TEMP%\shortcut.vbs"

echo.
echo SUCCESS! Desktop shortcut created!
echo Location: Desktop
echo Name: WorkOrders Management System
echo.
echo You can now launch the application from your desktop!
pause
