/* Generated By:JavaCC: Do not edit this line. ASCII_CharStream.java Version 0.7pre6 */

package org.webmacro.parser;

/**
 * An implementation of interface CharStream, where the stream is assumed to
 * contain only ASCII characters (without unicode processing).
 * Modified extensively by Brian Goetz (Oct 2000) to support being able to
 * back up in the buffer.
 */

public final class ASCII_CharStream
{

    private static final class Buffer
    {

        int size;
        int dataLen, curPos;
        char[] buffer;
        int[] bufline, bufcolumn;

        public Buffer (int n)
        {
            size = n;
            dataLen = 0;
            curPos = -1;
            buffer = new char[n];
            bufline = new int[n];
            bufcolumn = new int[n];
        }

        public void expand (int n)
        {
            char[] newbuffer = new char[size + n];
            int newbufline[] = new int[size + n];
            int newbufcolumn[] = new int[size + n];

            try
            {
                System.arraycopy(buffer, 0, newbuffer, 0, size);
                buffer = newbuffer;
                System.arraycopy(bufline, 0, newbufline, 0, size);
                bufline = newbufline;
                System.arraycopy(bufcolumn, 0, newbufcolumn, 0, size);
                bufcolumn = newbufcolumn;
            }
            catch (Throwable t)
            {
                throw new Error(t.getMessage());
            }

            size += n;
        }
    }

    private Buffer bufA, bufB, curBuf, otherBuf, tokenBeginBuf;
    private int tokenBeginPos;
    private int backupChars;

    public static final boolean staticFlag = false;

    private int column = 0;
    private int line = 1;
    private boolean prevCharIsCR = false;
    private boolean prevCharIsLF = false;

    private java.io.Reader inputStream;

    private final void swapBuf ()
    {
        Buffer tmp = curBuf;
        curBuf = otherBuf;
        otherBuf = tmp;
    }

    private final void FillBuff () throws java.io.IOException
    {
        // Buffer fill logic:
        // If there is at least 2K left in this buffer, just read some more
        // Otherwise, if we're processing a token and it either
        // (a) starts in the other buffer (meaning it's filled both part of
        //     the other buffer and some of this one, or
        // (b) starts in the first 2K of this buffer (meaning its taken up
        //     most of this buffer
        // we expand this buffer.  Otherwise, we swap buffers.
        // This guarantees we will be able to back up at least 2K characters.
        if (curBuf.size - curBuf.dataLen < 2048)
        {
            if (tokenBeginPos >= 0
                    && ((tokenBeginBuf == curBuf && tokenBeginPos < 2048)
                    || tokenBeginBuf != curBuf))
            {
                curBuf.expand(2048);
            }
            else
            {
                swapBuf();
                curBuf.curPos = curBuf.dataLen = 0;
            }
        }

        try
        {
            int i = inputStream.read(curBuf.buffer, curBuf.dataLen,
                    curBuf.size - curBuf.dataLen);
            if (i == -1)
            {
                inputStream.close();
                throw new java.io.IOException();
            }
            else
                curBuf.dataLen += i;
            return;
        }
        catch (java.io.IOException e)
        {
            if (curBuf.curPos > 0)
                --curBuf.curPos;
            if (tokenBeginPos == -1)
            {
                tokenBeginPos = curBuf.curPos;
                tokenBeginBuf = curBuf;
            }
            throw e;
        }
    }

    public final char BeginToken () throws java.io.IOException
    {
        tokenBeginPos = -1;
        char c = readChar();
        tokenBeginBuf = curBuf;
        tokenBeginPos = curBuf.curPos;

        return c;
    }

    private final void UpdateLineColumn (char c)
    {
        column++;

        if (prevCharIsLF)
        {
            prevCharIsLF = false;
            line += (column = 1);
        }
        else if (prevCharIsCR)
        {
            prevCharIsCR = false;
            if (c == '\n')
            {
                prevCharIsLF = true;
            }
            else
                line += (column = 1);
        }

        switch (c)
        {
            case '\r':
                prevCharIsCR = true;
                break;
            case '\n':
                prevCharIsLF = true;
                break;
            case '\t':
                column--;
                column += (8 - (column & 07));
                break;
            default :
                break;
        }

        curBuf.bufline[curBuf.curPos] = line;
        curBuf.bufcolumn[curBuf.curPos] = column;
    }

    public final char readChar () throws java.io.IOException
    {
        // When we hit the end of the buffer, if we're backing up, we just
        // swap, if we're not, we fill.
        if (++curBuf.curPos >= curBuf.dataLen)
        {
            if (backupChars > 0)
            {
                --curBuf.curPos;
                swapBuf();
            }
            else
                FillBuff();
        }
        ;

        char c = (char) ((char) 0xff & curBuf.buffer[curBuf.curPos]);

        // No need to update line numbers if we've already processed this char
        if (backupChars > 0)
            --backupChars;
        else
            UpdateLineColumn(c);

        return (c);
    }

    /**
     * @deprecated
     * @see #getEndColumn
     */

    public final int getColumn ()
    {
        return curBuf.bufcolumn[curBuf.curPos];
    }

    /**
     * @deprecated
     * @see #getEndLine
     */

    public final int getLine ()
    {
        return curBuf.bufline[curBuf.curPos];
    }

    public final int getEndColumn ()
    {
        return curBuf.bufcolumn[curBuf.curPos];
    }

    public final int getEndLine ()
    {
        return curBuf.bufline[curBuf.curPos];
    }

    public final int getBeginColumn ()
    {
        return tokenBeginBuf.bufcolumn[tokenBeginPos];
    }

    public final int getBeginLine ()
    {
        return tokenBeginBuf.bufline[tokenBeginPos];
    }

    public final void backup (int amount)
    {
        backupChars += amount;
        if (curBuf.curPos - amount < 0)
        {
            int addlChars = amount - 1 - curBuf.curPos;
            curBuf.curPos = 0;
            swapBuf();
            curBuf.curPos = curBuf.dataLen - addlChars - 1;
        }
        else
        {
            curBuf.curPos -= amount;
        }
    }

    public ASCII_CharStream (java.io.Reader dstream, int startline,
                             int startcolumn, int buffersize)
    {
        ReInit(dstream, startline, startcolumn, buffersize);
    }

    public ASCII_CharStream (java.io.Reader dstream, int startline,
                             int startcolumn)
    {
        this(dstream, startline, startcolumn, 4096);
    }

    public void ReInit (java.io.Reader dstream, int startline,
                        int startcolumn, int buffersize)
    {
        inputStream = dstream;
        line = startline;
        column = startcolumn - 1;

        if (bufA == null || bufA.size != buffersize)
            bufA = new Buffer(buffersize);
        if (bufB == null || bufB.size != buffersize)
            bufB = new Buffer(buffersize);
        curBuf = bufA;
        otherBuf = bufB;
        curBuf.curPos = otherBuf.dataLen = -1;
        curBuf.dataLen = otherBuf.dataLen = 0;

        prevCharIsLF = prevCharIsCR = false;
        tokenBeginPos = -1;
        tokenBeginBuf = null;
        backupChars = 0;
    }

    public void ReInit (java.io.Reader dstream, int startline,
                        int startcolumn)
    {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public ASCII_CharStream (java.io.InputStream dstream, int startline,
                             int startcolumn, int buffersize)
    {
        this(new java.io.InputStreamReader(dstream), startline, startcolumn, 4096);
    }

    public ASCII_CharStream (java.io.InputStream dstream, int startline,
                             int startcolumn)
    {
        this(dstream, startline, startcolumn, 4096);
    }

    public void ReInit (java.io.InputStream dstream, int startline,
                        int startcolumn, int buffersize)
    {
        ReInit(new java.io.InputStreamReader(dstream), startline, startcolumn,
                4096);
    }

    public void ReInit (java.io.InputStream dstream, int startline,
                        int startcolumn)
    {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public final String GetImage ()
    {
        if (tokenBeginBuf == curBuf)
            return new String(curBuf.buffer, tokenBeginPos,
                    curBuf.curPos - tokenBeginPos + 1);
        else
            return new String(otherBuf.buffer, tokenBeginPos,
                    otherBuf.dataLen - tokenBeginPos)
                    + new String(curBuf.buffer, 0, curBuf.curPos + 1);
    }

    public final char[] GetSuffix (int len)
    {
        char[] ret = new char[len];

        if ((curBuf.curPos + 1) >= len)
            System.arraycopy(curBuf.buffer, curBuf.curPos - len + 1, ret, 0, len);
        else
        {
            if (otherBuf.dataLen >= len - curBuf.curPos - 1)
                System.arraycopy(otherBuf.buffer,
                        otherBuf.dataLen - (len - curBuf.curPos - 1), ret, 0,
                        len - curBuf.curPos - 1);
            System.arraycopy(curBuf.buffer, 0, ret, len - curBuf.curPos - 1,
                    curBuf.curPos + 1);
        }

        return null;
    }

    public void Done ()
    {
        bufA = bufB = curBuf = otherBuf = null;
    }

}

