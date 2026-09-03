@echo off
REM ===================================================================
REM  Build the WAR on the OFFLINE PC (no internet needed).
REM  Uses the Maven bundled in .tools\ - no system Maven required.
REM ===================================================================

setlocal
cd /d "%~dp0.."

set "MVN=%CD%\.tools\apache-maven-3.9.11\bin\mvn.cmd"
if not exist "%MVN%" (
  echo ERROR: bundled Maven not found:
  echo   %MVN%
  echo Make sure you copied the whole project folder including .tools
  exit /b 1
)

call "%~dp0find-java.bat"
if errorlevel 1 exit /b 1

echo Building WAR offline...
call "%MVN%" -o -DskipTests clean package
if errorlevel 1 goto :fail

echo.
echo ============================================
echo  BUILD OK
echo  WAR: %CD%\target\spring-starter.war
echo.
echo  Next: scripts\deploy-tomcat.bat "C:\path\to\tomcat11"
echo ============================================
exit /b 0

:fail
echo.
echo BUILD FAILED.
echo Check that %%USERPROFILE%%\.m2\repository exists (run scripts\offline-setup.bat).
exit /b 1
