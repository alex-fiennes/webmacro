
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
  *         ( Condition cond | (Subject (Predicate[])? )?,
  *         ( Macro block | String text )?,
  *         ( Object subordinate )?
  *       );
  * </pre>
  * <p>
  * The build() method is the initializer for a directive, and it must
  * return a newly created object. It may also return a String, or a 
  * null if the object should not be included in the output stream. 
  * <p>
  * The arguments to the build method are:
  * <ul>
  * <li>A condition, or a subject. If a subject is present then there 
  * may optionally also be a Predicate array.
  * <li>An optional block of text. This is either a Macro (ie: a parsed
  * block of text), or a String (ie: an unparsed block of text). 
  * <li>An optional  subordinate directive. It is of type Object, since 
  * when it resolves itself it may actually return a String or a null, 
  * however this value represents the result of building a subordinate
  * directive.
  * </ul>
  * <p>
  * The other methods required for the Directive depend on the signature
  * of the build method as follows:
  * <ul>
  * <li>If the build method has a String text argument, then there must 
  * be a "public String getMarker(Object subject, Object object)" method
  * which can be used to retrieve an end-of-block marker for parsing. 
  * Both the subject and object passed to getMarker() may be null.
  * <p>
  * <b>NOTE: the subject and object that the getMarker() method sees are
  * pre-build phase. Thus, if they contain any variable references or
  * other complex strings, they may show up as objects of type Build. 
  * You can only extract information out of these if the arguments to 
  * your method are simple terms, or quoted strings with no internal 
  * variables.</b>
  * <p>
  * <pre>public static Object 
  * build(BuildContext c, Condition cond, Macro ifBlock, Object else)</pre>
  * The subordinate, if present, may be passed as a null--indicating that it
  * did not exist in the input. 
  * <p>
  * <li>If a directive has an Predicate array, it must have a 
  * "String[] getVerb()" method which can be used to find the names of 
  * the predicates that are possible. 
  * <li>If a directive has a subordinate, it 
  * must have a String[] getSubordinateNames() method which returns the 
  * names of the subordinate directives which may follow this one. These
  * will be loaded and introspected seperately by the DirectiveBuilder, and
  * may then appear as subordinate directives to this one.
  * </ul>
  * <p>
  * The build method may throw a BuildException if something
  * goes wrong.
  */
public interface Directive extends Macro
{

}
