
package org.webmacro.profile;

import java.util.*;

final public class WMProfile implements Profile
{

   private String _qName[] = new String[64];
   private long _qTime[] = new long[64];
   private int _qPtr = 0;

   private WMProfileEvent[] _eventBuffer = new WMProfileEvent[32];

   /** the ProfileCategory that controls us */
   WMProfileCategory _owner;

   /** WMProfileCategory uses this to manage us */
   protected long timestamp = 0;

   /** only our ProfileCategory should create new instances */
   protected WMProfile(WMProfileCategory owner) {
      _owner = owner;
   }

   /**
     * Retrieve a list of the events in this profile. The returned
     * iterator will cycle through the ProfileEvent objects that 
     * were generated during the execution of this Profile.
     */
   public Iterator getEvents() throws IllegalStateException
   {

      try {
         WMProfileEvent[] buf = _eventBuffer; 
         final int numEvents = _qPtr / 2;
         if (buf.length < numEvents) {
            buf = new WMProfileEvent[numEvents];
            System.arraycopy(_eventBuffer,0,buf,0,buf.length);
            _eventBuffer = buf;
         }
      
         final int last = _qPtr; 
         int next = -1;
         int lastOpen = -1;
         int depth = 0;
         for (int i = 0; i < last; i++) {
            String name = _qName[i];
            long time = _qTime[i];

            if (name != null) {
               // start a new event
               WMProfileEvent wme = buf[++next]; 
               if (wme == null) {
                  wme = new WMProfileEvent();
                  buf[next] = wme;
               }
               wme.name = name;
               wme.start = time;
               wme.duration = lastOpen; // tmp parent pointer
               wme.depth = depth++;
               lastOpen = next;
            } else {
               WMProfileEvent wme = buf[lastOpen];
               lastOpen = wme.duration; // a tmp parent pointer
               wme.duration = (int) (time - wme.start);
               depth--;
            }
         }
        
         if ((depth != 0) || (next+1 != numEvents)) {
            throw new Exception(); // not as many starts as stops
         }

         return new Iterator() {

            int pos = 0;

            public boolean hasNext() {
               return (pos < numEvents);
            }
            
            public Object next() {
               return (_eventBuffer[pos++]);
            }

            public void remove() throws UnsupportedOperationException
            {
               throw new UnsupportedOperationException("Cannot remove");
            }
            
         };

      } catch (Exception e) {
         StringBuffer sb = new StringBuffer();

         sb.append("Profile: start/stop timing call graph invalid:\n");

         char[] indent = new char[_qPtr * 4];
         Arrays.fill(indent, ' ');
         int depth = 0;
         for (int i = 0; i < _qPtr; i++) {
            if (_qName[i] != null) {
               sb.append(indent,0,depth);
               sb.append(_qName[i]);
               sb.append("\n");
               depth += 2;
            } else {
               depth -= 2;
            }
            if (depth > _qPtr/2) {
               sb.append("AT THIS POINT MORE START TIMINGS THAN STOPS\n");
            }
            if (depth < 0) {
               sb.append("AT THIS POINT MORE STOP TIMINGS THAN STARTS\n");
               depth = 0;
            }
         }
         sb.append("NUMBER OF START TIMINGS NOT EQUAL NUMBER OF STOPS\n");
         throw new IllegalStateException(sb.toString());
      }
   }

   /**
     * This method resets the data structures for this Profile
     * so it can be re-used. Ordinarily only our controlling 
     * category will call this method.
     */
   protected void reset() {
      _qPtr = 0;
   }

   /**
     * Start timing an event called 'name'. You *MUST* call stop() 
     * later in order to get valid results. 
     */
   public void startEvent(String name) {
      try {
         _qName[_qPtr] = name;      
      } catch (ArrayIndexOutOfBoundsException e) {
         expand();
         _qName[_qPtr] = name;      
      }
      _qTime[_qPtr++] = time();
   }

   /**
     * Stop timing the last event started. You *MUST* have called 
     * start() prior to this in order to get coherent results.
     * @exception IllegalStateException if stopEvent called too many times
     */
   public void stopEvent() 
   {
      long time = time();
      try {
         _qName[_qPtr] = null;
      } catch (ArrayIndexOutOfBoundsException e) {
         expand();
         _qName[_qPtr] = null;
      }
      _qTime[_qPtr++] = time;
   }

   /**
     * Double the size of the arrays
     */
   private void expand() {
      String tmpName[] = new String[ _qPtr * 2 ];
      System.arraycopy(_qName,0,tmpName,0,_qName.length);
      _qName = tmpName;
      long tmpTime[] = new long[ _qPtr * 2 ];
      System.arraycopy(_qTime,0,tmpTime,0,_qTime.length);
      _qTime = tmpTime;
   }

   /**
     * Get the current time as a long
     */
   private long time() {
      return System.currentTimeMillis();
   }


   /**
     * Terminate profiling, releasing any resources allocated to 
     * this Profile and forwarding any collected statistics back
     * to the ProfileSystem. It is expected that implementations 
     * will use this method to recycle the Profile objects 
     * themselves for efficiency.
     */
   public void destroy() {
      _owner.record(this);   
   }

   public static void main(String arg[]) {
      try {
         WMProfile test = new WMProfile(null);

         test.startEvent("1"); Thread.sleep(5);
          test.startEvent("1.1"); Thread.sleep(5);
           test.startEvent("1.1.1"); Thread.sleep(5);
           test.stopEvent(); Thread.sleep(5);
          test.stopEvent();
          test.startEvent("1.2"); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
          test.startEvent("1.3"); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
         test.stopEvent(); Thread.sleep(5);

         test.startEvent("2"); Thread.sleep(5);
          test.startEvent("2.1"); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
          test.startEvent("2.2"); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
         test.stopEvent(); Thread.sleep(5);

         test.startEvent("3"); Thread.sleep(5);
          test.startEvent("3.1"); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
          test.startEvent("3.2"); Thread.sleep(5);
           test.startEvent("3.2.1"); Thread.sleep(5);
           test.stopEvent(); Thread.sleep(5);
           test.startEvent("3.2.2"); Thread.sleep(5);
            test.startEvent("3.2.2.1"); Thread.sleep(5);
            test.stopEvent(); Thread.sleep(5);
           test.stopEvent(); Thread.sleep(5);
          test.stopEvent(); Thread.sleep(5);
         test.stopEvent(); Thread.sleep(5);

         test.startEvent("4"); Thread.sleep(5);
         test.stopEvent(); Thread.sleep(5);
        
        Iterator i = test.getEvents();
        int num = 0;
        while (i.hasNext()) {
          WMProfileEvent wme = (WMProfileEvent) i.next(); 
          System.out.println(num++ + "\t" + wme.depth + "\t" + wme.name + "\t" 
               + wme.start + "\t" + wme.duration);
        }
     } catch (Exception e) {
         e.printStackTrace();
     }
   }
}
