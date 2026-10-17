@echo off
echo ====================================================================
echo        SimUPI - UPI Payment Simulation Web Application
echo ====================================================================
echo.

set "LOCAL_MVN=%~dp0.maven\apache-maven-3.9.6\bin\mvn.cmd"

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo [INFO] System Maven found. Starting Spring Boot...
    call mvn spring-boot:run
) else if exist "%LOCAL_MVN%" (
    echo [INFO] Portable Maven found at %LOCAL_MVN%. Starting Spring Boot...
    call "%LOCAL_MVN%" spring-boot:run
) else (
    echo [ERROR] Maven not found.
    pause
)
