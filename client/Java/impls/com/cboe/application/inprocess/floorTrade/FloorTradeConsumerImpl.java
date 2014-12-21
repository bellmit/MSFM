package com.cboe.application.inprocess.floorTrade;

import com.cboe.interfaces.application.inprocess.FloorTradeConsumer;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteStatusV2Consumer;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer;
import com.cboe.exceptions.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.supplier.QuoteStatusCollectorSupplier;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.quote.QuoteCache;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/**
 * Created by IntelliJ IDEA.
 * User: mageem
 * Date: Jun 25, 2009
 * Time: 11:51:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class FloorTradeConsumerImpl extends BObject implements FloorTradeConsumer {
    protected InProcessSessionManager inProcessSessionManager;
    protected QuoteStatusCollectorSupplier quoteStatusCollectorSupplier = null;
    protected UserQuoteService userQuoteService;
    protected QuoteCache quoteCache;

    public CboeIdStruct acceptFloorTrade(FloorTradeEntryStruct floorTrade) throws
            SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (Log.isDebugOn()) {
            Log.debug(this, " inprocess/floorTrade/FloorTradeConsumerImpl-acceptFloorTrade");
        }
        CboeIdStruct returnTradeId = null;
        returnTradeId = ServicesHelper.getFloorTradeMaintenanceServiceHome().create(inProcessSessionManager).acceptFloorTrade(floorTrade);
        return returnTradeId;
    }

    public void deleteFloorTrade(String sessionName, int productKey, CboeIdStruct tradeId,
                                 ExchangeAcronymStruct user, ExchangeFirmStruct firm, String reason) throws
            SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException {
        if (Log.isDebugOn()) {
            Log.debug(this, " inprocess/floorTrade/FloorTradeConsumerImpl-deleteFloorTrade");
        }
        ServicesHelper.getFloorTradeMaintenanceServiceHome().create(inProcessSessionManager).deleteFloorTrade(
                sessionName, productKey, tradeId, user, firm, reason);
    }

    public void subscribeForFloorTradeReportsByClass(QuoteStatusV2Consumer quoteStatusV2Consumer, int classKey, boolean gmdCallBack) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        
        if (Log.isDebugOn()) {
            Log.debug(this, " inprocess/floorTrade/FloorTradeConsumerImpl-subscribeForFloorTradeReportsByClass");
        }
        String userId = inProcessSessionManager.getUserId();
        quoteCache = QuoteCacheFactory.find(userId);
        quoteStatusCollectorSupplier = quoteCache.getQuoteStatusCollectorSupplier();
        quoteStatusCollectorSupplier.setDynamicChannels(true);
        StringBuilder calling = new StringBuilder(userId.length()+85);
        calling.append("calling subscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey)
               .append(" gmd:").append(gmdCallBack);
        Log.information(this, calling.toString());

        ChannelListener proxyListener = 
            InProcessServicesHelper.getQuoteStatusV2ConsumerProxy(quoteStatusV2Consumer, inProcessSessionManager);

        ChannelKey channelKey = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, userId);
        quoteStatusCollectorSupplier.addChannelListener(this, proxyListener, channelKey);
        userQuoteService = InProcessServicesHelper.getUserQuoteService(inProcessSessionManager);
        userQuoteService.publishUnAckedQuotes();

        calling.setLength(0);
        calling.append("returning subscribeForFloorTradeReportsByClass for UID:").append(userId)
               .append(" CK:").append(classKey)
               .append(" gmd:").append(gmdCallBack);
        Log.information(this, calling.toString());
    }

    public void setInProcessSessionManager(InProcessSessionManager theSession) {
        inProcessSessionManager = theSession;
    }
}
