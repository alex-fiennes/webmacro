package org.webmacro.parser;

import junit.framework.TestCase;

import java.io.*;

public class TestBackupCharStream extends TestCase
{

    String loc = "org/webmacro/parser/";

    String[] testFiles = new String[]{
        loc + "test1.wm", loc + "test2.wm", loc + "test3.wm", loc + "test4.wm",
        loc + "test5.wm", loc + "test6.wm", loc + "test7.wm",
        loc + "nasty_sebastian.wm"
    };


    public TestBackupCharStream (String name)
    {
        super(name);
    }


    private char[] readFile (String name) throws IOException
    {
        Reader in;
        CharArrayWriter out;
        char[] buf = new char[512];

        try
        {
            in = new FileReader(name);
        }
        catch (IOException e)
        {
            in = new InputStreamReader(ClassLoader.getSystemResourceAsStream(name));
        }
        out = new CharArrayWriter();
        while (in.ready())
        {
            int n = in.read(buf, 0, 512);
            if (n > 0)
                out.write(buf, 0, n);
        }
        char[] result = out.toCharArray();
        out.close();
        in.close();
        return result;
    }


    private void failAt (String file, int pos, char c1, char c2)
    {
        fail("Characters do not match at position " + pos
                + " of " + file + " (" + c1 + "/" + c2 + ")");
    }


    private void assertBcsEqual (char[] chars, String file)
    {
        BackupCharStream in = new BackupCharStream(new CharArrayReader(chars));
        for (int i = 0; i < chars.length; i++)
        {
            char c;
            try
            {
                c = in.readChar();
            }
            catch (IOException e)
            {
                fail("Premature end-of-file reading stream with BCS");
                break;
            }
            if (c != chars[i])
                failAt(file, i, c, chars[i]);
        }
        try
        {
            char c = in.readChar();
            fail("Expecting BCS to return EOF; found /" + c + "/");
        }
        catch (IOException e)
        {
        }
    }


    private void assertBcsEqual (char[] chars, String file,
                                 int readChars, int backupChars)
            throws Exception
    {
        BackupCharStream in = new BackupCharStream(new CharArrayReader(chars));
        int counter = 0;
        char c;

        for (int i = 0; i < chars.length; i++)
        {
            try
            {
                c = (counter == 0) ? in.BeginToken() : in.readChar();
            }
            catch (IOException e)
            {
                fail("Premature end-of-file reading stream with BCS");
                break;
            }
            if (c != chars[i])
                failAt(file, i, c, chars[i]);

            ++counter;
            if (counter == readChars)
            {
                counter = 0;
                int jMax = Math.min(backupChars, i);
                in.backup(jMax);
                for (int j = 0; j < jMax; j++)
                {
                    c = in.readChar();
                    if (c != chars[i + 1 - jMax + j])
                        failAt(file, i + 1 - jMax + j, c, chars[i + 1 - jMax + j]);
                }
            }
        }
        try
        {
            c = in.readChar();
            fail("Expecting BCS to return EOF; found /" + c + "/");
        }
        catch (IOException e)
        {
        }

        in.backup(1);
        c = in.readChar();
        if (c != chars[chars.length - 1])
            failAt(file, chars.length - 1, c, chars[chars.length - 1]);
    }


    public void testBasic () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i]);
    }


    public void test_1_1 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 1, 1);
    }


    public void test_1_2 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 1, 2);
    }


    public void test_1_4 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 1, 4);
    }


    public void test_2_1 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 2, 1);
    }


    public void test_3_2 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 3, 2);
    }


    public void test_5_4 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 5, 4);
    }


    public void test_10_9 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 10, 9);
    }


    public void test_10_2 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 10, 2);
    }


    public void test_1000_500 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 1000, 500);
    }


    public void test_4000_2000 () throws Exception
    {
        for (int i = 0; i < testFiles.length; i++)
            assertBcsEqual(readFile(testFiles[i]), testFiles[i], 4000, 2000);
    }
}
