
package org.webmacro.profile;

/**
  * The ProfileEvent interface describes an event that took place
  * in the system. All that is recorded about the event is its name,
  * when it started, and when it stopped. 
  */
final public class ProfileEvent 
{

   /**
     * The name of this event
     */
   public String name;

   /**
     * Milliseconds since Jan 1, 1970 that this event started
     */
   public long start;

   /**
     * Milliseconds that this event lasted for
     */
   public int duration;

   /**
     * How many levels down the "call stack" is this event? In other
     * words, how many events enclose it.
     */
   public int depth;

   /**
     *  Create a new profile event with null and 0 values
     */
   public ProfileEvent() { this(null,0,0,0); }

   /**
     * Create a new profile event with the supplied values
     */
   public ProfileEvent(String name, int start, int duration, int depth) {
      this.name = name;
      this.start = start;
      this.duration = duration;
      this.depth = depth;
   }

}

