@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\Calculators\config
start %BROWSER% http://localhost:8080/servlet/Calculators
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\Calculators\config\servlet.properties