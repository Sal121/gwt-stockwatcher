@java -Xmx256M -cp "%~dp0\src;%~dp0\bin;%~dp0\lib\gwt-user.jar;%~dp0\lib\gwt-dev-windows.jar;%~dp0\lib\gwt-visualization.jar" com.google.gwt.dev.GWTCompiler -out "%~dp0\www" %* com.jsvest.crm.acl.StockWatcher