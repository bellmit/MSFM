package com.cboe.interfaces.cfix;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Mar 21, 2011
 * Time: 3:02:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CfixCasExternalLoginHome
{
    public final static String HOME_NAME = "CfixCasExternalLoginHome";

    public CfixCasExternalLogin find();

    public CfixCasExternalLogin create();
}
