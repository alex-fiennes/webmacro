@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\Calculator\config
explorer http://localhost:8080/servlet/Calculator
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\Calculator\config\servlet.properties