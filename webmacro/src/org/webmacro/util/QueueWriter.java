
package org.webmacro.util;

import java.io.*;

/**
  * This writer buffers characters into a char array. Also, if you pass 
  * it a char array, it queues it into a queue of char array. The goal 
  * is to handle string processing with as little copying as possible. 
  * You can reset it and re-use the same local buffer to avoid a malloc
  * as well. 
  * <p>
  * UNSYNCHRONIZED: It is not safe to use this writer from multiple 
  * threads. It is an unsynchronized writer. It is intended for use 
  * in WebMacro, where only one thread exists within a single request,
  * and is also the only thread with access to mutable data.
  */
final public class QueueWriter extends Writer
{

   /**
     * The array of buffers where written data is stored. Nulls in this 
     * array indicate that storage in local[] is used instead.
     */
   private char buffer[][];

   /**
     * This is where local data is kept. When the buffer[i] entry 
     * evaluates to a null, that means the offsets refer to this array.
     */
   private char local[];

   /**
     * The offset into the array in the buffer. If the array in the buffer
     * is null, then this is the offset into local[].
     */
   private int offset[];

   /**
     * The number of characters to write in the array in the buffer. If 
     * the array in the buffer is null, this is the length of the data
     * stored in local[].
     */
   private int length[];

   /**
     * The number of buffers we have.
     */
   private int count;

   /**
     * The total size of all the data we have
     */
   private int size;

   /**
     * The current position in the local buffer
     */
   private int localPos;

   /**
     * Create a new Writer
     */
   public QueueWriter() {
      this(1024);
   }

   /**
     * Create a new buffer. The integer argument is the expected number
     * of bytes locally written
     */
   public QueueWriter(int defaultSize) {
      buffer = new char[64][];
      offset = new int[64];
      length = new int[64];
      local  = new char[defaultSize];
      localPos = 0;
      count = -1;
      size = 0;
   }


   /**
     * This relatively expensive operation expands the number of buffers
     * this class is capable of tracking.
     */
   private void increaseCapacity() {
      int oldSize = buffer.length;
      int newSize = oldSize * 2 + 1;
      char[][] tmpBuffer = new char[newSize][];
      int[] tmpOffset = new int[newSize];
      int[] tmpLength = new int[newSize];

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

   /**
     * This increases the size of the local buffer.
     */
   private void ensureLocalCapacity(int size) {
      if (count < 0 || buffer[count] != null) {
         count++;
         offset[count] = localPos;
         length[count] = 0;
         if (count >= buffer.length) {
            increaseCapacity();
         }
      }
      if ((localPos + size) >= local.length) {
         char[] tmpLocal = new char[local.length * 2 + size];
         System.arraycopy(local,0,tmpLocal,0,local.length);  
         local = tmpLocal;
      }
   }

   /**
     * Write the c[] array to the local buffer for later writing out. Note
     * that this copies a reference. Subsequent changes to c *will* be
     * reflected in the output.
     */
   public void write(char c[], int off, int len) { 
      count++;
      if (count >= buffer.length) {
         increaseCapacity();
      }
      buffer[count] = c;
      offset[count] = off;
      length[count] = len;

      // update statistics 

      size += len;
   }

   /**
     * Write a character to the buffer
     */
   public void write(final int c) { 
      ensureLocalCapacity(1);
      local[localPos++] = (char)c;
      length[count]++;
      size++;
   }

   public void write(final String str, int off, int len) 
   { 
      ensureLocalCapacity(len);
      str.getChars(off,off + len,local,localPos);
      localPos += len;
      length[count] += len;
      size += len;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(size);
      char[] b;
      for (int i = 0; i <= count; i++) {
         b = (buffer[i] == null) ? local : buffer[i];
         buf.append(b,offset[i],length[i]);
      }
      return buf.toString();
   }

   public void writeTo(Writer out) 
      throws IOException
   {
      char[] b;
      for (int i = 0; i <= count; i++) {
         b = (buffer[i] == null) ? local : buffer[i];
         out.write(b,offset[i],length[i]);
      }
   }

   /**
     * Return the total number of characters stored in the buffer
     */
   public int size() {
      return size;
   }

   /**
     * Reset the buffer so it can be used again
     */
   public void reset() {
      for (int i = 0; i <= count; i++) {
         buffer[i] = null;
         offset[i] = 0;
         length[i] = 0;
         localPos = 0;
         count = -1;
         size = 0;
      }
      
   }

   /**
     * Does nothing
     */
   public void flush() { }


   /**
     * Does nothing
     */
   public void close() { }



   public static void main(String arg[]) throws Exception {

      QueueWriter qw = new QueueWriter(3);

      char cary[] = "Hello, brave new world".toCharArray();

      for (int j = 0; j < 2; j++) {
         for (int i = 0; i < arg.length; i++) {
            qw.write(arg[i],1,arg[i].length() - 2);
            // qw.write(arg[i]);
            qw.write(' ');
            qw.write(cary,7,6);
         }

         System.out.println("Count: " + qw.count);
         System.out.println("- - - - Output - - - -");
         System.out.println(qw.toString());
         qw.reset();
      } 
   }

}
