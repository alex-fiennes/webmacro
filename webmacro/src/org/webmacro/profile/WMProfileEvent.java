
package org.webmacro.profile;

/**
  * The ProfileEvent interface describes an event that took place
  * in the system. All that is recorded about the event is its name,
  * when it started, and when it stopped. 
  */
final public class WMProfileEvent implements ProfileEvent
{

   String name = null;
   long start = 0;
   int duration = 0;
   int depth = 0;

   /**
     * Milliseconds since Jan 1, 1970 that the event began
     */
   public long getStartTime() { return start; }

   /**
     * Milliseconds, since Jan 1, 1970 that the event ended
     */
   public long getStopTime() { return start + duration; }

   /**
     * Duration that the event lasted
     */
   public int getDuration() { return duration; }

   /**
     * How many levels deep is this event in the tree?
     */
   public int getDepth() { return depth; }


   /**
     * Descriptive name of the event
     */
   public String getName() { return name; }

}

