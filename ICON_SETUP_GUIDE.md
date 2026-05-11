# Application Icon Setup Guide

## 🎯 Goal: Show Canal Informatique Logo in Application Window

### ✅ Current Implementation

**What We Did:**
- ✅ Added `canal_logo.png` to resources folder
- ✅ Enhanced `Main.java` with better icon loading
- ✅ Added multiple fallback paths for icon detection
- ✅ Added logging to track icon loading status
- ✅ Created `canal_logo.ico` for Windows compatibility

### 🔍 How to Verify Icon is Working

**Method 1: Check Console Output**
When you launch the application, check console for:
```
INFO: Window icon applied successfully: 256x256
```

**Method 2: Visual Check**
- Look at the **top-left corner** of the application window
- You should see **Canal Informatique logo** instead of default Java coffee cup

**Method 3: Taskbar Preview**
- Hover over the application icon in taskbar
- Should show Canal Informatique logo

### 🛠️ If Icon Doesn't Appear

**Possible Causes:**
1. **Icon file not found in JAR**
2. **JavaFX caching old icon**
3. **Windows icon cache**

**Solutions:**

**1. Clear Java Cache:**
```bash
# Delete Java cache
del /s /q "%USERPROFILE%\.cache\javafx\*"
```

**2. Restart Application:**
- Close application completely
- Wait 10 seconds
- Launch again

**3. Check Resource Loading:**
Look for these messages in console:
- `Could not find /images/canal_logo.png in resources`
- `Could not find /canal_logo.png in resources`
- `Window icon image failed to load`

### 📁 Icon Files Location

**In Resources:**
- `src/main/resources/images/canal_logo.png` (209KB)
- Included in JAR at `/images/canal_logo.png`

**In Distribution:**
- `distribution/canal_logo.ico` (Windows format)
- `distribution/workorders-icon.png` (PNG format)

### 🔧 Technical Details

**Icon Loading Process:**
1. **Main.java** tries to load `/images/canal_logo.png`
2. **Fallback:** tries `/canal_logo.png` if first fails
3. **JavaFX** applies icon to window title bar
4. **Windows** shows icon in taskbar and alt-tab

**Supported Formats:**
- ✅ **PNG** - Primary format, works with JavaFX
- ✅ **ICO** - Windows native format (fallback)
- ✅ **Multiple sizes** - Automatic scaling

### 🎯 Expected Result

**What You Should See:**
- 🏢 **Window Title Bar:** Canal Informatique logo
- 📋 **Taskbar:** Canal Informatique logo  
- 🔄 **Alt-Tab:** Canal Informatique logo
- 📱 **Desktop Shortcut:** Canal Informatique logo

### 🚀 Testing Steps

1. **Launch application** using desktop shortcut
2. **Check console** for "Window icon applied successfully" message
3. **Look at window** top-left corner for logo
4. **Verify taskbar** icon matches

### 📞 Troubleshooting

**If icon still doesn't appear:**
1. **Check console output** for error messages
2. **Verify JAR** contains icon file
3. **Clear Java cache** and restart
4. **Try different Java version** if needed

---

**Status:** ✅ Implementation Complete  
**Next:** Test and verify icon appears correctly  
**Support:** Check console logs for detailed error information
