
package org.webmacro.resource;

import java.lang.ref.SoftReference;

/**
  * This class is used by CachingProvider to manage object 
  * references with timeouts. A TimedReference can be 
  * garbage collected prior to the timeout, but will be 
  * released no later than the specified timeout.
  */
public class TimedReference extends SoftReference {
   final long _timeout;

   /**
     * Construct a soft reference, specifying how long the 
     * reference is valid for. Beyond the specified timeout 
     * this object will return null.
     */
   public TimedReference(Object referent, long timeout) {
      super(referent);
      _timeout = timeout;
   }

   /**
     * Return the number of milliseconds this object is valid
     * for, beginning with the creation time.
     */
   public long getTimeout() {
      return _timeout;
   }

}
