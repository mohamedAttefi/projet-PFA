@echo off
echo Creating Windows executable wrapper...
echo.

REM Create a simple EXE launcher using PowerShell
powershell -Command "
Add-Type -AssemblyName System.Windows.Forms

# Create Windows Forms application that launches our JAR
$code = @'
using System;
using System.Diagnostics;
using System.Windows.Forms;
using System.IO;

class WorkOrdersLauncher
{
    [STAThread]
    static void Main()
    {
        try
        {
            // Get the directory where the EXE is located
            string appDir = Path.GetDirectoryName(Application.ExecutablePath);
            string batPath = Path.Combine(appDir, @"distribution\start.bat");
            
            // Check if start.bat exists
            if (!File.Exists(batPath))
            {
                MessageBox.Show(
                    \"Could not find start.bat in distribution folder.\n\nPlease ensure you're running from the correct directory.\n\nLooking for: \" + batPath,
                    \"WorkOrders Management System - Error\",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error
                );
                return;
            }
            
            // Launch the batch file hidden
            ProcessStartInfo psi = new ProcessStartInfo
            {
                FileName = batPath,
                WorkingDirectory = Path.Combine(appDir, \"distribution\"),
                UseShellExecute = false,
                CreateNoWindow = true,
                RedirectStandardOutput = true,
                RedirectStandardError = true
            };
            
            Process.Start(psi);
        }
        catch (Exception ex)
        {
            MessageBox.Show(
                \"Failed to start WorkOrders Management System:\n\n\" + ex.Message,
                \"WorkOrders Management System - Error\",
                MessageBoxButtons.OK,
                MessageBoxIcon.Error
            );
        }
    }
}
'@

    # Compile the C# code
    Add-Type -TypeDefinition $code -OutputAssembly \"distribution\WorkOrdersLauncher.exe\" -OutputType ConsoleApplication -ReferencedAssemblies System.Windows.Forms
    Write-Host \"EXE launcher created successfully!\"
"

echo.
echo Checking if EXE was created...
if exist "distribution\WorkOrdersLauncher.exe" (
    echo SUCCESS: WorkOrdersLauncher.exe created!
    echo Location: distribution\WorkOrdersLauncher.exe
) else (
    echo ERROR: Failed to create EXE launcher
    echo Falling back to batch method...
)

echo.
pause
