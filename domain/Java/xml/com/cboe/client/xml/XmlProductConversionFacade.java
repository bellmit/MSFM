//
// ------------------------------------------------------------------------
// FILE: XmlProductConversionFacade.java
//
// PACKAGE: com.cboe.client.xml.product
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.client.xml;

import com.cboe.client.xml.bind.*;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;

public class XmlProductConversionFacade
{
    private static XmlProductConversionFacade ourInstance;

    private XmlProductConversionFacade()
    {
    }

    public synchronized static XmlProductConversionFacade getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new XmlProductConversionFacade();
        }
        return ourInstance;
    }

    /**
     * CONVERSION TO STRUCTS
     */

    public TradingSessionStruct[] getTradingSessionStructs(GIProductQueryOperationsType pqo)
    {
        TradingSessionStruct[] tradingSessionStructs = new TradingSessionStruct[0];
        if(pqo != null && pqo.getTradingSessionStructSequence() != null)
        {
            tradingSessionStructs = getTradingSessionStructs(pqo.getTradingSessionStructSequence());
        }
        return tradingSessionStructs;
    }
    public TradingSessionStruct[] getTradingSessionStructs(GITradingSessionStructSequence giTradingSessionStructSequence)
    {
        TradingSessionStruct[] tradingSessionStructs = new TradingSessionStruct[0];
        if(giTradingSessionStructSequence != null && giTradingSessionStructSequence.getTradingSessionStructs() != null)
        {
            tradingSessionStructs = getTradingSessionStructs(giTradingSessionStructSequence.getTradingSessionStructs());
        }
        return tradingSessionStructs;
    }
    public TradingSessionStruct[] getTradingSessionStructs(GITradingSessionStructType[] giTradingSessionStructTypes)
    {
        TradingSessionStruct[] tradingSessionStructs = new TradingSessionStruct[0];
        if(giTradingSessionStructTypes != null && giTradingSessionStructTypes.length>0)
        {
            tradingSessionStructs = new TradingSessionStruct[giTradingSessionStructTypes.length];
            for (int i = 0; i < giTradingSessionStructTypes.length; i++)
            {
                tradingSessionStructs[i] = getTradingSessionStruct(giTradingSessionStructTypes[i]);
            }
        }
        return tradingSessionStructs;
    }

    public ProductTypeStruct[] getProductTypeStructs(GIProductQueryOperationsType pqo)
    {
        ProductTypeStruct[] productTypeStructs = new ProductTypeStruct[0];
        if(pqo != null && pqo.getProductTypeStructSequence() != null)
        {
            productTypeStructs = getProductTypeStructs(pqo.getProductTypeStructSequence());
        }
        return productTypeStructs;
    }
    public ProductTypeStruct[] getProductTypeStructs(GIProductTypeStructSequence giProductTypeStructSequence)
    {
        ProductTypeStruct[] productTypeStructs = new ProductTypeStruct[0];
        if(giProductTypeStructSequence != null && giProductTypeStructSequence.getProductTypeStructsLength()>0)
        {
            productTypeStructs = getProductTypeStructs(giProductTypeStructSequence.getProductTypeStructs());
        }
        return productTypeStructs;
    }
    public ProductTypeStruct[] getProductTypeStructs(GIProductTypeStructType[] giProductTypeStructTypes)
    {
        ProductTypeStruct[] productTypeStructs = new ProductTypeStruct[0];
        if(giProductTypeStructTypes != null && giProductTypeStructTypes.length>0)
        {
            productTypeStructs = new ProductTypeStruct[giProductTypeStructTypes.length];
            for (int i = 0; i < giProductTypeStructTypes.length; i++)
            {
                productTypeStructs[i] = getProductTypeStruct(giProductTypeStructTypes[i]);
            }
        }
        return productTypeStructs;
    }
    public SessionClassStruct[] getSessionClassStructs(GIProductQueryOperationsType pqo)
    {
        SessionClassStruct[] sessionClassStructs = new SessionClassStruct[0];
        if(pqo != null && pqo.getSessionClassStructSequence() != null )
        {
            sessionClassStructs = getSessionClassStructs(pqo.getSessionClassStructSequence());
        }
        return sessionClassStructs;
    }
    public SessionClassStruct[] getSessionClassStructs(GISessionClassStructSequence giSessionClassStructSequence)
    {
        SessionClassStruct[] sessionClassStructs = new SessionClassStruct[0];
        if(giSessionClassStructSequence != null && giSessionClassStructSequence.getSessionClassStructsLength()>0)
        {
            sessionClassStructs = getSessionClassStructs(giSessionClassStructSequence.getSessionClassStructs());
        }
        return sessionClassStructs;
    }
    public SessionClassStruct[] getSessionClassStructs(GISessionClassStructType[] giSessionClassStructTypes)
    {
        SessionClassStruct[] sessionClassStructs = new SessionClassStruct[0];
        if(giSessionClassStructTypes != null && giSessionClassStructTypes.length>0)
        {
            sessionClassStructs = new SessionClassStruct[giSessionClassStructTypes.length];
            for (int i = 0; i < giSessionClassStructTypes.length; i++)
            {
                sessionClassStructs[i] = getSessionClassStruct(giSessionClassStructTypes[i]);
            }
        }
        return sessionClassStructs;
    }
    public ClassStruct[] getClassStructs(GIProductQueryOperationsType pqo)
    {
        ClassStruct[] classStructs = new ClassStruct[0];
        if(pqo != null && pqo.getClassStructSequence() != null)
        {
            classStructs = getClassStructs(pqo.getClassStructSequence());
        }
        return classStructs;
    }
    public ClassStruct[] getClassStructs(GIClassStructSequence giClassStructSequence)
    {
        ClassStruct[] classStructs = new ClassStruct[0];
        if(giClassStructSequence != null && giClassStructSequence.getClassStructsLength() != 0)
        {
            GIClassStructType[] giClassStructs = giClassStructSequence.getClassStructs();
            if(giClassStructs != null && giClassStructs.length>0)
            {
                classStructs = getClassStructs(giClassStructs);
            }
        }
        return classStructs;
    }
    public ClassStruct[] getClassStructs(GIClassStructType[] giClassStructTypes)
    {
        ClassStruct[] classStructs = new ClassStruct[0];
        if(giClassStructTypes != null && giClassStructTypes.length>0)
        {
            classStructs = new ClassStruct[giClassStructTypes.length];
            for (int i = 0; i < giClassStructTypes.length; i++)
            {
                classStructs[i] = getClassStruct(giClassStructTypes[i]);
            }
            }
        return classStructs;
    }
    public ProductStruct[] getProductStructs(GIProductQueryOperationsType pqo)
    {
        ProductStruct[] productStructs = new ProductStruct[0];
        if(pqo != null && pqo.getProductStructSequence() != null)
        {
            productStructs = getProductStructs(pqo.getProductStructSequence());
        }
        return productStructs;
    }

    public ProductStruct[] getProductStructs(GIProductStructSequence giProductStructSequence)
    {
        ProductStruct[] productStructs = new ProductStruct[0];
        if(giProductStructSequence != null && giProductStructSequence.getProductStructsLength()>0)
        {
            productStructs = getProductStructs(giProductStructSequence.getProductStructs());
        }
        return productStructs;
    }

    public ProductStruct[] getProductStructs(GIProductStructType[] giProductStructTypes)
    {
        ProductStruct[] productStructs = new ProductStruct[0];
        if(giProductStructTypes != null && giProductStructTypes.length>0)
        {
            for (int i = 0; i < giProductStructTypes.length; i++)
            {
                productStructs[i] = getProductStruct(giProductStructTypes[i]);
            }
        }
        return productStructs;
    }

    public SessionProductStruct[] getSessionProductStructs(GIProductQueryOperationsType pqo)
    {
        SessionProductStruct[] sessionProductStructs = new SessionProductStruct[0];
        if(pqo != null && pqo.getSessionProductStructSequence() != null)
        {
            sessionProductStructs = getSessionProductStructs(pqo.getSessionProductStructSequence());
        }
        return sessionProductStructs;
    }

    public SessionProductStruct[] getSessionProductStructs(GISessionProductStructSequence giSessionProductStructSequence)
    {
        SessionProductStruct[] sessionProductStructs = new SessionProductStruct[0];
        if(giSessionProductStructSequence != null && giSessionProductStructSequence.getSessionProductStructsLength()>0)
        {
            sessionProductStructs = getSessionProductStructs(giSessionProductStructSequence.getSessionProductStructs());
        }
        return sessionProductStructs;
    }

    public SessionProductStruct[] getSessionProductStructs(GISessionProductStructType[] giSessionProductStructTypes)
    {
        SessionProductStruct[] sessionProductStructs = new SessionProductStruct[0];
        if(giSessionProductStructTypes != null && giSessionProductStructTypes.length>0)
        {
            for (int i = 0; i < giSessionProductStructTypes.length; i++)
            {
                sessionProductStructs[i] = getSessionProductStruct(giSessionProductStructTypes[i]);
            }
        }
        return sessionProductStructs;
    }


    public SessionStrategyStruct[] getSessionStrategyStructs(GIProductQueryOperationsType pqo)
    {
        SessionStrategyStruct[] sessionStrategyStructs = new SessionStrategyStruct[0];
        if(pqo != null && pqo.getSessionStrategyStructSequence() != null)
        {
            sessionStrategyStructs = getSessionStrategyStructs(pqo.getSessionStrategyStructSequence());
        }
        return sessionStrategyStructs;
    }

    public SessionStrategyStruct[] getSessionStrategyStructs(GISessionStrategyStructSequence giSessionStrategyStructSequence)
    {
        SessionStrategyStruct[] sessionStrategyStructs = new SessionStrategyStruct[0];
        if(giSessionStrategyStructSequence != null && giSessionStrategyStructSequence.getSessionStrategyStructsLength() > 0)
        {
            sessionStrategyStructs = getSessionStrategyStructs(giSessionStrategyStructSequence.getSessionStrategyStructs());
        }
        return sessionStrategyStructs;
    }
    public SessionStrategyStruct[] getSessionStrategyStructs(GISessionStrategyStructType[] giSessionStrategyStructTypes)
    {
        SessionStrategyStruct[] sessionStrategyStructs = new SessionStrategyStruct[0];
        if(giSessionStrategyStructTypes != null && giSessionStrategyStructTypes.length>0)
        {
            sessionStrategyStructs = new SessionStrategyStruct[giSessionStrategyStructTypes.length];
            for (int i = 0; i < giSessionStrategyStructTypes.length; i++)
            {
                sessionStrategyStructs[i] = getSessionStrategyStruct(giSessionStrategyStructTypes[i]);
            }
        }
        return sessionStrategyStructs;
    }

    public StrategyStruct[] getStrategyStructs(GIProductQueryOperationsType pqo)
    {
        StrategyStruct[] strategyStructs = new StrategyStruct[0];
        if(pqo != null && pqo.getStrategyStructSequence() != null)
        {
            strategyStructs = getStrategyStructs(pqo.getStrategyStructSequence());
        }
        return strategyStructs;
    }

    public StrategyStruct[] getStrategyStructs(GIStrategyStructSequence giStrategyStructSequence)
    {
        StrategyStruct[] strategyStructs = new StrategyStruct[0];
        if(giStrategyStructSequence != null && giStrategyStructSequence.getStrategyStructsLength() > 0)
        {
            strategyStructs = getStrategyStructs(giStrategyStructSequence.getStrategyStructs());
        }
        return strategyStructs;
    }
    public StrategyStruct[] getStrategyStructs(GIStrategyStructType[] giStrategyStructTypes)
    {
        StrategyStruct[] strategyStructs = new StrategyStruct[0];
        if(giStrategyStructTypes != null && giStrategyStructTypes.length>0)
        {
            strategyStructs = new StrategyStruct[giStrategyStructTypes.length];
            for (int i = 0; i < giStrategyStructTypes.length; i++)
            {
                strategyStructs[i] = getStrategyStruct(giStrategyStructTypes[i]);
            }
        }
        return strategyStructs;
    }

    public SessionStrategyLegStruct[] getSessionStrategyLegStructs(GISessionStrategyLegSequence giSessionStrategyLegStructSequence)
    {
        SessionStrategyLegStruct[] sessionStrategyLegStructs = new SessionStrategyLegStruct[0];
        if(giSessionStrategyLegStructSequence != null && giSessionStrategyLegStructSequence.getSessionStrategyLegsLength()>0)
        {
            sessionStrategyLegStructs = getSessionStrategyLegStructs(giSessionStrategyLegStructSequence.getSessionStrategyLegs());
        }
        return sessionStrategyLegStructs;
    }

    public SessionStrategyLegStruct[] getSessionStrategyLegStructs(GISessionStrategyLegStructType[] giSessionStrategyLegStructTypes)
    {
        SessionStrategyLegStruct[] sessionStrategyLegStructs = new SessionStrategyLegStruct[0];
        if(giSessionStrategyLegStructTypes != null && giSessionStrategyLegStructTypes.length>0)
        {
            sessionStrategyLegStructs = new SessionStrategyLegStruct[giSessionStrategyLegStructTypes.length];
            for (int i = 0; i < giSessionStrategyLegStructTypes.length; i++)
            {
                sessionStrategyLegStructs[i] = getSessionStrategyLegStruct(giSessionStrategyLegStructTypes[i]);
            }
        }
        return sessionStrategyLegStructs;
    }

    public StrategyLegStruct[] getStrategyLegSructs(GIStrategyLegSequence giStrategyLegSequence)
    {
        StrategyLegStruct[] strategyLegStructs = new StrategyLegStruct[0];
        if(giStrategyLegSequence != null && giStrategyLegSequence.getStrategyLegs() != null)
        {
            strategyLegStructs = getStrategyLegSructs(giStrategyLegSequence.getStrategyLegs());
        }
        return strategyLegStructs;
    }

    public StrategyLegStruct[] getStrategyLegSructs(GIStrategyLegStructType[] giStrategyLegStructTypes)
    {
        StrategyLegStruct[] strategyLegStructs = new StrategyLegStruct[0];
        if(giStrategyLegStructTypes != null && giStrategyLegStructTypes.length>0)
        {
            strategyLegStructs = new StrategyLegStruct[giStrategyLegStructTypes.length];
            for (int i = 0; i < giStrategyLegStructTypes.length; i++)
            {
                strategyLegStructs[i] = getStrategyLegStruct(giStrategyLegStructTypes[i]);
            }
        }
        return strategyLegStructs;
    }


    public TradingSessionStruct getTradingSessionStruct(GITradingSessionStructType giTradingSessionStructType)
    {
        TradingSessionStruct tradingSessionStruct = new TradingSessionStruct();
        tradingSessionStruct.endTime = getTimeStruct(giTradingSessionStructType.getEndTime());
        tradingSessionStruct.sequenceNumber = giTradingSessionStructType.getSequenceNumber();
        tradingSessionStruct.sessionName = giTradingSessionStructType.getSessionName();
        tradingSessionStruct.startTime = getTimeStruct(giTradingSessionStructType.getStartTime());
        tradingSessionStruct.state = giTradingSessionStructType.getState();
        return tradingSessionStruct;
    }

    public ProductTypeStruct getProductTypeStruct(GIProductTypeStructType giProductTypeStruct)
    {
        ProductTypeStruct productTypeStruct = new ProductTypeStruct();
        productTypeStruct.createdTime = getDateTimeStruct(giProductTypeStruct.getCreatedTime());
        productTypeStruct.description = giProductTypeStruct.getDescription();
        productTypeStruct.lastModifiedTime  = getDateTimeStruct(giProductTypeStruct.getLastModifiedTime());
        productTypeStruct.name= giProductTypeStruct.getName();
        productTypeStruct.type = giProductTypeStruct.getType();
        return productTypeStruct;
    }

    public SessionClassStruct getSessionClassStruct(GISessionClassStructType giSessionClassStructType)
    {
        SessionClassStruct sessionClassStruct = new SessionClassStruct();
        sessionClassStruct.classState = giSessionClassStructType.getClassState();
        sessionClassStruct.classStateTransactionSequenceNumber = giSessionClassStructType.getClassStateTransactionSequenceNumber();
        sessionClassStruct.classStruct = getClassStruct(giSessionClassStructType.getClassStruct());
        sessionClassStruct.eligibleSessions = getEligibleSessions(giSessionClassStructType.getEligibleSessionSequence());
        sessionClassStruct.sessionName = giSessionClassStructType.getSessionName();
        sessionClassStruct.underlyingSessionName = giSessionClassStructType.getUnderlyingSessionName();
        return sessionClassStruct;
    }

    public ClassStruct getClassStruct(GIClassStructType giClassStructType)
    {
        ClassStruct classStruct = new ClassStruct();
        classStruct.activationDate = getDateStruct(giClassStructType.getActivationDate());
        classStruct.classKey = giClassStructType.getClassKey();
        classStruct.classSymbol = giClassStructType.getClassSymbol();
        classStruct.createdTime = getDateTimeStruct(giClassStructType.getCreatedTime());
        classStruct.epwFastMarketMultiplier = giClassStructType.getEpwFastMarketMultiplier();
        classStruct.epwValues = getEpwStructs(giClassStructType.getEpwValueSequence());
        classStruct.inactivationDate = getDateStruct(giClassStructType.getInactivationDate());
        classStruct.lastModifiedTime = getDateTimeStruct(giClassStructType.getLastModifiedTime());
        classStruct.listingState = giClassStructType.getListingState();
        classStruct.primaryExchange = giClassStructType.getPrimaryExchange();
        classStruct.productDescription = getProductDescriptionStruct(giClassStructType.getProductDescription());
        classStruct.productType = giClassStructType.getProductType();
        classStruct.reportingClasses = getReportingClasses(giClassStructType.getReportingClassesSequence());
        classStruct.testClass = giClassStructType.isTestClass();
        classStruct.underlyingProduct = getProductStruct(giClassStructType.getUnderlyingProduct());
        return classStruct;
    }

    public SessionStrategyStruct getSessionStrategyStruct(GISessionStrategyStructType giSessionStrategyStructType)
    {
        SessionStrategyStruct sessionStrategyStruct = new SessionStrategyStruct();
        sessionStrategyStruct.sessionProductStruct = getSessionProductStruct(giSessionStrategyStructType.getSessionProductStruct());
        sessionStrategyStruct.sessionStrategyLegs = getSessionStrategyLegStructs(giSessionStrategyStructType.getSessionStrategyLegSequence());
        sessionStrategyStruct.strategyType = giSessionStrategyStructType.getStrategyType();
        return sessionStrategyStruct;
    }

    public StrategyStruct getStrategyStruct(GIStrategyStructType giStrategyStructType)
    {
        StrategyStruct strategyStruct = new StrategyStruct();
        strategyStruct.product = getProductStruct(giStrategyStructType.getProductStruct());
        strategyStruct.strategyLegs = getStrategyLegSructs(giStrategyStructType.getStrategyLegSequence());
        strategyStruct.strategyType = giStrategyStructType.getStrategyType();
        return strategyStruct;
    }
    public StrategyLegStruct getStrategyLegStruct(GIStrategyLegStructType giStrategyLegStructType)
    {
        StrategyLegStruct strategyLegStruct = new StrategyLegStruct();
        strategyLegStruct.product = giStrategyLegStructType.getProduct();
        strategyLegStruct.ratioQuantity = giStrategyLegStructType.getRatioQuantity();
        strategyLegStruct.side = giStrategyLegStructType.getSide();
        return strategyLegStruct;
    }
    public SessionProductStruct getSessionProductStruct(GISessionProductStructType giSessionProductStructType)
    {
        SessionProductStruct sessionProductStruct = new SessionProductStruct();
        sessionProductStruct.productState = giSessionProductStructType.getProductState();
        sessionProductStruct.productStateTransactionSequenceNumber = giSessionProductStructType.getProductStateTransactionSequenceNumber();
        sessionProductStruct.productStruct = getProductStruct(giSessionProductStructType.getProductStruct());
        sessionProductStruct.sessionName = giSessionProductStructType.getSessionName();
        return sessionProductStruct;
    }

    public SessionStrategyLegStruct getSessionStrategyLegStruct(GISessionStrategyLegStructType giSessionStrategyLegStructType)
    {
        SessionStrategyLegStruct sessionStrategyLegStruct = new SessionStrategyLegStruct();
        sessionStrategyLegStruct.product = giSessionStrategyLegStructType.getProduct();
        sessionStrategyLegStruct.ratioQuantity = giSessionStrategyLegStructType.getRatioQuantity();
        sessionStrategyLegStruct.sessionName = giSessionStrategyLegStructType.getSessionName();
        sessionStrategyLegStruct.side = giSessionStrategyLegStructType.getSide();
        return sessionStrategyLegStruct;
    }

    public ReportingClassStruct getReportingClassStruct(GIReportingClassStructType giReportingClassStructType)
    {
        ReportingClassStruct reportingClassStruct = new ReportingClassStruct();
        reportingClassStruct.activationDate = getDateStruct(giReportingClassStructType.getActivationDate());
        reportingClassStruct.classKey = giReportingClassStructType.getClassKey();
        reportingClassStruct.contractSize = giReportingClassStructType.getContractSize();
        reportingClassStruct.createdTime = getDateTimeStruct(giReportingClassStructType.getCreatedTime());
        reportingClassStruct.inactivationDate = getDateStruct(giReportingClassStructType.getInactivationDate());
        reportingClassStruct.lastModifiedTime = getDateTimeStruct(giReportingClassStructType.getLastModifiedTime());
        reportingClassStruct.listingState = giReportingClassStructType.getListingState();
        reportingClassStruct.productClassKey = giReportingClassStructType.getProductClassKey();
        reportingClassStruct.productClassSymbol = giReportingClassStructType.getProductClassSymbol();
        reportingClassStruct.productType = giReportingClassStructType.getProductType();
        reportingClassStruct.reportingClassSymbol = giReportingClassStructType.getReportingClassSymbol();
        reportingClassStruct.transactionFeeCode = giReportingClassStructType.getTransactionFeeCode();
        return reportingClassStruct;
    }



    public DateStruct getDateStruct(GIDateStructType giDateStructType)
    {
        DateStruct dateStruct = new DateStruct();
        dateStruct.day = giDateStructType.getDate();
        dateStruct.month = giDateStructType.getMonth();
        dateStruct.year = giDateStructType.getYear();
        return dateStruct;
    }
    public TimeStruct getTimeStruct(GITimeStructType giTimeStructType)
    {
        TimeStruct timeStruct = new TimeStruct();
        timeStruct.fraction = giTimeStructType.getFraction();
        timeStruct.hour = giTimeStructType.getHour();
        timeStruct.minute = giTimeStructType.getMinute();
        timeStruct.second = giTimeStructType.getSecond();
        return timeStruct;
    }
    public DateTimeStruct getDateTimeStruct(GIDateTimeStructType giDateTimeStructType)
    {
        DateTimeStruct dateTimeStruct = new DateTimeStruct();
        dateTimeStruct.date = new DateStruct();
        dateTimeStruct.date.day = giDateTimeStructType.getDate();
        dateTimeStruct.date.month = giDateTimeStructType.getMonth();
        dateTimeStruct.date.year = giDateTimeStructType.getYear();
        dateTimeStruct.time = new TimeStruct();
        dateTimeStruct.time.fraction = giDateTimeStructType.getFraction();
        dateTimeStruct.time.hour = giDateTimeStructType.getHour();
        dateTimeStruct.time.minute = giDateTimeStructType.getMinute();
        dateTimeStruct.time.second = giDateTimeStructType.getSecond();
        return dateTimeStruct;
    }
    public ProductDescriptionStruct getProductDescriptionStruct(GIProductDescriptionStructType giProductDescriptionStructType)
    {
        ProductDescriptionStruct productDescriptionStruct = new ProductDescriptionStruct();
        productDescriptionStruct.baseDescriptionName = giProductDescriptionStructType.getBaseDescriptionName();
        productDescriptionStruct.maxStrikePrice = getPriceStruct(giProductDescriptionStructType.getMaxStrikePrice());
        productDescriptionStruct.minimumAbovePremiumFraction = getPriceStruct(giProductDescriptionStructType.getMinimumAbovePremiumFraction());
        productDescriptionStruct.minimumBelowPremiumFraction = getPriceStruct(giProductDescriptionStructType.getMinimumBelowPremiumFraction());
        productDescriptionStruct.minimumStrikePriceFraction = getPriceStruct(giProductDescriptionStructType.getMinimumStrikePriceFraction());
        productDescriptionStruct.name = giProductDescriptionStructType.getName();
        productDescriptionStruct.premiumBreakPoint = getPriceStruct(giProductDescriptionStructType.getPremiumBreakPoint());
        productDescriptionStruct.premiumPriceFormat = giProductDescriptionStructType.getPremiumPriceFormat();
        productDescriptionStruct.priceDisplayType = giProductDescriptionStructType.getPriceDisplayType();
        productDescriptionStruct.strikePriceFormat = giProductDescriptionStructType.getStrikePriceFormat();
        productDescriptionStruct.underlyingPriceFormat = giProductDescriptionStructType.getUnderlyingPriceFormat();
        return productDescriptionStruct;
    }
    public PriceStruct getPriceStruct(GIPriceStructType giPriceStructType)
    {
        PriceStruct priceStruct = new PriceStruct();
        priceStruct.whole = giPriceStructType.getWhole();
        priceStruct.fraction = giPriceStructType.getFraction();
        priceStruct.type = giPriceStructType.getType();
        return priceStruct;
    }
    public ProductStruct getProductStruct(GIProductStructType giProductStructType)
    {
        ProductStruct productStruct = new ProductStruct();
        productStruct.activationDate = getDateStruct(giProductStructType.getActivationDate());
        productStruct.companyName = giProductStructType.getCompanyName();
        productStruct.createdTime = getDateTimeStruct(giProductStructType.getCreatedTime());
        productStruct.description = giProductStructType.getDescription();
        productStruct.inactivationDate = getDateStruct(giProductStructType.getInactivationDate());
        productStruct.lastModifiedTime = getDateTimeStruct(giProductStructType.getLastModifiedTime());
        productStruct.listingState = giProductStructType.getListingState();
        productStruct.maturityDate = getDateStruct(giProductStructType.getMaturityDate());
        productStruct.opraMonthCode = giProductStructType.getOpraMonthCode();
        productStruct.opraPriceCode = giProductStructType.getOpraPriceCode();
        productStruct.productKeys = getProductKeysStruct(giProductStructType.getProductKeys());
        productStruct.productName = getProductNameStruct(giProductStructType.getProductName());
        productStruct.standardQuantity = giProductStructType.getStandardQuantity();
        productStruct.unitMeasure = giProductStructType.getUnitMeasure();
        return productStruct;
    }
    public ProductKeysStruct getProductKeysStruct(GIProductKeysStructType giProductKeysStructType)
    {
        ProductKeysStruct productKeysStruct = new ProductKeysStruct();
        productKeysStruct.classKey = giProductKeysStructType.getClassKey();
        productKeysStruct.productKey = giProductKeysStructType.getProductKey();
        productKeysStruct.productType = giProductKeysStructType.getProductType();
        productKeysStruct.reportingClass = giProductKeysStructType.getReportingClass();
        return productKeysStruct;
    }
    public ProductNameStruct getProductNameStruct(GIProductNameStructType giProductNameStructType)
    {
        ProductNameStruct productNameStruct = new ProductNameStruct();
        productNameStruct.exercisePrice = getPriceStruct(giProductNameStructType.getExercisePrice());
        productNameStruct.expirationDate = getDateStruct(giProductNameStructType.getExpirationDate());
        productNameStruct.optionType = giProductNameStructType.getOptionType();
        productNameStruct.productSymbol = giProductNameStructType.getProductSymbol();
        productNameStruct.reportingClass = giProductNameStructType.getReportingClassSymbol();
        return productNameStruct;
    }
    public EPWStruct[] getEpwStructs(GIEpwValuesSequence epwValuesSequence)
    {
        EPWStruct[] epwStructs = new EPWStruct[0];
        if(epwValuesSequence != null && epwValuesSequence.getEpwValuesLength()>0)
        {
            GIEpwValueType[] giEpwValueTypes = epwValuesSequence.getEpwValues();
            epwStructs = new EPWStruct[giEpwValueTypes.length];
            for (int i = 0; i < giEpwValueTypes.length; i++)
            {
                epwStructs[i] = getEpwStruct(giEpwValueTypes[i]);
            }
        }
        return epwStructs;
    }
    public EPWStruct getEpwStruct(GIEpwValueType giEpwValueType)
    {
        EPWStruct epwStruct = new EPWStruct();
        epwStruct.maximumAllowableSpread = giEpwValueType.getMaximumAllowableSpread();
        epwStruct.maximumBidRange = giEpwValueType.getMaximumBidRange();
        epwStruct.minimumBidRange = giEpwValueType.getMinimumBidRange();
        return epwStruct;
    }
    public ReportingClassStruct[] getReportingClasses(GIReportingClassSequence giReportingClassSequence)
    {
        ReportingClassStruct[] reportingClassStructs = new ReportingClassStruct[0];
        if(giReportingClassSequence != null && giReportingClassSequence.getReportingClassesLength()>0)
        {
            GIReportingClassStructType[] giReportingClassStructTypes = giReportingClassSequence.getReportingClasses();
            reportingClassStructs = new ReportingClassStruct[giReportingClassStructTypes.length];
            for (int i = 0; i < giReportingClassStructTypes.length; i++)
            {
                reportingClassStructs[i] = getReportingClassStruct(giReportingClassStructTypes[i]);
            }
        }
        return reportingClassStructs;
    }

    public String[] getEligibleSessions(GIEligibleSessionsSequence giEligibleSessionsSequence)
    {
        String[] eligibleSessions = new String[0];
        if(giEligibleSessionsSequence != null && giEligibleSessionsSequence.getEligibleSessionsLength() > 0)
        {
            eligibleSessions = giEligibleSessionsSequence.getEligibleSessions();
        }
        return eligibleSessions;
    }



}

