var org = Packages.org;

// var config = new org.log4j.BasicConfigurator();
//config.configure();

var CWD="./contrib/Tests/URLTemplateProvider/";

var wm = new org.webmacro.WM();
var broker = wm.getBroker();

var url = new java.net.URL("file",null,CWD+"templates/include.wm");
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




