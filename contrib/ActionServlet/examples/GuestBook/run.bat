@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\GuestBook\config
start %BROWSER% http://localhost:8080/servlet/GuestBook
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\GuestBook\config\servlet.properties -d %AS_HOME%\examples\GuestBook\classes