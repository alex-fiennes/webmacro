
package org.webmacro.profile;

/**
  * The ProfileEvent interface describes an event that took place
  * in the system. All that is recorded about the event is its name,
  * when it started, and when it stopped. 
  */
public interface ProfileEvent {

   /**
     * Descriptive name of the event
     */
   public String getName();

   /**
     * Milliseconds since Jan 1, 1970 that the event began
     */
   public long getStartTime();

   /**
     * Milliseconds, since Jan 1, 1970 that the event ended
     */
   public long getStopTime();

   /**
     * Depth into the tree: how many levels deep is this?
     */
   public int getDepth();

   /**
     * How long did the event last?
     */
   public int getDuration();


}

