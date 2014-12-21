package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.exceptions.*;
import com.cboe.interfaces.application.FloorTradeMaintenanceService;

/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Jun 9, 2009
 * Time: 10:54:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FloorTradeConsumer {

    public CboeIdStruct acceptFloorTrade(
            FloorTradeEntryStruct floorTrade ) throws 
        SystemException,
        CommunicationException, 
        AuthorizationException, 
        DataValidationException, 
        NotAcceptedException, 
        TransactionFailedException;

    public void deleteFloorTrade ( 
            String sessionName, 
            int productKey, 
            CboeIdStruct tradeId,
            ExchangeAcronymStruct user,
            ExchangeFirmStruct firm,
            String reason ) throws
        SystemException,
        CommunicationException, 
        AuthorizationException, 
        DataValidationException,
        TransactionFailedException, NotAcceptedException, NotFoundException;

    public void subscribeForFloorTradeReportsByClass(
            QuoteStatusV2Consumer quoteStatusV2Consumer,
            int classKey,
            boolean gmdCallBack) throws
            SystemException,
                    CommunicationException,
                    AuthorizationException,
                    DataValidationException;
}
