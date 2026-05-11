@echo off
title WorkOrders Management System

REM Minimize this window immediately
if not "%minimized%"=="" (
    set minimized=true
    start /min cmd /c "%~f0"
    exit
)

REM Launch the actual application
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "workorders-1.0-SNAPSHOT.jar;lib/*" com.company.Main

REM Exit silently
exit
