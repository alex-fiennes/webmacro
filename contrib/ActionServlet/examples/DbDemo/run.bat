@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\DbDemo\config;%AS_HOME%\examples\DbDemo\lib\SimpleText.jar;%AS_HOME%\examples\DbDemo\lib\datatable.jar
start http://localhost:8080/servlet/DbDemo
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\DbDemo\config\servlet.properties