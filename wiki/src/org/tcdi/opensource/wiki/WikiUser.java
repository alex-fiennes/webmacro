/** 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Wiki.
 * 
 * The Initial Developer of the Original Code is Technology Concepts 
 * and Design, Inc. 
 * Copyright (C) 2000 Technology Concepts and Design, Inc.  All
 * Rights Reserved.
 * 
 * Contributor(s): Lane Sharman (OpenDoors Software)
 *                 Justin Wells (Semiotek Inc.)
 *                 Eric B. Ridge (Technology Concepts and Design, Inc.)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU General Public License Version 2 or later (the
 * "GPL"), in which case the provisions of the GPL are applicable 
 * instead of those above.  If you wish to allow use of your 
 * version of this file only under the terms of the GPL and not to
 * allow others to use your version of this file under the MPL,
 * indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by
 * the GPL.  If you do not delete the provisions above, a recipient
 * may use your version of this file under either the MPL or the
 * GPL.
 * 
 * 
 * This product includes sofware developed by OpenDoors Software.
 * 
 * This product includes software developed by Justin Wells and Semiotek Inc. 
 * for use in the WebMacro ServletFramework (http://www.webmacro.org).
 */
package org.tcdi.opensource.wiki;

import java.util.Hashtable;
import java.io.Serializable;

/**
 * Represents a single user of a WikiSystem.
 * @author Eric B. Ridge
 */
public class WikiUser implements Serializable
{
   String _identifier;   // user id
   String _name;         // full name, ie Eric B. Ridge
   String _password;
   boolean _isModerator;
   
   Hashtable _attributes = new Hashtable ();
   
   static final long serialVersionUID = 0L;   
   
   static Object _sync = new Object ();
   static long _cntr = 0;
                                     
   
   public WikiUser (String name, String password)
   {
      this (name, password, false);  
   }
   
   public WikiUser (String name, String password, boolean isModerator)
   {
      _name = name;
      _password = password;
      _isModerator = isModerator;
      _identifier = createIdentifier ();
   }
   
   private String createIdentifier ()
   {
      int size = _name.length();
      StringBuffer buff = new StringBuffer ();
      for (int x=0; x<size; x++)
      {
         char c = _name.charAt (x);
         if (Character.isLetterOrDigit (c))
            buff.append (c);
      }
      
      synchronized (_sync)
      {
         return buff.toString () + (int) (java.lang.Math.random ()*10000) + ("" + _cntr++);
      }
   }
   
   
   public String getIdentifier ()          { return _identifier; }
   public String getName ()                { return _name; }
   public String getPassword ()            { return _password; }
   public String get (String key)          { return getAttribute (key); }
   public String getAttribute (String key) { return (String) _attributes.get (key.toLowerCase()); }
   public Hashtable getAttributes ()       { return _attributes; }
   public boolean getIsModerator ()        { return _isModerator; }
   
   
   public void setName (String name)                     { _name = name; }
   public void setPassword (String password)             { _password = password; }
   public String setAttribute (String key, String value) { return (String) _attributes.put (key.toLowerCase(), value); }
   public void setIsModerator (boolean isModerator)      { _isModerator = isModerator; }
   public void setIdentifier (String identifier)         { _identifier = identifier; }
      
   public boolean containsAttribute (String key) { return _attributes.containsKey (key.toLowerCase()); }
   public String removeAttribute (String key)    { return (String) _attributes.remove (key.toLowerCase()); }
}
