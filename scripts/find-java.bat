@echo off
REM ===================================================================
REM  Finds a JDK 17+ and sets JAVA_HOME for the current cmd session.
REM  Called by the other scripts; you normally do not run this directly.
REM ===================================================================

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" goto :eof

REM --- Look for a JDK in the usual Windows locations -----------------
for %%D in (
  "C:\Program Files\Java"
  "C:\Program Files\Eclipse Adoptium"
  "C:\Program Files\Microsoft"
  "C:\Program Files\Amazon Corretto"
  "C:\Program Files\Zulu"
) do (
  if exist %%D (
    for /f "delims=" %%J in ('dir /b /ad /o-n %%D 2^>nul') do (
      if exist "%%~D\%%J\bin\javac.exe" (
        set "JAVA_HOME=%%~D\%%J"
        goto :found
      )
    )
  )
)

REM --- Fall back to a JDK shipped on the USB next to the project -----
if exist "%~dp0..\..\jdk\bin\javac.exe" (
  set "JAVA_HOME=%~dp0..\..\jdk"
  goto :found
)

echo ERROR: No JDK found. Install JDK 17+ or set JAVA_HOME manually:
echo   set JAVA_HOME=C:\Program Files\Java\jdk-21
exit /b 1

:found
echo Using JAVA_HOME=%JAVA_HOME%
goto :eof
