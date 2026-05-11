@echo off
echo Creating Desktop Shortcut for WorkOrders Management System...
echo.

REM Get current directory
set "APP_DIR=%~dp0distribution"
set "SHORTCUT_NAME=WorkOrders Management System"

REM Create VBS script to create shortcut
echo Set oWS = WScript.CreateObject("WScript.Shell") > "%TEMP%\CreateShortcut.vbs"
echo sLinkFile = oWS.SpecialFolders("Desktop") ^& "\%SHORTCUT_NAME%.lnk" >> "%TEMP%\CreateShortcut.vbs"
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> "%TEMP%\CreateShortcut.vbs"
echo oLink.TargetPath = "%APP_DIR%\start.bat" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.WorkingDirectory = "%APP_DIR%" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.Description = "Canal Informatique - Gestion des Interventions" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.IconLocation = "%APP_DIR%\workorders-1.0-SNAPSHOT.jar, 0" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.Save >> "%TEMP%\CreateShortcut.vbs"

REM Execute VBS script
cscript //nologo "%TEMP%\CreateShortcut.vbs"

REM Clean up
del "%TEMP%\CreateShortcut.vbs" 2>nul

echo.
echo ✅ Desktop shortcut created successfully!
echo 📁 Location: Desktop
echo 🎯 Name: %SHORTCUT_NAME%
echo.
echo You can now launch the application from your desktop!
echo.
pause
