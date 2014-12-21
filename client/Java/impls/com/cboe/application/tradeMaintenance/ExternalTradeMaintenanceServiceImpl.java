package com.cboe.application.tradeMaintenance;
import org.omg.CORBA.UserException;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiTrade.ExternalBustTradeStruct;
import com.cboe.idl.cmiTrade.ExternalTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalTradeReportStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManagerTMS;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceService;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceServiceHome;
import com.cboe.domain.util.StructBuilder;

public class ExternalTradeMaintenanceServiceImpl 
		extends BObject 
		implements com.cboe.interfaces.application.ExternalTradeMaintenanceService {
	protected SessionManagerTMS session;
	protected TradeMaintenanceService tms;
	protected UserEnablement userEnablement;
	protected ProductQueryService    pqs;
	protected String thisUserId;
	protected String thisExchange;
	protected String thisAcronym;
	
	public ExternalTradeMaintenanceServiceImpl(SessionManagerTMS theSession) {
		session = theSession;
		tms = findTradeMaintenanceService();
		try
        {
            thisUserId = session.getUserId();
            thisExchange = session.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            thisAcronym = session.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
        }
        catch(UserException e)
        {
            Log.exception(this, "fatal error in getting userId from session:"+session, e);
        }
	}


    /**
     * Retrieves the server side implementation on Trade Maintenance Service
     *
     */
   private TradeMaintenanceService findTradeMaintenanceService()
   {
      if ( tms == null)
      {
    	   try
    		{
			   TradeMaintenanceServiceHome
			      home = (TradeMaintenanceServiceHome)HomeFactory.getInstance().findHome(TradeMaintenanceServiceHome.ADMIN_HOME_NAME);

			   tms = (TradeMaintenanceService)home.find();
    		}
    		catch (CBOELoggableException e)
    		{
    			throw new NullPointerException("Could not find TradeMaintenanceServiceHome");
    		}
      }
      return tms;
    }
	
	public ExternalTradeReportStruct acceptExternalTrade(ExternalTradeEntryStruct efpOrBlockTrade) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException {
        String smgr = session.toString();
        String trade = getTradeEntryString(efpOrBlockTrade);
        StringBuilder calling = new StringBuilder(smgr.length()+trade.length()+41);
        calling.append("calling acceptExternalTrade for session: ").append(smgr).append(trade);
        Log.information(this, calling.toString());
		// verify user operation enablement for the session
		getUserEnablementService().verifyUserEnablementForSession(efpOrBlockTrade.sessionName,  OperationTypes.EFPBLOCKTRADE);
		return tms.acceptExternalTrade(efpOrBlockTrade);
	}

	public void acceptExternalTradeBust(String tradingSessionName, int productKey, CboeIdStruct tradeId, ExternalBustTradeStruct[] bustedTrades, String reason) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException {
		String smgr = session.toString();
        String tidString = StructBuilder.toString(tradeId);
        StringBuilder calling = new StringBuilder(smgr.length()+tradingSessionName.length()+tidString.length()+reason.length()+105);
        calling.append("calling acceptExternalTradeBust for session:").append(smgr)
               .append(" session name: ").append(tradingSessionName)
               .append(" productKey: ").append(productKey)
               .append(" tradeId: ").append(tidString)
               .append(" reason: ").append(reason);
        Log.information(this, calling.toString());
		//	verify user operation enablement for the session
		getUserEnablementService().verifyUserEnablementForSession(tradingSessionName,  OperationTypes.EFPBLOCKTRADE);
		tms.acceptExternalTradeBust(tradingSessionName, productKey, tradeId, bustedTrades, reason);
	}
	
	protected UserEnablement getUserEnablementService()
    {
        if(userEnablement == null)
        {
        	userEnablement = ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym);
        }
        return userEnablement;
    }
	
	private String getTradeEntryString(ExternalTradeEntryStruct trade) {
		StringBuilder strBuf = new StringBuilder(130);
		strBuf.append(" session name: ").append(trade.sessionName)
		      .append(" productKey: ").append(trade.productKey)
		      .append(" price:").append(StructBuilder.toString(trade.price))
		      .append(" qty: ").append(trade.quantity)
		      .append(" tradeType: ").append(trade.externalTradeType)
		      .append(" tradeSource: ").append(trade.theTradeSource)
		      .append(" handlingInstruction: ").append(trade.handlingInstruction);
		return strBuf.toString();
	}
	
}
