package org.webmacro;

public interface Visitable {
  public void accept(TemplateVisitor v);
}
