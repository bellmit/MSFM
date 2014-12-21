package com.cboe.proxy.businessServicesClient;

import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.trade.TradeBustResponseStruct;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiTrade.ExternalBustTradeStruct;
import com.cboe.idl.cmiTrade.ExternalTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalTradeReportStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.internalBusinessServices.TradeMaintenanceService;
import com.cboe.idl.trade.AtomicCmtaAllocationStruct;
import com.cboe.idl.trade.AtomicTradeBillingStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.RelatedTradeReportStruct;
import com.cboe.idl.trade.RelatedTradeReportSummaryStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.trade.TradeReportStructV3;
import com.cboe.idl.trade.TradeReportSummaryStruct;
import com.cboe.idl.trade.MultipleTradeBustStruct;
import com.cboe.domain.util.TradeReportStructBuilder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TradeMaintenanceServiceClientRoutingProxy
        extends NonGlobalServiceClientRoutingProxy
        implements com.cboe.interfaces.internalBusinessServices.TradeMaintenanceService
{
    /**
     * Default constructor
     **/
    public	TradeMaintenanceServiceClientRoutingProxy()
    {
        super();
    }

    public TradeReportStruct getTradeReportByTradeId(CboeIdStruct tradeId, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        TradeReportStruct tradeReportStruct = null;
        Iterator keys = routeMap.keySet().iterator();
        while ( keys.hasNext() )
        {
            String serviceRoute = (String)keys.next();
            if (Log.isDebugOn())
            {
                Log.debug( this, "Sending request to service: " + serviceRoute );
            }
            TradeMaintenanceService targetService = (TradeMaintenanceService)routeMap.get( serviceRoute );
            try
            {
                tradeReportStruct = targetService.getTradeReportByTradeId( tradeId, activeOnly );
                break;
            }
            catch ( NotFoundException nfe )
            {
                if( !keys.hasNext() )
                {
                    // If there are no more routes, throw the NotFoundException
                    throw nfe;
                }
            }
        }
        return  tradeReportStruct;
    }

    public TradeReportStructV2 getTradeReportV2ByTradeId(CboeIdStruct tradeId, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        TradeReportStructV2 tradeReportStructV2 = null;
        Iterator keys = routeMap.keySet().iterator();
        while ( keys.hasNext() )
        {
            String serviceRoute = (String)keys.next();
            if (Log.isDebugOn())
            {
                Log.debug( this, "Sending request to service: " + serviceRoute );
            }
            TradeMaintenanceService targetService = (TradeMaintenanceService)routeMap.get( serviceRoute );
            try
            {
                tradeReportStructV2 = targetService.getTradeReportV2ByTradeId( tradeId, activeOnly );
                break;
            }
            catch ( NotFoundException nfe )
            {
                if( !keys.hasNext() )
                {
                    // If there are no more routes, throw the NotFoundException
                    throw nfe;
                }
            }
        }
        return  tradeReportStructV2;
    }

    public TradeReportStruct[] getTradeReports(ExchangeAcronymStruct broker, ExchangeFirmStruct firm,
            int productKey, String sessionName,
            DateTimeStruct beginTime, DateTimeStruct endTime,
            char buySellInd, boolean activeOnly )
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(sessionName, productKey);

        return targetService.getTradeReports(broker, firm, productKey, sessionName, beginTime, endTime, buySellInd, activeOnly);
    }

    public TradeReportStructV2[] getTradeReportsV2(ExchangeAcronymStruct broker, ExchangeFirmStruct firm,
            int productKey, String sessionName,
            DateTimeStruct beginTime, DateTimeStruct endTime,
            char buySellInd, boolean activeOnly )
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(sessionName , productKey);

        return targetService.getTradeReportsV2(broker, firm, productKey, sessionName, beginTime, endTime, buySellInd, activeOnly);
    }

    public TradeReportStruct acceptTrade(TradeReportStruct externalTradeStruct)
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(externalTradeStruct.parties[0].sessionName,externalTradeStruct.productKey);

        return targetService.acceptTrade(externalTradeStruct);
    }

    public TradeReportStruct acceptTradeWithClearingType(TradeReportStruct externalTrade,
            boolean isParTrade, char tradedSide, AtomicCmtaAllocationStruct[] atomicCmtaAllocations)
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        try
        {
            TradeMaintenanceService targetService = (TradeMaintenanceService) getServiceByProduct(
                    externalTrade.parties[0].sessionName, externalTrade.productKey);

            return targetService.acceptTradeWithClearingType(externalTrade, isParTrade, tradedSide, atomicCmtaAllocations);
        }
        catch(org.omg.CORBA.COMM_FAILURE cf)
        {
            Log.exception(this, "Report as CommunicationException", cf);
            throw ExceptionBuilder.communicationException("COMM_FAILURE: Failed create the Trade Report:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.TRANSIENT trans)
        {
            Log.exception(this, "Report as CommunicationException", trans);
            throw ExceptionBuilder.communicationException("TRANSIENT: Failed create the Trade Report:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST oe)
        {
            Log.exception(this, "Report as CommunicationException", oe);
            throw ExceptionBuilder.communicationException("OBJECT_NOT_EXIST: Failed create the Trade Report:", CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
    }

    public TradeReportStructV2 acceptTradeV2( TradeReportStructV2 tradeReport )
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(tradeReport.tradeReport.parties[0].sessionName, tradeReport.tradeReport.productKey);
        return targetService.acceptTradeV2(tradeReport);
    }

    public void acceptTradeBust(String sessionName, int productKey, CboeIdStruct tradeId,
            BustTradeStruct [] bustedTrades, String reason)
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(sessionName,productKey);

        targetService.acceptTradeBust(sessionName, productKey, tradeId, bustedTrades, reason);
    }

    /**
     * Return helper class name
     */
    protected String getHelperClassName()
    {
        return "com.cboe.idl.internalBusinessServices.TradeMaintenanceServiceHelper";
    }


    public TradeReportStruct[] findTradeReportsBetween(DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean activeOnly)
        throws NotFoundException, CommunicationException, SystemException, AuthorizationException
    {
        Collection<TradeReportStruct> tradeReports = new ArrayList<TradeReportStruct>();

        for (String serviceRoute : routeMap.keySet())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service: " + serviceRoute);
            }

            TradeMaintenanceService targetService =
                    (TradeMaintenanceService) routeMap.get(serviceRoute);
            try
            {
                TradeReportStruct[] tradeReportStructs =
                        targetService.findTradeReportsBetween(beginDateTime, endDateTime, activeOnly);

                tradeReports.addAll(Arrays.asList(tradeReportStructs));
            }
            catch (NotFoundException e)
            {
                // do nothing, move on
            }
        }
        return (TradeReportStruct[]) tradeReports.toArray();
    }

    public TradeReportStructV2[] findTradeReportsV2Between(DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean activeOnly)
        throws NotFoundException, CommunicationException, SystemException, AuthorizationException
    {
        Collection<TradeReportStructV2> tradeReports = new ArrayList<TradeReportStructV2>();
        for (String serviceRoute : routeMap.keySet())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service: " + serviceRoute);
            }
            TradeMaintenanceService targetService = (TradeMaintenanceService) routeMap.get(serviceRoute);

            try
            {
                TradeReportStructV2[] tradeReportStructs = targetService.findTradeReportsV2Between(beginDateTime, endDateTime, activeOnly);
                tradeReports.addAll(Arrays.asList(tradeReportStructs));
            }
            catch (NotFoundException e)
            {
                // do nothing, move on
            }
        }
        return (TradeReportStructV2[]) tradeReports.toArray();
    }

    public TradeReportStruct[] findUnsentTradeReportsBetween( DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean activeOnly )
        throws NotFoundException, CommunicationException, SystemException, AuthorizationException
    {
        Collection<TradeReportStruct> tradeReports = new ArrayList<TradeReportStruct>();
        for (String serviceRoute : routeMap.keySet())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service: " + serviceRoute);
            }

            TradeMaintenanceService targetService = (TradeMaintenanceService) routeMap.get(serviceRoute);
            try
            {
                TradeReportStruct[] tradeReportStructs = targetService.findUnsentTradeReportsBetween(beginDateTime, endDateTime, activeOnly);
                tradeReports.addAll(Arrays.asList(tradeReportStructs));
            }
            catch (NotFoundException e)
            {
                // do nothing, move on
            }
        }
        return (TradeReportStruct[]) tradeReports.toArray();
    }

    public TradeReportStructV2[] findUnsentTradeReportsV2Between( DateTimeStruct begindateTimeStruct, DateTimeStruct endDateTime, boolean activeOnly )
        throws NotFoundException, CommunicationException, SystemException, AuthorizationException
    {
        Collection<TradeReportStructV2> tradeReports = new ArrayList<TradeReportStructV2>();
        for (String serviceRoute : routeMap.keySet())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service: " + serviceRoute);
            }
            TradeMaintenanceService targetService = (TradeMaintenanceService) routeMap.get(serviceRoute);

            try
            {
                TradeReportStructV2[] tradeReportStructs = targetService.findUnsentTradeReportsV2Between(begindateTimeStruct, endDateTime, activeOnly);
                tradeReports.addAll(Arrays.asList(tradeReportStructs));
            }
            catch (NotFoundException e)
            {
                // do nothing, move on
            }
        }
        return (TradeReportStructV2[]) tradeReports.toArray();
    }


    public com.cboe.idl.cmiUtil.DateStruct getSettlementDate(String sessionName, int productKey, char tradeType, boolean asOfFlag, short daysAhead)
        throws NotFoundException, DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(sessionName, productKey);

        return targetService.getSettlementDate(sessionName, productKey, tradeType, asOfFlag, daysAhead);
    }

    public void acceptTradeUpdate(String sessionName, int productKey, CboeIdStruct tradeId, AtomicTradeStruct[] atomicTradeStructs)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =  (TradeMaintenanceService) getServiceByProduct(sessionName, productKey);
        targetService.acceptTradeUpdate(sessionName, productKey, tradeId, atomicTradeStructs);
    }

    public ExternalTradeReportStruct acceptExternalTrade(ExternalTradeEntryStruct p_externalTrade)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(p_externalTrade.parties[0].sessionName,p_externalTrade.productKey);

        return targetService.acceptExternalTrade(p_externalTrade);
    }


    public void acceptExternalTradeBust(String p_tradingSessionName, int p_productKey, CboeIdStruct p_tradeId, ExternalBustTradeStruct[] p_bustedTrades, String p_reason) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(p_tradingSessionName,p_productKey);

        targetService.acceptExternalTradeBust(p_tradingSessionName, p_productKey, p_tradeId, p_bustedTrades,p_reason);
    }

    public TradeReportStructV2 acceptCrossProductLegTrade( String originatingSessionName, TradeReportStructV2 tradeReport, AtomicTradeBillingStruct[] billings )
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(tradeReport.tradeReport.parties[0].sessionName, tradeReport.tradeReport.productKey);
        return targetService.acceptCrossProductLegTrade(originatingSessionName, tradeReport, billings);
    }

    public TradeReportStructV2 acceptCrossProductLegTradeV2(
            String p_originatingSessionName, TradeReportStructV2 p_crossProductLegTrade,
            AtomicTradeBillingStruct[] p_billings, OrderStruct[] p_buyers, OrderStruct[] p_sellers)
        throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(p_crossProductLegTrade.tradeReport.parties[0].sessionName,
                        p_crossProductLegTrade.tradeReport.productKey);
        return targetService.acceptCrossProductLegTradeV2(p_originatingSessionName, p_crossProductLegTrade,
                p_billings, p_buyers, p_sellers);
    }

    public void deleteFloorTrade( String tradingSessionName, int productKey, com.cboe.idl.cmiUtil.CboeIdStruct tradeId, com.cboe.idl.cmiUser.ExchangeAcronymStruct user, com.cboe.idl.cmiUser.ExchangeFirmStruct userFirm, String reason) throws SystemException, DataValidationException, AuthorizationException, NotAcceptedException, CommunicationException, NotFoundException, TransactionFailedException
    {

        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(tradingSessionName,
                        productKey);
        targetService.deleteFloorTrade(tradingSessionName,productKey,tradeId,user,userFirm,reason);
    }

    public com.cboe.idl.cmiUtil.CboeIdStruct acceptFloorTrade(com.cboe.idl.cmiTrade.FloorTradeEntryStruct floorTrade, String userId, char tradeType) throws SystemException, DataValidationException, AuthorizationException, NotAcceptedException, CommunicationException, TransactionFailedException
    {
        TradeMaintenanceService targetService =
                (TradeMaintenanceService) getServiceByProduct(floorTrade.sessionName,
                        floorTrade.productKey);
        return targetService.acceptFloorTrade(floorTrade, userId, tradeType);

    }

    public com.cboe.idl.trade.TradeReportStruct[] acceptManualTrade(com.cboe.idl.trade.ManualTradeReportStruct externalTrade, boolean sweepTrade) throws SystemException, DataValidationException, AuthorizationException, NotAcceptedException, CommunicationException, TransactionFailedException
    {
        throw new RuntimeException("Not supported operation :acceptManualTrade");
    }


      public TradeBustResponseStruct[] acceptMultipleTradeBust(MultipleTradeBustStruct[] trades,
                      String requestingUserId, String transactionId, DateTimeStruct timeStamp, KeyValueStruct[] auditLogProperties)


                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException,

                  NotAcceptedException, TransactionFailedException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "acceptMultipleTradeBust invoked");         
            return null;

      }

 


      public TradeReportStructV3[] getTradeReportsBySummary(

                  TradeReportSummaryStruct[] tradeReportSummary, boolean activeOnly)

                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

              /*
          TradeReportStructV3[] reports = new TradeReportStructV3[1];

            reports[0] = TradeReportStructBuilder.buildTradeReportStructV2();         

            Log.information(this, "getTradeReportsBySummary invoked");

            return reports;
            */
              return null;

      }

 


      public RelatedTradeReportSummaryStruct[] getTradeReportsByTime(

                  DateTimeStruct beginTime, DateTimeStruct endTime, String session, boolean activeOnly)

                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "getTradeReportsByTime invoked");

            RelatedTradeReportSummaryStruct[] reportSummary = new RelatedTradeReportSummaryStruct[1];

            reportSummary[0] = buildRelatedTradeReportSummaryStruct();

            return reportSummary;         

      }

 



      public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndClass(

                  DateTimeStruct beginTime, DateTimeStruct endTime, String session, int[] classKeys, boolean activeOnly)

                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "getTradeReportsByTimeAndClass invoked");

            RelatedTradeReportSummaryStruct[] reportSummary = new RelatedTradeReportSummaryStruct[1];

            reportSummary[0] = buildRelatedTradeReportSummaryStruct();

            return reportSummary;         

      }

 



      public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndExchange(

                  DateTimeStruct beginTime, DateTimeStruct endTime, String session, 

                  String[] primaryExchanges, boolean activeOnly) throws SystemException,

                  CommunicationException, AuthorizationException,

                  DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "getTradeReportsByTimeAndExchange invoked");

            RelatedTradeReportSummaryStruct[] reportSummary = new RelatedTradeReportSummaryStruct[1];

            reportSummary[0] = buildRelatedTradeReportSummaryStruct();

            return reportSummary;         

      }

 



      public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndProduct(

                  DateTimeStruct beginTime, DateTimeStruct endTime, String session, int[] productKeys, boolean activeOnly)

                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "getTradeReportsByTimeAndProduct invoked");

            RelatedTradeReportSummaryStruct[] reportSummary = new RelatedTradeReportSummaryStruct[1];

            reportSummary[0] = buildRelatedTradeReportSummaryStruct();

            return reportSummary;         

      }

 



      public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndUser(

                  DateTimeStruct beginTime, DateTimeStruct endTime, String session, String[] users, boolean activeOnly)

                  throws SystemException, CommunicationException,

                  AuthorizationException, DataValidationException, NotFoundException 

      {

            // TODO Dummy stub to be implemented later.

            Log.information(this, "getTradeReportsByTimeAndUser invoked");

            RelatedTradeReportSummaryStruct[] reportSummary = new RelatedTradeReportSummaryStruct[1];

            reportSummary[0] = buildRelatedTradeReportSummaryStruct();

            return reportSummary;         

      }


	 private RelatedTradeReportSummaryStruct buildRelatedTradeReportSummaryStruct()

    {

      TimeStruct ts = new TimeStruct((byte) -1, (byte) 0, (byte) 0, (byte) 0);

      DateStruct ds = new DateStruct();

      RelatedTradeReportSummaryStruct reportSummary = new RelatedTradeReportSummaryStruct();

      reportSummary.relatedTradeReports[1].productKey = 0;

      reportSummary.relatedTradeReports[1].timeTraded = new DateTimeStruct(ds, ts);

      reportSummary.relatedTradeReports[1].tradeId = new CboeIdStruct(0, 0);

      return reportSummary;

    }

    public RelatedTradeReportSummaryStruct getTradeReportSummaryByTradeId(CboeIdStruct p_tradeId,
            boolean p_activeOnly) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException
    {
        // TODO Auto-generated method stub
        return null;
    }


}
