#!/bin/bash

echo "Creating Desktop Shortcut for WorkOrders Management System..."
echo

# Get current directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$SCRIPT_DIR/distribution"
DESKTOP_DIR="$HOME/Desktop"
SHORTCUT_FILE="$DESKTOP_DIR/WorkOrders Management System.desktop"

# Check if distribution folder exists
if [ ! -d "$APP_DIR" ]; then
    echo "❌ Error: distribution folder not found!"
    echo "   Please run this script from the main project folder"
    echo "   (the folder containing the 'distribution' folder)"
    exit 1
fi

# Create desktop entry
cat > "$SHORTCUT_FILE" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=WorkOrders Management System
Comment=Canal Informatique - Gestion des Interventions
Exec=$APP_DIR/start.sh
Icon=$APP_DIR/workorders-1.0-SNAPSHOT.jar
Terminal=false
Categories=Office;Management;
StartupNotify=true
EOF

# Make shortcut executable
chmod +x "$SHORTCUT_FILE"

# Make start script executable
chmod +x "$APP_DIR/start.sh"

echo
echo "✅ Desktop shortcut created successfully!"
echo "📁 Location: $DESKTOP_DIR"
echo "🎯 Name: WorkOrders Management System"
echo
echo "You can now launch the application from your desktop!"
echo

# Ask if user wants to test
read -p "Do you want to test the shortcut now? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🚀 Launching application from desktop shortcut..."
    "$SHORTCUT_FILE"
fi

echo
echo "Installation complete! Enjoy your WorkOrders Management System!"
EOF
