
package org.webmacro.profile;

/**
  * The ProfileEvent interface describes an event that took place
  * in the system. All that is recorded about the event is its name,
  * when it started, and when it stopped. 
  */
final public class WMProfileEvent 
{

   long start;
   long stop;
   String name;
   int depth;

   /**
     * Milliseconds since Jan 1, 1970 that the event began
     */
   public long getStartTime() { return start; }

   /**
     * Milliseconds, since Jan 1, 1970 that the event ended
     */
   public long getStopTime() { return stop; }

   /**
     * Descriptive name of the event
     */
   public String getName() { return name; }

   /**
     * How many levels of nesting are we at? 
     */
   public int getDepth() { return depth; }
}

