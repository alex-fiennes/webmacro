/*
 * @(#)BaseCommand.java	1.0 98/10/01
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

import java.util.*;
import java.io.Serializable;

/**
 * The command is a general purpose semantic for lcis peer-to-peer communication.
 * <p>
 * Subclasses may add functionality as required such as filtering, replying, loopback.
 */
public class BaseCommand implements CommandConstants, Serializable {

	/**
	 * The command identifier.
	 */
	public long identifier = NOT_SET;

	/**
	 * Transactions span many commands. This ID propagates for the duration of the protocol exchange
	 * to complete a transaction.
	 */
	public long transactionID = NOT_SET;

	/**
	 * The command.
	 */
	public int command = NOT_SET;

	/**
	 * The command parameters.
	 */
	public Hashtable parameters = null;

	/**
	 * The resulting data, possibly repeating or enumerated.
	 */
	public Object data = null;

	/**
	 * The schema.
	 */
	public Object schema = null;

	/**
	 * This command may be a reply to a command.
	 */
	public long replyToIdentifier = NOT_SET;

	/**
	 * The outcome of the operation.
	 */
	public int outcome = NOT_SET;

	/**
	 * An integer count used for providing a count of things.
	 */
	public int count = NOT_SET;

	/**
	 * User's sequence number.
	 */
	public long userSeqNbr = NOT_SET;

	public void addFieldData(Object key, Object param) {
		if (parameters == null)
			parameters = new Hashtable();
		parameters.put(key, param);
	}

	public Object getObject(Object key) {
		if (parameters == null)
			return null;
		else
			return (Object) parameters.get(key);
	}

}

