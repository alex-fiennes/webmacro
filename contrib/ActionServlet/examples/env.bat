@echo off
rem
rem *******************************************************************
rem ****************** DO NOT CHANGE THIS FILE MANUALLY! **************
rem *** See "install.txt" for information how to configure examples ***
rem *******************************************************************
rem
rem
rem Environment setup - called by compile.bat and run.bat files
rem
echo *** You may need to adjust the settings below (see env.bat) !!! ***
rem
echo on
@rem
@rem -----------------------------
@rem
set JSDK_HOME=C:\Progra~1\JSDK2.0
set WM_HOME=C:\Progra~1\webmacro
set AS_HOME=C:\Progra~1\ActionServlet
@rem
@rem -----------------------------
@rem
@set CLASSPATH=%JSDK_HOME%\lib\jsdk.jar;%WM_HOME%\webmacro.jar;%AS_HOME%\lib\ActionServlet.jar;%AS_HOME%\lib\jaxp.jar;%AS_HOME%\lib\parser.jar
