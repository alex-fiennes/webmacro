/*
 * TestTemplate.java
 *
 * Created on March 25, 2001, 8:04 PM
 */

import org.webmacro.Context;

/**
 * TestTemplate for testing whitespace.<p>
 * 
 * 
 * @author  e_ridge
 * @version 
 */
public class TestTemplate extends AbstractTemplateEvaluator
{
   public void stuffContext (Context c) throws Exception
   {
	int[] numbers = {1,2,3,4,5};
	c.put ("Numbers", numbers);
   }
}
