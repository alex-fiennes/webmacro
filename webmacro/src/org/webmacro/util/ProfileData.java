
/**
  * ProfileStatistics are data points about some object which is 
  * being profiled using the Profiler class. 
  */
public interface ProfileStatistics {

   /**
     * What is the name of the object being tracked?
     */
   public String getName();

   /**
     * How much time (milliseconds) were spent in the object 
     * being tracked?
     */
   public long getTime();

   /**
     * How many calls were made to the object being tracked?
     */
   public long getCalls();

   /**
     * This method may return null. If it returns non-null then it 
     * represents some kind of breakdown of the current Statistics. 
     */
   public Statistics[] getBreakdown();

}

