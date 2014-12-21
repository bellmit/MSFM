set long 4096
set linesize 32767
set pagesize 50000
set NULL ''
set CONCAT .

set SERVEROUT on

declare
    parties NUMBER;
    cursor cursor1 is
    select /*+ ORDERED USE_HASH(sbt_order) USE_HASH(g) */ distinct 
    report.DATABASEIDENTIFIER as databaseid,
    -- groups [ ]
    -- sourcegroup
    -- h.DATABASEIDENTIFIER as sourceGroup,
    -- 'sourcegroup' as sourceGroup,
    -- ProductKeysStruct    
    report.PRODUCT as productKey,
    trading_class.DATABASEIDENTIFIER as classKey,
    trading_class.PROD_TYPE_CODE as productType,
    trading_prod.RPT_CLASS as reportingClass,
    -- TradeReportStruct
    report.QUANTITY as quantity,
    '2 ' as priceType,
    trunc(report.PRICE) as proceWhole,
    trunc((report.PRICE  - trunc(report.PRICE)) * 1000000000)as priceFraction,
    entry.SESSION_NAME as sessionName,
    -- report.PRODUCT as productKey,
    report.TRADESOURCE as tradeSource,
     trunc(report.DATABASEIDENTIFIER / POWER(2,32) ) as highCboeId,
    (report.DATABASEIDENTIFIER - (POWER(2,32) * trunc(report.DATABASEIDENTIFIER /POWER( 2,32)))) as lowCboeId,
    report.TRADE_TYPE as tradeType,
    -- 'true ' as bustable,
    --CASE WHEN sbt_order.DATABASEIDENTIFIER IN (Select distinct z.ORDERDBID From SBTORDERLEGDETAIL z, SBTORDER sbt_order) THEN 'false' ELSE 'true' END as bustable,
 	CASE WHEN report.PREFIX IN('CANC','CNCO','SPRD') THEN 'false' ELSE 'true' END as bustable,
    EXTRACT (MONTH FROM TO_DATE(report.REPORTEDTRADEDATE,'YYYYMMDD')) as businessDate,
    EXTRACT (DAY FROM TO_DATE(report.REPORTEDTRADEDATE,'YYYYMMDD')) as businessDay,
    EXTRACT (YEAR FROM TO_DATE(report.REPORTEDTRADEDATE,'YYYYMMDD')) as businessYear,
    EXTRACT (MONTH FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)  ) as timeTradedMonth,
    EXTRACT (DAY FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as timeTradedDay,
    EXTRACT (YEAR FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as timeTradedYear,
    EXTRACT (HOUR FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as timeTradedHour,
    EXTRACT (MINUTE FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as timeTradedMinute,
    EXTRACT (SECOND FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as timeTradedSecond,
    trunc( (EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(report.TRANSACTIONTIME/1000 , 'SECOND')) -  trunc( EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(report.TRANSACTIONTIME/1000 , 'SECOND')))) * 100) as timeTradedFraction,
    -- START tradeReport.parties[ ]
    -- tradeReport.parties[0].atomicTradeId.highCboeId
    trunc(entry.DATABASEIDENTIFIER / POWER(2,32) ) as atomicHighCboeId,
    -- tradeReport.parties[0].atomicTradeId.lowCboeId 
    (entry.DATABASEIDENTIFIER - (POWER(2,32) * trunc(entry.DATABASEIDENTIFIER /POWER( 2,32)))) as atomicLowCboeId,
    entry.MATCHEDSEQUENCENUMBER as matchedSequenceNumber,
    CASE WHEN entry.ACTIVE=0 THEN 'false' ELSE 'true' END as active,
    EXTRACT (MONTH FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeMonth,
    EXTRACT (DAY FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeDay,
    EXTRACT (YEAR FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeYear,
    EXTRACT (HOUR FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeHour,
    EXTRACT (MINUTE FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeMinute,
    EXTRACT (SECOND FROM CAST(convert_java_time(entry.ENTRY_TIME) as timestamp)) as entryTimeSecond,
    trunc( (EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(entry.ENTRY_TIME/1000 , 'SECOND')) -  trunc( EXTRACT (SECOND FROM
        TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(entry.ENTRY_TIME/1000 , 'SECOND')))) * 100) as entryTimeFraction,
    entry.ENTRY_TYPE as entryType,
    EXTRACT (MONTH FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateMonth,
    EXTRACT (DAY FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateDay,
    EXTRACT (YEAR FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateYear,
    EXTRACT (HOUR FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateHour,
    EXTRACT (MINUTE FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateMinute,
    EXTRACT (SECOND FROM CAST(convert_java_time(entry.LAST_UPDATE_TIME) as timestamp)) as lastUpdateSecond,
    trunc( (EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(entry.LAST_UPDATE_TIME/1000 , 'SECOND')) -  trunc( EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(entry.LAST_UPDATE_TIME/1000 , 'SECOND')))) * 100) as lastUpdateFraction,
    entry.LAST_ENTRY_TYPE as lastEntryType,
    entry.QUANTITY as entryQuantity,
    entry.SESSION_NAME as entrySessionName,
    entry.BUYERORIGINTYPE as buyerOriginType,
    entry.BUY_FIRM_BRANCH as buyerFirmBranch,
    entry.BUY_FIRM_BRANCH_SEQ_NO as buyerFirmBranchSequenceNumber,
    entry.BUY_CMTA_EXCH as buyerCMTAExchange,
    entry.BUYERCMTA as buyerCMTAFirmNumber,
    entry.BUY_CORR_ID as buyerCorrespondentId,
    entry.BUYERPOSITIONEFFECT as buyerPositionEffect,
    entry.BUY_ACCT as buyerAccount,
    entry.BUYERSUBACCOUNT as buyerSubaccount,
    entry.BUY_BROKER_EXCH as buyerBrokerExchange,
    entry.BUYER as buyerBrokerAcronym,
    entry.BUY_ORIGINATOR_EXCH as buyerOriginatorExchange,
    entry.BUY_ORIGINATOR as buyerOriginatorAcronym,
    entry.BUY_FIRM_EXCH as buyerFirmExchange,
    entry.BUYFIRM as buyerFirmNumber,
    REPLACE(entry.BUYEROPTIONALDATA, ',', ' ') as buyerOptionalData,
    --  parties[0].buyerOrderOrQuoteKey.highCboeId
     trunc(entry.BUYORDERID / POWER(2,32) ) as buyerOrderOrQuoteKeyHighCboeId,
    --  parties[0].buyerOrderOrQuoteKey.lowCboeId
    CASE WHEN entry.BUYORDERID=0 THEN entry.BUYQUOTEID
    ELSE (entry.BUYORDERID - (POWER(2,32) * trunc(entry.BUYORDERID /POWER( 2,32))))
    END as buyerOrderOrQuoteKeyLowCboeId,
    CASE WHEN entry.BUYORDERID=0 THEN 'false' ELSE 'true' END as buyerOrderOrQuote,
    CASE WHEN entry.BUYREINSTATABLE=0 THEN 'false' ELSE 'true' END as reinstatableForBuyer,
    entry.SELLERORIGINTYPE as sellerOriginType,
    entry.SELL_FIRM_BRANCH as sellerFirmBranch,
    entry.SELL_FIRM_BRANCH_SEQ_NO as sellerFirmBranchSequenceNumber,
    entry.SELL_CMTA_EXCH as sellerCMTAExchange,
    entry.SELLERCMTA as sellerCMTAFirmNumber,
    entry.SELL_CORR_ID as sellerCorrespondentId,
    entry.SELLERPOSITIONEFFECT as sellerPositionEffect,
    entry.SELL_ACCT as sellerAccount,
    entry.SELLERSUBACCOUNT as sellerSubaccount,
    entry.SELL_BROKER_EXCH as sellerBrokerExchange,
    entry.SELLER as sellerBrokerAcronym,
    entry.SELL_ORIGINATOR_EXCH as sellerOriginatorExchange,
    entry.SELL_ORIGINATOR as sellerOriginatorAcronym,
    entry.SELL_FIRM_EXCH as sellerFirmExchange,
    entry.SELLFIRM as sellerFirmNumber,
    REPLACE(entry.SELLEROPTIONALDATA, ',', ' ') as sellerOptionalData,
    -- tradeReport.parties[0].sellerOrderOrQuoteKey.highCboeId

    trunc(entry.SELLORDERID / POWER(2,32) ) as sellOrdOrQKeyHighCboeId,
    -- tradeReport.parties[0].sellerOrderOrQuoteKey.lowCboeId
    CASE WHEN entry.SELLORDERID=0 THEN entry.SELLQUOTEID ELSE (entry.SELLORDERID - (POWER(2,32) * trunc(entry.SELLORDERID /POWER( 2,32)))) END as sellOrdOrQKeyLowCboeId,
    CASE WHEN entry.SELLORDERID=0 THEN 'false' ELSE 'true' END  as sellerOrderOrQuote,
    CASE WHEN entry.SELLREINSTATABLE=0 THEN 'false' ELSE 'true' END as reinstatableForSeller,
    -- END tradeReport.parties[ ]
    
    -- com.cboe.idl.trade.TradeReportBillingStruct
    -- HIGHCBOEID
    trunc(report.DATABASEIDENTIFIER / POWER(2,32) ) as billingStructHighCboeId,
    -- LOWCBOEID
    (report.DATABASEIDENTIFIER - (POWER(2,32) * trunc(report.DATABASEIDENTIFIER /POWER( 2,32)))) as billingStructLowCboeId, 
    entry.EXTENSIONS as billingStructExtensions,
    -- settlementTradeReport:
    EXTRACT (MONTH FROM TO_DATE(report.SETTLEMENTDATE,'YYYYMMDD')) as settlementDateMonth,
    EXTRACT (DAY FROM TO_DATE(report.SETTLEMENTDATE,'YYYYMMDD')) as settlementDateDay,
    EXTRACT (YEAR FROM TO_DATE(report.SETTLEMENTDATE,'YYYYMMDD')) as settlementDateYear,
    EXTRACT (MONTH FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeMonth,
    EXTRACT (DAY FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeDay,
    EXTRACT (YEAR FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeYear,
    EXTRACT (HOUR FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeHour,
    EXTRACT (MINUTE FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeMinute,
    EXTRACT (SECOND FROM CAST(convert_java_time(report.TRANSACTIONTIME) as timestamp)) as transactionTimeSecond,
    trunc( (EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(report.TRANSACTIONTIME/1000 , 'SECOND')) -  trunc( EXTRACT (SECOND FROM TO_TIMESTAMP('19700101000000','YYYYMMDDHH24MISS') + NUMTODSINTERVAL(report.TRANSACTIONTIME/1000 , 'SECOND')))) * 100) as transactionTimeFraction,
    CASE WHEN report.ASOFFLAG=0 THEN 'false' ELSE 'true' END as asOfFlag,
    --  START billingStruct.partiesBillingType[ ]
    -- HIGHCBOEID
    trunc(entry.DATABASEIDENTIFIER / POWER(2,32) ) as billingStructAtomicHighCboeId,
    -- LOWCBOEID
     (entry.DATABASEIDENTIFIER - (POWER(2,32) * trunc(entry.DATABASEIDENTIFIER /POWER( 2,32)))) as billingStructAtomicLowCboeId,
    entry.BUY_BILL_TYPE_CODE as buyBillingType,
    SELL_BILL_TYPE_CODE as sellBillingType,
    entry.ROUND_LOT_QTY as roundLotQuantity,
    entry.BUYER_CLEAR_TYPE as buyerClearingType,
    entry.SELLER_CLEAR_TYPE as sellerClearingType,
    entry.BUY_AWAY_EXCH_TEXT as buyAwayExchanges,
    entry.SELL_AWAY_EXCH_TEXT as sellAwayExchanges,
    entry.EXTENSIONS as partiesBillingTypeExtensions,
    -- END billingStruct.partiesBillingType[ ]
    -- START billingStruct.cmtaStruct.cmta[ ]
    -- HIGHCBOEID
    trunc(entry.DATABASEIDENTIFIER / POWER(2,32) ) as atomicTradeIdHighCboeId,
    -- LOWCBOEID
    (entry.DATABASEIDENTIFIER - (POWER(2,32) * trunc(entry.DATABASEIDENTIFIER /POWER( 2,32)))) as atomicTradeIdLowCboeId,
    entry.BUY_AWAY_EXCH_TEXT as buyerAwayExchAcronymExch,
    entry.BUY_AWAY_EXCHANGE_ACRONYM as buyerAwayExchAcronym,
    entry.SELL_AWAY_EXCH_TEXT as sellerAwayExchAcronymExch,
    entry.SELL_AWAY_EXCHANGE_ACRONYM as sellerAwayExchAcronym,
    entry.BUY_ORDERDATE as buyerOrderDate,
    entry.SELL_ORDERDATE as sellerOrderDate,
    entry.BUY_ORSID as buyerOrsid,
    entry.SELL_ORSID as sellerOrsid,
    entry.BUY_SUPRESSION_REASON as buyerSupressionReason,
    entry.SELL_SUPRESSION_REASON as sellerSupressionReason
    -- END billingStruct.cmtaStruct.cmta[ ]
    FROM
        &1.SBTTRADEREPORTENTRY entry  INNER JOIN &1._SBT_TRADEREPORT  report ON (report.DATABASEIDENTIFIER=entry.TRADEREPORTFORENTRY)
        LEFT JOIN &1._SBTORDER sbt_order  ON (sbt_order.DATABASEIDENTIFIER=entry.BUYORDERID or sbt_order.DATABASEIDENTIFIER=entry.SELLORDERID)
        INNER JOIN PRODUCT trading_prod ON (report.PRODUCT = trading_prod.DATABASEIDENTIFIER)
        INNER JOIN PROD_CLASS trading_class ON (trading_prod.PROD_CLASS=trading_class.DATABASEIDENTIFIER)
	LEFT JOIN &1._SBTORDERHISTORY g ON(sbt_order.DATABASEIDENTIFIER=g.ORDERDBID)
    WHERE 
        TO_DATE(report.REPORTEDTRADEDATE,'YYYYMMDD')=TO_DATE(CURRENT_DATE) AND trading_class.PROD_TYPE_CODE !=11 &2 &3 &4 &5 &6 &7;
    
begin
     for record in cursor1 loop
       select COUNT (*) INTO parties From &1.SBTTRADEREPORTENTRY x where record.databaseid=x.TRADEREPORTFORENTRY;
       -- to be removed
       -- dbms_output.put(record.databaseid||', ');
       -- #of parties
       -- dbms_output.put(parties||', ');
       -- dbms_output.put(record.sourceGroup||', ');
       dbms_output.put(record.productKey||', ');
       dbms_output.put(record.classKey||', ');
       dbms_output.put(record.productType||', ');
       dbms_output.put(record.reportingClass||', ');
       -- TradeReportStruct: 
       dbms_output.put(record.quantity||', ');
       dbms_output.put(record.priceType||', ');
       dbms_output.put(record.proceWhole||', ');
       dbms_output.put(record.priceFraction||', ');
       dbms_output.put(record.sessionName||', ');
       dbms_output.put(record.productKey||', ');
       dbms_output.put(record.tradeSource||', ');
       dbms_output.put(record.highCboeId||', ');
       dbms_output.put(record.lowCboeId||', ');
       dbms_output.put(record.tradeType||', ');
       dbms_output.put(record.bustable||', ');
       dbms_output.put(record.businessDate||', ');
       dbms_output.put(record.businessDay||', ');
       dbms_output.put(record.businessYear||', ');
       dbms_output.put(record.timeTradedMonth||', ');
       dbms_output.put(record.timeTradedDay||', ');
       dbms_output.put(record.timeTradedYear||', ');
       dbms_output.put(record.timeTradedHour||', ');
       dbms_output.put(record.timeTradedMinute||', ');
       dbms_output.put(record.timeTradedSecond||', ');
       dbms_output.put(record.timeTradedFraction||', ');       
                dbms_output.put(record.atomicHighCboeId||', ');
		dbms_output.put(record.atomicLowCboeId||', ');
		dbms_output.put(record.matchedSequenceNumber||', ');
		dbms_output.put(record.active||', ');
		dbms_output.put(record.entryTimeMonth||', ');
		dbms_output.put(record.entryTimeDay||', ');
		dbms_output.put(record.entryTimeYear||', ');
		dbms_output.put(record.entryTimeHour||', ');
		dbms_output.put(record.entryTimeMinute||', ');
		dbms_output.put(record.entryTimeSecond||', ');
		dbms_output.put(record.entryTimeFraction||', ');
		dbms_output.put(record.entryType||', ');
		dbms_output.put(record.lastUpdateMonth||', ');
		dbms_output.put(record.lastUpdateDay||', ');
		dbms_output.put(record.lastUpdateYear||', ');
		dbms_output.put(record.lastUpdateHour||', ');
		dbms_output.put(record.lastUpdateMinute||', ');
		dbms_output.put(record.lastUpdateSecond||', ');
		dbms_output.put(record.lastUpdateFraction||', ');
		dbms_output.put(record.lastEntryType||', ');
		dbms_output.put(record.entryQuantity||', ');
		dbms_output.put(record.entrySessionName||', ');
		dbms_output.put(record.buyerOriginType||', ');
		dbms_output.put(record.buyerFirmBranch||', ');
		dbms_output.put(record.buyerFirmBranchSequenceNumber||', ');
		dbms_output.put(record.buyerCMTAExchange||', ');
		dbms_output.put(record.buyerCMTAFirmNumber||', ');
		dbms_output.put(record.buyerCorrespondentId||', ');
		dbms_output.put(record.buyerPositionEffect||', ');
		dbms_output.put(record.buyerAccount||', ');
		dbms_output.put(record.buyerSubaccount||', ');
		dbms_output.put(record.buyerBrokerExchange||', ');
		dbms_output.put(record.buyerBrokerAcronym||', ');
		dbms_output.put(record.buyerOriginatorExchange||', ');
		dbms_output.put(record.buyerOriginatorAcronym||', ');
		dbms_output.put(record.buyerFirmExchange||', ');
		dbms_output.put(record.buyerFirmNumber||', ');
		dbms_output.put(record.buyerOptionalData||', ');
                IF record.buyerOrderOrQuoteKeyLowCboeId<0 THEN
		    dbms_output.put(-1||', ');
		ELSE
		    dbms_output.put(record.buyerOrderOrQuoteKeyHighCboeId||', ');
		END IF;
		-- dbms_output.put(record.buyerOrderOrQuoteKeyHighCboeId||', ');
		dbms_output.put(record.buyerOrderOrQuoteKeyLowCboeId||', ');
		dbms_output.put(record.buyerOrderOrQuote||', ');
		dbms_output.put(record.reinstatableForBuyer||', ');
		dbms_output.put(record.sellerOriginType||', ');
		dbms_output.put(record.sellerFirmBranch||', ');
		dbms_output.put(record.sellerFirmBranchSequenceNumber||', ');
		dbms_output.put(record.sellerCMTAExchange||', ');
		dbms_output.put(record.sellerCMTAFirmNumber||', ');
		dbms_output.put(record.sellerCorrespondentId||', ');
		dbms_output.put(record.sellerPositionEffect||', ');
		dbms_output.put(record.sellerAccount||', ');
		dbms_output.put(record.sellerSubaccount||', ');
		dbms_output.put(record.sellerBrokerExchange||', ');
		dbms_output.put(record.sellerBrokerAcronym||', ');
		dbms_output.put(record.sellerOriginatorExchange||', ');
		dbms_output.put(record.sellerOriginatorAcronym||', ');
		dbms_output.put(record.sellerFirmExchange||', ');
		dbms_output.put(record.sellerFirmNumber||', ');
		dbms_output.put(record.sellerOptionalData||', ');
        IF record.sellOrdOrQKeyLowCboeId<0 THEN
            dbms_output.put(-1||', ');
        ELSE
            dbms_output.put(record.sellOrdOrQKeyHighCboeId||', ');
        END IF;
		-- dbms_output.put(record.sellOrdOrQKeyHighCboeId||', ');
		dbms_output.put(record.sellOrdOrQKeyLowCboeId||', ');
		dbms_output.put(record.sellerOrderOrQuote||', ');
		dbms_output.put(record.reinstatableForSeller||', ');
            dbms_output.put(record.billingStructHighCboeId||', ');
            dbms_output.put(record.billingStructLowCboeId||', ');
               dbms_output.put(record.billingStructAtomicHighCboeId||', ');
               dbms_output.put(record.billingStructAtomicLowCboeId||', ');
               dbms_output.put(record.buyBillingType||', ');
	       dbms_output.put(record.sellBillingType||', ');
	       dbms_output.put(record.roundLotQuantity||', ');
	       dbms_output.put(record.buyerClearingType||', ');
	       dbms_output.put(record.sellerClearingType||', ');
	       dbms_output.put(record.buyAwayExchanges||', ');
	       dbms_output.put(record.sellAwayExchanges||', ');	       
	       dbms_output.put(record.partiesBillingTypeExtensions||', ');
            dbms_output.put(record.billingStructExtensions||', ');
                dbms_output.put(record.atomicTradeIdHighCboeId||', ');
                dbms_output.put(record.atomicTradeIdLowCboeId||', ');
                dbms_output.put(record.buyerAwayExchAcronymExch||', ');
		dbms_output.put(record.buyerAwayExchAcronym||', ');
		dbms_output.put(record.sellerAwayExchAcronymExch||', ');
		dbms_output.put(record.sellerAwayExchAcronym||', ');
		dbms_output.put(record.buyerOrderDate||', ');
		dbms_output.put(record.sellerOrderDate||', ');
		dbms_output.put(record.buyerOrsid||', ');
		dbms_output.put(record.sellerOrsid||', ');
		dbms_output.put(record.buyerSupressionReason||', ');
		dbms_output.put(record.sellerSupressionReason||', ');                
            -- com.cboe.idl.trade.TradeReportSettlementStruct
            dbms_output.put(record.settlementDateMonth||', ');
	    dbms_output.put(record.settlementDateDay||', ');
	    dbms_output.put(record.settlementDateYear||', ');
	    dbms_output.put(record.transactionTimeMonth||', ');
	    dbms_output.put(record.transactionTimeDay||', ');
	    dbms_output.put(record.transactionTimeYear||', ');
	    dbms_output.put(record.transactionTimeHour||', ');
	    dbms_output.put(record.transactionTimeMinute||', ');
	    dbms_output.put(record.transactionTimeSecond||', ');
	    dbms_output.put(record.transactionTimeFraction||', ');
	    dbms_output.put(record.asOfFlag);
    dbms_output.put_line('');
    end loop;
end; 
/

