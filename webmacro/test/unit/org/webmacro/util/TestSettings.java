package org.webmacro.util;

import junit.framework.TestCase;

public class TestSettings extends TestCase
{
    public TestSettings (String name)
    {
        super(name);
    }


    protected void setUp ()
    {
    }


    public void testSimple () throws Exception
    {
        Settings s = new Settings();

        s.load("org/webmacro/util/settings1.properties");
//      String k[] = s.getKeys();
//      for (int i=0; i<k.length; i++) 
//        System.out.println("/" + k[i] + "/" + s.getSetting(k[i]) + "/");
        assertTrue(s.getIntegerSetting("one") == 1);
        assertTrue(s.getIntegerSetting("two") == 2);
        assertTrue(s.getIntegerSetting("three") == 3);
        assertTrue(s.getIntegerSetting("four") == 4);
        assertTrue(s.getIntegerSetting("notthere", 0) == 0);
        assertTrue(s.getSetting("a").equals("a"));
        assertTrue(s.getSetting("b").equals("b"));
        assertTrue(s.getSetting("c").equals("c"));
        assertTrue(s.getSetting("d").equals("d"));
        assertTrue(s.getSetting("e").equals("e"));
        assertTrue(s.getSetting("f").equals("f"));

        assertTrue(s.getSetting("a.b.c").equals("abc"));
        assertTrue(s.getSetting("a.*.c").equals("astarc"));

        assertTrue(s.getSetting("quoted").equals("quoted"));
        assertTrue(s.getSetting("quoted2").equals("quoted2"));
        assertTrue(s.getSetting("quoted3").equals(" quoted3 "));
        assertTrue(s.getSetting("with.spaces").equals("with spaces"));
        assertTrue(s.getSetting("nothing").equals(""));
        assertTrue(s.getSetting("alsonothing").equals(""));
    }


    public void testOcclude () throws Exception
    {
        Settings s = new Settings();

        s.load("org/webmacro/util/settings1.properties");
        s.load("org/webmacro/util/settings2.properties");

        assertTrue(s.getSetting("a").equals("aa"));
        assertTrue(s.getSetting("bb").equals("bb"));
    }


    public void testSubsettings () throws Exception
    {
        Settings s = new Settings();
        s.load("org/webmacro/util/settings1.properties");
        Settings ss = new SubSettings(s, "sub");
        Settings sss = new SubSettings(ss, "sub");
        Settings sssStar = new SubSettings(ss, "*");
        Settings ssss = new SubSettings(s, "sub.sub");

        assertTrue(s.getSetting("sub.sub.a").equals("a"));
        assertTrue(ss.getSetting("sub.a").equals("a"));
        assertTrue(ss.getSetting("a").equals("not a"));
        assertTrue(sss.getSetting("a").equals("a"));
        assertTrue(ssss.getSetting("a").equals("a"));
        assertTrue(sssStar.getSetting("a").equals("default"));
    }
}


