/*
 * @(#)AuthenticationCommand.java
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
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
 * Applications use or extend this command to obtain and confirm authentication.
 */
public class AuthenticationCommand extends BaseCommand {

	/**
	 * The login name.
	 */
	public String loginName = null;

	/**
	 * The password.
	 */
	public String password = null;

	/**
	 * The authentication result.
	 */
	public boolean authenticated = false;

}

