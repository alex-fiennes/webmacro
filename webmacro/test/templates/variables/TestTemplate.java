/*
 * TestTemplate.java
 *
 * Created on March 25, 2001, 8:04 PM
 */

import org.webmacro.Context;

/**
 *
 * @author  e_ridge
 * @version 
 */
public class TestTemplate extends AbstractTemplateEvaluator
{
    public static class TestObject
    {
        String firstName;
        String lastName;
        public TestObject (String firstName, String lastName);
        {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        public String getFirstName () { return firstName; }
        public String getLastName () { return lastName; }
        public String toString () { return firstName + " " + lastName; }
        
    }
    
    public void stuffContext (Context c) throws Exception
    {
        Object nullObject = null;
        
        c.put ("NullObject", nullObject);
        c.put ("TestObject", new TestObject ("Eric", "Ridge"));
        c.put ("FirstNameNull", new TestObject (null, "Ridge"));        
    }
}