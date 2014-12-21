package com.cboe.domain.util;

import com.cboe.domain.util.DateWrapper;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.idl.cmiTrade.ExternalAtomicTradeResultStruct;
import com.cboe.idl.cmiTrade.ExternalBustTradeStruct;
import com.cboe.idl.cmiTrade.ExternalTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalTradeReportStruct;
import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiConstants.OrderOriginsOperations;
import com.cboe.idl.cmiConstants.PositionEffectsOperations;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.idl.constants.TradeSources;
import com.cboe.idl.constants.TradeTypes;
import com.cboe.idl.trade.AtomicTradeAcknowledgmentStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.ErrorFlagStruct;
import com.cboe.idl.trade.RelatedTradeReportStruct;
import com.cboe.idl.trade.RelatedTradeReportSummaryStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.idl.trade.TradeReportSettlementStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.trade.TradeReportSummaryStruct;
import com.cboe.util.ExceptionBuilder;


/**
 * A helper that makes it easy to create valid TradeReport structs.
 *
 * @author Alex Brazhnichenko
 */
public class TradeReportStructBuilder
{
    /**
     * All methods are static, so no instance is needed.
     */
    private TradeReportStructBuilder()
    {
        super();
    }

    /**
     * Creates a default instance of a TradeReportStruct.
     *
     * @return default instance of struct
     */
    public static TradeReportStruct buildTradeReportStruct()
    {
        TradeReportStruct struct = new TradeReportStruct();
        struct.quantity = 0;
        struct.price = StructBuilder.buildPriceStruct();
        struct.sessionName = "";
        struct.productKey = 0;
        struct.tradeSource = "";
        struct.tradeId = StructBuilder.buildCboeIdStruct();
        struct.tradeType = ' ';
        struct.bustable = true;
        struct.businessDate = StructBuilder.buildDateStruct();
        struct.timeTraded = StructBuilder.buildDateTimeStruct();
        struct.parties = new AtomicTradeStruct[0];

        return struct;
    }

    /**
     * Creates a default instance of a TradeReportStruct.
     *
     * @return default instance of struct
     */
    public static ExternalTradeReportStruct buildExternalTradeReportStruct()
    {
        ExternalTradeReportStruct struct = new ExternalTradeReportStruct();
        struct.quantity = 0;
        struct.price = StructBuilder.buildPriceStruct();
        struct.sessionName = "";
        struct.productKey = 0;
        struct.theTradeSource = "";
        struct.tradeId = StructBuilder.buildCboeIdStruct();
        struct.externalTradeType = ' ';
        struct.bustable = true;
        struct.businessDate = StructBuilder.buildDateStruct();
        struct.timeTraded = StructBuilder.buildDateTimeStruct();
        struct.parties = new ExternalAtomicTradeResultStruct[0];

        return struct;
    }
    
    /**
     * Creates a default instance of a AtomicTradeAcknowledgmentStruct.
     *
     * @return default instance of struct
     */
    public static AtomicTradeAcknowledgmentStruct buildAtomicTradeAcknowledgmentStruct()
    {
        AtomicTradeAcknowledgmentStruct struct = new AtomicTradeAcknowledgmentStruct();
        struct.atomicTradeId = StructBuilder.buildCboeIdStruct();
        struct.classKey = 0;
        struct.entryType = ' ';
        struct.errorFlags = new ErrorFlagStruct();
        struct.errorFlags.highBits = 0;
        struct.errorFlags.lowBits = 0;
        
        struct.matchedSequenceNumber = 0;            
        struct.processOk = false;
        struct.productKey = 0;
        struct.sourceGroup = 0;
        struct.sourceGroup = 0;
        return struct;
    }
/**
     * Creates a default instance of a TradeReportStructV2.
     *
     * @author Crystal Chen
     * @return default instance of struct
     */
    public static TradeReportStructV2 buildTradeReportStructV2()
    {
        TradeReportStructV2 struct = new TradeReportStructV2();
        struct.tradeReport     = buildTradeReportStruct();

        TradeReportSettlementStruct settlement = new TradeReportSettlementStruct();
        settlement.settlementDate  = StructBuilder.buildDateStruct();
        settlement.transactionTime = StructBuilder.buildDateTimeStruct();
        settlement.asOfFlag        = false;
        struct.settlementTradeReport = settlement;

        return struct;
    }

    /**
     * Creates a default instance of a AtomicTradeStruct.
     *
     * @return default instance of struct
     */
    public static AtomicTradeStruct buildAtomicTradeStruct()
    {
        AtomicTradeStruct struct = new AtomicTradeStruct();
        struct.atomicTradeId = StructBuilder.buildCboeIdStruct();
        struct.matchedSequenceNumber = 0;
        struct.active = true;
        struct.entryTime = StructBuilder.buildDateTimeStruct();
        struct.entryType = ' ';
        struct.lastUpdateTime = StructBuilder.buildDateTimeStruct();
        struct.lastEntryType = ' ';
        struct.quantity = 0;
        struct.sessionName = "";
        struct.buyerOriginType = ' ';
        struct.buyerFirmBranch = "";
        struct.buyerFirmBranchSequenceNumber = 0;
        struct.buyerCmta = StructBuilder.buildExchangeFirmStruct("","");
        struct.buyerCorrespondentId = "";
        struct.buyerPositionEffect = ' ';
        struct.buyerAccount = "";
        struct.buyerSubaccount = "";
        struct.buyerBroker = StructBuilder.buildExchangeAcronymStruct("","");
        struct.buyerOriginator = StructBuilder.buildExchangeAcronymStruct("","");
        struct.buyerFirm = StructBuilder.buildExchangeFirmStruct("","");
        struct.buyerOptionalData = "";
        struct.buyerOrderOrQuoteKey = StructBuilder.buildCboeIdStruct();
        struct.buyerOrderOrQuote = false;
        struct.reinstatableForBuyer = true;
        struct.sellerOriginType = ' ';
        struct.sellerFirmBranch = "";
        struct.sellerFirmBranchSequenceNumber = 0;
        struct.sellerCmta = StructBuilder.buildExchangeFirmStruct("","");
        struct.sellerCorrespondentId = "";
        struct.sellerPositionEffect = ' ';
        struct.sellerAccount = "";
        struct.sellerSubaccount = "";
        struct.sellerBroker = StructBuilder.buildExchangeAcronymStruct("","");
        struct.sellerOriginator = StructBuilder.buildExchangeAcronymStruct("","");
        struct.sellerFirm = StructBuilder.buildExchangeFirmStruct("","");
        struct.sellerOptionalData = "";
        struct.sellerOrderOrQuoteKey = StructBuilder.buildCboeIdStruct();
        struct.sellerOrderOrQuote = false;
        struct.reinstatableForSeller = true;

        return struct;
    }


    /**
     * Creates a default instance of a ExternalAtomicTradeStruct.
     *
     * @return default instance of struct
     */
    public static ExternalAtomicTradeResultStruct buildExternalAtomicTradeResultStruct()
    {
        ExternalAtomicTradeResultStruct struct = new ExternalAtomicTradeResultStruct();
        struct.atomicTradeId = StructBuilder.buildCboeIdStruct();
        struct.matchedSequenceNumber = 0;
        struct.active = true;
        struct.entryTime = StructBuilder.buildDateTimeStruct();
        struct.entryType = ' ';
        struct.lastUpdateTime = StructBuilder.buildDateTimeStruct();
        struct.lastEntryType = ' ';
        struct.quantity = 0;
        struct.sessionName = "";
        struct.buyerOriginType = ' ';
        struct.buyerFirmBranch = "";
        struct.buyerFirmBranchSequenceNumber = 0;
        struct.buyerCmta = StructBuilder.buildExchangeFirmStruct("","");
        struct.buyerCorrespondentId = "";
        struct.buyerPositionEffect = ' ';
        struct.buyerAccount = "";
        struct.buyerSubaccount = "";
        struct.buyerBroker = StructBuilder.buildExchangeAcronymStruct("","");
        struct.buyerOriginator = StructBuilder.buildExchangeAcronymStruct("","");
        struct.buyerFirm = StructBuilder.buildExchangeFirmStruct("","");
        struct.buyerOptionalData = "";
        struct.buyerOrderOrQuoteKey = StructBuilder.buildCboeIdStruct();
        struct.buyerOrderOrQuote = false;
        struct.reinstatableForBuyer = true;
        struct.sellerOriginType = ' ';
        struct.sellerFirmBranch = "";
        struct.sellerFirmBranchSequenceNumber = 0;
        struct.sellerCmta = StructBuilder.buildExchangeFirmStruct("","");
        struct.sellerCorrespondentId = "";
        struct.sellerPositionEffect = ' ';
        struct.sellerAccount = "";
        struct.sellerSubaccount = "";
        struct.sellerBroker = StructBuilder.buildExchangeAcronymStruct("","");
        struct.sellerOriginator = StructBuilder.buildExchangeAcronymStruct("","");
        struct.sellerFirm = StructBuilder.buildExchangeFirmStruct("","");
        struct.sellerOptionalData = "";
        struct.sellerOrderOrQuoteKey = StructBuilder.buildCboeIdStruct();
        struct.sellerOrderOrQuote = false;
        struct.reinstatableForSeller = true;

        return struct;
    }
    /**
     * Clones TradeReportStruct.
     *
     * @param TradeReportStruct struct to be cloned
     * @return cloned struct
     */
    public static TradeReportStruct cloneTradeReportStruct(TradeReportStruct struct)
    {
        TradeReportStruct result = null;
        if (struct != null)
        {
            result = new TradeReportStruct();
            result.quantity = struct.quantity;
            result.price = StructBuilder.clonePrice(struct.price);
            result.sessionName = struct.sessionName;
            result.productKey = struct.productKey;
            result.tradeSource = struct.tradeSource;
            result.tradeId = StructBuilder.cloneCboeId(struct.tradeId);
            result.tradeType = struct.tradeType;
            result.bustable = struct.bustable;
            result.businessDate = StructBuilder.cloneDate(struct.businessDate);
            result.timeTraded = StructBuilder.cloneDateTime(struct.timeTraded);
            int size = struct.parties.length;
            result.parties = new AtomicTradeStruct[size];
            for (int i = 0; i < struct.parties.length; i++)
            {
                  result.parties[i] = cloneAtomicTradeStruct(struct.parties[i]);
            }
        }
        return result;
    }

    /**
     * Clones TradeReportStructV2.
     *
     * @author Crystal Chen
     * @param TradeReportStructV2 struct to be cloned
     * @return cloned struct
     */
    public static TradeReportStructV2 cloneTradeReportStructV2(TradeReportStructV2 struct)
    {
        TradeReportStructV2 result = null;
        if (struct != null)
        {
            result = new TradeReportStructV2();
            result.tradeReport = cloneTradeReportStruct(struct.tradeReport);

            TradeReportSettlementStruct settlement = new TradeReportSettlementStruct();
            settlement.settlementDate = StructBuilder.cloneDate(struct.settlementTradeReport.settlementDate);
            settlement.transactionTime = StructBuilder.cloneDateTime(struct.settlementTradeReport.transactionTime);
            settlement.asOfFlag = struct.settlementTradeReport.asOfFlag;
            result.settlementTradeReport = settlement;
        }
        return result;
    }

    /**
     * Clones AtomicTradeStruct.
     *
     * @param AtomicTradeStruct struct to be cloned
     * @return cloned struct
     */
    public static AtomicTradeStruct cloneAtomicTradeStruct(AtomicTradeStruct struct)
    {
        AtomicTradeStruct result = null;
        if (struct != null)
        {
            result = new AtomicTradeStruct();
            result.atomicTradeId = StructBuilder.cloneCboeId(struct.atomicTradeId);
            result.matchedSequenceNumber = struct.matchedSequenceNumber;
            result.active = struct.active;
            result.entryTime = StructBuilder.cloneDateTime(struct.entryTime);
            result.entryType = struct.entryType;
            result.lastUpdateTime = StructBuilder.cloneDateTime(struct.lastUpdateTime);
            result.lastEntryType = struct.lastEntryType;
            result.quantity = struct.quantity;
            result.sessionName = struct.sessionName;
            result.buyerOriginType = struct.buyerOriginType;
            result.buyerFirmBranch = struct.buyerFirmBranch;
            result.buyerFirmBranchSequenceNumber = struct.buyerFirmBranchSequenceNumber;
            result.buyerCmta = StructBuilder.cloneExchangeFirmStruct(struct.buyerCmta);
            result.buyerCorrespondentId = struct.buyerCorrespondentId;
            result.buyerPositionEffect = struct.buyerPositionEffect;
            result.buyerAccount = struct.buyerAccount;
            result.buyerSubaccount = struct.buyerSubaccount;
            result.buyerBroker = StructBuilder.cloneExchangeAcronymStruct(struct.buyerBroker);
            result.buyerOriginator = StructBuilder.cloneExchangeAcronymStruct(struct.buyerOriginator);
            result.buyerFirm = StructBuilder.cloneExchangeFirmStruct(struct.buyerFirm);
            result.buyerOptionalData = struct.buyerOptionalData;
            result.buyerOrderOrQuoteKey = StructBuilder.cloneCboeId(struct.buyerOrderOrQuoteKey);
            result.buyerOrderOrQuote = struct.buyerOrderOrQuote;
            result.reinstatableForBuyer = struct.reinstatableForBuyer;
            result.sellerOriginType = struct.sellerOriginType;
            result.sellerFirmBranch = struct.sellerFirmBranch;
            result.sellerFirmBranchSequenceNumber = struct.sellerFirmBranchSequenceNumber;
            result.sellerCmta = StructBuilder.cloneExchangeFirmStruct(struct.sellerCmta);
            result.sellerCorrespondentId = struct.sellerCorrespondentId;
            result.sellerPositionEffect = struct.sellerPositionEffect;
            result.sellerAccount = struct.sellerAccount;
            result.sellerSubaccount = struct.sellerSubaccount;
            result.sellerBroker = StructBuilder.cloneExchangeAcronymStruct(struct.sellerBroker);
            result.sellerOriginator = StructBuilder.cloneExchangeAcronymStruct(struct.sellerOriginator);
            result.sellerFirm = StructBuilder.cloneExchangeFirmStruct(struct.sellerFirm);
            result.sellerOptionalData = struct.sellerOptionalData;
            result.sellerOrderOrQuoteKey = StructBuilder.cloneCboeId(struct.sellerOrderOrQuoteKey);
            result.sellerOrderOrQuote = struct.sellerOrderOrQuote;
            result.reinstatableForSeller = struct.reinstatableForSeller;
        }
        return result;
    }
    /**
     * Creates a default instance of a BustTradeStruct.
     *
     * @return default instance of struct
     */
    public static BustTradeStruct buildBustTradeStruct()
    {
        BustTradeStruct struct = new BustTradeStruct();
        struct.atomicTradeId = StructBuilder.buildCboeIdStruct();
        struct.bustedQuantity = 0;
        struct.buyerReinstateRequested = false;
        struct.sellerReinstateRequested = false;

        return struct;
    }

    /**
     * Creates a default instance of a BustTradeStruct.
     *
     * @return default instance of struct
     */
    public static BustTradeStruct cloneBustTradeStruct(BustTradeStruct struct)
    {
        BustTradeStruct result= null;
        if (struct != null)
        {
            result = new BustTradeStruct();
            result.atomicTradeId = StructBuilder.cloneCboeId(struct.atomicTradeId);
            result.bustedQuantity = struct.bustedQuantity;
            result.buyerReinstateRequested = struct.buyerReinstateRequested;
            result.sellerReinstateRequested = struct.sellerReinstateRequested;
        }

        return struct;
    }
    
    
    public static TradeReportStructV2 convertToTradeReportStructV2(ExternalTradeEntryStruct p_externalTradeEntry ){
        
        TradeReportStruct tradeReportStruct = TradeReportStructBuilder.buildTradeReportStruct();
        tradeReportStruct.businessDate = p_externalTradeEntry.businessDate;
        tradeReportStruct.bustable     = p_externalTradeEntry.bustable;
        
        if (p_externalTradeEntry.parties == null) 
        {
            tradeReportStruct.parties    =  new AtomicTradeStruct[0];
        }
        else 
        {
            AtomicTradeStruct[] atomicTradeStructs = new AtomicTradeStruct[p_externalTradeEntry.parties.length];
            for ( int i = 0; i < p_externalTradeEntry.parties.length; i++ )
            {
                atomicTradeStructs[i] = TradeReportStructBuilder.buildAtomicTradeStruct();
                atomicTradeStructs[i].buyerAccount = p_externalTradeEntry.parties[i].buyerAccount;
                atomicTradeStructs[i].buyerBroker = p_externalTradeEntry.parties[i].buyerBroker;
                atomicTradeStructs[i].buyerFirm = p_externalTradeEntry.parties[i].buyerFirm;
                atomicTradeStructs[i].buyerOptionalData = p_externalTradeEntry.parties[i].buyerOptionalData;
                atomicTradeStructs[i].buyerCmta = p_externalTradeEntry.parties[i].buyerCmta;
                atomicTradeStructs[i].buyerOriginator = p_externalTradeEntry.parties[i].buyerOriginator;
                atomicTradeStructs[i].buyerOriginType = p_externalTradeEntry.parties[i].buyerOriginType;
                atomicTradeStructs[i].buyerPositionEffect = p_externalTradeEntry.parties[i].buyerPositionEffect;
                atomicTradeStructs[i].buyerSubaccount = p_externalTradeEntry.parties[i].buyerSubaccount;
                atomicTradeStructs[i].entryTime = p_externalTradeEntry.parties[i].entryTime;
                atomicTradeStructs[i].entryType = p_externalTradeEntry.parties[i].entryType;
                atomicTradeStructs[i].quantity = p_externalTradeEntry.parties[i].quantity;
                atomicTradeStructs[i].sellerAccount = p_externalTradeEntry.parties[i].sellerAccount;
                atomicTradeStructs[i].sellerBroker = p_externalTradeEntry.parties[i].sellerBroker;
                atomicTradeStructs[i].sellerFirm = p_externalTradeEntry.parties[i].sellerFirm;
                atomicTradeStructs[i].sellerOptionalData = p_externalTradeEntry.parties[i].sellerOptionalData;
                atomicTradeStructs[i].sellerCmta = p_externalTradeEntry.parties[i].sellerCmta;
                atomicTradeStructs[i].sellerOriginator = p_externalTradeEntry.parties[i].sellerOriginator;
                atomicTradeStructs[i].sellerOriginType = p_externalTradeEntry.parties[i].sellerOriginType;
                atomicTradeStructs[i].sellerPositionEffect = p_externalTradeEntry.parties[i].sellerPositionEffect;
                atomicTradeStructs[i].sellerSubaccount = p_externalTradeEntry.parties[i].sellerSubaccount;
                atomicTradeStructs[i].sessionName = p_externalTradeEntry.parties[i].sessionName;
            }
            
            tradeReportStruct.parties    = atomicTradeStructs;
        }
        
        tradeReportStruct.price        = p_externalTradeEntry.price;
        tradeReportStruct.productKey   = p_externalTradeEntry.productKey;
        tradeReportStruct.quantity     = p_externalTradeEntry.quantity;
        tradeReportStruct.sessionName  = p_externalTradeEntry.sessionName;
        tradeReportStruct.timeTraded   = p_externalTradeEntry.timeTraded;
        tradeReportStruct.tradeSource  = p_externalTradeEntry.theTradeSource;
        tradeReportStruct.tradeType    = p_externalTradeEntry.externalTradeType;
    
        TradeReportStructV2 tradeReportStructV2 = new TradeReportStructV2();
        
        tradeReportStructV2.settlementTradeReport = com.cboe.domain.util.StructBuilder.buildDefaultTradeReportSettlement();
        tradeReportStructV2.settlementTradeReport.asOfFlag = p_externalTradeEntry.asOfFlag;
        tradeReportStructV2.settlementTradeReport.transactionTime = p_externalTradeEntry.transactionTime;
        tradeReportStructV2.settlementTradeReport.settlementDate = p_externalTradeEntry.settlementDate;
        tradeReportStructV2.tradeReport = tradeReportStruct;
       
        return tradeReportStructV2;
        
    }
    

    public static TradeReportStructV2 convertToTradeReportStructV2(FloorTradeEntryStruct p_TradeEntry)
    throws  
           NotAcceptedException
    {
        TradeReportStruct tradeReportStruct     = TradeReportStructBuilder.buildTradeReportStruct();
        AtomicTradeStruct atomicTradeStruct     = TradeReportStructBuilder.buildAtomicTradeStruct();
        
        // Uses current time if the passed time is null
        DateWrapper timestamp = null;
        if (p_TradeEntry.timeTraded != null)
        {
            timestamp = new DateWrapper(p_TradeEntry.timeTraded);
        }
        else
        {
            timestamp = new DateWrapper();
        }        
        
        atomicTradeStruct.sessionName   = p_TradeEntry.sessionName;  
        atomicTradeStruct.quantity      = p_TradeEntry.quantity;
        atomicTradeStruct.entryTime     = timestamp.toDateTimeStruct();
        
        if(p_TradeEntry.side == Sides.BUY) 
        {
            //executing side information
            atomicTradeStruct.buyerAccount              = p_TradeEntry.account;
            atomicTradeStruct.buyerBroker               = p_TradeEntry.executingMarketMaker;
            atomicTradeStruct.buyerFirm                 = p_TradeEntry.firm;
            atomicTradeStruct.buyerOptionalData         = p_TradeEntry.optionalData;
            atomicTradeStruct.buyerCmta                 = p_TradeEntry.cmta;
            atomicTradeStruct.buyerOriginType           = OrderOriginsOperations.MARKET_MAKER;           
            atomicTradeStruct.buyerPositionEffect       = p_TradeEntry.positionEffect;
            atomicTradeStruct.buyerSubaccount           = p_TradeEntry.subaccount;
            //contra side information
            atomicTradeStruct.sellerBroker              = p_TradeEntry.contraBroker;
            atomicTradeStruct.sellerFirm                = p_TradeEntry.contraFirm;
            atomicTradeStruct.sellerPositionEffect      = PositionEffectsOperations.NOTAPPLICABLE;
        } 
        else
        if (p_TradeEntry.side == Sides.SELL)
        {
            //executing side information
            atomicTradeStruct.sellerAccount             = p_TradeEntry.account;
            atomicTradeStruct.sellerBroker              = p_TradeEntry.executingMarketMaker;
            atomicTradeStruct.sellerFirm                = p_TradeEntry.firm;
            atomicTradeStruct.sellerOptionalData        = p_TradeEntry.optionalData;
            atomicTradeStruct.sellerCmta                = p_TradeEntry.cmta;
            atomicTradeStruct.sellerOriginType          = OrderOriginsOperations.MARKET_MAKER;
            atomicTradeStruct.sellerPositionEffect      = p_TradeEntry.positionEffect;
            atomicTradeStruct.sellerSubaccount          = p_TradeEntry.subaccount;
            //contra side information
            atomicTradeStruct.buyerBroker               = p_TradeEntry.contraBroker;
            atomicTradeStruct.buyerFirm                 = p_TradeEntry.contraFirm;   
            atomicTradeStruct.buyerPositionEffect       = PositionEffectsOperations.NOTAPPLICABLE;
        }
        else
        {
            throw ExceptionBuilder.notAcceptedException("Invalid side found in FloorTradeEntryStruct", 
                                                        NotAcceptedCodes.INVALID_REQUEST);
        }
        
        AtomicTradeStruct[] atomicTradeStructs = {atomicTradeStruct};
        tradeReportStruct.parties = atomicTradeStructs;
        tradeReportStruct.businessDate = timestamp.toDateStruct();
        tradeReportStruct.bustable     = true;    
        tradeReportStruct.price        = p_TradeEntry.price;
        tradeReportStruct.productKey   = p_TradeEntry.productKey;
        tradeReportStruct.quantity     = p_TradeEntry.quantity;
        tradeReportStruct.sessionName  = p_TradeEntry.sessionName;
        tradeReportStruct.tradeSource  = TradeSources.MARKET_MAKER_MANUAL; 
        tradeReportStruct.tradeType    = TradeTypes.HANDHELD_TRADE;  // NOTICE: this method supports only handheld trades
        tradeReportStruct.timeTraded   = timestamp.toDateTimeStruct();
        
        TradeReportStructV2 tradeReportStructV2 = new TradeReportStructV2();
        tradeReportStructV2.settlementTradeReport = com.cboe.domain.util.StructBuilder.buildDefaultTradeReportSettlement();
        tradeReportStructV2.settlementTradeReport.transactionTime = timestamp.toDateTimeStruct();
        tradeReportStructV2.settlementTradeReport.settlementDate = timestamp.toDateStruct();  
        tradeReportStructV2.tradeReport = tradeReportStruct;
       
        return tradeReportStructV2;
    }    
    
    public static ExternalTradeReportStruct convertToExternalTradeReportStruct(TradeReportStructV2 tradeReportStructV2 ){
        ExternalTradeReportStruct externalTradeReportStruct =  TradeReportStructBuilder.buildExternalTradeReportStruct();
        
        externalTradeReportStruct.asOfFlag = tradeReportStructV2.settlementTradeReport.asOfFlag;
        externalTradeReportStruct.settlementDate = tradeReportStructV2.settlementTradeReport.settlementDate;
        externalTradeReportStruct.transactionTime = tradeReportStructV2.settlementTradeReport.transactionTime;
        
        TradeReportStruct tradeReportStruct =  tradeReportStructV2.tradeReport;
        externalTradeReportStruct.businessDate = tradeReportStruct.businessDate;
        externalTradeReportStruct.bustable = tradeReportStruct.bustable;

        
        if ( tradeReportStruct.parties == null )
        {
            externalTradeReportStruct.parties = new ExternalAtomicTradeResultStruct[0];
        }
        else
        {
            externalTradeReportStruct.parties = new ExternalAtomicTradeResultStruct[tradeReportStruct.parties.length];
            
            for (int i = 0 ; i < tradeReportStruct.parties.length; i++ )
            {
                externalTradeReportStruct.parties[i] = TradeReportStructBuilder.buildExternalAtomicTradeResultStruct();
                externalTradeReportStruct.parties[i].active = tradeReportStruct.parties[i].active;
                externalTradeReportStruct.parties[i].atomicTradeId = tradeReportStruct.parties[i].atomicTradeId;
                externalTradeReportStruct.parties[i].buyerAccount = tradeReportStruct.parties[i].buyerAccount;
                externalTradeReportStruct.parties[i].buyerBroker = tradeReportStruct.parties[i].buyerBroker;
                externalTradeReportStruct.parties[i].buyerCmta = tradeReportStruct.parties[i].buyerCmta;
                externalTradeReportStruct.parties[i].buyerCorrespondentId = tradeReportStruct.parties[i].buyerCorrespondentId;
                externalTradeReportStruct.parties[i].buyerFirm  = tradeReportStruct.parties[i].buyerFirm;
                externalTradeReportStruct.parties[i].buyerFirmBranch = tradeReportStruct.parties[i].buyerFirmBranch;
                externalTradeReportStruct.parties[i].buyerFirmBranchSequenceNumber  = tradeReportStruct.parties[i].buyerFirmBranchSequenceNumber;
                externalTradeReportStruct.parties[i].buyerOptionalData = tradeReportStruct.parties[i].buyerOptionalData;
                externalTradeReportStruct.parties[i].buyerOrderOrQuote = tradeReportStruct.parties[i].buyerOrderOrQuote;
                externalTradeReportStruct.parties[i].buyerOrderOrQuoteKey  = tradeReportStruct.parties[i].buyerOrderOrQuoteKey;
                externalTradeReportStruct.parties[i].buyerOriginator = tradeReportStruct.parties[i].buyerOriginator;
                externalTradeReportStruct.parties[i].buyerOriginType = tradeReportStruct.parties[i].buyerOriginType;
                externalTradeReportStruct.parties[i].buyerPositionEffect  = tradeReportStruct.parties[i].buyerPositionEffect;
                externalTradeReportStruct.parties[i].buyerSubaccount = tradeReportStruct.parties[i].buyerSubaccount;
                externalTradeReportStruct.parties[i].entryTime = tradeReportStruct.parties[i].entryTime;
                externalTradeReportStruct.parties[i].entryType = tradeReportStruct.parties[i].entryType;
                externalTradeReportStruct.parties[i].lastEntryType = tradeReportStruct.parties[i].lastEntryType;
                externalTradeReportStruct.parties[i].lastUpdateTime = tradeReportStruct.parties[i].lastUpdateTime;
                externalTradeReportStruct.parties[i].matchedSequenceNumber = tradeReportStruct.parties[i].matchedSequenceNumber;
                externalTradeReportStruct.parties[i].quantity = tradeReportStruct.parties[i].quantity;
                externalTradeReportStruct.parties[i].reinstatableForBuyer  = tradeReportStruct.parties[i].reinstatableForBuyer;
                externalTradeReportStruct.parties[i].reinstatableForSeller = tradeReportStruct.parties[i].reinstatableForSeller;
                externalTradeReportStruct.parties[i].sellerAccount = tradeReportStruct.parties[i].sellerAccount;
                externalTradeReportStruct.parties[i].sellerBroker = tradeReportStruct.parties[i].sellerBroker;
                externalTradeReportStruct.parties[i].sellerCmta = tradeReportStruct.parties[i].sellerCmta;
                externalTradeReportStruct.parties[i].sellerCorrespondentId  = tradeReportStruct.parties[i].sellerCorrespondentId;
                externalTradeReportStruct.parties[i].sellerFirm  = tradeReportStruct.parties[i].sellerFirm;
                externalTradeReportStruct.parties[i].sellerFirmBranch  = tradeReportStruct.parties[i].sellerFirmBranch;
                externalTradeReportStruct.parties[i].sellerFirmBranchSequenceNumber  = tradeReportStruct.parties[i].sellerFirmBranchSequenceNumber;
                externalTradeReportStruct.parties[i].sellerOptionalData = tradeReportStruct.parties[i].sellerOptionalData;
                externalTradeReportStruct.parties[i].sellerOrderOrQuote = tradeReportStruct.parties[i].sellerOrderOrQuote;
                externalTradeReportStruct.parties[i].sellerOrderOrQuoteKey  = tradeReportStruct.parties[i].sellerOrderOrQuoteKey;
                externalTradeReportStruct.parties[i].sellerOriginator  = tradeReportStruct.parties[i].sellerOriginator;
                externalTradeReportStruct.parties[i].sellerOriginType  = tradeReportStruct.parties[i].sellerOriginType;
                externalTradeReportStruct.parties[i].sellerPositionEffect  = tradeReportStruct.parties[i].sellerPositionEffect;
                externalTradeReportStruct.parties[i].sellerSubaccount =  tradeReportStruct.parties[i].sellerSubaccount;
                externalTradeReportStruct.parties[i].sessionName  = tradeReportStruct.parties[i].sessionName;
            }
        }
        
        externalTradeReportStruct.price = tradeReportStruct.price;
        externalTradeReportStruct.productKey = tradeReportStruct.productKey;
        externalTradeReportStruct.quantity = tradeReportStruct.quantity;
        externalTradeReportStruct.sessionName= tradeReportStruct.sessionName;
        externalTradeReportStruct.timeTraded= tradeReportStruct.timeTraded;
        externalTradeReportStruct.tradeId= tradeReportStruct.tradeId;
        externalTradeReportStruct.theTradeSource= tradeReportStruct.tradeSource;
        externalTradeReportStruct.externalTradeType= tradeReportStruct.tradeType;
        externalTradeReportStruct.transactionTime= tradeReportStructV2.settlementTradeReport.transactionTime;
        externalTradeReportStruct.settlementDate= tradeReportStructV2.settlementTradeReport.settlementDate;
        externalTradeReportStruct.asOfFlag= tradeReportStructV2.settlementTradeReport.asOfFlag;
        
        return externalTradeReportStruct;
        
    }

    public static BustTradeStruct[] convertToBustTradeStruct(ExternalBustTradeStruct[] externalBustTradeStructs ){
        
        if (externalBustTradeStructs == null) 
        {
            return new BustTradeStruct[0];
        }
        BustTradeStruct[] bustTradeStructs = new BustTradeStruct[externalBustTradeStructs.length];
        
        for ( int i = 0; i < externalBustTradeStructs.length ; i++ )
        {
            bustTradeStructs[i] = TradeReportStructBuilder.buildBustTradeStruct();
            bustTradeStructs[i].atomicTradeId = externalBustTradeStructs[i].atomicTtradeId;
            bustTradeStructs[i].bustedQuantity = externalBustTradeStructs[i].bustedQuantity;
            bustTradeStructs[i].buyerReinstateRequested = externalBustTradeStructs[i].buyerReinstateRequested;
            bustTradeStructs[i].sellerReinstateRequested = externalBustTradeStructs[i].sellerReinstateRequested;
        }
        return bustTradeStructs;
        
    }

    public static TradeReportSummaryStruct buildTradeReportSummaryStruct()
    {
    	TradeReportSummaryStruct reportSummary = new TradeReportSummaryStruct();
    	reportSummary.productKey = 0;
    	reportSummary.timeTraded = StructBuilder.buildDateTimeStruct();
    	reportSummary.tradeId = StructBuilder.buildCboeIdStruct();
    	return reportSummary;
    }
    
    public static RelatedTradeReportStruct buildRelatedTradeReportStruct()
    {
    	RelatedTradeReportStruct struct = new RelatedTradeReportStruct();
		struct.relatedTradeReports = new TradeReportStruct[1];
		struct.parentReportIndex = 0;
		struct.relatedTradeReports[0] = TradeReportStructBuilder.buildTradeReportStruct();		
		return struct;
    }
    
    public static RelatedTradeReportStruct buildRelatedTradeReportStruct(int size)
    {
        RelatedTradeReportStruct struct = new RelatedTradeReportStruct();
        struct.relatedTradeReports = new TradeReportStruct[size];
        struct.parentReportIndex = 0;
        struct.relatedTradeReports = new TradeReportStruct[size];
        for( int i = 0; i< size; i++ ) {
            struct.relatedTradeReports[ i ] = TradeReportStructBuilder.buildTradeReportStruct();
        }
        return struct;
    }
    
    public static RelatedTradeReportSummaryStruct buildRelatedTradeReportSummaryStruct()
    {
    	RelatedTradeReportSummaryStruct reportSummary = new RelatedTradeReportSummaryStruct();
    	reportSummary.relatedTradeReports = new TradeReportSummaryStruct[1];
    	reportSummary.relatedTradeReports[0] = TradeReportStructBuilder.buildTradeReportSummaryStruct();
    	reportSummary.parentReportIndex = 0;
    	reportSummary.extraSearchReportIndexes = new int[0];
    	return reportSummary;
    }

}
