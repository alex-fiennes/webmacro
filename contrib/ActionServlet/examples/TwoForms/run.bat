@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\TwoForms\config
start %BROWSER% http://localhost:8080/servlet/TwoForms
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\TwoForms\config\servlet.properties