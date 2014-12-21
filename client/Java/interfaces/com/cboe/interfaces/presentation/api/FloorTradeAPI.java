package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.util.event.EventChannelListener;

public interface FloorTradeAPI
{

	public CboeIdStruct acceptFloorTrade(FloorTradeEntryStruct aFloorTrade) 
				throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException;


	public void deleteFloorTrade( String sessionName, int productKey, CboeIdStruct tradeId, ExchangeAcronymStruct user, ExchangeFirmStruct firm, String reason) 
				throws	SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException; 


    public void subscribeForFloorTradeReportsByClass( int classKey, EventChannelListener clientListener) 
    			throws SystemException, CommunicationException, AuthorizationException, DataValidationException; 

    public void unsubscribeForFloorTradeReportsByClass( int classKey, EventChannelListener clientListener) 
    			throws SystemException, CommunicationException, AuthorizationException, DataValidationException; 

}