var wm = new Packages.org.webmacro.WM();

print(wm.getTemplate("file:./contrib/Tests/URLTemplateProvider/templates/include.wm"));

java.lang.Thread.sleep(500);

print(wm.getTemplate("file:./contrib/Tests/URLTemplateProvider/templates/include.wm"));

java.lang.Thread.sleep(1500);

print(wm.getTemplate("file:./contrib/Tests/URLTemplateProvider/templates/include.wm"));
