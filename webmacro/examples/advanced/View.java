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
import org.opendoors.store.*;
import com.acctiva.bo.BusinessObject;

/**
 * All views extend from this base class.
 */
public interface View {

	/**
	 * Every view must be initialized with its fields.
	 */
	public void initView(FieldData[] templateFields, String localeLabel,
				ActionVerb initialAction, String contextLink) throws Exception;

   /**
    * Every view has a unique sequence number identifying it.	
 	*/
	public SeqNbr getViewIdentifier();
	
   /**
    * A view has a set of viewable fields to be rendered, processed.
    * <p>
    * These are the viewable fields as meta fields.
    * @return An array of the viewable fields.
    */
   public FieldData[] getViewableFields();

   /**
    * Current attributes at any time in lifecycle of view.
    */
   public Attributes getViewableAttributes();

   /**
    * Handle an http request on the current view.
    */
	public View dispatchAction(HttpServletRequest request);
   
}
