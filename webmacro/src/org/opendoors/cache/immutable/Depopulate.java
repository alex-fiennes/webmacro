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
 * Depopulates a shared cache from integer n up to m with Integer(n <= i < m).
 * <p>
 * Use as a driver to depopulate a cache.
 * @see Populate
 */
public class Depopulate implements Runnable {

  private Cache cache;
  int n, m;

  /** Construct the depopulator with test parameters. */
  public Depopulate(Cache cache, int n, int m) {
    this.cache = cache;
    this.n = n;
    this.m = m;
  }

  /** Depopulates the cache according to the instance parameters. */
  public void run() {
    for (int value = n; value < m; value++) {
      Integer i = new Integer(value);
      cache.invalidate(i);
    }
  }
}
