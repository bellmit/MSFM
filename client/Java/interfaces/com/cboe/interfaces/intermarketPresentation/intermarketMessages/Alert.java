package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.idl.cmiIntermarketMessages.AlertStruct;

import java.beans.PropertyChangeListener;

/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.interfaces.internalPresentation.alert
 * User: torresl
 * Date: Jan 9, 2003 9:09:47 AM
 */
public interface Alert extends MutableBusinessModel, Cloneable
{
    public static final String  RESOLUTION_PROPERTY = "RESOLUTION_PROPERTY";
    public static final String  COMMENTS_PROPERTY = "COMMENTS_PROPERTY";

    AlertHeader             getAlertHeader();

    CBOEId                  getAlertId();
    DateTime                getAlertCreationTime();
    short                   getAlertType();
    String                  getSessionName();

    String                  getResolution();
    String                  getComments();
    OrderId                 getOrderId();
    String                  getNbboAgentId();
    String                  getUpdatedById();
    CBOEId                  getTradeId();
    ProductKeys             getProductKeys();
    ExchangeMarket[]        getExchangeMarket();
    boolean                 getCboeMarketableOrder();
    String                  getExtensions();
    String                  getExtensionField(String fieldName);

    void                    setResolution(String resolution);
    void                    setComments(String comments);
    public Object           clone();

    AlertStruct             getStruct();
}
