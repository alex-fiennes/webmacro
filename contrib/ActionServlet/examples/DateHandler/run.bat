@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\DateHandler\config
start http://localhost:8080/servlet/DateHandler
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\DateHandler\config\servlet.properties