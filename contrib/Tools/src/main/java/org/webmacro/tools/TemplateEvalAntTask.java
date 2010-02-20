package org.webmacro.tools;
/**
 * Subclass of WMTemplateAntTask for naming
 * EvalTemplates as the ant task to run.
 * 
 * @author lane
 */
public class TemplateEvalAntTask extends WMTemplateAntTask
{

    public TemplateEvalAntTask ()
    {
        super();
    }
    
    /**
     * Returns org.webmacro.tools.EvalTemplates
     */
    protected String getMainClass ()
    {
        return "org.webmacro.tools.EvalTemplates";
    }

}
