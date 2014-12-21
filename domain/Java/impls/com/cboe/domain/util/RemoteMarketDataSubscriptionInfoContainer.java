package com.cboe.domain.util;

/**
 * Generic container for remote MD CAS subscribe/unsubscribe for market data events.
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Mar 21, 2003
 * Time: 1:57:40 PM
 */

public class RemoteMarketDataSubscriptionInfoContainer
{
    private final String sessionName;
    private final int classKey;
    private final int productKey;
    private final String casOrigin;
    private final String userId;
    private final String userSessionIOR;
    private final Object cmiConsumer;
    private final short actionOnQueue;
    private int hashCode;


    /**
     *
     * @param sessionName
     * @param classKey
     * @param productKey (ignore if this is a "by class" event)
     * @param casOrigin
     * @param userId
     * @param userSessionIOR
     * @param cmiConsumer
     * @param actionOnQueue (ignore if this is an "unsubscribe" event)
     */
    public RemoteMarketDataSubscriptionInfoContainer(String sessionName, int classKey, int productKey, String casOrigin,
                                                     String userId, String userSessionIOR, Object cmiConsumer, short actionOnQueue)
    {
        this.sessionName = sessionName;
        this.classKey = classKey;
        this.productKey = productKey;
        this.casOrigin = casOrigin;
        this.userId = userId;
        this.userSessionIOR = userSessionIOR;
        this.cmiConsumer = cmiConsumer;
        this.actionOnQueue = actionOnQueue;
        hashCode = sessionName.hashCode() +
                classKey +
                productKey +
                casOrigin.hashCode() +
                userId.hashCode() +
                userSessionIOR.hashCode() +
                cmiConsumer.hashCode();
    }


    public String getSessionName()
    {
        return sessionName;
    }

    public int getClassKey()
    {
        return classKey;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public String getCasOrigin()
    {
        return casOrigin;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserSessionIOR()
    {
        return userSessionIOR;
    }

    public Object getCmiConsumer()
    {
        return cmiConsumer;
    }

    public short getActionOnQueue()
    {
        return actionOnQueue;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof RemoteMarketDataSubscriptionInfoContainer))
        {
// Please note that actionQueue is not in the equals comparison to meet the special needs of keeping track on subscription and unsubscripiont.
// Action will not be provided in the unsubscriptions.
            return (this.productKey == ((RemoteMarketDataSubscriptionInfoContainer)obj).getProductKey()
                    && this.classKey == ((RemoteMarketDataSubscriptionInfoContainer)obj).getClassKey()
                    && this.sessionName.equals(((RemoteMarketDataSubscriptionInfoContainer)obj).getSessionName())
                    && this.casOrigin.equals(((RemoteMarketDataSubscriptionInfoContainer)obj).getCasOrigin())
                    && this.userId.equals(((RemoteMarketDataSubscriptionInfoContainer)obj).getUserId())
                    && this.userSessionIOR.equals(((RemoteMarketDataSubscriptionInfoContainer)obj).getUserSessionIOR())
                    && this.cmiConsumer.equals(((RemoteMarketDataSubscriptionInfoContainer)obj).getCmiConsumer())
            );
        }
        return false;
    }
}
