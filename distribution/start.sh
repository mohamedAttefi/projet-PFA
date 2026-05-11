#!/bin/bash

echo "Starting WorkOrders Management System..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher and try again"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or higher is required"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "workorders-1.0-SNAPSHOT.jar" ]; then
    echo "Error: workorders-1.0-SNAPSHOT.jar not found"
    echo "Please ensure you're running this script from the correct directory"
    exit 1
fi

# Check if lib folder exists
if [ ! -d "lib" ]; then
    echo "Error: lib folder not found"
    echo "Please ensure you're running this script from the correct directory"
    exit 1
fi

# Start the application using JavaFX modules
echo "Launching application..."
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "workorders-1.0-SNAPSHOT.jar:lib/*" com.company.Main

# Check exit code
if [ $? -ne 0 ]; then
    echo
    echo "Application exited with error code $?"
    echo "Trying alternative launch method..."
    java -cp "workorders-1.0-SNAPSHOT.jar:lib/*" com.company.Main
    
    if [ $? -ne 0 ]; then
        echo
        echo "Both launch methods failed. Please check:"
        echo "- Java 17+ is installed"
        echo "- All JAR files are present"
        echo "- Database is accessible"
        exit 1
    fi
fi
