package com.cboe.infrastructureServices.systemsManagementService;

/**
 * This class is comparable to a CORBA struct and was, in fact, created
 * to ease transition to a CORBA interface, should such a thing be necessary.
 *
 * @author Craig Murphy
 * @creation date 12/09/1998
 */
public class Property {
	public String name;
	public String value;
/**
 * Property constructor comment.
 */
public Property() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param propertyName java.lang.String
 * @param propertyValue java.lang.String
 */
public Property( String propertyName, String propertyValue ) {
	name = propertyName;
	value = propertyValue;
}
}