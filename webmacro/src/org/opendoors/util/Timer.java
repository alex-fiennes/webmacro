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

import java.awt.AWTEventMulticaster;
import java.awt.event.*;
import java.beans.*;
import java.util.Date;
import java.io.Serializable;

/**
 *
 * This class provides timer and supporting events to client objects.
 * <p>
 * The instance may be a one shot timer as well as a recurring timers.
 * Multiple listeners may listen to an instance of a single timer instance.
 */
public class Timer implements Runnable, Serializable {

  /** The number of timers to date. */
	private static int	numTimers = 0;

  /** The timer thread. */
	private transient Thread timerThread;

  /** Signal to stop the thread. */
	private transient boolean stopThread = false;

  /** The listeners to this timer. */
	private ActionListener  actionListener;

  /** The name. */
	private String name;

  /** The interval for firing. */
	private int  period;  // in milliseconds

  /** Is a one shot timer. */
	private boolean oneShot;

  /** The action event which so as to reduce object churn. */
	private ActionEvent timerEvent;

	/**
	 * Public constructor to create a timer event source using factory defaults.
	 */
	public Timer() {
		this(new String("timer" + numTimers++), 1000, false);
		timerEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
	}

	/**
	 * Construct a timer with the certain properties set.
	 * @param The name of the timer.
	 * @param The period in milliseconds.
	 * @param True if this is a oneShot instance
	 */
	public Timer(String name, int period, boolean oneShot) {
		// Allow the superclass constructor to do its thing
		super();

		// Set properties
		this.name = name;
		this.period = period;
		this.oneShot = oneShot;

		// Create the clock thread
		timerThread = new Thread(this, "Timer");
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

	// Other public methods
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
  		processActionEvent(timerEvent);

		  if (oneShot || stopThread)
		    break;
		}
		// clean up:
		timerThread = null;
		actionListener = null;
		timerEvent = null;
	}

	// Event processing methods

	/**
	 * Adds a client listener for this timer's events
	 * @param The listener.
	 */
	public synchronized void addActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	/**
	 * Removes the client listener for this timer's events
	 * @param The listener.
	 */
	public synchronized void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	/**
	 * Process an action event and notify listeners
	 * @param The action event.
	 */
	protected void processActionEvent(ActionEvent e) {
		// Deliver the event to all registered action event listeners
		if (actionListener != null)
		  actionListener.actionPerformed(e);
	}

}
