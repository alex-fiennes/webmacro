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

import java.io.Serializable;

/**
 * A WikiAttachment is a file attachment to a WikiPage.<p>
 * 
 * An attachment contains a name, a filename, a mime type, and the
 * title of the WikiPage to which it belongs.  It does <b>not</b>
 * contain the actual bytes of the attachment.  It is the responsibility
 * of the storage system to keep up with the bytes.<p>
 * 
 * An attachment has no concept of how or where it is stored.  However,
 * for convenience, the method getUniqueName() is provided  It ensures a 
 * unique name for the attachment within it's WikiPage.  It may also be 
 * safe to assume it provides a unique name across the entire WikiSystem.  
 * This assumes however that no two WikiPages can have the same 
 * name . . . probably a safe assumption.<p>
 * 
 * @author Eric B. Ridge
 * */
public class WikiAttachment implements Serializable, Cloneable
{
   private String _name;
   private String _filename;
   private String _mimeType;
   private String _parentWikiPageTitle;
   private String _identifier = null;
   
   static final long serialVersionUID = 0L;
   
   /**
    * Constructor to specify all properties of a WikiAttachment<p>
    *
    * @param name the Name of this WikiAttachment
    * @param mimeType the mime-type of this attachment (ie, text/html, application/zip, image/gif, etc)
    * @param filename the filename of the attachment bytes.  This should <b>not</b> contain
    *                 path information of ANY KIND!
    * @param parentWikiPageTitle the title of the WikiPage that the attachment belongs to
    */
   public WikiAttachment (String name, String mimeType, String filename, String parentWikiPageTitle)
   {
      _name = name;
      _mimeType = mimeType;
      _filename = filename;
      _parentWikiPageTitle = parentWikiPageTitle;
      
      createIdentifier ();
   }

   /**
    * clone this WikiAttachment
    */
   public Object clone ()
   {
      Object cloned = null;
      
      try { cloned = super.clone (); }
      catch (CloneNotSupportedException cnse)
      {
         throw new RuntimeException ("Clone of WikiAttachment failed: " + cnse.toString ());
      }
      
      return cloned;  
   }
   
   //
   // get methods
   //
   
   /**
    * @return the name of this attachment
    */
   public String getName ()                  { return _name; }
   
   /**
    * @return the mime-type of this attachment
    */
   public String getMimeType ()              { return _mimeType; }
   
   /**
    * @return the filename of this attachment
    */
   public String getFilename ()              { return _filename; }
   
   /**
    * @return the title of the parent WikiPage
    */
   public String getParentWikiPageTitle ()   { return _parentWikiPageTitle; }

   
   /**
    * An attachment has no concept of how or where it is stored.  However,
    * for convenience, this method is provided.  It ensures a unique name for the 
    * attachment within it's WikiPage.  It may also be safe to assume it
    * provides a unique name across the entire WikiSystem.  This assumes however
    * that no two WikiPages can have the same name . . . probably a safe
    * assumption.<p>
    * 
    * The unique name returned should be safe to use as a filename as it only
    * contains alpha-numeric characters and underscores.<p>
    * 
    * This unique name is stored internally so that if any of the properties
    * (name, filename, wikipagetitle, etc) are changed, the corresponding attachment
    * can still be found.<p>
    * 
    * In the event changing any of these properties would call for a new unique name
    * to be created, the programmer should simply create a new WikiAttachment object
    * and throw this one away.  But make sure to update the references in the 
    * corresponding WikiPage!<p>
    */
   public String getIdentifier ()            
   { 
      if (_identifier == null)
         createIdentifier ();
      
      return _identifier; 
   }
   
   // private method actually create the unique name
   // this is called upon construction
   private void createIdentifier ()
   {
      if (_identifier == null)
      {
         byte[] tmp = ((String) (_parentWikiPageTitle + "_" + _name + "_" + _filename)).getBytes ();
      
         // strip all non-alphanumeric bytes 
         StringBuffer buff = new StringBuffer ();
         buff.append (java.lang.Math.random());    // some pseudo-randomness
         for (int x=0; x<tmp.length; x++)
         {
            char c = (char) tmp[x];
            if (Character.isLetterOrDigit(c) || c == '_')
               buff.append (c);
         }
         // store it internally
         _identifier = buff.toString ();
      }
   }
   
   
   //
   // set methods
   //
   
   /**
    * set the name of this attachment
    * @param name the name of this attachment
    */
   public void setName (String name)                               { _name = name; }
   
   /**
    * set the mime-type of this attachment
    * @param mimeType the mime-type of this attachment (ie, text/html, application/zip, image/gif, etc)
    */
   public void setMimeType (String mimeType)                      { _mimeType = mimeType; }   
   
   /**
    * set the filename of this attachment
    * @param filename the filename of this attachment
    */
   public void setFilename (String filename)                       { _filename = filename; }
   
   /**
    * set the parent WikiPage 
    * @param parentWikiPageTitle the title of the parent WikiPage
    */
   public void setParentWikiPageTitle (String parentWikiPageTitle) { _parentWikiPageTitle = parentWikiPageTitle; }
   

   /**
    * compares string value of unique names.  if they are equal, so
    * are the WikiAttachment objects.
    * 
    * @return true if equal.  false if not.  duh!
    */
   public boolean equals (Object toCompare)
   {
      if (!(toCompare instanceof WikiAttachment))
         return false;
      
      if (getIdentifier().equals ( ((WikiAttachment) toCompare).getIdentifier()))
         return true;
      
      return false;
   }
}
