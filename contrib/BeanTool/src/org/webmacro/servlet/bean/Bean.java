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

public class Bean {
    private WebContext wc;
    public Bean(WebContext wc) {
        this.wc=wc;
    }
    public Object create(String name) {
        try {
            return Class.forName(name).newInstance();
        }
        catch (Exception e) {
            System.err.println("**** Error creating bean "+name+": "+e);
            return null;
        }
    }

    public String init(Object bean, String property) {
        return init(bean, property, property);
    }

    public String init(Object bean, String property, String parameter) {
        try {
            ServletRequest request = wc.getRequest();
            if ("*".equals(property)) {
                JspRuntimeLibrary.introspect(bean, request);
            }
            else {
    	        String value = request.getParameter(parameter);
	            JspRuntimeLibrary.introspecthelper(
                        bean, property, value,
                        request, parameter, true
                    );
            }
        } catch (org.apache.jasper.JasperException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return "";
    }


    public String toString() {
        return "Bean: "+super.toString();
    }
    
}
