@echo off
REM ===================================================================
REM  Copy everything needed for offline development onto a USB drive.
REM  Usage: scripts\copy-to-usb.bat F:\
REM ===================================================================

setlocal
cd /d "%~dp0.."

set "USB=%~1"
if "%USB%"=="" (
  echo ERROR: give the USB drive or folder.
  echo   Usage: scripts\copy-to-usb.bat F:\
  exit /b 1
)
if not exist "%USB%" (
  echo ERROR: path not found: %USB%
  exit /b 1
)

set "PROJECT=%CD%"
set "REPO=%USERPROFILE%\.m2\repository"

echo ============================================
echo  Copying to %USB%
echo ============================================
echo.

echo [1/3] Project (excluding target)...
robocopy "%PROJECT%" "%USB%\spring-starter" /E /XD "%PROJECT%\target" /NFL /NDL /NJH /NJS /NP
if errorlevel 8 goto :fail

echo [2/3] Maven repository (~1.7 GB, this takes a while)...
robocopy "%REPO%" "%USB%\m2-repository" /E /NFL /NDL /NJH /NJS /NP
if errorlevel 8 goto :fail

echo [3/3] Tomcat 11...
set "TOMCAT="
for /f "delims=" %%T in ('dir /b /ad /o-n "%CD%\..\apache-tomcat-11*" 2^>nul') do (
  if exist "%CD%\..\%%T\webapps" set "TOMCAT=%CD%\..\%%T&set TOMCAT_NAME=%%T"
)
if "%TOMCAT%"=="" (
  echo       WARNING: Tomcat 11 not found next to the project - copy it manually.
) else (
  robocopy "%TOMCAT%" "%USB%\%TOMCAT_NAME%" /E /XD "%TOMCAT%\logs" "%TOMCAT%\work" "%TOMCAT%\temp" /NFL /NDL /NJH /NJS /NP
  if errorlevel 8 goto :fail
)

echo.
echo ============================================
echo  DONE. USB layout:
echo    %USB%\spring-starter\
echo    %USB%\m2-repository\
echo    %USB%\apache-tomcat-11.0.x\
echo.
echo  On the offline PC see how-to-run.md section 2.
echo ============================================
exit /b 0

:fail
echo.
echo COPY FAILED. Check free space on %USB%  (need ~2 GB).
exit /b 1
