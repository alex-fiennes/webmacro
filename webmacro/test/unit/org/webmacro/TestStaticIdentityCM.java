package org.webmacro;

import org.webmacro.resource.*;

import junit.framework.*;

import java.util.Random;

/**
 * Test integrity and behaviour of
 * org.webmacro.resource.StaticIdentityCacheManager
 * @author Sebastian Kanthak
 */
public class TestStaticIdentityCM extends TestCase {
    public static final int THREADS = 10;
    public static final int LOOPS = 10000;
    public static final Random random = new Random();

    private WebMacro wm;
    private CacheManager cm;
    
    public TestStaticIdentityCM(String name) {
        super(name);
    }

    protected void setUp() throws InitException {
        if (System.getProperties().getProperty("org.webmacro.LogLevel") == null)
            System.getProperties().setProperty("org.webmacro.LogLevel", "ERROR");
        wm = new WM();
        cm = new StaticIdentityCacheManager();
        cm.init(wm.getBroker(),wm.getBroker().getSettings(),"foo");
    }

    /**
     * Tests integrity of cache under high load
     */
    public void testCacheIntegrity() throws InterruptedException {
        TestThread[] threads = new TestThread[THREADS];
        for (int i=0; i < THREADS; i++) {
            threads[i] = new TestThread(cm,i);
        }
        for (int i=0; i < THREADS; i++) {
            threads[i].start();
        }
        for (int i=0; i < THREADS; i++) {
            threads[i].join();
        }
        for (int i=0; i < THREADS; i++) {
            assertTrue("Test integrity check failed in thread "+i,
                   threads[i].correct);
        }
    }

    /**
     * Assures, that cm uses "==" to test
     * for equality
     */
    public void testCacheIdentityBehaviour() {
        String key1 = new String("foo");
        String key2 = new String("foo");
        assertTrue("test preconditions not met: keys should be different",
               key1 != key2 && key1.equals(key2));
        Object cached1 = new Object();
        Object cached2 = new Object();
        cm.put(key1,cached1);
        cm.put(key2,cached2);
        assertTrue("cm returned wrong object",
               cm.get(key1) == cached1);
        assertTrue("cm returned wrong object",
               cm.get(key2) == cached2);
    }

    static class TestThread extends Thread {
        private CacheManager cm;
        private int id;

        private Object[] sources = new Object[10];
        private Object[] cached = new Object[10];
        
        boolean correct = true; // until proved otherwise
        
        public TestThread(CacheManager cm,int id) {
            super();
            this.cm = cm;
            this.id = id;
        }

        public void run() {
            int count = 0;
            int loops = 0;
            for (int i=0; i < LOOPS; i++) {
                if (count++ == 0) 
                    fillCache();
                if (count == 100) {
                    count = 0;
                }
                int index = random.nextInt(sources.length);
                try {
                    Object test = cm.get(sources[index]);
                    if (test != cached[index])
                        correct = false;
                } catch (Exception e) {
                    correct = false;
                }
            }
        }

        private void fillCache() {
            for (int i=0; i < sources.length; i++) {
                sources[i] = new byte[1000]; // dummy cache key
                cached[i] = new Object(); // dummy cache load
                cm.put(sources[i],cached[i]);
            }
        }

    }

}
