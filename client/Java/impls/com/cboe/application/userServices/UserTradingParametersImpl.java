// $Workfile$ com.cboe.application.userService.UserTradingParametersImpl.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Implementation             Michael Pyatetsky
*/

//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.userServices;

import java.util.Iterator;
import java.util.Map;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiQuote.*;
import com.cboe.exceptions.*;
import com.cboe.idl.user.SessionProfileUserStructV2;

import com.cboe.application.order.common.UserOrderServiceHomeImpl;
import com.cboe.application.quote.common.UserQuoteServiceHomeImpl;
import com.cboe.application.shared.*;
import com.cboe.application.shared.consumer.*;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.domain.logout.LogoutServiceFactory;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.domain.util.RateMonitorKeyContainer;



/**
 * UserTradingParameters implementation
 *
 * @author Mike Pyatetsky
 *
 * @version
 */
public class UserTradingParametersImpl extends BObject implements UserTradingParametersV5, UserSessionLogoutCollector
{
    ///////////////////////// instance variables //////////////////
    /** UserService reference */
    private UserTradingParameterService userTradingParameterService;

    /** SessionManager reference */
    private SessionManager sessionManager;

    private UserSessionLogoutProcessor logoutProcessor;

    /** User structure */
    private SessionProfileUserStructV2 userStruct;

    /**
     * UserTradingParametersImpl constructor.
     *
     */
    
    private RateMonitorHome rateMonitorHome;
    
    
    public UserTradingParametersImpl()
    {
        super();
        getUserTradingParameterService();
    }// end of constructor

    /**
     * This method sets session manager instance variable
     *
     * @param theSession SessionManager object reference
     */
    public void setSessionManager(SessionManager theSession)
    {
        sessionManager = theSession;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
        LogoutServiceFactory.find().addLogoutListener(theSession, this);
        try
        {
            userStruct = theSession.getValidSessionProfileUserV2();
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + sessionManager, e);
        }

    }// end of setSessionManager


    //////// Exported methods -Implementation of UserTradingParameters interface /////////////
    /*****************************************************************************************/

    /******************************************************************************************
     * The Following block of methods is Quote Risk Management (QRM) profile query methods
     */
    /**
     * Queries all User Quote Risk Management (QRM) Profile for this user including defaults and triggers
     *
     * @return UserQuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getAllQuoteRiskProfiles for " + sessionManager);
        }
        UserQuoteRiskManagementProfileStruct userQRMProfile = null;
        try
        {
            userQRMProfile = getUserTradingParameterService().getAllQuoteRiskProfiles(getSessionProfileUserStructV2().userInfo.userId);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }
        
        return userQRMProfile;
     }

     /**
     * Queries all Quote Risk Management (QRM) Profile for given classKey
     *
     * @return QuoteRiskManagementProfileStruct
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getQuoteRiskManagementProfileByClass for " + sessionManager);
        }
        QuoteRiskManagementProfileStruct quoteRiskManagementProfile = null;

        try
        {
            quoteRiskManagementProfile = getUserTradingParameterService().getQuoteRiskManagementProfileByClass(getSessionProfileUserStructV2().userInfo.userId, classKey);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }
        return quoteRiskManagementProfile;
     }

    /**
     * Sets QRM global on/off switch status for this user
     *
     * @param status Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void setQuoteRiskManagementEnabledStatus(boolean status)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException
     {
        String id = sessionManager.getUserId();
        StringBuilder calling = new StringBuilder(id.length()+65);
         calling.append("calling setQuoteRiskManagementEnabledStatus USER:").append(id)
                .append(" Status:").append(status);
        Log.information(this, calling.toString());
        try
        {
            getUserTradingParameterService().setQuoteRiskManagementEnabledStatus(getSessionProfileUserStructV2().userInfo.userId, status);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }
     }

    /**
     * Gets QRM global on/off switch status for this user
     *
     * @return  Boolean to enable/disable QRM globally
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
     public boolean getQuoteRiskManagementEnabledStatus()
        throws SystemException, CommunicationException, AuthorizationException
     {
        String smgr = sessionManager.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+50);
        calling.append("calling getQuoteRiskManagementEnabledStatus for ").append(smgr);
        if (Log.isDebugOn()) {
            Log.debug(this, calling.toString());
        }
        boolean userQRMStatus = false;
        try
        {
            userQRMStatus = getUserTradingParameterService().getQuoteRiskManagementEnabledStatus(getSessionProfileUserStructV2().userInfo.userId);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }
        return userQRMStatus;
     }

    /**
     * Gets defauld QRM profile for this user
     *
     * @return QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception NotFoundException
     */
     public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
     {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getDefaultQuoteRiskProfile for " + sessionManager);
        }
        QuoteRiskManagementProfileStruct QRMProfile = null;
        try
        {
            QRMProfile = getUserTradingParameterService().getDefaultQuoteRiskProfile(getSessionProfileUserStructV2().userInfo.userId);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }
        return QRMProfile;

     }

    /**
     * Sets QRM profile for this user per class key passed in QuoteRiskManagementProfileStruct
     *
     * @param quoteRiskProfile Object of type QuoteRiskManagementProfileStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void setQuoteRiskProfile(QuoteRiskManagementProfileStruct quoteRiskProfile)
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException
     {
         String id = sessionManager.getUserId();
         StringBuilder calling = new StringBuilder(id.length()+90);
         calling.append("calling setQuoteRiskProfile")
                .append(" USER:").append(id)
                .append(" CK:").append(quoteRiskProfile.classKey)
                .append(" Enabled:").append(quoteRiskProfile.quoteRiskManagementEnabled)
                .append(" TD:").append(quoteRiskProfile.volumeThreshold)
                .append(" TW:").append(quoteRiskProfile.timeWindow);
         Log.information(this, calling.toString());
         getUserTradingParameterService().setQuoteRiskProfile(getSessionProfileUserStructV2().userInfo.userId, quoteRiskProfile);
     }

    /**
     * Removes QRM profile for this user for given class key
     *
     * @param classKey int class key
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     * @exception DataValidationException
     */
     public void removeQuoteRiskProfile(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
     {
        String id = sessionManager.getUserId();
        StringBuilder calling = new StringBuilder(id.length()+50);
        calling.append("calling removeQuoteRiskProfile USER:").append(id)
               .append(" CK:").append(classKey);
        Log.information(this, calling.toString());
        try
        {
            getUserTradingParameterService().removeQuoteRiskProfile(getSessionProfileUserStructV2().userInfo.userId, classKey);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }

     }

    /**
     * Removes all quote risk profiles for this user
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception TransactionFailedException
     */
     public void removeAllQuoteRiskProfiles()
        throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException
     {
        String id = sessionManager.getUserId();
        StringBuilder calling = new StringBuilder(id.length()+40);
        calling.append("calling removeAllQuoteRiskProfiles USER:").append(id);
        Log.information(this, calling.toString());
        try
        {
            getUserTradingParameterService().removeAllQuoteRiskProfiles(getSessionProfileUserStructV2().userInfo.userId);
        } catch (DataValidationException dve){
            ExceptionBuilder.systemException(dve.details.message, dve.details.error);
        }

     }

     /**********************  END of QRM profile handling methodes ***********************************************/

     public com.cboe.idl.cmiUtil.KeyValueStruct[] getUserRateSettings(java.lang.String sessionName)
     	throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException
     {
    	 int blockWindowSize = 0;     
         long blockWindowMilliSecondPeriod =0L;
         int callWindowSize = 0;     
         long callWindowMilliSecondPeriod =0L;
         int quoteWindowSize = 0;     
         long quoteWindowMilliSecondPeriod =0L;
         int orderWindowSize = 0;     
         long orderWindowMilliSecondPeriod =0L;
         
      	 String quoteCalls = "quoteConstraints.rateMonitorWindow";
      	 String quoteCallsInterval = "quoteConstraints.rateMonitorInterval";
      	 String quoteRateInterval = "quoteConstraints.quoteRateMonitorInterval";
      	 String quoteRate = "quoteConstraints.quoteRateMonitorWindow";
      	 String quoteBlockSize = "quoteConstraints.rateMonitorQuoteSequenceSize";
      	 String orderCalls = "orderConstraints.rateMonitorWindow";
      	 String orderCallsInterval = "orderConstraints.rateMonitorInterval";
      	
      	 String userId = sessionManager.getUserId();
         String exchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
         String acronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
          
         
         StringBuilder calling = new StringBuilder(sessionName.length()+40);
         calling.append("calling getUserRateSettings for session:").append(sessionName);
         Log.information(this, calling.toString());
    	 Map allQuoteConstraints = ServicesHelper.getUserQuoteServiceHome().getSessionConstraints();
         Map quoteConstraints = (Map)allQuoteConstraints.get(sessionName);
    	 
    	 Map allMarketDataConstraints = ServicesHelper.getUserMarketDataServiceHome().getSessionConstraints();
         Map marketDataConstraints = (Map)allMarketDataConstraints.get(sessionName);

    	 Map allOrderConstraints = ServicesHelper.getUserOrderServiceHome().getSessionConstraints();
         Map orderConstraints = (Map)allOrderConstraints.get(sessionName);

         if (quoteConstraints == null || marketDataConstraints == null || allOrderConstraints == null)
         {
        	 throw ExceptionBuilder.dataValidationException("Constraints not found for session: " + sessionName, DataValidationCodes.INVALID_SESSION);
         }
         
         int size = quoteConstraints.size() + orderConstraints.size() + marketDataConstraints.size();
         com.cboe.idl.cmiUtil.KeyValueStruct[] rateSettings = new com.cboe.idl.cmiUtil.KeyValueStruct[size];

         
//////////////////get actual Quote/Order Rates Limit///////////////////////////////////////////
        
        
     	RateMonitor blockRateMonitor = findBlockRateMonitor(quoteConstraints, sessionName, userId, exchange, acronym);
     	
 		if (blockRateMonitor != null) {
 			blockWindowSize=blockRateMonitor.getWindowSize();    
 			blockWindowMilliSecondPeriod=blockRateMonitor.getWindowMilliSecondPeriod();
            
 		}  
 		
 		RateMonitor callRateMonitor = findCallRateMonitor(quoteConstraints, sessionName, userId, exchange, acronym);
     	
 		if (callRateMonitor != null) {
 			callWindowSize=callRateMonitor.getWindowSize();    
 			callWindowMilliSecondPeriod=callRateMonitor.getWindowMilliSecondPeriod();
            
 		}  
 		RateMonitor quoteRateMonitor = findQuoteRateMonitor(quoteConstraints, sessionName, userId, exchange, acronym);
     	
 		if (quoteRateMonitor != null) {
 			quoteWindowSize=quoteRateMonitor.getWindowSize();    
 			quoteWindowMilliSecondPeriod=quoteRateMonitor.getWindowMilliSecondPeriod();
            
 		}  	
 		
 		RateMonitor orderRateMonitor = findOrderRateMonitor(quoteConstraints, sessionName, userId, exchange, acronym);
     	
 		if (orderRateMonitor != null) {
 			orderWindowSize=orderRateMonitor.getWindowSize();    
 			orderWindowMilliSecondPeriod=orderRateMonitor.getWindowMilliSecondPeriod();
            
 		} 		
 		
        /*
         *  build rateSetting. Currently allConstraints Map is have default value from xml setting.
         *  We get the right quote and order rate monitor from RateMonitor cache.
         */
         int index = 0;
         Iterator iter = quoteConstraints.entrySet().iterator();
         StringBuilder keyName = new StringBuilder(50);       
         while (iter.hasNext())
         {
        	 Map.Entry keyValue = (Map.Entry)(iter.next());
             keyName.setLength(0);
             keyName.append("quoteConstraints.").append(keyValue.getKey());
             
             
             if(keyName.toString().equalsIgnoreCase(quoteCalls)){
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(callWindowSize)); 
            	 
             }
             else if (keyName.toString().equalsIgnoreCase(quoteCallsInterval)){
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(callWindowMilliSecondPeriod));
            	 
             }
             else if (keyName.toString().equalsIgnoreCase(quoteRateInterval)){
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(quoteWindowMilliSecondPeriod));
            	  
             }
             else if (keyName.toString().equalsIgnoreCase(quoteRate)){
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(quoteWindowSize));
            	 
         	 }
             else if (keyName.toString().equalsIgnoreCase(quoteBlockSize)){
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(blockWindowSize));
            	 
             }else{
            	 rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), keyValue.getValue().toString());
            	 
             }
           	         
         }
         		
		iter = orderConstraints.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry keyValue = (Map.Entry)(iter.next());
            keyName.setLength(0);
            keyName.append("orderConstraints.").append(keyValue.getKey());
            
            if(keyName.toString().equalsIgnoreCase(orderCalls)){
           	 	rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(orderWindowSize)); 
           	 	
            }
            else if (keyName.toString().equalsIgnoreCase(orderCallsInterval)){
           	  	rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), String.valueOf(orderWindowMilliSecondPeriod));
           	  	
            }
            else{
            	rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), keyValue.getValue().toString());
            	
            }	
            
		}
		
		
		iter = marketDataConstraints.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry keyValue = (Map.Entry)(iter.next());
            keyName.setLength(0);
            keyName.append("marketDataConstraints.").append(keyValue.getKey());
            rateSettings[index++] = new com.cboe.idl.cmiUtil.KeyValueStruct(keyName.toString(), keyValue.getValue().toString());
		}
		
		if (Log.isDebugOn())
		{
			for (com.cboe.idl.cmiUtil.KeyValueStruct kV: rateSettings)
			{
				Log.debug(this, kV.key + "=" + kV.value);
			}
		}
		
		
		
		return rateSettings;
     }
    
     private RateMonitorHome getRateMonitorHome()
     {
         if (rateMonitorHome == null )
         {
            try {
                 rateMonitorHome = (RateMonitorHome)HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
             }
             catch (CBOELoggableException e) {
                     Log.exception(this, "session : " + sessionManager, e);
                     
                     throw new NullPointerException("Could not find RateMonitor Home");
             }
         }

         return rateMonitorHome;
     }
        
     
     private RateMonitor findOrderRateMonitor(Map sessionConstraints,String sessionName,String userId, String exchange, String acronym)
     {
 		RateMonitor retRateMonitor = null;
 		int windowSize = 0;
 		long windowMilliSecondPeriod = 0;
 		
 		
 		if (sessionConstraints != null) {
 			Object constraint = sessionConstraints
 					.get(UserOrderServiceHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME);
 			if (constraint != null) {
 				windowMilliSecondPeriod = ((Long) constraint).longValue();
 			}
 			constraint = sessionConstraints
 					.get(UserOrderServiceHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME);
 			if (constraint != null) {
 				windowSize = ((Integer) constraint).intValue();
 			}
 		}

 		if (windowSize > 0 && windowMilliSecondPeriod > 0) {
 			RateMonitorKeyContainer rateMonitorKey = new RateMonitorKeyContainer(
 					userId, exchange, acronym, sessionName,
 					RateMonitorTypeConstants.ACCEPT_ORDER);
 			
 			retRateMonitor = getRateMonitorHome().find(rateMonitorKey,
 					windowSize, windowMilliSecondPeriod);
 			
 		}

 		return retRateMonitor;

 	}
     
     
     
     private RateMonitor findCallRateMonitor(Map sessionConstraints, String sessionName, String userId, String exchange, String acronym) {

 		RateMonitor retRateMonitor = null;
 		int windowSize = 0;
 		long windowMilliSecondPeriod = 0;
 
 		if (sessionConstraints != null) {
 			Object constraint = sessionConstraints
 					.get(UserQuoteServiceHomeImpl.CALL_WINDOW_INTERVAL_PROPERTY_NAME);
 			if (constraint != null) {
 				windowMilliSecondPeriod = ((Long) constraint).longValue();
 			}
 			constraint = sessionConstraints
 					.get(UserQuoteServiceHomeImpl.CALL_WINDOW_SIZE_PROPERTY_NAME);
 			if (constraint != null) {
 				windowSize = ((Integer) constraint).intValue();
 			}
 		}

 		if (windowSize > 0 && windowMilliSecondPeriod > 0) {
 			RateMonitorKeyContainer rateMonitorKey = new RateMonitorKeyContainer(
 					userId, exchange, acronym, sessionName,
 					RateMonitorTypeConstants.ACCEPT_QUOTE);
 			retRateMonitor = getRateMonitorHome().find(rateMonitorKey,
 					windowSize, windowMilliSecondPeriod);
 		}

 		return retRateMonitor;

 	}
     
     private RateMonitor findQuoteRateMonitor(Map sessionConstraints, String sessionName, String userId, String exchange, String acronym) {

 		RateMonitor retRateMonitor = null;
 		int windowSize = 0;
 		long windowMilliSecondPeriod = 0;
 		

 		if (sessionConstraints != null) {
 			Object constraint = sessionConstraints
 					.get(UserQuoteServiceHomeImpl.QUOTE_WINDOW_INTERVAL_PROPERTY_NAME);
 			if (constraint != null) {
 				windowMilliSecondPeriod = ((Long) constraint).longValue();
 			}
 			constraint = sessionConstraints
 					.get(UserQuoteServiceHomeImpl.QUOTE_WINDOW_SIZE_PROPERTY_NAME);
 			if (constraint != null) {
 				windowSize = ((Integer) constraint).intValue();
 			}
 		}

 		if (windowSize > 0 && windowMilliSecondPeriod > 0) {
 			RateMonitorKeyContainer rateMonitorKey = new RateMonitorKeyContainer(
 					userId, exchange, acronym, sessionName,
 					RateMonitorTypeConstants.QUOTES);
 			retRateMonitor = getRateMonitorHome().find(rateMonitorKey,
 					windowSize, windowMilliSecondPeriod);
 		}

 		return retRateMonitor;

 	}
     
     private RateMonitor findBlockRateMonitor(Map sessionConstraints, String sessionName, String userId, String exchange, String acronym) {
 		
 		RateMonitor retRateMonitor = null;
 		int maxSequenceSize = 0;
 		

 		if (sessionConstraints != null) {
 			Object constraint = sessionConstraints
 					.get(UserQuoteServiceHomeImpl.QUOTE_SEQUENCE_SIZE_PROPERTY_NAME);
 			if (constraint != null) {
 				maxSequenceSize = ((Integer) constraint).intValue();
 			}
 		}
 		if (maxSequenceSize > 0) {
 			RateMonitorKeyContainer rateMonitorKeyForCalls = new RateMonitorKeyContainer(
 					userId, exchange, acronym, sessionName,
 					RateMonitorTypeConstants.QUOTE_BLOCK_SIZE);
 			
 			retRateMonitor = getRateMonitorHome().find(rateMonitorKeyForCalls,
 					maxSequenceSize, 0L);
 		}

 		return retRateMonitor;

 	}
     
     
     
    /**
     * Returns reference to user trading parameter service.
     *
     * @return UserTradingParameterService Returnds reference to UserTradingParameterService object
     */
    private UserTradingParameterService getUserTradingParameterService()
    {
        if ( userTradingParameterService == null )
        {
            userTradingParameterService = ServicesHelper.getUserTradingParameterService();
        }

        return userTradingParameterService;
    }// end of getUserTradingParametersService

    /**
     * gets the current user information
     *
     * @return UserStruct Returns reference UserStruct object
     */
    private SessionProfileUserStructV2 getSessionProfileUserStructV2()
    {
        if ( userStruct == null )
        {
           try
           {
              userStruct = getSessionManager().getValidSessionProfileUserV2();
           }
           catch(Exception e)
           {
               Log.exception(this, "session : " + sessionManager, e);
           }
        }
        return userStruct;
    }// end of getUserStruct

    /**
    * get the session manager reference
    *
    * @return SessionManager Returns reference to SessionManager object
    */
    private SessionManager getSessionManager()
    {
        return sessionManager;
    }// end of getSessionManager

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        userTradingParameterService = null;
        sessionManager = null;
    }
    
    
    
}// EOF
