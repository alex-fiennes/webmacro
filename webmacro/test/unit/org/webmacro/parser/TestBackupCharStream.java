package org.webmacro.parser;

import java.io.*;
import junit.framework.*;
import org.webmacro.*;

public class TestBackupCharStream extends TestCase {

  String loc = "org/webmacro/parser/";

  String[] testFiles = new String[] {
    loc+"test1.wm", loc+"test2.wm", loc+"test3.wm", loc+"test4.wm", 
    loc+"test5.wm", loc+"test6.wm", loc+"test7.wm", 
    loc+"nasty_sebastian.wm"
  };

  public TestBackupCharStream (String name) {
    super (name);
  }
  
  private char[] readFile(String name) throws IOException {
    Reader in;
    CharArrayWriter out;
    char[] buf = new char[512];

    try {
      in = new FileReader(name);
    }
    catch (IOException e) {
      in = new InputStreamReader(ClassLoader.getSystemResourceAsStream(name));
    }
    out = new CharArrayWriter();
    while (in.ready()) {
      int n = in.read(buf, 0, 512);
      if (n > 0)
        out.write(buf, 0, n);
    }
    char[] result = out.toCharArray();
    out.close();
    in.close();
    return result;
  }

  private void failAt(String file, int pos, char c1, char c2) {
    fail("Characters do not match at position " + pos
         + " of " + file + " (" + c1 + "/" + c2 + ")");
  }
    
  private void assertBcsEqual(char[] chars, String file) {
    BackupCharStream in = new BackupCharStream(new CharArrayReader(chars));
    for (int i=0; i<chars.length; i++) {
      char c;
      try {
        c = in.readChar();
      }
      catch (IOException e) {
        fail("Premature end-of-file reading stream with BCS");
        break;
      }
      if (c != chars[i])
        failAt(file, i, c, chars[i]);
    }
    try {
      char c = in.readChar();
      fail("Expecting BCS to return EOF; found /" + c + "/");
    }
    catch (IOException e) {
    }
  }

  private void assertBcsEqual(char[] chars, String file, 
                              int readChars, int backupChars) 
    throws Exception {
    BackupCharStream in = new BackupCharStream(new CharArrayReader(chars));
    int counter = 0;

    for (int i=0; i<chars.length; i++) {
      char c;
      try {
        c = (counter == 0) ? in.BeginToken() : in.readChar();
      }
      catch (IOException e) {
        fail("Premature end-of-file reading stream with BCS");
        break;
      }
      if (c != chars[i])
        failAt(file, i, c, chars[i]);

      ++counter;
      if (counter == readChars) {
        counter = 0;
        in.backup(backupChars);
        for (int j=0; j<backupChars; j++) {
          c = in.readChar();
          if (c != chars[i+1-backupChars+j])
            failAt(file, i+1-backupChars+j, c, chars[i+1-backupChars+j]);
        }
      }
    }
    try {
      char c = in.readChar();
      fail("Expecting BCS to return EOF; found /" + c + "/");
    }
    catch (IOException e) {
    }
  }
  
  public void testBasic() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i]);
  }

  public void test_2_1() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 2, 1);
  }

  public void test_3_2() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 3, 2);
  }

  public void test_10_9() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 10, 9);
  }

  public void test_10_2() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 10, 2);
  }

  public void test_1000_500() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 1000, 500);
  }

  public void test_4000_2000() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 4000, 2000);
  }

  public void test_8000_2000() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 8000, 2000);
  }

  public void test_8000_4000() throws Exception {
    for (int i=0; i<testFiles.length; i++) 
      assertBcsEqual(readFile(testFiles[i]), testFiles[i], 8000, 4000);
  }

}
