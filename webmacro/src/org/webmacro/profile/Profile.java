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


package org.webmacro.profile;

import java.util.*;

final public class Profile
{

   private String _qName[] = new String[64];
   private long _qTime[] = new long[64];
   private int _qPtr = 0;

   private ProfileEvent[] _eventBuffer = new ProfileEvent[32];

   /** the ProfileCategory that controls us */
   ProfileCategory _owner;

   /** ProfileCategory uses this to manage us */
   protected long timestamp = 0;

   /** only our ProfileCategory should create new instances */
   protected Profile(ProfileCategory owner) {
      _owner = owner;
   }

   /**
     * Retrieve a list of the events in this profile. The returned
     * iterator will cycle through the ProfileEvent objects that 
     * were generated during the execution of this Profile.
     */
   public Iterator getEvents() throws IllegalStateException
   {
      final int last = _qPtr; 
      int next = -1;
      int lastOpen = -1;
      int depth = 0;
      final int numEvents = _qPtr / 2;
      ProfileEvent[] buf = _eventBuffer; 
      if (buf.length < numEvents) {
         buf = new ProfileEvent[numEvents];
         System.arraycopy(_eventBuffer,0,buf,0,_eventBuffer.length);
         _eventBuffer = buf;
      }

      try {
         for (int i = 0; i < last; i++) {
            String name = _qName[i];
            long time = _qTime[i];

            if (name != null) {
               // start a new event
               ProfileEvent wme = buf[++next]; 
               if (wme == null) {
                  wme = new ProfileEvent();
                  buf[next] = wme;
               }
               wme.name = name;
               wme.start = time;
               wme.duration = lastOpen; // tmp parent pointer
               wme.depth = depth++;
               lastOpen = next;
            } else {
               ProfileEvent wme = buf[lastOpen];
               lastOpen = wme.duration; // a tmp parent pointer
               wme.duration = (int) (time - wme.start);
               depth--;
            }
         }
        
         if ((depth != 0) || (next+1 != numEvents)) {
            throw new Exception("Invalid stack state");
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
         int mdepth = 0;
         for (int i = 0; i < _qPtr; i++) {
            if (_qName[i] != null) {
               sb.append(indent,0,mdepth);
               sb.append(_qName[i]);
               sb.append("\n");
               mdepth += 2;
            } else {
               mdepth -= 2;
            }
            if (mdepth > _qPtr/2) {
               sb.append("AT THIS POINT MORE START TIMINGS THAN STOPS\n");
            }
            if (mdepth < 0) {
               sb.append("AT THIS POINT MORE STOP TIMINGS THAN STARTS\n");
               mdepth = 0;
            }
         }
         sb.append("NUMBER OF START TIMINGS NOT EQUAL NUMBER OF STOPS:" 
               + "depth=" + depth + " " + "lastOpen=" + lastOpen + " " 
               + "next=" + next + " " + "last=" + last + " " 
               + "buf.length=" + buf.length);
         e.printStackTrace();

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
         Profile test = new Profile(null);

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
          ProfileEvent wme = (ProfileEvent) i.next(); 
          System.out.println(num++ + "\t" + wme.depth + "\t" + wme.name + "\t" 
               + wme.start + "\t" + wme.duration);
        }
     } catch (Exception e) {
         e.printStackTrace();
     }
   }
}
