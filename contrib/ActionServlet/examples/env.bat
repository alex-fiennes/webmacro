@rem Environment setup - called by compile.bat and run.bat files
@rem
@echo >>> You may need to adjust the settings below!!! <<<
@rem
set JAVA_HOME=c:\progra~1\jdk1.1\bin
set JSDK_HOME=c:\progra~1\jsdk2.0
set AS_HOME=d:\as\ActionServlet
set CLASSPATH=%CLASSPATH%;%JSDK_HOME%\lib\jsdk.jar;c:\progra~1\webmacro\webmacro.jar;%AS_HOME%\lib\ActionServlet.jar;.