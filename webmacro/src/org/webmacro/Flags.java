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


package org.webmacro;

/**
  * This class contains some static final settings which control the 
  * behavior of WebMacro at runtime. There are two ways you can take
  * advantage of this class:
  * <ul>
  *   <li>Change these constants and recompile the sources 
  *   <li>Insert a copy of this class earlier in your classpath 
  * </ul>
  * Note that if you insert the copy earlier in your classpath your
  * JIT may be able to optimize out code that depends on the static
  * final constants, but it will not be able to optimize in code that
  * your compiler has already removed! So if you want to try the 
  * classpath trick, you probably need to compile the WebMacro 
  * sources with these constants set to "true" first.
  */
public final class Flags {

   /**
     * Nobody is allowed to create one
     */
   private Flags() { }

   /**
     * Use debug statements in performance critical code?
     */
   public static final boolean DEBUG = false; 

   /**
     * Use in profiling statements in performance critical code?
     */
   public static final boolean PROFILE = true;

}


