@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\LoginServlet\config
start %BROWSER% http://localhost:8080/servlet/LoginServlet
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\LoginServlet\config\servlet.properties