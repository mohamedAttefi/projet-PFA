' WorkOrders Management System Launcher
' Launches the application without showing terminal window

Option Explicit

Dim objShell, scriptPath, appPath

' Create shell object
Set objShell = CreateObject("WScript.Shell")

' Get the directory where this script is located
scriptPath = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
appPath = scriptPath & "\start.bat"

' Change to the distribution directory
objShell.CurrentDirectory = scriptPath

' Launch the application hidden
objShell.Run """" & appPath & """", 0, False

' Clean up
Set objShell = Nothing

' Exit script
WScript.Quit
