package org.webmacro.broker;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.webmacro.Broker;
import org.webmacro.Context;
import org.webmacro.Log;
import org.webmacro.PropertyException;
import org.webmacro.util.Settings;


/**
 * DefaultContextAutoLoader
 *
 * @author Brian Goetz
 */
public class DefaultContextAutoLoader implements ContextAutoLoader {

    private Log _log;
    private Broker _broker;
    private Map _factories = new ConcurrentHashMap();

    public void init(Broker b, String name) {
        _broker = b;
        _log = b.getLog("broker");
        loadFactories(name);
    }

    public Object get(String name, Context context) throws PropertyException {
        Object o = _factories.get(name);
        if (o == null)
            return null;
        ContextObjectFactory f = (ContextObjectFactory) o;
        return f.get(context);
    }

    /**
     * Load the context tools listed in the supplied string. See
     * the ComponentMap class for a description of the format of
     * this string.
     */
    private void loadFactories (String keyName)
    {
        _broker.getSettings().processListSetting(keyName, new AutoContextSettingsHandler());
    }

    private class AutoContextSettingsHandler extends Settings.ListSettingHandler
    {
        public void processSetting (String settingKey, String settingValue)
        {
            try
            {
                addAutoVariable(settingKey, settingValue);
            }
            catch (Exception e)
            {
                _log.error("Automatic variable (" + settingValue + ") failed to load", e);
            }
        }
    }

    /**
     * Attempts to instantiate the tool using two different constructors
     * until one succeeds, in the following order:
     * <ul>
     * <li>new MyTool(String key)</li>
     * <li>new MyTool()</li>
     * </ul>
     * The key is generally the unqualified class name of the tool minus the
     * "Tool" suffix, e.g., "My" in the example above
     * The settings are any configured settings for this tool, i.e, settings
     * prefixed with the tool's key.
     * <br>
     * NOTE: keats - 25 May 2002, no tools are known to use the settings mechanism.
     * We should create an example of this and test it, or abolish this capability!
     * Abolished -- BG
     */
    private void addAutoVariable(String toolName, String className) {
        Class c;
        try
        {
            c = _broker.classForName(className);
        }
        catch (ClassNotFoundException e)
        {
            _log.warning("Context: Could not locate class for context tool " + className);
            return;
        }
        if (toolName == null || toolName.equals(""))
        {
            toolName = className;
            int start = 0;
            int end = toolName.length();
            int lastDot = toolName.lastIndexOf('.');
            if (lastDot != -1)
                start = lastDot + 1;
            toolName = toolName.substring(start, end);
        }

        Constructor ctor = null;
        Constructor[] ctors = c.getConstructors();
        Class[] parmTypes = null;
        Object instance = null;

        // check for 1 arg (String) constructor
        for (int i = 0; i < ctors.length; i++)
        {
            parmTypes = ctors[i].getParameterTypes();
            if (parmTypes.length == 1 && parmTypes[0].equals(String.class))
            {
                ctor = ctors[i];
                Object[] args = {toolName};
                try
                {
                    instance = ctor.newInstance(args);
                }
                catch (Exception e)
                {
                    _log.error("Failed to instantiate tool "
                               + toolName + " of class " + className + " using constructor "
                               + ctor.toString(), e);
                }
            }
        }
        if (instance == null)
        {
            // try no-arg constructor
            try
            {
                instance = c.newInstance();
            }
            catch (Exception e)
            {
                _log.error("Unable to construct tool " + toolName + " of class " + className, e);
                return;
            }
        }
        if (instance instanceof ContextObjectFactory) {
            _factories.put(toolName, instance);
            _broker.registerAutoContextVariable(toolName, this);
            _log.info("Registered automatic variable factory " + toolName);
        }
        else {
            _log.warning("Context object " + toolName + " is not of type ContextObjectFactory");
        }
    }
}
