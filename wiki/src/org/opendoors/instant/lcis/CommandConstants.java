/*
 * @(#)CommandConstants.java	1.0 98/10/01
 * Copyright (c) 1998 Open Doors Software, Inc. All Rights Reserved.
 * 
 * Open Doors Software MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. OPEN DOORS SOFTWARE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */
package org.opendoors.instant.lcis;

/**
 * LCIS Commands is a set of useful constants for LCIS commands from one application
 * to another.
 * <p>
 * Command identifiers > 1000 reserved for user definition outside this package.
 */

public interface CommandConstants {

	/**
	 * Identifier is not set.
	 */
	public static final int NOT_SET = -1;	

	/**
	 * Read something.
	 */
	public static final int READ = 0;

	/**
	 * Read something in shared mode. No updates allowed
	 */
	public static final int READ_SHARE = 1;

	/**
	 * Read something in exclusive mode. No other readers, updaters.
	 */
	public static final int READ_EXCLUSIVE = 2;

	/**
	 * Release share.
	 */
	public static final int RELEASE_SHARE = 3;

	/**
	 * Release exclusive.
	 */
	public static final int RELEASE_EXCLUSIVE = 4;

	/** Explicit lease semantic, share. */
	public static final int LEASE_SHARE = 10;

	/** Explicit lease semantic, exclusive. */
	public static final int LEASE_EXCLUSIVE = 11;

	/** Explicit lease semantic, cancel share. */
	public static final int CANCEL_LEASE_SHARE = 12;

	/** Explicit lease semantic, cancel exclusive. */
	public static final int CANCEL_LEASE_EXCLUSIVE = 13;

	/** Notification of lease expirtation. */
	public static final int LEASE_EXPIRED = 14;

	/**
	 * Navigation, first.
	 */
	public static final int FIRST = 20;

	/**
	 * Navigation, next.
	 */
	public static final int NEXT = 21;

	/**
	 * Navigation, previous.
	 */
	public static final int PREVIOUS = 22;

	/**
	 * Navigation, first.
	 */
	public static final int LAST = 23;

	/**
	 * Navigation, key
	 */
	public static final int KEY_ACCESS = 24;

	/**
	 * Refresh.
	 */
	public static final int REFRESH = 40;

	/**
	 * Refresh  filter on
	 */
	public static final int REFRESH_FILTER_ON = 41;

	/**
	 * Refresh filter off
	 */
	public static final int REFRESH_FILTER_OFF = 42;

	/**
	 * Query an entity
	 */
	public static final int QUERY_ENTITY = 43;
	
	/**
	 * Perform a transaction
	 */
	public static final int PERFORM_TRANSACTION = 44;

	/**
	 * Servers will send this command to a user in response to the fact that the database
	 * has been updated on the server so the user's image is stale.
	 */
	public static final int DATA_CACHE_STALE = 45;
	
	/**
	 * Return Value Provided 
	 */
	public static final int RETURN_VALUE_PROVIDED = 50;
	
	/**
	 * Replace or create this object
	 */
	public static final int REPLACE_CREATE = 100;

	/**
	 * Outcome OK.
	 */
	public static final int OUTCOME_OK = 200;

	/**
	 * Outcome Failed
	 */
	public static final int OUTCOME_FAIL = 201;

	/**
	 * Outcome Failed, Resource Unavailable
	 */
	public static final int OUTCOME_FAIL_RESOURCE_UNAVAILABLE = 202;

	/**
	 * Outcome Failed, Timeout
	 */
	public static final int OUTCOME_FAIL_TIMEOUT = 203;

	/**
	 * Transaction Semantic: Begin
	 */
	public static final int BEGIN = 300;

	/**
	 * Transaction Semantic: Commit
	 */
	public static final int COMMIT = 301;

	/**
	 * Transaction Semantic: Abort
	 */
	public static final int ABORT = 302;
	
	/**
	 * Transaction Semantic Operation: select objects
	 */
	public static final int SELECT = 400;

	/**
	 * Transaction Semantic Operation: insert
	 */
	public static final int INSERT = 401;

	/**
	 * Transaction Semantic Operation: update
	 */
	public static final int UPDATE = 402;

	/**
	 * Transaction Semantic Operation: delete
	 */
	public static final int DELETE = 403;

	/**
	 * Transaction Semantic Operation: create
	 */
	public static final int CREATE = 404;

	/**
	 * Transaction Semantic Operation: drop
	 */
	public static final int DROP = 405;

	/**
	 * Transaction Semantic Operation: select count() of objects.
	 */
	public static final int SELECT_COUNT = 406;

	/**
	 * Transaction Semantic Operation: select count() of objects where ...
	 */
	public static final int SELECT_COUNT_FILTER = 407;

	/**
	 * Transaction Semantic Operation: select objects where ...
	 */
	public static final int SELECT_FILTER = 408;

	/**
	 * Transaction Semantic Operation: select objects where creator ==
	 */
	public static final int SELECT_CREATOR = 409;

	/**
	 * Transaction Semantic Operation: select objects where dynamicState == true|false
	 */
	public static final int SELECT_DYNAMIC = 410;

	/**
	 * Transaction Semantic Operation: select objects where authorizedBy == 
	 */
	public static final int SELECT_AUTHORIZER = 411;

	/**
	 * Transaction Semantic Operation: select objects where state == 
	 */
	public static final int SELECT_STATE = 412;

	/**
	 * Transaction Semantic Operation: select objects where state.readWriteable == true|false
	 */
	public static final int SELECT_STATE_RW = 413;

	/**
	 * Transaction Semantic Operation: select persistent database, dynamic content.
	 */
	public static final int SELECT_DYNAMIC_DATABASE = 420;

	/**
	 * Transaction Semantic Operation: select persistent database, dynamic content.
	 */
	public static final int SELECT_REFERENCE_DATABASE = 421;

	/**
	 * Lease semantic: renew
	 */
	public static final int RENEW = 500;

	/**
	 * Lease semantic: cancel
	 */
	public static final int CANCEL = 501;

	/**
	 * Application command: Policy
	 */
	public static final int REQUEST_POLICY = 600;

	/**
	 * Application command: Language related objects
	 */
	public static final int REQUEST_LANGUAGE = 601;

	/**
	 * Application command: Properties
	 */
	public static final int REQUEST_PROPERTIES = 602;

	/**
	 * Application command: Authentication
	 */
	public static final int REQUEST_AUTHENTICATION = 603;

	/**
	 * Application command: New sequence number
	 */
	public static final int REQUEST_SEQUENCE = 604;

	/**
	 * Application command: An arbitrary resource
	 */
	public static final int REQUEST_RESOURCE = 605;

	/**
	 * Application command: Request the assignment of an object
	 */
	public static final int REQUEST_ASSIGNMENT = 606;

	/**
	 * Server initiated command: Who are you?
	 */
	public static final int WHO_ARE_YOU = 700;

	/**
	 * Server initiated command: Application Model Update
	 */
	public static final int APP_MODEL_UPDATE = 701;

	/**
	 * Server initiated command: Software Update
	 */
	public static final int SOFTWARE_UPDATE = 702;

	/**
	 * Perform a loop back with a possible delay.
	 */
	public static final int LOOP_BACK = 703;

	/**
	 * Identification response
	 */
	public static final int IDENTIFICATION_ENCLOSURE = 710;

}

