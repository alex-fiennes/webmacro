package org.webmacro.servlet.bean;

import org.webmacro.*;
import org.webmacro.servlet.*;

public class StockTickerServlet extends WMServlet
{

   public Template handle(WebContext context) throws HandlerException
   {

      Template view;

      try {
         view = getTemplate("stockticker.wm");

      } catch (NotFoundException e) {
         throw new HandlerException(e.getClass().getName()+" "+e.getMessage());
      }

      return view;
   }

}

