
package org.webmacro;

/**
  * A FilterTool is a factory which returns a Filter. A Filter is a 
  * Macro whose express purpose is to filter the results of another Macro.
  * The FilterTool interface is used to instantiate Filters as needed
  * on a page.
  * <p>
  * In addition to instantiating filters, a FilterTool must define 
  * the mapping of a property name list to a specific tool. For 
  * example, if a FilterTool applies to a property called $Customer
  * then it must determine how $Customer.Name is to be handled.
  */
public interface FilterTool {

  /**
    * Return the FilterTool which should be used to handle a
    * sub-property. Three options are available here: return a 
    * null to indicate no filtering, return self to indicate 
    * the same filtering, or return a different FilterTool to
    * indicate different handling for the sub-property.
    * @param name the name of the sub-property to be filtered
    * @return the FilterTool to be used for the sub-property, or null
    */
  public FilterTool getFilterTool(String name); 

  /**
    * Instantiate a new filter. There are several options for the
    * return value of this method: return null to drop the Macro 
    * from the input stream entirely; return the Macro itself to 
    * avoid filtering this particular case, or return some new 
    * Macro to replace the supplied Macro. The expectation is that
    * the returned Macro will execute the original and apply some
    * post-processing to it. 
    * @param source the Macro which this filter will post-process
    * @return the Macro wrapper to be executed in place of source
    */
  public Macro getFilter(Macro source);

}
