var org = Packages.org;
var CWD="./contrib/Tests/URLTemplateProvider/";

function getTemplate(url) {
    var url = new java.net.URL("file",null,CWD+url);
    var t = broker.getValue("template",url.toString());
    print(t+" "+t.hashCode());
}


var wm = new org.webmacro.WM();
var broker = wm.getBroker();

getTemplate("templates/full{_en_GB}.wm");
getTemplate("templates/short{_en_GB}.wm");
getTemplate("templates/short{_en_CN}.wm");
getTemplate("templates/short{_en_DE}.wm");

getTemplate("templates/notexist{_en_GB}.wm");
