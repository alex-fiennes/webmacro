
package org.webmacro.engine;

import org.webmacro.*;

/**
  * Directives must implement this interface and obey the following 
  * contract:
  * <p>
  * 1. The name of the class must end with the word Directive
  * <p>
  * 2. The directive must have a build method. The signature of 
  * this method will be analyzed to determine what kind of parsing 
  * needs to be done for this directive--by inspecting what parameters
  * build takes, the parser determines what tokens this directive 
  * requires, and therefore how to parse it.
  * <p>
  * The signature must have the form: <pre>
  *    public static Object build(
  *         BuildContext rc,
  *         [ Condition cond | ( Object target, [ Object source, ] ]
  *         [ Macro block | String text ],
  *         [ Object subordinate ]
  *       );
  * </pre>
  * <p>
  * In other words, all directives have a BuildContext, followed 
  * by an optional Condition or a Target, and a Target may optionally
  * be followed by a Source, a directive may also have a Macro (a 
  * block in particular) or a text (an unparsed block), and it may
  * have a subordinate directive.  <p>
  * <p>
  * If a directive has a text, then it is responsible for parsing the 
  * contents of the text itself. In this case, it must also provide a 
  * static method with the following signature:<pre>
  *    public String getMarker(Object target, Object source)
  * </pre>The marker will be used to determine where the block ends. 
  * Both the target and source passed to getMarker() may be null.
  * <b>NOTE: the target and source that the getMarker() method sees are
  * pre-build phase. Thus, if they contain any variable references or
  * other complex strings, they may show up as objects of type Build. 
  * You can only extract information out of these if the arguments to 
  * your method are simple terms, or quoted strings with no internal 
  * variables.</b>
  * <p>
  * For example, an IfDirective has a build signature like this"
  * <pre>public static Object 
  * build(BuildContext c, Condition cond, Macro ifBlock, Object else)</pre>
  * The subordinate, if present, may be passed as a null--indicating that it
  * did not exist in the input. 
  * <p>
  * If a directive has a source, it must have a String getVerb() method 
  * returning the name of that source. If it has a subordinate, it 
  * must have a String[] getSubordinateNames() method which returns its name. 
  * In both cases the return value must be a String.
  * <p>
  * In the case of the if-directive, the name of the subordinate 
  * would be "else".
  * <p>
  * When analyzing build arguments, the analyzer is greedy--thus 
  * the signature build(Object,Object) is target and source, not
  * target and subordinate; and the signature build(Object) similarly
  * means target. This implies that you cannot have a directive with
  * a target and a subordinate without also having either a block or a 
  * source--otherwise the second Object will be considered a source.
  * <p>
  * The build method should throw a BuildException if something
  * goes wrong.
  */
public interface Directive extends Macro
{

}
