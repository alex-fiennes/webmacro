
package org.webmacro.resource;
import org.webmacro.*;
import java.util.Properties;

/**
  * A very simple provider which simply takes the config information
  * passed to it by the broker and returns it. 
  */
public class ConfigProvider implements Provider
{

   private Properties _config;

   public String getType() { return "config"; }

   public void init(Broker b, Properties config) throws InitException
   {  
      _config = config;
      if (_config == null) {
         throw new InitException("Attempt to init with no configuration");
      }
   }

   public void flush() { } 

   public void destroy() { _config = null; }

   public Object get(String key) throws NotFoundException 
   {
      Object o = _config.getProperty(key);
      if (o == null) {
         throw new NotFoundException("No config information for: " + key);
      }
      return o;
   }
}
