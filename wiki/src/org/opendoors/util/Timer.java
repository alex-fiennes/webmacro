/*
 * @(#)Timer.java	1.1 97/10/01
 * 
 * Copyright (c) 1996, 1997 Open Doors Software, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Open Doors Software
 * Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Open Doors Software.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1
 * 
 */

package org.opendoors.util;

// Imports
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Date;
import java.io.Serializable;

/**
 *
 * This class provides timer events to client objects.
 * <p>
 * This class is a fully compliant, invisible bean.
 * 
 */
public class Timer implements Runnable, Serializable {

	private static int	numTimers = 0;

	private transient Thread timerThread;

	private transient boolean stopThread = false;

	private ActionListener  actionListener;

	private String name;

	private int  period;  // in milliseconds

	private boolean oneShot;

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
		  fireActionEvent();

		  if (oneShot || stopThread)
		    break;
		}
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

	// Private support methods

	/**
	 * Call-back for a timer event
	 */
	private void fireActionEvent() {
		processActionEvent(timerEvent);
	}
}
