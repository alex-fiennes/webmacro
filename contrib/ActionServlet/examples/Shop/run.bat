@echo off
call ..\env.bat
set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\Shop\config;%AS_HOME%\examples\Shop\classes;%AS_HOME%\examples\Shop\lib\hsqldb.jar;%AS_HOME%\examples\Shop\lib\jdbcpool-0.99.jar;%AS_HOME%\examples\Shop\lib\datatable.jar
start http://localhost:8080/servlet/Shop
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\Shop\config\servlet.properties -d %AS_HOME%\examples\Shop\classes