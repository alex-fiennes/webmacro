/*
 * Copyright (c) 1998, 1999 Semiotek Inc. All Rights Reserved.
 *
 * This software is the confidential intellectual property of
 * of Semiotek Inc.; it is copyrighted and licensed, not sold.
 * You may use it under the terms of the GNU General Public License,
 * version 2, as published by the Free Software Foundation. If you 
 * do not want to use the GPL, you may still use the software after
 * purchasing a proprietary developers license from Semiotek Inc.
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See the attached License.html file for details, or contact us
 * by e-mail at info@semiotek.com to get a copy.
 */


package org.webmacro.servlet.bean;

import org.webmacro.*;
import org.webmacro.servlet.*;
import org.webmacro.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xalan.xpath.XPathSupport;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XPathProcessorImpl;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;
import org.apache.xalan.xpath.xml.PrefixResolverDefault;
import org.apache.xalan.xpath.XObject;
import javax.xml.parsers.*;

/**
  * Provide Template with access to form data.
  */
public class XMLTool implements ContextTool
{
   
    public Object init(Context context) 
    throws PropertyException
    {
        try {
            WebContext wc = (WebContext) context;
            return this;
        } catch (ClassCastException ce) {
            throw new PropertyException(
                "BeanTool only works with WebContext: " + ce);
        }
    }

    public static final List getNodeList(Node node, String xpath) 
    throws SAXException 
    {
        NodeList nList = selectNodeList(node,xpath);
        int size = nList.getLength();
        List list = new ArrayList(size);
        System.out.println(xpath + ": found "+size+" nodes in "+node);
        for (int i=0; i<size; i++) {
            list.add(nList.item(i));
        }
        return list;
            
    }

   public void destroy(Object o) { }

  public static String getNodeValue(Node contextNode, String str)
    throws SAXException
  {
    return getNode(contextNode, str, contextNode).getNodeValue();
  }

  /**
   * Use an XPath string to select a single node. XPath namespace
   * prefixes are resolved from the context node, which may not
   * be what you want (see the next method).
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @return The first node found that matches the XPath, or null.
   */
  public static Node getNode(Node contextNode, String str)
    throws SAXException
  {
    return getNode(contextNode, str, contextNode);
  }

  /**
   * Use an XPath string to select a single node.
   * XPath namespace prefixes are resolved from the namespaceNode.
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
   * @return The first node found that matches the XPath, or null.
   */
  public static Node getNode(Node contextNode, String str, Node namespaceNode)
    throws SAXException
  {
    // Have the XObject return its result as a NodeSet.
    NodeList nl = selectNodeList(contextNode, str, namespaceNode);

    // Return the first node, or null
    return (nl.getLength() > 0) ? nl.item(0) : null;
  }

 /**
   * Use an XPath string to select a nodelist.
   * XPath namespace prefixes are resolved from the contextNode.
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @return A nodelist, should never be null.
   */
  public static NodeList selectNodeList(Node contextNode, String str)
    throws SAXException
  {
    return selectNodeList(contextNode, str, contextNode);
  }

 /**
   * Use an XPath string to select a nodelist.
   * XPath namespace prefixes are resolved from the namespaceNode.
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
   * @return A nodelist, should never be null.
   */
  public static NodeList selectNodeList(Node contextNode, String str, Node namespaceNode)
    throws SAXException
  {
    // Execute the XPath, and have it return the result
    XObject list = eval(contextNode, str, namespaceNode);

    // Have the XObject return its result as a NodeSet.
    return list.nodeset();

  }

 /**
   * Evaluate XPath string to an XObject.  Using this method,
   * XPath namespace prefixes will be resolved from the namespaceNode.
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
   * @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
   * @see org.apache.xalan.xpath.XObject
   * @see org.apache.xalan.xpath.XNull
   * @see org.apache.xalan.xpath.XBoolean
   * @see org.apache.xalan.xpath.XNumber
   * @see org.apache.xalan.xpath.XString
   * @see org.apache.xalan.xpath.XRTreeFrag
   */
  public static XObject eval(Node contextNode, String str)
    throws SAXException
  {
    return eval(contextNode, str, contextNode);
  }

 /**
   * Evaluate XPath string to an XObject.
   * XPath namespace prefixes are resolved from the namespaceNode.
   * The implementation of this is a little slow, since it creates
   * a number of objects each time it is called.  This could be optimized
   * to keep the same objects around, but then thread-safety issues would arise.
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
   * @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
   * @see org.apache.xalan.xpath.XObject
   * @see org.apache.xalan.xpath.XNull
   * @see org.apache.xalan.xpath.XBoolean
   * @see org.apache.xalan.xpath.XNumber
   * @see org.apache.xalan.xpath.XString
   * @see org.apache.xalan.xpath.XRTreeFrag
   */
  public static XObject eval(Node contextNode, String str, Node namespaceNode)
    throws SAXException
  {
    // Since we don't have a XML Parser involved here, install some default support
    // for things like namespaces, etc.
    // (Changed from: XPathSupportDefault xpathSupport = new XPathSupportDefault();
    //    because XPathSupportDefault is weak in a number of areas... perhaps
    //    XPathSupportDefault should be done away with.)
    XPathSupport xpathSupport = new XMLParserLiaisonDefault();
    
    if(null == namespaceNode)
      namespaceNode = contextNode;

    // Create an object to resolve namespace prefixes.
    // XPath namespaces are resolved from the input context node's document element
    // if it is a root node, or else the current context node (for lack of a better
    // resolution space, given the simplicity of this sample code).
    PrefixResolverDefault prefixResolver = new PrefixResolverDefault((namespaceNode.getNodeType() == Node.DOCUMENT_NODE)
                                                         ? ((Document)namespaceNode).getDocumentElement() :
                                                           namespaceNode);

    // Create the XPath object.
    XPath xpath = new XPath();

    // Create a XPath parser.
    XPathProcessorImpl parser = new XPathProcessorImpl(xpathSupport);
    parser.initXPath(xpath, str, prefixResolver);

    // Execute the XPath, and have it return the result
    return xpath.execute(xpathSupport, contextNode, prefixResolver);
  }



    public static Element getRootElement(InputSource source, boolean validate)
    throws ParserConfigurationException, SAXException, IOException 
    {
        

        DocumentBuilderFactory docBuilderFactory
            = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setValidating(validate);
        docBuilderFactory.setNamespaceAware(true);

        DocumentBuilder parser = docBuilderFactory.newDocumentBuilder();
        XMLErrorHandler errHandler = new XMLErrorHandler();
        parser.setErrorHandler(errHandler);

        Document document = parser.parse (source);
        if (errHandler.getErrorCount() > 0) throw new SAXException("Parsing errors occurred");

        return document.getDocumentElement();

    }

}


