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
import org.apache.jasper.runtime.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


// 
public class StockTickerBean {
    private String name = "";

    public void setName(String s) {
        System.out.println("set name="+s);

        name = s;
    }
    public String getName() {
        return name;
    }
    

    public Element getXML() throws Exception {

        URL url = new URL("http://www.xmltoday.com/examples/stockquote/getxmlquote.vep?s="+
            URLEncoder.encode(name)
        );

        return XMLTool.getRootElement(new InputSource(url.openStream()),false);

    }
    public String toString() 
    {
        try {
            return getXML().toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "XML-ERROR";
        }
    }
}
