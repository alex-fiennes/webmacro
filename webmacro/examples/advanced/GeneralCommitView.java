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
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import org.opendoors.util.Console;
import org.opendoors.adt.OrderedSet;
import org.opendoors.store.*;
import com.acctiva.views.*;
import com.acctiva.schema.Fields;
import com.acctiva.bo.*;

/**
 * A General Commit View is a subclass of General View with
 * a commit phase at the end allowing the user to review
 * the changes. This is a much harder class to implement subclass
 * and therefore you should first consider subclassing GeneralView.
 */
abstract public class GeneralCommitView extends GeneralView {

	/** A repeating set of notes on the person. */
	public GeneralCommitView() { super();	}

  /**
   * What is the commit handler.
   */
  abstract public CommitActionHandler getCommitHandler(CommitView v);

  /**
   * The attributes to lay out the commit with.
   */
	abstract public Attributes getCommitAttributes();

  /** The commit title. */
	abstract public String getCommitTitle();

	protected OrderedSet createViews(HttpServletRequest request, String contextLink) throws Exception {
	  OrderedSet relatedViews = super.createViews(request, contextLink);

		// the commit view:
		CommitView c = new CommitView(null, getCommitAttributes(), relatedViews);
		c.initView(null, getCommitTitle(), ActionVerb.NULL, contextLink);
		CommitActionHandler cah = getCommitHandler(c);
		ViewActionHandler[] c1 = null;
		if (cah == null) {
  		c1 = new ViewActionHandler[]
  		      {new CancelActionHandler(ActionVerb.CANCEL, c),
						new ViewActionHandler(ActionVerb.PREVIOUS, c) };
		}
		else {
		  c1 = new ViewActionHandler[]
		            {new CancelActionHandler(ActionVerb.CANCEL, c),
								new ViewActionHandler(ActionVerb.PREVIOUS, c), cah};
		}
		c.addActionHandler(c1);
		c.commitView = true;
		
		return relatedViews;
	}


	public String toString() {return "GeneralCommitView";}
				
}

