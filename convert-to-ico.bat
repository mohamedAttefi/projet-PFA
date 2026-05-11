@echo off
echo Converting PNG to ICO format...
echo.

REM Use ImageMagick if available (most reliable method)
where magick >nul 2>&1
if %errorlevel% equ 0 (
    echo Using ImageMagick to create ICO...
    magick distribution\workorders-icon.png -resize 256x256 -define icon:auto-resize=256,128,64,48,32,16 distribution\workorders-icon.ico
    echo ICO created successfully with ImageMagick
    goto :end
)

REM Try using PowerShell with System.Drawing
echo Trying PowerShell method...
powershell -Command "
try {
    Add-Type -AssemblyName System.Drawing
    $img = [System.Drawing.Image]::FromFile((Resolve-Path 'distribution\workorders-icon.png'))
    $img.Save((Join-Path (Get-Location) 'distribution\workorders-icon.ico'), [System.Drawing.Imaging.ImageFormat]::Icon)
    $img.Dispose()
    Write-Host 'ICO created with PowerShell'
} catch {
    Write-Host 'PowerShell method failed:', $_.Exception.Message
}
"

:end
echo.
echo Icon conversion complete!
echo Check distribution\workorders-icon.ico
echo.
pause
