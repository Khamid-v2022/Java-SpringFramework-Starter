@echo off
REM Prepare a portable Maven repository for USB / offline use.
REM Run this ONCE while you still have internet.

setlocal
cd /d "%~dp0.."

set "MVN=mvn"
if exist "%CD%\.tools\apache-maven-3.9.11\bin\mvn.cmd" (
  set "MVN=%CD%\.tools\apache-maven-3.9.11\bin\mvn.cmd"
)

echo Using Maven: %MVN%
echo [1/3] Resolving project dependencies...
call "%MVN%" -U dependency:resolve
if errorlevel 1 goto :fail

echo [2/3] Resolving plugins...
call "%MVN%" -U dependency:resolve-plugins
if errorlevel 1 goto :fail

echo [3/3] Packaging WAR to verify and warm caches...
call "%MVN%" -U -DskipTests clean package
if errorlevel 1 goto :fail

echo.
echo SUCCESS.
echo Copy this project folder AND your local Maven repository to the USB:
echo   Project : %cd%
echo   Maven   : %USERPROFILE%\.m2\repository
echo.
echo On the offline PC:
echo   mvn -o -Dmaven.repo.local=E:\m2-repository clean package
echo.
echo See how-to-run.md for full offline instructions.
exit /b 0

:fail
echo FAILED. Fix network/Maven settings and retry.
exit /b 1
