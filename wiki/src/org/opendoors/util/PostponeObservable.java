/*
 * @(#)PostponeObservable.java	1.0 yy/mm/dd
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

import java.awt.event.*;
import java.util.*;
import java.beans.*;

import org.opendoors.util.Console;
import org.opendoors.util.Timer;


/**
 * <P> PostponeObservable delegates observing and notification
 * of changes. This class will notify changes with deferral
 * of time so that the observers of the changes are not inundated
 * with notifications.
 * <p>
 * Since this class implements the PropertyChangeListener interface, the class
 * PropertyChangeSupport may also be employed as well to notify observers of changes.
 * <p>
 * This class is extends java.util.Observable. Observers must implement the 
 * interface java.util.Observer
 * @see java.util.observable
 * @see java.util.observer
 */
public class PostponeObservable extends Observable implements PropertyChangeListener, ActionListener {

	//-------public members-----
	/**
	 * On property change, postpone event notification this number of millis.
	 */
	protected int postponeInterval = 120000; //2 minutes

	/**
	 * While waiting to fire a notification, reset the interval if another change comes in.
	 */
	protected boolean resetClockOnUpdate = true;
	

	//-------private and protected members-----
	private long timeToNotify = System.currentTimeMillis();

	private Timer tick = null;


	//-------constructor(s)-----
	/**
	 * Constructs a default observable with no observers.
	 */
	public PostponeObservable() {
		init();
	}

	/**
	 * Constructs a default observer with the following settings.
	 * @param The interval to wait in millis between notifications.
	 * @param Resets the clock so that changes can be aggregated over a period of time
	 */
	public PostponeObservable(int postponeInterval, boolean resetClockOnUpdate) {
		this.postponeInterval = postponeInterval;
		this.resetClockOnUpdate = resetClockOnUpdate;
		init();
	}
		

	//-------public initializers/destroyers-----
	public void init() {
		tick = new Timer("PropertyObservable", postponeInterval, false);
		tick.addActionListener(this);
	}

	public void setPostponePeriod(int postponeInterval) {
		this.postponeInterval = postponeInterval;
		tick.setPeriod(postponeInterval);
	}

	public void enableResetClockOnUpdate(boolean enable) {
		this.resetClockOnUpdate = enable;
	}

	//-------public event handlers-----
	/**
	 * Changes to the source will get propagated through this method
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		setChanged();
	}

	public void actionPerformed(ActionEvent evt) {
		if (hasChanged() && (timeToNotify < System.currentTimeMillis())) {
			System.out.println("Observations: Notifying observers...");
			notifyObservers();
		}
	}

	//-------public getters and setters-----
	public void setChanged() {
		super.setChanged();
		if (resetClockOnUpdate) {
			System.out.println("Observation! Resetting clock to now + " + postponeInterval);
			timeToNotify = (System.currentTimeMillis() + postponeInterval);
		}
	}

}

