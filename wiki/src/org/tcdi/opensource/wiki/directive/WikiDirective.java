/*
 * Created by IntelliJ IDEA.
 * User: e_ridge
 * Date: Jan 9, 2003
 * Time: 6:13:49 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tcdi.opensource.wiki.directive;

import org.webmacro.directive.Directive;
import org.webmacro.directive.DirectiveDescriptor;
import org.webmacro.directive.DirectiveBuilder;
import org.webmacro.engine.BuildContext;
import org.webmacro.engine.BuildException;
import org.webmacro.engine.Block;
import org.webmacro.FastWriter;
import org.webmacro.Context;
import org.webmacro.PropertyException;
import org.webmacro.*;
import org.tcdi.opensource.wiki.renderer.WikiURLRenderer;
import org.tcdi.opensource.wiki.renderer.WikiPageRenderer;
import org.tcdi.opensource.wiki.parser.WikiParser;
import org.tcdi.opensource.wiki.builder.WikiPageBuilder;
import org.tcdi.opensource.wiki.builder.DefaultPageBuilder;
import org.tcdi.opensource.wiki.WikiTermMatcher;
import org.tcdi.opensource.wiki.WikiPage;

import java.io.IOException;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.Constructor;

/**
 * <b>#wiki { }</b> -- render an expanded WebMacro block as a WikiPage.<p>
 *
 * Using a user-specified <code>WikiPageRenderer</code> (configured via WebMacro.properties),
 * renders a block of text as a Wiki page.  Example:<pre>
 *
 *      #set $name = "Eric Ridge"
 *      #wiki {
 *          Hello, *$name*
 *      }
 *
 * </pre>
 *
 * The above will output:<pre>
 *
 *         Hello, &lt;b&gt;Eric Ridge&lt;/&gt;
 *
 * </pre>
 *
 *
 * You can configure custom WikiPage- and WikiURL-Renderers to be used by this directive by
 * modifying your custom <code>WebMacro.properties</code> file:<pre>
 *
 *    wiki.PageRenderer = com.mycompany.wiki.MyWikiPageRenderer
 *    wiki.URLRenderer = com.mycompany.wiki.MyWikiURLRenderer
 *
 * </pre>
 *
 * Note that the default values for the above configuration settings are:<pre>
 *
 *    wiki.PageRenderer = org.tcdi.opensource.wiki.renderer.HTMLPageRenderer
 *    wiki.URLRenderer = org.tcdi.opensource.wiki.renderer.HTMLURLRenderer
 *
 * </pre>
 *
 * The default settings will give you Wiki-->HTML rendering capabilities.  If you prefer plain text
 * instead of HTML, you can use:<pre>
 *
 *    wiki.PageRenderer = org.tcdi.opensource.wiki.renderer.TextPageRenderer
 *    wiki.URLRenderer = org.tcdi.opensource.wiki.renderer.TextURLRenderer
 *
 * </pre>
 *
 * @author Eric B. Ridge (mailto:ebr@tcdi.com)
 * @version 1.0
 */
public class WikiDirective extends Directive {

    private static final int WIKI_BODY = 1;

    private static final Directive.ArgDescriptor[] _args = new Directive.ArgDescriptor[] {
        new Directive.BlockArg(WIKI_BODY),
    };
    private static final DirectiveDescriptor _desc = new DirectiveDescriptor("wiki", null, _args, null);

    public static DirectiveDescriptor getDescriptor() {
        return _desc;
    }

    /** a static cache of WikiPageRenderers that have been used. */
    private static final Map _rendererCache = Collections.synchronizedMap(new HashMap());
    private static final WikiTermMatcher _wikiTermMatcher = new WikiTermMatcher () {
        // the #wiki directive is only for text formatting.  It doesn't
        // actually understand SmashedTogetherWords
        public boolean isWikiTermReference(String word) {
            return false;
        }
    };

    /** the body of text to evaluate/expand, then parse as Wiki text */
    private Macro _body;
    /** the renderer we'll use.  comes from _wikiCache */
    private WikiPageRenderer _renderer;

    public Object build(DirectiveBuilder builder, BuildContext bc) throws BuildException {
        // The WikiPageRenderer to use is configurable via WebMacro.properties
        // if it's not found, we'll use a default renderer: org.tcdi.opensource.wiki.renderer.HTMLPageRenderer
        String pageRendererClassname = bc.getBroker().getSettings().getSetting("wiki.PageRenderer", "org.tcdi.opensource.wiki.renderer.HTMLPageRenderer");


        // if it hasn't already been done,
        // create and cache the page renderer
        _renderer = (WikiPageRenderer) _rendererCache.get(pageRendererClassname);
        if (_renderer == null) {
            try {
                // the WikiURLRenderer is also configurable via WebMacro.properties
                // if not specified, use the default url renderer: org.tcdi.opensource.wiki.renderer.HTMLURLRenderer
                String urlRendererClassname = bc.getBroker().getSettings().getSetting("wiki.URLRenderer", "org.tcdi.opensource.wiki.renderer.HTMLURLRenderer");
                _renderer = createPageRenderer (bc.getBroker(), pageRendererClassname, createURLRenderer (bc.getBroker(), urlRendererClassname));
                _rendererCache.put(pageRendererClassname, _renderer);
            } catch (Exception e) {
                throw new BuildException("Unable to create url or page renderer", e);
            }
        }

        _body = (Block) builder.getArg(WIKI_BODY, bc);
        return this;
    }

    /**
     * Evaluate/Expand our block of text and returned the Wiki-rendered output as
     * a java.lang.String.
     */
    public Object evaluate(Context context) throws PropertyException {
        FastWriter fw = FastWriter.getInstance(context.getBroker());
        String text;

        // first, expand the body of this directive into a String
        // so that a) all WebMacro #directives and $variables are recognized
        // and b) we can parse the output as Wiki text
        try {
            _body.write(fw, context);
        } catch (Exception e) {
            throw new PropertyException ("Unable to expand body", e);
        }
        text = fw.toString();


        // we use the WikiParser and the DefaultPageBuilder here
        // to parse and build a WikiPage that we'll soon render
        // using the renderer specified in WM's configuration
        WikiParser parser = new WikiParser (new java.io.StringReader(text));
        WikiPageBuilder builder = new DefaultPageBuilder (_wikiTermMatcher);
        WikiPage page;

        try {
            page = parser.parse(builder);
        } catch (org.tcdi.opensource.wiki.parser.ParseException e) {
            throw new PropertyException ("Unable to parse body into a WikiPage.", e);
        }

        // finally, render the WikiPage object into some kind of text form
        // using the configured renderer
        try {
            return _renderer.render(page);
        } catch (org.tcdi.opensource.wiki.renderer.HTMLPageRenderer.RenderException e) {
            throw new PropertyException ("Unable to render WikiPage using " + _renderer.getClass().getName());
        }
    }

    /**
     * Here we evaluate and expand our block into a plain text String.  This string is then
     * parsed and re-formatted using the configured WikiPageRenderer.  It is this wiki-formatted
     * block of text that gets written to the FastWriter.
     */
    public void write(FastWriter fastWriter, Context context) throws PropertyException, IOException {
        fastWriter.write(evaluate(context).toString());
    }

    //
    // private methods
    //

    /** dynamically create a WikiURLRenderer.  Locates the Class by using a WebMacro Broker */
    private static final WikiURLRenderer createURLRenderer (Broker b, String classname) throws Exception {
        return (WikiURLRenderer) b.classForName(classname).newInstance();
    }

    /** dynamically create a WikiPageRenderer.  Locates the Class by using a WebMacro Broker */
    private static final WikiPageRenderer createPageRenderer (Broker b, String classname, WikiURLRenderer urlRenderer) throws Exception {
        Class pageRendererClass = b.classForName(classname);
        Constructor ctor = pageRendererClass.getConstructor(new Class[] { WikiURLRenderer.class});
        return (WikiPageRenderer) ctor.newInstance (new Object[] { urlRenderer});
    }
}