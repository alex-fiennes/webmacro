@echo You should change path settings in ./properties/WebMacro.properties
@call ..\env.bat
@set CLASSPATH=%CLASSPATH%;%AS_HOME%\examples\LoginServlet\properties
explorer http://localhost:8080/servlet/LoginServlet
%JSDK_HOME%\bin\servletrunner -s %AS_HOME%\examples\LoginServlet\properties\servlet.properties -d %AS_HOME%\examples\LoginServlet\classes