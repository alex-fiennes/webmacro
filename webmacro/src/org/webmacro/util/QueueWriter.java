
package org.webmacro.util;

import java.io.*;

final public class QueueWriter extends Writer
{

   /**
     * The array of buffers where written data is stored
     */
   private char buffer[][];

   /**
     * The offset into the array in the buffer
     */
   private int offset[];

   /**
     * The number of characters to write in the array in the buffer
     */
   private int length[];

   /**
     * The number of buffers
     */
   private int count;

   /**
     * The total size of all the data we have
     */
   private int size;

   /**
     * The last buffer added was locally allocated
     */
   private boolean local;

   /**
     * Create a new Writer
     */
   public QueueWriter() {
      this(100);
   }

   /**
     * Create a new buffer. The integer argument is the expected number
     * of things we will write to this buffer.
     */
   public QueueWriter(int expectedWrites) {
      buffer = new char[expectedWrites][];
      offset = new int[expectedWrites];
      length = new int[expectedWrites];
      count = -1;
      size = 0;
      local = false;
   }


   private void increaseCapacity() {
      int oldSize = buffer.length;
      int newSize = oldSize * 2 + 1;
      char[][] tmpBuffer = new char[newSize][];
      int[] tmpOffset = new int[newSize];
      int[] tmpLength = new int[newSize];

System.out.println("Incresing capacity");

      // copy the old arrays to the new arrays

      System.arraycopy(buffer,0,tmpBuffer,0,oldSize);
      System.arraycopy(offset,0,tmpOffset,0,oldSize);
      System.arraycopy(length,0,tmpLength,0,oldSize);

      // clear the old arrays with nulls from the new array

      System.arraycopy(tmpBuffer,oldSize,buffer,0,oldSize);
      System.arraycopy(tmpOffset,oldSize,offset,0,oldSize);
      System.arraycopy(tmpLength,oldSize,length,0,oldSize);

      // reassign
      buffer = tmpBuffer;
      offset = tmpOffset;
      length = tmpLength;
   }

   private void newBuffer(int size) {
      count++;
      if (count >= buffer.length) {
         increaseCapacity();
      }
      buffer[count] = new char[size];
      offset[count] = 0;
      length[count] = 0;
      local = true;
   }

   public void write(char c[], int off, int len) { 
System.out.println("write_array");
      count++;
      if (count >= buffer.length) {
         increaseCapacity();
      }
      buffer[count] = c;
      offset[count] = off;
      length[count] = len;

      // update statistics 

      size += len;
      local = false;
   }

   /**
     * Write a character to the buffer
     */
   public void write(final int c) { 
System.out.println("write_character: " + c);
      if (!local) {
         newBuffer(16); 
      } else if (length[count] == buffer[count].length) {
         newBuffer(length[count] * 2 + 1);
      }
      buffer[count][length[count]++] = (char)c;
   }

   public void write(final String str, int off, int len) 
   { 
System.out.println("write_string: " + str);
      if (!local) {
         newBuffer(len + 16);
      } 

      size += len; // do this here before len is destroyed

      final int end = off + len;
      while(off < end) {
         int  bufpos = length[count];
         char buf[] = buffer[count];
         int num = (bufpos + len < buf.length) ? len : (buf.length - bufpos);
         str.getChars(off,off + num, buf, bufpos);
         length[count] += num;
         off += num;
         len -= num;
         if (off < end) {
            newBuffer(buf.length * 2 + 1);
         }
      }
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(size);
      for (int i = 0; i <= count; i++) {
         buf.append(buffer[i],offset[i],length[i]);
      }
      return buf.toString();
   }

   public void flush() { }
   public void close() { }

   public static void main(String arg[]) throws Exception {

      QueueWriter qw = new QueueWriter(3);

      char cary[] = "Hello, brave new world".toCharArray();

      for (int i = 0; i < arg.length; i++) {
         qw.write(arg[i],1,arg[i].length() - 2);
         qw.write(' ');
         qw.write(cary,7,6);
      }

      System.out.println("Count: " + qw.count);
      System.out.println("- - - - Output - - - -");
      System.out.println(qw.toString());
   }

}
