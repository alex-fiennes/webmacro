package org.webmacro.util;

import java.io.*;

import org.webmacro.*;
import org.webmacro.util.*;

import junit.framework.*;

public class TestSettings extends TestCase {
  public TestSettings(String name) {
    super(name);
  }

  protected void setUp() {
  }
  
  public void testSimple() throws Exception {
    Settings s = new Settings();

    s.load("org/webmacro/util/settings1.properties");
//      String k[] = s.getKeys();
//      for (int i=0; i<k.length; i++) 
//        System.out.println("/" + k[i] + "/" + s.getSetting(k[i]) + "/");
    assert(s.getIntegerSetting("one") == 1);
    assert(s.getIntegerSetting("two") == 2);
    assert(s.getIntegerSetting("three") == 3);
    assert(s.getIntegerSetting("four") == 4);
    assert(s.getIntegerSetting("notthere", 0) == 0);
    assert(s.getSetting("a").equals("a"));
    assert(s.getSetting("b").equals("b"));
    assert(s.getSetting("c").equals("c"));
    assert(s.getSetting("d").equals("d"));
    assert(s.getSetting("e").equals("e"));
    assert(s.getSetting("f").equals("f"));

    assert(s.getSetting("a.b.c").equals("abc"));
    assert(s.getSetting("a.*.c").equals("astarc"));

    assert(s.getSetting("quoted").equals("quoted"));
    assert(s.getSetting("quoted2").equals("quoted2"));
    assert(s.getSetting("quoted3").equals(" quoted3 "));
    assert(s.getSetting("with.spaces").equals("with spaces"));
    assert(s.getSetting("nothing").equals(""));
    assert(s.getSetting("alsonothing").equals(""));
  }

  public void testOcclude() throws Exception {
    Settings s = new Settings();

    s.load("org/webmacro/util/settings1.properties");
    s.load("org/webmacro/util/settings2.properties");

    assert(s.getSetting("a").equals("aa"));
    assert(s.getSetting("bb").equals("bb"));
  }

  public void testSubsettings() throws Exception {
    Settings s = new Settings();
    s.load("org/webmacro/util/settings1.properties");
    Settings ss = new SubSettings(s, "sub");
    Settings sss = new SubSettings(ss, "sub");
    Settings sssStar = new SubSettings(ss, "*");
    Settings ssss = new SubSettings(s, "sub.sub");

    assert(s.getSetting("sub.sub.a").equals("a"));
    assert(ss.getSetting("sub.a").equals("a"));
    assert(ss.getSetting("a").equals("not a"));
    assert(sss.getSetting("a").equals("a"));
    assert(ssss.getSetting("a").equals("a"));
    assert(sssStar.getSetting("a").equals("default"));
  }
}


