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
 * This is the base class of all WebMacro exceptions. You could use it
 * to catch any exception enerated by WebMacro code.
 */
public class WebMacroException extends RethrowableException {

   private String _contextLocation;

   public WebMacroException() {
      super();
   }

   public WebMacroException(String reason) {
      super(reason);
   }

   public WebMacroException(String reason, Throwable e) {
      super(reason, e);
   }


    /**
	 * Overloaded to return the <code>reason</code> specified during construction
	 * <b>plus</b> the context location, if any.
	 */
	public String getMessage() {
		String msg = super.getMessage();
		if ( _contextLocation != null && msg != null ) {
			msg += " at " + _contextLocation;
		}

		return msg;
	}


    /**
	 * Record the line and column info from the template that
	 * caused this ProeprtyException to be thrown.
	 */
	public void setContextLocation( String location ) {
		_contextLocation = location;
		Throwable cause = getCause();
		if ( cause instanceof PropertyException ) {
			PropertyException pe = ( PropertyException ) cause;
			if ( pe.getContextLocation() == null ) {
				pe.setContextLocation( location );
			}
		}
		cause = getRootCause();
		if ( cause instanceof PropertyException ) {
			PropertyException pe = ( PropertyException ) cause;
			if ( pe.getContextLocation() == null ) {
				pe.setContextLocation( location );
			}
		}

	}

	/**
	 * @return location (line/column) from the template that caused
	 *         this PropertyException to be thrown.  Can be null
	 *         if this exception instance wasn't previously handled
	 *         by a core EvaluationExceptionHandler.
	 */
	public String getContextLocation() {
		return _contextLocation;
	}


}

