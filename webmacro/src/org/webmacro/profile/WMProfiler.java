
package org.webmacro.profile;

import java.util.*;

final public class WMProfiler implements Profiler
{

   /** this is a queue of all the events that have happened */
   private WMProfileEvent[] _q = new WMProfileEvent[64];
   private int _qPtr = 0;

   /** this is a stack pointing into the queue, representing which 
     * events are still outstanding and have not yet been stopped 
     */
   private int[] _stack = new int[16];
   private int _stackPtr = 0;

   /** the ProfileCategory that controls us */
   WMProfileCategory _owner;


   /** WMProfilerCategory uses this to manage us */
   protected long timestamp = 0;

   /** only our ProfileCategory should create new instances */
   protected WMProfiler(WMProfileCategory owner) {
      _owner = owner;
   }

   /**
     * Retrieve a list of the events in this profile. The returned
     * iterator will cycle through the ProfileEvent objects that 
     * were generated during the execution of this Profiler.
     */
   public Iterator getEvents() {
      final int end = _qPtr;
      return new Iterator() {
         int pos = 0;

         public boolean hasNext() { return (pos < end); }

         public Object next() throws NoSuchElementException
         { 
            try {
               return _q[pos++]; 
            } catch (ArrayIndexOutOfBoundsException e) {
               throw new NoSuchElementException("Advanced past end of list");
            }
         }

         public void remove() throws UnsupportedOperationException
         {
            throw new UnsupportedOperationException("Cannot remove nodes");
         }
      };
   }

   /**
     * This method resets the data structures for this Profiler
     * so it can be re-used. Ordinarily only our controlling 
     * category will call this method.
     */
   protected void reset() {
      _qPtr = 0;
      _stackPtr = 0;
   }

   /**
     * Start timing an event called 'name'
     */
   public void startEvent(String name) {
      WMProfileEvent evt;
      try {
         evt = _q[_qPtr];
      } catch (ArrayIndexOutOfBoundsException e) {
         WMProfileEvent[] tmp = new WMProfileEvent[ _qPtr * 2 ];
         System.arraycopy(_q,0,tmp,0,_q.length);
         _q = tmp; 
         evt = null;
      } 
      if (evt == null) {
         evt = new WMProfileEvent();
         _q[_qPtr] = evt;
      }

      try {
         _stack[_stackPtr] = _qPtr;
      } catch (ArrayIndexOutOfBoundsException e) {
         int[] tmp = new int[ _stackPtr * 2 ];
         System.arraycopy(_stack,0,tmp,0,_stackPtr - 1);
         _stack = tmp;
         _stack[_stackPtr] = _qPtr;
      }
      _qPtr++;
      _stackPtr++;
      evt.start = System.currentTimeMillis();
   }

   /**
     * Stop timing the last event started
     * @exception IllegalStateException if stopEvent called too many times
     */
   public void stopEvent() 
      throws IllegalStateException
   {
      long stop = System.currentTimeMillis();
      _stackPtr--; 
      try {
         _q[ _stack[_stackPtr] ].stop = stop;
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalStateException("stopEvent() called more than startEvent(): mismatched calls to stop and start on profiler.");
      }
   }

   /**
     * Terminate profiling, releasing any resources allocated to 
     * this Profiler and forwarding any collected statistics back
     * to the ProfileSystem. It is expected that implementations 
     * will use this method to recycle the Profiler objects 
     * themselves for efficiency.
     */
   public void destroy() {
      _owner.record(this);   
   }
}


