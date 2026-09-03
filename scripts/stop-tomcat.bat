@echo off
REM ===================================================================
REM  Stop Tomcat 11.
REM  Usage: scripts\stop-tomcat.bat "C:\apache-tomcat-11.0.x"
REM ===================================================================

setlocal
cd /d "%~dp0.."

set "TOMCAT=%~1"
if "%TOMCAT%"=="" set "TOMCAT=%CATALINA_HOME%"

if "%TOMCAT%"=="" (
  for /f "delims=" %%T in ('dir /b /ad /o-n "%CD%\..\apache-tomcat-11*" 2^>nul') do (
    if exist "%CD%\..\%%T\webapps" set "TOMCAT=%CD%\..\%%T"
  )
)

if not exist "%TOMCAT%\bin\shutdown.bat" (
  echo ERROR: Tomcat not found. Pass the folder:
  echo   scripts\stop-tomcat.bat "C:\apache-tomcat-11.0.x"
  exit /b 1
)

call "%~dp0find-java.bat"
if errorlevel 1 exit /b 1

set "CATALINA_HOME=%TOMCAT%"
pushd "%TOMCAT%\bin"
call shutdown.bat
popd

echo Tomcat stop requested.
exit /b 0
