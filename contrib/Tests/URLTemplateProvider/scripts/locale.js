var org = Packages.org;
// var wm = new org.webmacro.WM("./conf/WebMacro_locale.properties");
var wm = new org.webmacro.WM();
var broker = wm.getBroker();

var t = wm.getTemplate("file:templates/webmacro/full{_en_GB}.wm");
print(t+" "+t.hashCode());
var t = wm.getTemplate("file:templates/webmacro/short{_en_GB}.wm");
print(t+" "+t.hashCode());
var t = wm.getTemplate("file:templates/webmacro/short{_en_CN}.wm");
print(t+" "+t.hashCode());
var t = wm.getTemplate("file:templates/webmacro/short{_en_GB}.wm");
print(t+" "+t.hashCode());
var t = wm.getTemplate("file:templates/webmacro/short{_en_DE}.wm");
print(t+" "+t.hashCode());
//wm.getTemplate("file:templates/webmacro/notexist{_en_GB}.wm");
//wm.getTemplate("./webmacro/include.wm");
