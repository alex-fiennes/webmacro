@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\OutputVars\config
start http://localhost:8080/servlet/OutputVars
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\OutputVars\config\servlet.properties