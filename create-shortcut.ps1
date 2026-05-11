# PowerShell script to create desktop shortcut for WorkOrders Management System

Write-Host "Creating Desktop Shortcut for WorkOrders Management System..." -ForegroundColor Green
Write-Host ""

# Get paths
$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$AppPath = Join-Path $ScriptPath "distribution"
$ShortcutPath = [Environment]::GetFolderPath("Desktop")
$ShortcutFile = Join-Path $ShortcutPath "WorkOrders Management System.lnk"

# Create shell object
$WScriptShell = New-Object -ComObject WScript.Shell

# Create shortcut
$Shortcut = $WScriptShell.CreateShortcut($ShortcutFile)
$Shortcut.TargetPath = Join-Path $AppPath "start.bat"
$Shortcut.WorkingDirectory = $AppPath
$Shortcut.Description = "Canal Informatique - Gestion des Interventions"
$Shortcut.IconLocation = Join-Path $AppPath "workorders-1.0-SNAPSHOT.jar, 0"
$Shortcut.Save()

Write-Host ""
Write-Host "✅ Desktop shortcut created successfully!" -ForegroundColor Green
Write-Host "📁 Location: $ShortcutPath" -ForegroundColor Cyan
Write-Host "🎯 Name: WorkOrders Management System" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can now launch the application from your desktop!" -ForegroundColor Yellow
Write-Host ""
Read-Host "Press Enter to exit"
