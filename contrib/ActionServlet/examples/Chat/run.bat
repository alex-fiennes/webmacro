@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\Chat\config;%AS_HOME%\examples\Chat\lib\SimpleText.jar
start http://localhost:8080/servlet/Chat
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\Chat\config\servlet.properties -d %AS_HOME%\examples\Chat\classes