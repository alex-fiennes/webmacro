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

/**
 * The base class for all classes which can be requested to create
 * a view.
 */
abstract public class ViewFactory {

	/** Create the view associated with the implementation. */
	abstract public View createView(HttpServletRequest request,
	                      String contextLink) throws Exception;


}
