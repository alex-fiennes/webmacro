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

package org.opendoors.util;
import java.util.*;
import java.io.Serializable;

/**
 * This class provides timer and supporting events to client objects
 * by extending the Observable base class. Clients get notifications
 * of changes in the clock.
 * <p>
 * The interval for firing changes to observers is resetable in the event
 * the observer needs more frequent updates of changes.
 * <p>
 * The instance may be a one shot timer as well as a recurring timers. This
 * supports a one shot notification in the future.
 * <p>
 * Observable is thread-safe and supports multiple listeners to a single
 * observable such as an observable which fires every 24 hours.
 */
public class Timer extends Observable implements Runnable, Serializable {

  /** The timer thread. */
	private transient Thread timerThread;

  /** Signal to stop the thread. */
	private transient boolean stopThread = false;

  /** The name. */
	private String name;

  /** The interval for firing. */
	private int  period;  // in milliseconds

  /** Is a one shot timer. */
	private boolean oneShot;

	/**
	 * Public constructor to create a timer event source using factory defaults
	 * which are 1 second timer, recurring.
	 */
	public Timer() {
		this("PerSecondTimer", 1000, false);
	}

	/**
	 * Construct a timer with the certain properties set.
	 * @param The name of the timer.
	 * @param The period in milliseconds.
	 * @param True if this is a oneShot instance
	 */
	public Timer(String name, int period, boolean oneShot) {
		super();

		// Set properties
		this.name = name;
		this.period = period;
		this.oneShot = oneShot;

		// Create the clock thread
		timerThread = new Thread(this, name);
		timerThread.setDaemon(true);
		timerThread.start();
	}

	/**
	 * Stop the timer
	 */
	public void stop() {
		stopThread = true;
	}
	

	// Accessor methods
	/**
	 * Gets the name of the timer.
	 * @return The timer name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the timer.
	 * @param The textual name.
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * Gets the period between timer events.
 	 * @return The interval in milliseconds
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Sets the period in milliseconds.
	 * @param The interval.
	 */
	public void setPeriod(int p) {
		period = p;
		if (timerThread != null)
			timerThread.interrupt();
	}

	/**
	 * Interrogates if this is a one-shot operation.
	 * @return  True, if a one-shot operation.
	 */
	public boolean isOneShot() {
		return oneShot;
	}

	/**
	 * Set this object to be one-shot or not.
	 * @param True, if one-shot operation desired
	 */
	public void setOneShot(boolean os) {
		oneShot = os;
	}

	/**
	 * Run the timer.
	 */
	public void run() {
		while (timerThread != null) {
		  // Sleep for the period
		  try {
		    timerThread.sleep(period);
		  } catch (InterruptedException e) {
		    // Restart the loop
		    continue;
		  }

		  // Fire an action event
		  setChanged();
  		notifyObservers();

		  if (oneShot || stopThread)
		    break;
		}
		// clean up:
		timerThread = null;
	}

}
