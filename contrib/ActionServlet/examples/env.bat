@echo off
rem
rem Environment setup - called by compile.bat and run.bat files
rem
echo *** You may need to adjust the settings below (see env.bat) !!! ***
rem
echo on
@rem
@rem -----------------------------
@rem
set JAVA_HOME=c:\progra~1\jdk1.1.8
set JSDK_HOME=c:\progra~1\jsdk2.0
set WM_HOME=c:\progra~1\webmacro
set AS_HOME=c:\progra~1\ActionServlet
@rem
@rem -----------------------------
@rem
@set CLASSPATH=%JSDK_HOME%\lib\jsdk.jar;%WM_HOME%\webmacro.jar;%AS_HOME%\lib\ActionServlet.jar;%AS_HOME%\lib\jaxp.jar;%AS_HOME%\lib\parser.jar
