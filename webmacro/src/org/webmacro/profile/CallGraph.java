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
         int evtDepth = evt.depth;
         int evtDuration = evt.duration;

         // move to parent
         while (evtDepth <= depth) {
            cg = (CallGraph) cgStack.pop();
            depth--;
         }

         // find child
         CallGraph child = (CallGraph) cg.children.get( evt.name );
         if (child == null) {
            child = new CallGraph(evt.name);
            cg.children.put(evt.name, child);
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

   public String format() {
      StringBuffer buf = new StringBuffer();
      format(buf,0);
      return buf.toString();
   }

   private static char[] _indentation = new char[0];
   public void format(StringBuffer buf, int depth) {
      int indent = depth * 2;
      if (indent > _indentation.length) {
         _indentation = new char[indent * 2];
         Arrays.fill(_indentation, ' ');
      }
      buf.append(_indentation,0,indent);
      buf.append(name);
      buf.append(": ");
      buf.append(duration);
      buf.append(" (");
      buf.append(calls);
      buf.append(", ");
      buf.append((double) duration/calls);
      buf.append(")\n");
      CallGraph kids[] = getChildren();
      for (int i = 0; i < kids.length; i++) {
         kids[i].format(buf, depth + 1);
      }
   }

}

