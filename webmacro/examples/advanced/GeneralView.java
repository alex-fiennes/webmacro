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
 * A General View is a subclass of this view which uses
 * headless views but no commit view. The headless views can
 * be multi-element views. Action handlers can be supplied to
 * provide persistence on the accept action if desired.
 * <p>
 * Note: The last view created is not identified as a commit
 * view. If the last view is to be a commit view, the client subclass
 * must make it so.
 * @see GeneralCommitView
 */
abstract public class GeneralView extends MultipleViewFactory implements Fields {

	/** A repeating set of notes on the person. */
	public GeneralView() { super();	}

  /** Subclasses must provide the value panels for each view. */
	abstract public FieldData[][] getDataPanels();

	/**
	 * Subclasses indicate if the view is a multi-view by setting the
	 * the value to true.
	 */
	abstract public boolean[] isMultiView();

  /**
   * When a view has multiple views, the layout of the
   * the rows is dictated by the a row meta panel.
   */
  abstract public MetaField[][] getMultiLayout();

  /**
   * When a view is a multi-view, there can be existing values
   * and they should be in the form of an array of attributes.
   * <p>
   * This can be blank in many cases.
   */
  abstract public Attributes[][] getMultiValues();


  /** Subclass provides the panel titles. */
	abstract public String[] getPanelTitles();

	protected OrderedSet createViews(HttpServletRequest request, String contextLink) throws Exception {
		OrderedSet relatedViews = new OrderedSet();

    FieldData[][] panel = getDataPanels();
    boolean[] multiView = isMultiView();
    Attributes[][] multiValues = getMultiValues();
    MetaField[][] listLayout = getMultiLayout();
    String[] title = getPanelTitles();

    for (int index = 0; index < panel.length; index++) {

      Attributes attributes = new Attributes();
		  // Creates the property elements.
  		HeadlessView p = new HeadlessView(null, attributes, relatedViews,
				multiView[index], listLayout[index]);
  		p.initView(panel[index], title[index], ActionVerb.NULL, contextLink);
  		if (multiView[index] && multiValues[index] != null) {
  		  p.appendMultiValue(multiValues[index]);
  		}
  		  
  		// if count == 1, add below else add next
  		ViewActionHandler[] actions = null;
  		if (panel.length == 1) {
    		ViewActionHandler[] vah = {new CancelActionHandler(ActionVerb.CANCEL, p),
	  							new FinishActionHandler(ActionVerb.FINISH, p)};
	      actions = vah;
	  	}
	    else {
	      if (index == 0) {
      		ViewActionHandler[] vah = {new CancelActionHandler(ActionVerb.CANCEL, p),
	  							new ViewActionHandler(ActionVerb.NEXT, p)};
	  			actions = vah;
	  		}
	  		else {
	  		  if (index < (panel.length-1)) {
        		ViewActionHandler[] vah = {new CancelActionHandler(ActionVerb.CANCEL, p),
								new ViewActionHandler(ActionVerb.PREVIOUS, p),
								new ViewActionHandler(ActionVerb.NEXT, p)};
						actions = vah;
					}
					else { // the last view in the panel list
          	ViewActionHandler[] vah = {new CancelActionHandler(ActionVerb.CANCEL, p),
								new ViewActionHandler(ActionVerb.PREVIOUS, p),
								new FinishActionHandler(ActionVerb.FINISH, p)};
						actions = vah;
					}
	  		}
	    } 
	  	p.addActionHandler( actions );

	  	if (multiView[index]) {
  		  ViewActionHandler[] create = {new AddItemActionHandler(ActionVerb.ADDITEM, p)};
	  	  ViewActionHandler[] edit = {new SaveItemActionHandler(ActionVerb.SAVEITEM, p),
		  							new CancelModifyItemActionHandler(ActionVerb.CANCELMODIFICATION, p)};
		    ViewActionHandler[] rlist = {new RemoveItemActionHandler(ActionVerb.REMOVEITEM, p),
				  					new ModifyItemActionHandler(ActionVerb.MODIFYITEM, p)};

		    p.setMultiActionList( rlist, edit, create, create ); // sets all values, initial state
		  }
		  p.contextLink = contextLink;
		}

		return relatedViews;
	}


	public String toString() {return "GeneralView";}
				
}

