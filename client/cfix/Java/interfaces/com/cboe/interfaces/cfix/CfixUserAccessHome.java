/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 19, 2003
 * Time: 12:02:25 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.cfix;

public interface CfixUserAccessHome {

	public final static String HOME_NAME = "CfixUserAccessHome";

    public CfixUserAccess find();

    public CfixUserAccess create();
}
