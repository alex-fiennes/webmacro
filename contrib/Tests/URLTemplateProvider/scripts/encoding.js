var org = Packages.org;
var CWD="./contrib/Tests/URLTemplateProvider/";

var wm = new org.webmacro.WM();
var broker = wm.getBroker();
var context = wm.getContext();

function getTemplate(url) {
    var url = new java.net.URL("file",null,CWD+url);
    var t = broker.getValue("template",url.toString());
    print(t+" "+t.hashCode());

    var w = new org.webmacro.FastWriter(java.lang.System.out,"ISO8859-1");
    t.write(w,context);
    w.flush();
}

getTemplate("templates/utf8/encoding_utf8.wm");
getTemplate("templates/iso8859_1/encoding_iso8859_1.wm");
getTemplate("templates/utf8/encoding_utf8.wm");
getTemplate("templates/native_ascii/encoding_native_ascii.wm");
