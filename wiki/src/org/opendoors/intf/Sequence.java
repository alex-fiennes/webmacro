/*
 * @(#)Sequence.java
 * 
 * Copyright (c) 1999 Open Doors Software, Inc. All Rights Reserved.
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
 * CopyrightVersion 2.0
 * 
 */
package org.opendoors.intf;

/**
 * Sequence servers implement
 * this interface so as to 
 * provide a definition for
 * public use.
 * @author Lane Sharman
 * @version 2.0
 */
public interface Sequence  {

	/**
	 * Gets the next sequence.
	 * <p>
	 * Returns an exception for any error.
	 * @return The next sequence
	 * @throws If the sequence could not be refreshed or it could not be saved.
	 */
	public long nextSequence() throws Exception;

}

