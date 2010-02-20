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


package org.webmacro.engine;

import java.io.IOException;
import java.util.List;

import org.webmacro.Context;
import org.webmacro.FastWriter;
import org.webmacro.Macro;
import org.webmacro.PropertyException;
import org.webmacro.TemplateVisitor;
import org.webmacro.Visitable;
import org.webmacro.WebMacroRuntimeException;
import org.webmacro.util.Encoder;

/**
 * A Block is essentially a Macro[] that knows how to write itself
 * out as a String.
 */
final public class Block implements Macro, Visitable
{

    private final String[] _strings;
    private final Macro[] _macros;
    private final int[] _lineNos, _colNos;

    private final Encoder.Block _block;
    private final int _length;
    private final int _remainder;
    private String _name;

    /**
     * A Block must be constructed from a BlockBuilder. The format
     * of a block is:  String (Macro String)*
     * and the constructor expects to receive two arrays matching
     * this structure. The output of the block will be the first
     * string, followed by the first macro, followed by the second
     * string, followed by the second macro, etc., and terminated
     * by the final string.
     */
    protected Block (String name, String[] strings, Macro[] macros,
                     int lineNos[], int colNos[])
    {
        _name = name;
        _strings = strings;
        _macros = macros;
        _lineNos = lineNos;
        _colNos = colNos;

        _length = _macros.length;
        _remainder = 10 - _length % 10;

        // we'll use this to encode our strings for output to the user
        _block = new Encoder.Block(_strings);
    }

    /**
     * Interpret the directive and write it out, using the values in
     * the supplied context as appropriate.
     * 
     * @exception PropertyException if required data was missing from context
     * @exception IOException if we could not successfully write to out
     */
    final public void write (final FastWriter out, final Context context)
            throws PropertyException, IOException
    {
        final byte[][] bcontent = out.getEncoder().encode(_block);
        byte[] b;
        Context.TemplateEvaluationContext teC = context.getTemplateEvaluationContext();
        String oldName = teC._templateName;

        teC._templateName = _name;
        //
        // The _remainder is 10 minus the number of bytes left.
        // If we need to write out 3 bytes remainder will be 7, 
        // so control starts at case 7 and falls through 8 and 9, 
        // so 3 bytes are written.
        //
        int i = 0;
        switch (_remainder)
        {
            case 1:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 2:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 3:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 4:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 5:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 6:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 7:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 8:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 9:
                b = bcontent[i];
                out.write(b, 0, b.length);
                teC._lineNo = this.getLineNo(i);
                teC._columnNo = this.getColNo(i);
                _macros[i++].write(out, context);
            case 10:
                break;
            default :
                throw new WebMacroRuntimeException(
                        "Bug: _remainder value not 0 to 10: " + _remainder); 
                
        }

        while (i < _length)
        {
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
            b = bcontent[i];
            out.write(b, 0, b.length);
            teC._lineNo = this.getLineNo(i);
            teC._columnNo = this.getColNo(i);
            _macros[i++].write(out, context);
        }
        b = bcontent[_length];
        out.write(b, 0, b.length);
        teC._templateName = oldName;
    }

    public String getTemplateName ()
    {
        return _name;
    }

    public void setTemplateName (String name)
    {
        _name = name;
    }

    public int getLineNo (int i)
    {
        return (_lineNos != null && i >= 0 && _lineNos.length > i) ? _lineNos[i] : 0;
    }

    public int getColNo (int i)
    {
        return (_colNos != null && i >= 0 && _colNos.length > i) ? _colNos[i] : 0;
    }

    private static class BlockIterator implements BlockBuilder.BlockIterator
    {

        private int i = 0;
        private boolean doneString = false, done = false;
        private String[] strings;
        private Macro[] macros;
        private Block block;

        public BlockIterator (String[] strings, Macro[] macros, Block b)
        {
            this.strings = strings;
            this.macros = macros;
            this.block = b;
        }

        public boolean hasNext ()
        {
            return !done;
        }

        public String getName ()
        {
            return block.getTemplateName();
        }

        public int getLineNo ()
        {
            return block.getLineNo(i - 1);
        }

        public int getColNo ()
        {
            return block.getColNo(i - 1);
        }

        public void remove ()
        {
            throw new UnsupportedOperationException();
        }

        public Object next ()
        {
            if (doneString)
            {
                doneString = false;
                return macros[i++];
            }
            else
            {
                if (i == strings.length - 1)
                    done = true;
                doneString = true;
                return strings[i];
            }
        }
    }

    public BlockBuilder.BlockIterator getBlockIterator ()
    {
        return new BlockIterator(_strings, _macros, this);
    }

    final void appendTo (List l)
    {
        final int len = _macros.length;
        for (int i = 0; i < _macros.length; i++)
        {
            l.add(_strings[i]);
            l.add(_macros[i]);
        }
        l.add(_strings[len]);
    }

    final public void accept (TemplateVisitor v)
    {
        v.beginBlock();
        final int len = _macros.length;
        for (int i = 0; i < len; i++)
        {
            v.visitString(_strings[i]);
            v.visitMacro(_macros[i]);
        }
        v.visitString(_strings[len]);
        v.endBlock();
    }

    /**
     * @exception PropertyException if required data was missing from context
     */
    final public Object evaluate (Context context) throws PropertyException
    {
        try
        {
            FastWriter fw = FastWriter.getInstance(context.getBroker());
            write(fw, context);
            String ret = fw.toString();
            fw.close();
            return ret;
        }
        catch (IOException e)
        {
            context.getBroker().getLog("engine", 
                                       "parsing and template execution")
                .error("StringWriter threw an IOException!", e);
            return null;
        }
    }

}

