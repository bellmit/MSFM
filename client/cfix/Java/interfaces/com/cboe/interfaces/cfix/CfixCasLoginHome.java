package com.cboe.interfaces.cfix;

/**
 * Created by IntelliJ IDEA.
 * User: lip
 * Date: May 5, 2010
 * Time: 4:29:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CfixCasLoginHome {
	public final static String HOME_NAME = "CfixCasLoginHome";

    public CfixCasLogin find();

    public CfixCasLogin create();
}
