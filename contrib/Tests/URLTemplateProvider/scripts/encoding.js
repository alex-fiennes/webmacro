var org = Packages.org;

var config = new org.log4j.BasicConfigurator();
config.configure();

var wm = new org.webmacro.WM();
var broker = wm.getBroker();

function printTemplate(path, enc) {
    var url = new java.net.URL("file",null,path);
    var template = broker.getValue("template",":"+enc+":"+url.toString());
    var context = wm.getContext();
    
    var w = java.lang.System.out; // new java.io.PrintWriter(java.lang.System.out);
    var fw = new org.webmacro.FastWriter(w,"ISO8859_1");
    fw.write(template+"\n");
    fw.write("===============\n");
    template.write(fw,context);
    fw.write("===============\n");
    fw.flush();
}

printTemplate("templates/webmacro/encoding_iso8859_1.wm","ISO8859_1");
printTemplate("templates/webmacro/encoding_utf8.wm","UTF-8");
printTemplate("out.txt","utf-8");




