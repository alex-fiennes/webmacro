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
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.opendoors.store.*;
import org.opendoors.util.Console;
import com.acctiva.bo.*;
import com.acctiva.intf.ExceptionReporting;

/**
 * All commit acction handers should derive from this class which does no
 * processing.
 */
public class CommitActionHandler extends ViewActionHandler {

	/**
	 * Every action handler must have a locale senstive label.
	 */
	public CommitActionHandler(ActionVerb verb, AbstractViewAgent handledView) {
		super(verb, handledView);
	}

	/**
	 * The commit handler should must implement this process handler.
	 */
	protected View processAction(HttpServletRequest request){
	  try {
  	  commitAction();
  	}
  	catch (Exception e) {
  		Console.error("CommitHandler. Unable to commit", this, e);
    }
  	handledView.actionTaken = this.verb;
    return handledView;
  }

  /** Subclasses should implement this adapter method. */
  protected void commitAction() throws Exception {}

}

