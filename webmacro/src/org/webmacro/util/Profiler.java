
package org.webmacro.util;
import java.util.*;

/**
  * A Profiler is a tool which you can use to collect timing statistics
  * on a portion of code. You do so by marking the start() and the end()
  * of some execution block. Later you can extract statistics from 
  * the Profiler showing where the time is going.
  * <p>
  * The Profiler works by sampling techniques. It does not measure the
  * time on every method call, but instead measures the time on one out
  * of every N method calls, where you get to specify N. If you set N
  * to zero then no profiling is performed. If you set it to 1 then 
  * every method is timed. You should probably pick a prime-like 
  * number for N to reduce the chance that one out of every N methods
  * is actually the same method call each time (if the method called
  * on a typical request were a multiple of N, for example).
  */
public class Profiler 
{

   // how often we sample
   final private int _rate;

   // how many times we've been called since last sample
   private volatile int _count;

   // name of this profiler
   final private String _name;

   // a pool of ProfileNode objects which we re-use
   final SimpleStack _nodeStack = new SimpleStack();

   // data points collected by this profiler
   private HashMap _leaves = new HashMap();

   // complex data points: sub-profilers
   final private List _children = new LinkedList();

   /**
     * The samplignRate is an integer. The rate at which results will
     * be recorded is 1/samplingRate. If samplignRate is zero then no
     * results will be recorded. If it is 1 then all results will be
     * recorded. If it is 1000 then 1/1000 of results will be sampled.
     * <p>
     * It is probably a good idea to set samplingRate to a prime-like
     * number.
     */
   public Profiler(String name, int samplingRate) {
      _name = name;
      _rate = samplingRate;   
   }

   /**
     * Create a profiler which has this profiler as a parent. This
     * will help generate hierarchical profiling data. The child 
     * profiler will have its results included in the parents, with
     * each of its timing data points prefixed with the name of
     * the parent profiler.
     */
   public Profiler newProfiler(String childName) {
      Profiler child = new Profiler(_name + ":" + childName, _rate);
      synchronized(this) {
         _children.add(child);
      }
      return child;
   }

   /**
     * Convenience method... the actual name will be the concatenation
     * of the supplied strings, but only if profiling occurs. The 
     * array will be joined with '.' characters. 
     */
   final public Object start(String name, String arg[]) {
      if ((_rate == 0) || (++_count < _rate)) return null;
      _count = 0;
      StringBuffer buf = new StringBuffer();
      buf.append(name);
      buf.append(":");
      for (int i = 0; i < arg.length; i++) {
         if (i != 0) {
            buf.append("."); 
         }
         buf.append(arg[i]);
      }
      return sample(buf.toString());
   }

   /**
     * Convenience method... the actual name will be the concatenation
     * of the supplied Object.toString()'s, but only if profiling occurs. 
     * The array will be joined with '.' characters. 
     */
   final public Object start(String name, Object arg[]) {
      if ((_rate == 0) || (++_count < _rate)) return null;
      _count = 0;
      StringBuffer buf = new StringBuffer();
      buf.append(name);
      buf.append(":");
      for (int i = 0; i < arg.length; i++) {
         if (i != 0) {
            buf.append("."); 
         }
         if (arg[i] != null) {
            buf.append(arg[i].toString());
         } else {
            buf.append("null");
         }
      }
      return sample(buf.toString());
   }

   /**
     * Convenience method... the actual name will be the concatenation
     * of the supplied strings, but only if profiling occurs.
     */
   final public Object start(String name1, String name2) {
      if ((_rate == 0) || (++_count < _rate)) return null;
      _count = 0;
      return (sample(name1 + ":" + name2));
   }

   /**
     * Convenience method... the actual name will be the concatenation
     * of the supplied strings, but only if profiling occurs.
     */
   final public Object start(String name1, Object name2) {
      if ((_rate == 0) || (++_count < _rate)) return null;
      _count = 0;
      return sample(name1 + ":" + ((name2 != null) ? name2 : "null") );
   }

   /**
     * Mark the start of a timing section. You must call stop with 
     * the object returned from this method.
     */
   final public Object start(String name) {
      if ((_rate == 0) || (++_count < _rate)) return null;
      _count = 0;
      return sample(name);
   }

   /**
     * This is the method that actually collects timing data. It is
     * typically called by start(). The object returned must 
     * eventually be passed to stop().
     */
   final protected Object sample(String name) {
      TimingNode n = (TimingNode) _nodeStack.pop();
      if (n == null) {
         n = new TimingNode(); 
      }
      synchronized(this) {
         n.data = (ProfilerStatistics) _leaves.get(name);
         if (n.data == null) {
            n.data = new ProfilerStatistics(_name + ":" + name);
            _leaves.put(name,n.data);
         }
      }
      n.start = System.currentTimeMillis();
      return n;
   }

   /**
     * You must call this method with the object returned by
     * start() or record(). Note: you must call it from the same thread 
     * that called the start() method. 
     */
   final public void stop(Object token) {
      if (token == null) return; // no token was issued
      long stop = System.currentTimeMillis();
      TimingNode n = (TimingNode) token;
      ProfilerStatistics data = n.data;
      long duration = stop - n.start;
      synchronized(data) {
         data.time += duration;
         data.calls++;
      }
      _nodeStack.push(n);
   }

   /**
     * Get a breakdown of where the time has been going.
     */
   final public ProfilerStatistics[] getStatistics() {
      SortedSet ss = new TreeSet(_profileComparator);
      getStatistics(ss);
      return (ProfilerStatistics[]) ss.toArray(new ProfilerStatistics[0]);
   }

   /**
     * This method actually implements the collection of statistics. 
     * It tries to synchronize only briefly so as not to lock up the
     * application during the collection of statistics. 
     */
   final private void getStatistics(SortedSet ss) {
      synchronized(this) {
         ss.addAll(_leaves.values());
      }
      Iterator i = _children.iterator();
      while (i.hasNext()) {
         ((Profiler) i.next()).getStatistics(ss);
      }
   }

   private final static Comparator _profileComparator = new Comparator() 
   {
      public int compare(Object a, Object b) {
         ProfilerStatistics pa = (ProfilerStatistics) a;
         ProfilerStatistics pb = (ProfilerStatistics) b;
         if (pa.time > pb.time) return -1;
         if (pa.time == pb.time) {
            if (pa.hashCode() < pb.hashCode()) return -1;
         }
         return 1;
      }
   };

   public static void main(String arg[]) {

      try {
         Profiler p = new Profiler("test", 7);

         Object o;
         long duration = 0;

         for (int i = 0; i < 5000; i++) {
            if (Math.abs(i % 100) == 0) {
               System.out.println("iteration " + i);
            }

            o = p.start("sleep 5 milliseconds");
            Thread.sleep(5);
            p.stop(o);

            long start = System.currentTimeMillis();
            Thread.sleep(5);
            long stop = System.currentTimeMillis();
            duration += stop - start;

         }
         System.out.println("Real duration: " + (duration/5000));

         ProfilerStatistics ps[] = p.getStatistics();
         for (int i = 0; i < ps.length; i++) {
            long time = ps[i].getTime();
            long calls = ps[i].getCalls();
            long avgTime = time/calls;

            System.out.println("Name    : " + ps[i].getName());   
            System.out.println("Time    : " + time);
            System.out.println("Calls   :" + calls);
            System.out.println("Avg Time: " + avgTime);
            System.out.println();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
}

class TimingNode {
   long start;
   ProfilerStatistics data;
}


