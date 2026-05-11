@echo off
echo Creating professional application icon...
echo.

REM Create a simple icon using PowerShell
powershell -Command "
Add-Type -AssemblyName System.Drawing

# Create bitmap for icon
$bitmap = New-Object System.Drawing.Bitmap 256, 256
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)

# Background - professional blue
$graphics.Clear([System.Drawing.Color]::FromArgb(41, 128, 185))

# Add white circle for background
$brush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
$graphics.FillEllipse($brush, 38, 38, 180, 180)

# Add blue gear symbol
$blueBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(41, 128, 185))
$graphics.FillEllipse($blueBrush, 78, 78, 100, 100)

# Add inner white circle
$graphics.FillEllipse($brush, 98, 98, 60, 60)

# Add center blue dot
$graphics.FillEllipse($blueBrush, 118, 118, 20, 20)

# Save as PNG first
$bitmap.Save('distribution\workorders-icon.png', [System.Drawing.Imaging.ImageFormat]::Png)
$graphics.Dispose()
$bitmap.Dispose()

Write-Host 'Icon created as PNG'
"

REM Convert PNG to ICO using PowerShell (if possible)
echo Converting to ICO format...
powershell -Command "
Add-Type -AssemblyName System.Drawing

try {
    # Load the PNG
    $png = [System.Drawing.Image]::FromFile('distribution\workorders-icon.png')
    
    # Create icon with multiple sizes
    $icon = New-Object System.Drawing.Icon([System.IO.Stream]::Null)
    
    # Save as ICO (simple method)
    $png.Save('distribution\workorders-icon.ico', [System.Drawing.Imaging.ImageFormat]::Icon)
    
    Write-Host 'ICO file created successfully'
} catch {
    Write-Host 'ICO creation failed, PNG will be used'
}
"

echo.
echo Icon creation complete!
echo Files created:
echo - distribution\workorders-icon.png
echo - distribution\workorders-icon.ico (if successful)
echo.
pause
