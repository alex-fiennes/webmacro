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


package org.webmacro.parser;

import org.webmacro.WebMacroRuntimeException;

/**
 * Runtime exception for passing exceptions through the parser.
 * The parser used by WebMacro is autogenerated and works well,
 * but has a bad exception handling. If we want to pass a exception,
 * through the parser, we use this runtime exception in code called
 * by the parser (for example BackupCharStream) and catch it in the
 * code calling the parser.
 * @author Sebastian Kanthak
 */
public class ParserRuntimeException extends WebMacroRuntimeException
{

    public ParserRuntimeException ()
    {
        super();
    }

    public ParserRuntimeException (String reason)
    {
        super(reason);
    }

    public ParserRuntimeException (String reason, Throwable e)
    {
        super(reason, e);
    }
}

