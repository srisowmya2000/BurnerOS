@echo off
REM Gradle wrapper batch script (lightweight).
SET SCRIPT_DIR=%~dp0
SET WRAPPER_DIR=%SCRIPT_DIR%gradle\wrapper
SET JAR=%WRAPPER_DIR%\gradle-wrapper.jar

IF NOT EXIST "%JAR%" (
  echo Warning: gradle-wrapper.jar not found at %JAR%
  echo You should run "gradle wrapper" on a machine with Gradle installed to generate the real wrapper jar.
)

java -jar "%JAR%" %*
