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
import java.beans.*;

/**
 * PostponeObservable is the delegated instance for an observable
 * with the following characteristic: notification of the change arrives
 * after the observable has not changed for a period of n milliseconds.
 * <p>
 * This pattern solves a fundamental problem, namely thrashing. Thrashing
 * occurs when the same action occurs repeatedly over short periods of
 * time because the same event is fired, usually hundreds or thousand of
 * times per second.
 * <p>
 * So, for example, if a cache changes thousands of times per second during
 * a load or a transition to a new working set, how many times do you want
 * to refresh it? Once or a thousand times? An instance of PostponeObservable
 * allows your cache (or any changing object) to notify that it has changed
 * just once.
 * <p>
 * Thus, changes are deferred so that the observers
 * of the changes are not inundated with notifications.
 * <p>
 * Since this class implements the PropertyChangeListener interface, the class
 * PropertyChangeSupport may also be employed as well to notify
 * observers of changes.
 * <p>
 * This class is extends java.util.Observable. Observers must implement the 
 * interface java.util.Observer.
 * <p><b>Note:</b> Shared instances of a PostponeObservable must implement their
 * own synchronizing strategy.
 * <p>Postponeability may be enabled/disabled at runtime.
 * @see java.util.Observable
 * @see java.util.Observer
 */
public class PostponeObservable extends Observable implements PropertyChangeListener {

	//-------public members-----
	/**
	 * On property change, postpone event notification this number of millis.
	 */
	protected int postponeInterval = 120000; //2 minutes

	/**
	 * While waiting to fire a notification,
	 * reset the interval if another change comes in.
	 */
	protected boolean resetClockOnUpdate = true;
	

	//-------private and protected members-----
	private long timeToNotify = System.currentTimeMillis();

	private Timer tick = null;


	//-------constructor(s)-----
	/**
	 * Constructs a default observable.
	 */
	public PostponeObservable() {
		init();
	}

	/**
	 * Constructs a default observable with the following settings.
	 * @param The interval to wait in millis between notifications.
	 * @param Resets the clock so that changes can be aggregated
	 * over a period of time
	 */
	public PostponeObservable(int postponeInterval, boolean resetClockOnUpdate) {
		this.postponeInterval = postponeInterval;
		this.resetClockOnUpdate = resetClockOnUpdate;
		init();
	}
		
  /** Initializes the instance. */
	protected void init() {
		tick = new Timer("PropertyObservable", postponeInterval, false);
		tick.addObserver(new TimerObserver());
	}

	//-------public initializers/destroyers-----
	

  /** Sets the observable period. */
	public void setPostponePeriod(int postponeInterval) {
		this.postponeInterval = postponeInterval;
		tick.setPeriod(postponeInterval);
	}

  /** Enables postponeability if true. */
	public void enablePostponeability(boolean enable) {
		this.resetClockOnUpdate = enable;
	}

	//-------public event handlers-----
	/**
	 * Using the property event model
	 * propagate a change event to the observable.
	 * @param evt The property change event which can be null.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		setChanged();
	}

  /**
   * Call back from the timer when
   * the observation period has expired.
   */
	public void timerAction() {
		if (hasChanged() && (timeToNotify < System.currentTimeMillis())) {
			notifyObservers();
		}
	}

  /**
   * Signals that the observable has changed.
   * <p>
   * Observers will be
   * notified when
   * <pre>
   * current time > (time of last change + postponeInterval)
   * </pre>
   * provided postponeability is enabled.
   */
	public void setChanged() {
		super.setChanged();
		if (resetClockOnUpdate) {
			timeToNotify = (System.currentTimeMillis() + postponeInterval);
		}
	}

	/** Destoys this instance and the associated timer. */
	public void destroy() {
	  tick.stop();
	  tick = null;
	}

  /**
   * Class which listens to updates in the observable tick and calls
   * the timer notifiction method.
   */
	class TimerObserver implements Observer {
	  public void update(Observable o, Object arg) { timerAction(); }
	}
	    
	  

}

