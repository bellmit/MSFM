package com.cboe.interfaces.remoteApplication;

/**
 * @author Jing Chen
 */
public interface RemoteCASMarketDataService {

    public void subscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeRecapForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeRecapForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeRecapForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeRecapForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeRecapForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeRecapForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForClassV3(String sessionName, int classKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForProductV3(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForProductV3(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeCurrentMarketForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeCurrentMarketForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeNBBOForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeNBBOForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeNBBOForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeNBBOForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeNBBOForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeTickerForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeTickerForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeTickerForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeTickerForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeExpectedOpeningPriceForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeExpectedOpeningPriceForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeExpectedOpeningPriceForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeExpectedOpeningPriceForClass(String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumer, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumer, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer orderBookConsumer, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthUpdateForClassV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthUpdateForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer orderBookConsumer, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthUpdateForProductV2(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeBookDepthUpdateForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void unsubscribeBookDepthUpdateForProduct(String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer orderBookConsumer)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;

    public void subscribeLargeTradeLastSaleForClass(String sessionName, int classKey, com.cboe.idl.consumers.TickerConsumer clientListener, short actionOnQueue)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
    
    public void unsubscribeLargeTradeLastSaleForClass(String sessionName, int classKey, com.cboe.idl.consumers.TickerConsumer clientListener)
      throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
  
}
