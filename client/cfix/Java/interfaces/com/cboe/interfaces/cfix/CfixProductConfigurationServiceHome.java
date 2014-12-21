package com.cboe.interfaces.cfix;

/**
 * @author Jing Chen
 */
public interface CfixProductConfigurationServiceHome
{
    public final static String HOME_NAME = "CfixProductConfigurationServiceHome";
    public CfixProductConfigurationService create();
    public CfixProductConfigurationService find();
}
