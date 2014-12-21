package com.cboe.cfix.util;

/**
 * OverlayPolicyNeverOverlayMarketDataNbboStructList.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * List of mdReqIDs/NbboStructs, with no overlay
 *
 */

import com.cboe.client.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public class OverlayPolicyNeverOverlayMarketDataNbboStructList extends OverlayPolicyNeverOverlayMarketDataStructList implements OverlayPolicyMarketDataNbboStructListIF
{
    protected NBBOStruct[] structs = CollectionHelper.EMPTY_NBBOStruct_ARRAY;

    public OverlayPolicyNeverOverlayMarketDataNbboStructList(String mdReqID)
    {
        super(mdReqID);
    }

    private OverlayPolicyNeverOverlayMarketDataNbboStructList(String mdReqID, NBBOStruct[] structs, int offset, int length)
    {
        super(mdReqID);

        if (structs.length != length)
        {
            this.structs = CollectionHelper.arrayclone(structs, offset, length, length);
        }
        else
        {
            this.structs = structs;
        }
    }

    private OverlayPolicyNeverOverlayMarketDataNbboStructList(String mdReqID, NBBOStruct struct)
    {
        super(mdReqID);

        this.structs = CollectionHelper.arrayclone(struct);
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, NBBOStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataNbbo(new OverlayPolicyNeverOverlayMarketDataNbboStructList(mdReqID, structs, offset, length));
    }

    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, NBBOStruct struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        cfixMarketDataConsumer.acceptMarketDataNbbo(new OverlayPolicyNeverOverlayMarketDataNbboStructList(mdReqID, struct));
    }

    public void remove(OverlayPolicyMarketDataHolderIF cfixOverlayPolicyMarketDataHolder)
    {
        super.remove(cfixOverlayPolicyMarketDataHolder);

        cfixOverlayPolicyMarketDataHolder.setMarketDataType(CfixMarketDataDispatcherIF.MarketDataType_Nbbo);

        cfixOverlayPolicyMarketDataHolder.setSize(structs.length);

        if (cfixOverlayPolicyMarketDataHolder.getNbboStructs().length < structs.length)
        {
            cfixOverlayPolicyMarketDataHolder.setNbboStructs(new NBBOStruct[structs.length]);
        }

        System.arraycopy(this.structs, 0, cfixOverlayPolicyMarketDataHolder.getNbboStructs(), 0, structs.length);
    }

    public boolean equals(Object other)
    {
        return this == other &&
               other instanceof OverlayPolicyNeverOverlayMarketDataNbboStructList &&
               mdReqID.compareTo(((OverlayPolicyNeverOverlayMarketDataNbboStructList) other).mdReqID) == 0;
    }

    public int size()
    {
        return structs.length;
    }
}
