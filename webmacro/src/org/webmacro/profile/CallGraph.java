
package org.webmacro.profile;

import org.webmacro.util.SimpleStack;
import java.util.*;

/**
  * A CallGraph is a view of the events in a ProfileCategory. It 
  * is a tree-like structure which you can browse to learn where 
  * the time is going in your application.
  */
public class CallGraph {

   private String name;
   private long duration;
   private int calls;

   private HashMap children;

   private CallGraph(String name) {
      this.name = name;
      duration = 0;
      calls = 0;
      children = new HashMap();
   }

   final private static Comparator comparator = new Comparator() {
      final public int compare(Object a, Object b) {
         CallGraph ca = (CallGraph) a;
         CallGraph cb = (CallGraph) b;
         if (ca.duration < cb.duration) return 1;
         if (ca.duration > cb.duration) return -1;
         return 0;
      }
   };


   /**
     * Construct a CallGraph representing the nodes in the supplied root
     */
   public CallGraph(ProfileCategory root) {
      this(root.getName());
      Profile[] profiles = root.getProfiles();
      for (int i = 0; i < profiles.length; i++) {
         addProfile(profiles[i]);
      }
   }

   /**
     * Add the data from this profile to the call graph. 
     */
   private void addProfile(Profile p) {
      SimpleStack cgStack = new SimpleStack(); // rebuild invocation stack
      CallGraph cg = this;
      int depth = -1; // 0 is the first actual invocation, we are "above" that

      Iterator i = p.getEvents();
      while (i.hasNext()) {
         ProfileEvent evt = (ProfileEvent) i.next();
         int evtDepth = evt.getDepth();
         long evtDuration = evt.getStopTime() - evt.getStartTime();

         // move to parent
         while (evtDepth <= depth) {
            cg = (CallGraph) cgStack.pop();
            depth--;
         }

         // find child
         CallGraph child = (CallGraph) cg.children.get( evt.getName() );
         if (child == null) {
            child = new CallGraph(evt.getName());
            cg.children.put(evt.getName(), child);
         }
         cgStack.push(cg); 
         depth++;
         cg = child;

         // record statistics
         cg.calls++;
         cg.duration += evtDuration;
      }
   }

   public String toString() { return "CallGraph(" + name + ")"; }

   public String getName() { return name; }

   public CallGraph[] getChildren() { 
      CallGraph[] ret = 
         (CallGraph[]) children.values().toArray(new CallGraph[0]);
      Arrays.sort(ret, comparator);
      return ret;
   }

   public long getTime() { return duration; }

   public int getCalls() { return calls; }

}

