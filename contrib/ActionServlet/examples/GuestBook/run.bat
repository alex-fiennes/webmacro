@echo You should change path settings in ./properties/WebMacro.properties
@call ..\env.bat
@set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\GuestBook\classes;%AS_HOME%\examples\GuestBook\properties
explorer http://localhost:8080/servlet/GuestBook
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\GuestBook\properties\servlet.properties -d %AS_HOME%\examples\GuestBook\classes