Steps to run the examples:

First make sure everything is built:
  cd ..
  ant dist
  cd examples
  ant rebuild

Next start a servlet engine such that the examples/webapp directory is loaded as a web application. Browse to you local servlet engine... Thats it!

The file server.xml.example is an example server.xml file I use to run the examples on my own machine. When I want to start tomcat I type: $TOMCAT_HOME/bin/tomcat.sh run -config ./server.xml
