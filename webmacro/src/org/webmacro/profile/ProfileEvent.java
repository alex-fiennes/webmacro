/*
 * Copyright (C) 1998-2000 Semiotek Inc.  All Rights Reserved.  
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of either of the following
 * Open Source licenses:
 *
 * The GNU General Public License, version 2, or any later version, as
 * published by the Free Software Foundation
 * (http://www.fsf.org/copyleft/gpl.html);
 *
 *  or 
 *
 * The Semiotek Public License (http://webmacro.org/LICENSE.)  
 *
 * This software is provided "as is", with NO WARRANTY, not even the 
 * implied warranties of fitness to purpose, or merchantability. You
 * assume all risks and liabilities associated with its use.
 *
 * See www.webmacro.org for more information on the WebMacro project.  
 */


package org.webmacro.profile;

/**
  * The ProfileEvent interface describes an event that took place
  * in the system. All that is recorded about the event is its name,
  * when it started, and when it stopped. 
  */
final public class ProfileEvent 
{

   /**
     * The name of this event
     */
   public String name;

   /**
     * Milliseconds since Jan 1, 1970 that this event started
     */
   public long start;

   /**
     * Milliseconds that this event lasted for
     */
   public int duration;

   /**
     * How many levels down the "call stack" is this event? In other
     * words, how many events enclose it.
     */
   public int depth;

   /**
     *  Create a new profile event with null and 0 values
     */
   public ProfileEvent() { this(null,0,0,0); }

   /**
     * Create a new profile event with the supplied values
     */
   public ProfileEvent(String name, int start, int duration, int depth) {
      this.name = name;
      this.start = start;
      this.duration = duration;
      this.depth = depth;
   }

}

