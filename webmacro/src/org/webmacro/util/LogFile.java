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
 *
 * @author Marcel Huijkman
 *
 * @version	17-07-2002
 *
 */

/*
	ChangeLog:

	17-07-2002
	setting for WebMacro.properties added
	LogFilePerDay
		usage:
			LogFilePerDay=FALSE or TRUE

	LogFileAutoFlush
		usage:
			LogFileAutoFlush=FALSE or TRUE
	(Notice: Only handy when developing on a Operating system, that buffers file-writing,
	not recommended for production, since it obviously slows down the machine.)

*/


package org.webmacro.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogFile extends AbstractLogFile
{

    private PrintStream _out;
    private boolean _logFilePerDay = false;
    private boolean _logFileAutoFlush = false;
    private String _prevLogDate = "";
    private String _actLogDate = "";
    private SimpleDateFormat _logFileSuffix = new SimpleDateFormat("_yyyyMMdd");
    private String _orgLogFile = "";

    /**
     * Create a new LogFile instance.
     */
    public LogFile (Settings s) throws FileNotFoundException
    {
        super(s);
        init(s.getSetting("LogFile"));

        String strLogFilePerDay = s.getSetting("LogFilePerDay");

        if (strLogFilePerDay != null)
        {
            if (strLogFilePerDay.equals("TRUE"))
            {
                _logFilePerDay = true;
            }
        }
        String strLogFileAutoFlush = s.getSetting("LogFileAutoFlush");
        if (strLogFileAutoFlush != null)
        {
            if (strLogFileAutoFlush.equals("TRUE"))
            {
                _logFileAutoFlush = true;
            }
        }
    }

    public LogFile (String fileName) throws FileNotFoundException
    {
        init(fileName);
    }

    private void init (String fileName) throws FileNotFoundException
    {
        if ((fileName == null)
                || (fileName.equalsIgnoreCase("system.err")
                || fileName.equalsIgnoreCase("none")
                || fileName.equalsIgnoreCase("stderr")))
        {
            _out = System.err;
            _name = "System.err";
        }
        else
        {
            if (_logFilePerDay)
            {
                // change logfilename if a logfile per day is wanted
                _orgLogFile = fileName;
                _actLogDate = _logFileSuffix.format(Calendar.getInstance().getTime());
                _prevLogDate = _actLogDate;
                fileName += _actLogDate;
            }

            _out = new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(fileName, true)));
            _name = fileName;
            if (_orgLogFile.length() == 0)
            {
                _orgLogFile = fileName;
            }
        }
        if (_defaultLevel <= LogSystem.NOTICE)
        {
            log(Clock.getDate(), "LogFile", "NOTICE", "--- Log Started ---",
                    null);
        }
    }

    /**
     * Create a new LogFile instance
     */
    public LogFile (PrintStream out)
    {
        _out = out;
        _name = out.toString();
    }

    public void log (Date date, String name, String level, String message, Throwable e)
    {

        if (_logFilePerDay)
        {
            _actLogDate = _logFileSuffix.format(Calendar.getInstance().getTime());
            if (!_actLogDate.equals(_prevLogDate))
            {
                try
                {
                    init(_orgLogFile);
                }
                catch (FileNotFoundException f_ex)
                {
                    System.out.println("Logfile " + _name + " not found");
                }
            }
        }

        Object[] _args = {date, name, level, message};
        _out.println(_mf.format(_args));
        if (_trace && (e != null))
        {
            e.printStackTrace(_out);
        }

        if (_logFileAutoFlush)
        {
            flush();
        }
    }

    public void flush ()
    {
        _out.flush();
    }

}