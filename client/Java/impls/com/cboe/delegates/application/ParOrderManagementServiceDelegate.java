package com.cboe.delegates.application;

import com.cboe.interfaces.application.ParOrderManagementService;

public class ParOrderManagementServiceDelegate extends com.cboe.idl.par.POA_ParOrderManagementService_tie
{
    public ParOrderManagementServiceDelegate(ParOrderManagementService delegate)
    {
        super(delegate);
    }
}
