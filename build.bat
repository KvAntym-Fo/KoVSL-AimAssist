@echo off
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle is not installed or not in PATH.
  echo Open this folder in IntelliJ IDEA as a Gradle project, or install Gradle.
  pause
  exit /b 1
)
gradle build
pause
