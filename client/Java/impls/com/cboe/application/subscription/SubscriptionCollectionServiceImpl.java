package com.cboe.application.subscription;

import com.cboe.application.subscription.firm.FirmSubscriptionCollectionImpl;
import com.cboe.application.subscription.productType.ProductTypeSubscriptionCollectionImpl;
import com.cboe.application.subscription.sessionClass.SessionClassSubscriptionCollectionImpl;
import com.cboe.application.subscription.user.UserSubscriptionCollectionImpl;
import com.cboe.application.subscription.tradingFirm.TradingFirmSubscriptionCollectionImpl;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.application.subscription.*;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionCollectionServiceImpl implements SubscriptionCollectionService
{
    protected final Map userSubscriptionCollections;
    protected final Map sessionClassSubscriptionCollections;
    protected final Map firmSubscriptionCollections;
    protected final Map productTypeSubscriptionCollections;

    // Subscriptions for trading firm user (Drop Copy Enhancement)
    protected final Map tradingFirmSubscriptionCollections;

    protected boolean defaultSubscriptionOn;

    public SubscriptionCollectionServiceImpl()
    {
        userSubscriptionCollections = new HashMap();
        sessionClassSubscriptionCollections = new HashMap();
        firmSubscriptionCollections = new HashMap();
        productTypeSubscriptionCollections = new HashMap();
        tradingFirmSubscriptionCollections = new HashMap();
        defaultSubscriptionOn = true;
    }

    public void setDefaultSubscriptonFlag(boolean defaultSubscriptionOn)
    {
        this.defaultSubscriptionOn = defaultSubscriptionOn;
    }

    public UserSubscriptionCollection getUserSubscriptionCollection(SessionProfileUserStructV2 userStruct)
    {
        synchronized(userSubscriptionCollections)
        {
            UserSubscriptionCollection userSubscriptionCollection = (UserSubscriptionCollection)userSubscriptionCollections.get(userStruct);
            if(userSubscriptionCollection == null)
            {
                userSubscriptionCollection = new UserSubscriptionCollectionImpl(userStruct, defaultSubscriptionOn);
                userSubscriptionCollections.put(userStruct, userSubscriptionCollection);
            }
            return userSubscriptionCollection;
        }
    }
    public SessionClassSubscriptionCollection getSessionClassSubscriptionCollection(SessionKeyWrapper sessionClass)
    {
        synchronized(sessionClassSubscriptionCollections)
        {
            SessionClassSubscriptionCollection sessionClassSubscriptionCollection = (SessionClassSubscriptionCollection)sessionClassSubscriptionCollections.get(sessionClass);
            if(sessionClassSubscriptionCollection == null)
            {
                sessionClassSubscriptionCollection = new SessionClassSubscriptionCollectionImpl(sessionClass, defaultSubscriptionOn);
                sessionClassSubscriptionCollections.put(sessionClass,sessionClassSubscriptionCollection);
            }
            return sessionClassSubscriptionCollection;
        }
    }
    public FirmSubscriptionCollection getFirmSubscriptionCollection(ExchangeFirmStructWrapper exchangeFirm)
    {
        synchronized(firmSubscriptionCollections)
        {
            FirmSubscriptionCollection firmSubscriptionCollection = (FirmSubscriptionCollection)firmSubscriptionCollections.get(exchangeFirm);
            if(firmSubscriptionCollection == null)
            {
                firmSubscriptionCollection = new FirmSubscriptionCollectionImpl(exchangeFirm, defaultSubscriptionOn);
                firmSubscriptionCollections.put(exchangeFirm,firmSubscriptionCollection);
            }
            return firmSubscriptionCollection;
        }
    }

    public FirmSubscriptionCollection getTradingFirmSubscriptionCollection(TradingFirmGroupWrapper tradingFirm)
    {
        synchronized(firmSubscriptionCollections)
        {
            FirmSubscriptionCollection tradingFirmSubscriptionCollection =
                    (FirmSubscriptionCollection)tradingFirmSubscriptionCollections.get(tradingFirm.getTradingFirmId());
            if(tradingFirmSubscriptionCollection == null)
            {
                tradingFirmSubscriptionCollection = new TradingFirmSubscriptionCollectionImpl(tradingFirm, defaultSubscriptionOn);
                tradingFirmSubscriptionCollections.put(tradingFirm, tradingFirmSubscriptionCollection);
            }
            return tradingFirmSubscriptionCollection;
        }
    }

    public ProductTypeSubscriptionCollection getProductTypeSubscriptionCollection(short productType)
    {
        synchronized(productTypeSubscriptionCollections)
        {
            ProductTypeSubscriptionCollection productTypeSubscriptionCollection = (ProductTypeSubscriptionCollection)productTypeSubscriptionCollections.get(Short.valueOf(productType));
            if(productTypeSubscriptionCollection == null)
            {
                productTypeSubscriptionCollection = new ProductTypeSubscriptionCollectionImpl(productType, defaultSubscriptionOn);
                productTypeSubscriptionCollections.put(Short.valueOf(productType), productTypeSubscriptionCollection);
            }
            return productTypeSubscriptionCollection;
        }
    }
}
