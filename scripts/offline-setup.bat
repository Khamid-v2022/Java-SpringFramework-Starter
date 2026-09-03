@echo off
REM ===================================================================
REM  Run on the OFFLINE PC, one time, after copying files from USB.
REM  Installs the Maven repository and offline settings.xml.
REM ===================================================================

setlocal
cd /d "%~dp0.."

echo ============================================
echo  Offline setup for spring-starter
echo ============================================
echo.

REM --- 1. Maven repository -------------------------------------------
set "TARGET_REPO=%USERPROFILE%\.m2\repository"

if exist "%TARGET_REPO%\org\springframework" (
  echo [1/2] Maven repository already present: %TARGET_REPO%
  goto :settings
)

set "SRC_REPO="
if exist "%~dp0..\..\m2-repository\org\springframework" set "SRC_REPO=%~dp0..\..\m2-repository"
if exist "%~dp0..\m2-repository\org\springframework"    set "SRC_REPO=%~dp0..\m2-repository"

if "%SRC_REPO%"=="" (
  echo [1/2] ERROR: could not find "m2-repository" next to the project folder.
  echo.
  echo       Expected layout on the USB / disk:
  echo         ...\m2-repository\
  echo         ...\spring-starter\
  echo.
  echo       Copy m2-repository manually to:
  echo         %TARGET_REPO%
  goto :fail
)

echo [1/2] Copying Maven repository...
echo       from: %SRC_REPO%
echo       to  : %TARGET_REPO%
if not exist "%USERPROFILE%\.m2" mkdir "%USERPROFILE%\.m2"
robocopy "%SRC_REPO%" "%TARGET_REPO%" /E /NFL /NDL /NJH /NJS /NP >nul
if errorlevel 8 goto :fail
echo       done.

:settings
REM --- 2. Offline settings.xml ---------------------------------------
echo [2/2] Installing offline settings.xml...
if exist "%USERPROFILE%\.m2\settings.xml" (
  echo       settings.xml already exists - keeping it.
) else (
  copy /Y "%CD%\offline\settings.xml" "%USERPROFILE%\.m2\settings.xml" >nul
  echo       copied to %USERPROFILE%\.m2\settings.xml
)

echo.
echo ============================================
echo  DONE. Next step:
echo    scripts\build-offline.bat
echo ============================================
exit /b 0

:fail
echo.
echo FAILED. See messages above.
exit /b 1
