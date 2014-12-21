package com.cboe.cfix.cas.product;

import com.cboe.domain.startup.*;
import com.cboe.interfaces.cfix.*;

/**
 * @author Jing Chen
 */
public class CfixProductConfigurationServiceHomeImpl extends ClientBOHome implements CfixProductConfigurationServiceHome
{
    protected CfixProductConfigurationService cfixProductConfigurationService;

    /**
    * Create an instance of home.
    */
    public CfixProductConfigurationServiceHomeImpl()
    {
        super();
    }

    public CfixProductConfigurationService create()
    {
        return cfixProductConfigurationService;
    }

    public CfixProductConfigurationService find()
    {
        return cfixProductConfigurationService;
    }

    public void clientInitialize() throws Exception
    {
        if (cfixProductConfigurationService == null)
        {
            cfixProductConfigurationService = new CfixProductConfigurationServiceImpl();
        }
    }
}
