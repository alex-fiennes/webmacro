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

import java.io.*;
import java.util.*;

/**
 * @author Jason Bowman (jasonb42@mediaone.net)
 */
public class Helper
	{
	static public void configureLog4J()
		{
		InputStream log4jConfigStream = null;
		try
			{
			// Config Log4J
			log4jConfigStream = getStream("log4j.properties");
			Properties log4jConfig = new Properties();
			log4jConfig.load(log4jConfigStream);
			org.apache.log4j.PropertyConfigurator.configure(log4jConfig);

			System.out.println("CUSTOM CONFIG LOADED!");
			}
		catch (IOException e)
			{
			log4jConfigStream = null;	
			}
		finally
			{
			if (log4jConfigStream == null)
				org.apache.log4j.BasicConfigurator.configure();
			}

		}

	/**
	 * This Method searches the classpath and default location in order to returns an input stream to the file named.
	 * @return null if the file could not be found.
	 */
	static public InputStream getStream(String file) //throws IOException
		{
		InputStream in = null;

		try
			{
			ClassLoader c = Helper.class.getClassLoader();
			in = c.getResourceAsStream(file);
			if (null == in)
				{
				c = Class.class.getClassLoader();
				in = c.getResourceAsStream(file);
				}
			if (null == in)
				{
				in = new FileInputStream(file); 
				}
			return in;
			}
		catch (Exception e)
			{
			//throw new IOException("Could not read stream: " + e);
			}

		return in;
		}


	/**
	 * Helper method to parse a delimeted string and extract the values into a List.<p>
	 * Breaks currently include any of the following characters: ";, \\t\\n\\r\\f"
	 */
	static public List extractList(String listSource)
		{
		List list = new ArrayList();

		// For list
		if (list != null)
			{
			StringTokenizer st = new StringTokenizer(listSource, ";, \t\n\r\f", false);
			while (st.hasMoreTokens())
				{
				list.add(list.size(), st.nextToken());
				}
			}

		return list;
		}



	}
