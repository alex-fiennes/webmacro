

var wm = new Packages.org.webmacro.WM("WebMacro_classpath.properties");

print("Found template.wm as "
    +wm.getClass().getClassLoader().getResource("templates/include.wm"));
print("Found /template.wm as "
    +wm.getClass().getClassLoader().getResource("/templates/include.wm"));


print(wm.getTemplate("include.wm"));
