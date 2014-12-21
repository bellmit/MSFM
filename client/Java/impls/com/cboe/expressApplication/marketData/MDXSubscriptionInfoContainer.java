//
// -----------------------------------------------------------------------------------
// Source file: MDXSubscriptionInfoContainer.java
//
// PACKAGE: com.cboe.expressApplication.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.ORB.DelegateImpl;

/**
 * Overrides hashCode() and equals() to compare the cmiConsumers' IOR Strings,
 * rather than using their hashCodes directly.  Two ObjectImpls will be
 * considered equal if their IORs are equal, even though the corba objects'
 * hashcodes may not be equal.
 *
 * cmiConsumer must be a com.cboe.ORBInfra.ORB.ObjectImpl.
 */
public class MDXSubscriptionInfoContainer extends RemoteMarketDataSubscriptionInfoContainer
{
    private String cmiConsumerIORStr;
    private boolean disseminateExternalMarketData;
    private int hashCode;

    public MDXSubscriptionInfoContainer(String sessionName, int classKey, int productKey, String casOrigin,
                                        String userId, String userSessionIOR, Object cmiConsumer,
                                        short actionOnQueue, boolean disseminateExternalMarketData)
    {
        super(sessionName, classKey, productKey, casOrigin, userId, userSessionIOR, cmiConsumer, actionOnQueue);
        IORImpl iorImpl = ((DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl)cmiConsumer)._get_delegate()).getIOR();
        cmiConsumerIORStr = iorImpl.getStringDigest();
        this.disseminateExternalMarketData = disseminateExternalMarketData;
        this.hashCode = getSessionName().hashCode() +
                getClassKey() +
                getProductKey() +
                getCasOrigin().hashCode() +
                getUserId().hashCode() +
                getUserSessionIOR().hashCode() +
                getCMIConsumerIORStr().hashCode();
    }

    public String getCMIConsumerIORStr()
    {
        return cmiConsumerIORStr;
    }

    public boolean isExternalMarketDataEnabled()
    {
        return disseminateExternalMarketData;
    }

    public boolean equals(Object obj)
    {
        if((obj != null) && (obj instanceof MDXSubscriptionInfoContainer))
        {
            MDXSubscriptionInfoContainer castedObj = (MDXSubscriptionInfoContainer)obj;
            return (getProductKey() == castedObj.getProductKey()
                    && getClassKey() == castedObj.getClassKey()
                    && getSessionName().equals(castedObj.getSessionName())
                    && getCasOrigin().equals(castedObj.getCasOrigin())
                    && getUserId().equals(castedObj.getUserId())
                    && getUserSessionIOR().equals(castedObj.getUserSessionIOR())
                    && getCMIConsumerIORStr().equals(castedObj.getCMIConsumerIORStr())
            );
        }
        return false;
    }

    public int hashCode()
    {
        return hashCode;
    }
}
