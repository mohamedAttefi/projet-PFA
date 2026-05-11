# Desktop Shortcut Creation Guide

## 🎯 Quick Setup

### For Windows Users

**Option 1: Use the Automated Script (Recommended)**
1. Double-click `CREATE_SHORTCUT.bat`
2. Wait for "SUCCESS!" message
3. Find "WorkOrders Management System" on your desktop
4. Double-click to launch

**Option 2: Manual Creation**
1. Right-click on desktop → New → Shortcut
2. Location: `[Project Folder]\distribution\start.bat`
3. Name: `WorkOrders Management System`
4. Click Finish

### For Linux/Mac Users

**Option 1: Use the Automated Script**
1. Open terminal in project folder
2. Run: `chmod +x create-desktop-shortcut.sh`
3. Run: `./create-desktop-shortcut.sh`
4. Find shortcut on desktop

**Option 2: Manual Creation**
1. Create `.desktop` file in `~/Desktop/`
2. Add content from `create-desktop-shortcut.sh`
3. Make executable: `chmod +x ~/Desktop/WorkOrders\ Management\ System.desktop`

## 📋 What the Shortcut Does

**✅ Launches Application:**
- Runs the correct startup script
- Sets proper working directory
- Includes all JavaFX dependencies
- Handles Java version checking

**✅ Professional Appearance:**
- Uses application icon
- Shows proper name and description
- Opens in correct directory

## 🔧 Troubleshooting

### Windows Issues

**"Cannot find start.bat"**
- Run script from main project folder
- Ensure `distribution` folder exists
- Check that `start.bat` is in `distribution` folder

**"Java not found"**
- Install Java 17+ from java.com
- Add Java to system PATH
- Restart computer after installation

**"Access denied"**
- Run as Administrator
- Check antivirus is not blocking
- Ensure desktop folder is accessible

### Linux/Mac Issues

**"Permission denied"**
- Run: `chmod +x distribution/start.sh`
- Run: `chmod +x create-desktop-shortcut.sh`
- Check folder permissions

**"Cannot execute"**
- Install OpenJDK 17+: `sudo apt install openjdk-17-jdk`
- Add to PATH if needed
- Restart terminal

## 🎉 Success!

Once created, you can:
- **Double-click** the desktop shortcut to launch
- **Right-click** → Properties to modify
- **Copy** shortcut to other locations
- **Pin** to taskbar or Start menu

## 📁 Files Created

- `CREATE_SHORTCUT.bat` - Windows automated script
- `create-desktop-shortcut.sh` - Linux/Mac automated script
- `create-shortcut.ps1` - PowerShell alternative
- `WorkOrders Management System.lnk` - Desktop shortcut (after running)

---

**Version:** 1.0  
**Compatible:** Windows 10+, Linux, macOS  
**Requirements:** Java 17+
