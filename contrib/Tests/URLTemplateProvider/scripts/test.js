var org = Packages.org;

// var config = new org.log4j.BasicConfigurator();
//config.configure();

var CWD="./contrib/Tests/URLTemplateProvider/";
var url = new java.net.URL("file",null,CWD+"templates/include.wm");
var cl = url.getClass().getClassLoader();
print ("URL: classloader="+cl);
var wm;
if (cl == null) {
    wm = new org.webmacro.WM();
}
else {
    wm = new org.webmacro.WM(cl);
}
var broker = wm.getBroker();

var template = broker.getValue("template",url.toString());

var context = wm.getContext();

var v = new java.util.Vector();
v.addElement("val1");
v.addElement("val2");

var context = wm.getContext();
context.put("vec",v);

var w = new org.webmacro.FastWriter(java.lang.System.out,"UTF-8");
template.write(w,context);
w.flush();




