/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 2, 2002
 * Time: 9:28:08 AM
 */
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.BookDepthDetailed;
import com.cboe.interfaces.presentation.product.SessionProduct;

public interface IntermarketQueryAPI
{
    CurrentIntermarketStruct getIntermarketByProductForSession( int productKey, String session )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException;

    CurrentIntermarketStruct[] getIntermarketByClassForSession(int classKey, String session)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException;

    AdminStruct[] getAdminMessage(String sessionName, int productKey, int adminMessageKey,
                                  String sourceExchange)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException;

    BookDepthDetailedStruct getDetailedOrderBook(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;

    BookDepthDetailed getDetailedOrderBook(SessionProduct sessionProduct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;
    BookDepthDetailedStruct showMarketableOrderBookAtPrice(String sessionName, int productKey,PriceStruct price)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;
    short getOrderBookStatus(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;
}
