/*
* Copyright Acctiva Corporation, 2000.
*
* Software is governed and protected under the patent,
* copyright and trademark laws of governing countries
* and this code and embodiment herein has been registered in the
* the United States with the Patent and Trademark Office.
* 
* Right to use is strictly licensed by Acctiva corporation.
*
* Direct all questions and comments to support@acctiva.com.
*/
package com.acctiva.views;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.opendoors.store.*;
import org.opendoors.util.Console;
import com.acctiva.bo.*;
import com.acctiva.intf.ExceptionReporting;

/**
 * All commit acction handers should derive from this class which does no
 * processing.
 */
public class InsertInterimBOHandler extends CommitActionHandler {

  private String name, value;

	/**
	 * Every action handler must have a locale senstive label.
	 */
	public InsertInterimBOHandler(ActionVerb verb, AbstractViewAgent handledView) {
		super(verb, handledView);
	}

	/** Sets the name and value properties required. */
	public void setProperties(String name, String value) {
	  this.name = name;
	  this.value = value;
	}

  /**
   * The logic for processing the insert of
   * a interim business object in a background thread.
   */
  protected void commitAction() throws Exception {
    (new Thread() {
      public void run() {
        InterimBusinessObject ibo = createIBO();
        ibo.init();
        ibo.setProperty(name, value);
        insertActions(ibo);
        Map m = ibo.getMap();
        Enumeration e = handledView.getViewElements();
        while (e.hasMoreElements()) {
          AbstractViewAgent v = (AbstractViewAgent) e.nextElement();
          v.mapAttributeData(m);
        }
        try {
          ibo.create();
          iboCreated(ibo); // lifecycle
          System.out.println("Commit Complete of IBO in background thread=" + this + " " + ibo);
        }
        catch(Exception ex) {
          Console.error("Unable to create interim bo", ibo, ex);
        }
      }
    }).start();
  }

  /** Subclasses provide insert actions by implementing the adapter method. */
  protected void insertActions(InterimBusinessObject obj) {}

  /** Subclasses provide a subclass of an interim business object. */
  protected InterimBusinessObject createIBO() {
    return new InterimBusinessObject();
  }

  /** Subclasses can override once an interim business object has been created. */
  protected void iboCreated(InterimBusinessObject ibo) {}

}

