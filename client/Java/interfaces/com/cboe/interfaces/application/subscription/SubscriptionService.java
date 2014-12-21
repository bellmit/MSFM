package com.cboe.interfaces.application.subscription;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import java.util.List;

/**
 * @author Jing Chen
 */
public interface SubscriptionService
{
    public void addUserInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addFirmInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addTradingFirmInterest(Object source, List<String> users)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addProductTypeInterest(Object source, short productType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addSessionClassInterest(Object source, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeFirmInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeUserInterest(Object source)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeProductTypeInterest(Object souce, short productType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeSessionClassInterest(Object source, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addCurrentMarketClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addNBBOClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addRecapClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addTickerClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addOpeningPriceClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addAuctionClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addRFQClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addBookDepthClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeCurrentMarketClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeNBBOClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeTickerClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeRecapClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeRFQClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeOpeningPriceClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeBookDepthClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeAuctionClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addQuoteLockedNotificationUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeQuoteLockedNotificationUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addAuctionUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeAuctionUserInterest(Object listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addCurrentMarketProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addNBBOProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addRecapProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addTickerProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addOpeningPriceProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addBookDepthProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeOpeningPriceProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeBookDepthProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeCurrentMarketProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeNBBOProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeTickerProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeRecapProductInterest(Object listener, String sessionName, int classKey, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void addLargeTradeLastSaleClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeLargeTradeLastSaleClassInterest(Object listener, String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
