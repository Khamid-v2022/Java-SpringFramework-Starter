@echo off
REM ===================================================================
REM  Deploy the WAR to Tomcat 11 and start it.
REM  Usage: scripts\deploy-tomcat.bat "C:\apache-tomcat-11.0.x"
REM  If the path is omitted, CATALINA_HOME or a sibling
REM  apache-tomcat-11.* folder is used.
REM ===================================================================

setlocal
cd /d "%~dp0.."

REM --- Locate Tomcat -------------------------------------------------
set "TOMCAT=%~1"
if "%TOMCAT%"=="" set "TOMCAT=%CATALINA_HOME%"

if "%TOMCAT%"=="" (
  for /f "delims=" %%T in ('dir /b /ad /o-n "%CD%\..\apache-tomcat-11*" 2^>nul') do (
    if exist "%CD%\..\%%T\webapps" set "TOMCAT=%CD%\..\%%T"
  )
)

if "%TOMCAT%"=="" (
  echo ERROR: Tomcat 11 folder not found.
  echo   Usage: scripts\deploy-tomcat.bat "C:\apache-tomcat-11.0.x"
  exit /b 1
)
if not exist "%TOMCAT%\webapps" (
  echo ERROR: not a Tomcat folder ^(no webapps^): %TOMCAT%
  exit /b 1
)
if not exist "%CD%\target\spring-starter.war" (
  echo ERROR: WAR not found. Run scripts\build-offline.bat first.
  exit /b 1
)

REM --- Need a JDK/JRE for Tomcat -------------------------------------
call "%~dp0find-java.bat"
if errorlevel 1 exit /b 1

echo Tomcat: %TOMCAT%
echo Removing old deployment...
if exist "%TOMCAT%\webapps\spring-starter.war" del /Q "%TOMCAT%\webapps\spring-starter.war"
if exist "%TOMCAT%\webapps\spring-starter"     rmdir /S /Q "%TOMCAT%\webapps\spring-starter"

echo Copying WAR to Tomcat...
copy /Y "%CD%\target\spring-starter.war" "%TOMCAT%\webapps\" >nul
if errorlevel 1 exit /b 1

echo Starting Tomcat...
set "CATALINA_HOME=%TOMCAT%"
pushd "%TOMCAT%\bin"
call startup.bat
popd

echo.
echo ============================================
echo  Deployed. Wait ~15 seconds, then open:
echo    http://localhost:8080/spring-starter/products
echo.
echo  To stop Tomcat:
echo    scripts\stop-tomcat.bat "%TOMCAT%"
echo ============================================
exit /b 0
