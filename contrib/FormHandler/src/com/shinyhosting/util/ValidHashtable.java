/*
 * Copyright (C) 2001 Jason Bowman.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *      Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *      Neither name of Jason Bowman nor the names of any contributors 
 * may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */  

package com.shinyhosting.util;

/**
 * @author Jason Bowman (jasonb42@mediaone.net)
 */
public class ValidHashtable extends java.util.Hashtable
	{
	public boolean containsKey(Object key)
		{
		// Make sure it is removed if it is invalid...
		this.get(key);

		return super.containsKey(key);
		}

	private boolean isValid(Object value)
		{
		if (value instanceof Invalidatable)
			{
			if (((Invalidatable)value).isValid())
				return true;
			return false;
			}
		else
			return true;
		}

	public Object get(Object key)
		{
		Object value = super.get(key);

		if (value != null && !isValid(value))
			{
			super.remove(key);
			return null;
			}

		return value;
		}

	//public Object put(Object key, Invalidatable value) {
	//	return super.put(key, (Object)value);
	//	}

	}