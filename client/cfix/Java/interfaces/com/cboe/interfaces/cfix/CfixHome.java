/**
 * @author Jing Chen
 */
package com.cboe.interfaces.cfix;

import com.cboe.exceptions.SystemException;

public interface CfixHome
{
	public final static String HOME_NAME = "CfixHome";
    public void initializeCfixConnection() throws SystemException;
    public void stopCfixConnection();
}
