package org.webmacro.tools;

public class TemplateEvalAntTask extends WMTemplateAntTask {

   public TemplateEvalAntTask() {
      super();
   }

   protected String getMainClass() {
      return "org.webmacro.tools.EvalTemplates";
   }

}
