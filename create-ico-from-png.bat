@echo off
echo Creating high-quality ICO file from PNG...
echo.

REM Use Python if available to create proper ICO
python --version >nul 2>&1
if %errorlevel% equ 0 (
    echo Using Python to create ICO...
    python -c "
from PIL import Image
import os

try:
    # Open PNG image
    img = Image.open('distribution/canal_logo.png')
    
    # Convert to different sizes for ICO
    sizes = [(256, 256), (128, 128), (64, 64), (48, 48), (32, 32), (16, 16)]
    icons = []
    
    for size in sizes:
        resized = img.resize(size, Image.Resampling.LANCZOS)
        icons.append(resized)
    
    # Save as ICO
    icons[0].save('distribution/canal_logo.ico', format='ICO', sizes=[(img.width, img.height) for img in icons])
    print('ICO created successfully with sizes:', [size[0] for size in sizes])
    
except Exception as e:
    print('Error creating ICO:', e)
    print('Falling back to PNG method')
"
    goto :end
)

REM Fallback: Copy PNG as ICO (Windows can use PNG as icon)
echo Creating fallback icon file...
copy distribution\canal_logo.png distribution\canal_logo.ico >nul 2>&1

:end
echo.
echo Icon creation complete!
echo Files created:
echo - distribution/canal_logo.ico
echo - distribution/canal_logo.png (original)
echo.
pause
