/*
* Copyright Open Doors Software and Acctiva, 1996-2001.
* All rights reserved.
*
* Software is provided according to the MPL license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.cache.immutable;
import org.opendoors.cache.Cache;

/**
 * Populates a shared cache from integer n up to m with Integer(n <= i < m).
 * <p>
 * Use as a test driver to populate a cache.
 * @see Depopulate
 */
public class Populate implements Runnable {

  private Cache cache;
  int n, m;
  public long tet;

  /** Construct the populator with test parameters. */
  public Populate(Cache cache, int n, int m) {
    this.cache = cache;
    this.n = n;
    this.m = m;
  }

  /** Populates the cache according to the instance parameters. */
  public void run() {
    tet = System.currentTimeMillis();
    for (int value = n; value < m; value++) {
      Integer i = new Integer(value);
      cache.put(i, i);
    }
    tet = System.currentTimeMillis()-tet;
  }
}
