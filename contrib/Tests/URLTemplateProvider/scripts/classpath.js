

var wm = new Packages.org.webmacro.WM("WebMacro_classpath.properties");

print("Classpath is "+java.lang.System.getProperty("java.class.path"));

print("Found templates/template.wm as "
    +wm.getClass().getClassLoader().getResource("templates/include.wm"));

var url = wm.getClass().getClassLoader().getResource("/templates/include.wm");
if (url != null) {
    print("ERROR: Found /templates/template.wm as "+url);
}
else {
    print("/templates/include.wm not found - OK");
}


print(wm.getTemplate("templates/include.wm"));
