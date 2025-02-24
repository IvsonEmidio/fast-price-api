@echo off
echo Starting Fast Price API...

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -v >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Using Maven Wrapper instead...
    
    REM Build the project using Maven Wrapper
    call mvnw.cmd clean package -DskipTests
) else (
    REM Build the project using Maven
    call mvn clean package -DskipTests
)

if %errorlevel% neq 0 (
    echo Error: Build failed
    pause
    exit /b 1
)

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

REM Run the application
echo Starting application...
java -jar target/fast-price-api-0.0.1-SNAPSHOT.jar

pause
