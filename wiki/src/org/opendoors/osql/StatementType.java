/*
* Copyright Open Doors Software and Acctiva, 2000.
*
* Software is provided according to the ___ license.
* Open Doors Software and Acctiva provide this
* software on an as-is basis and make no representation as
* to fitness for a specific purpose.
*
* Direct all questions and comments to support@opendoors.com
*/

package org.opendoors.osql;

/**
 * StatementType defines the kinds of statements OSQL handles intrinsically. 
 *  
 *  <p> They are: 
 *  
 *  <ul>
 *  <li>DQL Data query statements/language</li> 
 *  <li>DML Data manipulation language</li> 
 *  <li>DDL Data definition language</li>
 *   </ul> 
 *  
 *  <p>
 *  These types are not ints, they are proper members of this class, albeit final. 
 *  
 *  <p>
 *   Subclass this file to add other types which are type safe in the sense they are not ints but singleton
 *   static instances of this class.
 * 
 * <p>
 * An instance of this class is immutable by nature.
 * @author Lane Sharman
 * @version 3.0
 * @since 18-July-2000
 */
public class StatementType {
    /** The id for DDL. */
    private final static int DDLValue=1;

    /** The id for DML. */
    private final static int DMLValue=2;

    /** The id for DQL. */
    private final static int DQLValue=3;

    /** The type representing DDL statements. */
    public final static StatementType DDL = new StatementType(StatementType.DDLValue);

    /** The type representing DML statements.*/
    public final static StatementType DML = new StatementType(StatementType.DMLValue);

    /** The type representing DML statements.*/
    public final static StatementType DQL = new StatementType(StatementType.DQLValue);

    /** The actual value of the instance. */
    private int value;

    /**
     * Subclasses may instantiate this as it is done here.
     * @param type The type of statement.
     */
    protected StatementType(int type) {
        value = type;
    }

    /**
     * Gets the integer value of this type. Should only be used in rare circumstances. 
     */
    public int getValue() {
        return value;
    }
}
