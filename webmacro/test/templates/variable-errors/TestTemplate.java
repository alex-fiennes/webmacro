/*
 * TestTemplate.java
 *
 * Created on March 25, 2001, 8:04 PM
 */

import org.webmacro.Context;
import org.webmacro.engine.*;

/**
 * Tester for testing rouge variables
 * @author  e_ridge
 * @version 
 */
public class TestTemplate extends AbstractTemplateEvaluator
{
   public static class TestObject
   {
      String firstName;
      String lastName;                    
      public TestObject (String firstName, String lastName) {
         this.firstName = firstName;
         this.lastName = lastName;
      }
                 
      public String getFirstName () { return firstName; }
      public String getLastName () { return lastName; }
      public String toString () { return firstName + " " + lastName; }      
      public String returnNull () { return null; }      
      public void voidMethod() { ; }
      public String throwException () throws Exception { 
         throw new Exception ("boo!"); 
      }
   }
             
   /** 
   * the toString() method of this class returns null
   */
   public static class TestObject2 {
      public String toString () { return null; }  
   }
             
   public void stuffContext (Context c) throws Exception {
      Object nullObject = null;
                    
      c.put ("NullObject", nullObject);
      c.put ("TestObject", new TestObject ("Eric", "Ridge"));
      c.put ("TestObject2", new TestObject2 ());
   }
}
