//
// ------------------------------------------------------------------------
// FILE: XmlProductBindingFacade.java
//
// PACKAGE: com.cboe.client.xml
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.client.xml;

import com.cboe.client.xml.bind.*;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;

import javax.xml.bind.JAXBException;

public class XmlProductBindingFacade
{
    private static XmlProductBindingFacade ourInstance;

    public synchronized static XmlProductBindingFacade getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new XmlProductBindingFacade();
        }
        return ourInstance;
    }

    private XmlProductBindingFacade()
    {
    }
    /**
     * CONVERSION TO XML
     */

    public GITradingSessionStructSequence getTradingSessionStructSequence(TradingSessionStruct[] tradingSessionStructs)
    throws IllegalStateException
    {
        try
        {
            GITradingSessionStructSequence giTradingSessionStructSequence = XmlBindingFacade.getInstance().getObjectFactory().createGITradingSessionStructSequence();
            giTradingSessionStructSequence.setTradingSessionStructs(getTradingSessionStructTypes(tradingSessionStructs));
            return giTradingSessionStructSequence;
        }
        catch (javax.xml.bind.JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GITradingSessionStructType[] getTradingSessionStructTypes(TradingSessionStruct[] tradingSessionStructs)
           throws IllegalStateException
    {
        return null;
    }
    public GITradingSessionStructType getTradingSessionStructType(TradingSessionStruct tradingSessionStruct)
           throws IllegalStateException
    {
        try
        {
            GITradingSessionStructType giTradingSessionStructType = XmlBindingFacade.getInstance().getObjectFactory().createGITradingSessionStructType();
            giTradingSessionStructType.setEndTime(getGITimeStructType(tradingSessionStruct.endTime));
            giTradingSessionStructType.setSequenceNumber(tradingSessionStruct.sequenceNumber);
            giTradingSessionStructType.setSessionName(tradingSessionStruct.sessionName);
            giTradingSessionStructType.setStartTime(getGITimeStructType(tradingSessionStruct.startTime));
            giTradingSessionStructType.setState(tradingSessionStruct.state);
            return giTradingSessionStructType;
        }
        catch (javax.xml.bind.JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GITimeStructType getGITimeStructType(TimeStruct timeStruct)
            throws IllegalStateException
    {
        try
        {
            GITimeStructType giTimeStructType = XmlBindingFacade.getInstance().getObjectFactory().createGITimeStructType();
            giTimeStructType.setFraction(timeStruct.fraction);
            giTimeStructType.setHour(timeStruct.hour);
            giTimeStructType.setMinute(timeStruct.minute);
            giTimeStructType.setSecond(timeStruct.second);
            return giTimeStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GIDateStructType getGIDateStructType(DateStruct dateStruct)
            throws IllegalStateException
    {
        try
        {
            GIDateStructType giDateStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIDateStructType();
            giDateStructType.setDate(dateStruct.day);
            giDateStructType.setMonth(dateStruct.month);
            giDateStructType.setYear(dateStruct.year);
            return giDateStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIDateTimeStructType getGIDateTimeStructType(DateTimeStruct dateTimeStruct)
            throws IllegalStateException
    {
        try
        {
            GIDateTimeStructType giDateTimeStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIDateTimeStructType();
            giDateTimeStructType.setDate(dateTimeStruct.date.day);
            giDateTimeStructType.setMonth(dateTimeStruct.date.month);
            giDateTimeStructType.setYear(dateTimeStruct.date.year);
            giDateTimeStructType.setFraction(dateTimeStruct.time.fraction);
            giDateTimeStructType.setHour(dateTimeStruct.time.hour);
            giDateTimeStructType.setMinute(dateTimeStruct.time.minute);
            giDateTimeStructType.setSecond(dateTimeStruct.time.second);
            return giDateTimeStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GIPriceStructType getGIPriceStructType(PriceStruct priceStruct)
            throws IllegalStateException
    {
        try
        {
            GIPriceStructType giPriceStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIPriceStructType();
            giPriceStructType.setType(priceStruct.type);
            giPriceStructType.setWhole(priceStruct.whole);
            giPriceStructType.setFraction(priceStruct.fraction);
            return giPriceStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIProductKeysStructType getGIProductKeysStructType(ProductKeysStruct productKeysStruct)
            throws IllegalStateException
    {
        try
        {
            GIProductKeysStructType giProductKeysStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIProductKeysStructType();
            giProductKeysStructType.setClassKey(productKeysStruct.classKey);
            giProductKeysStructType.setProductKey(productKeysStruct.productKey);
            giProductKeysStructType.setProductType(productKeysStruct.productType);
            giProductKeysStructType.setReportingClass(productKeysStruct.reportingClass);
            return giProductKeysStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GIProductNameStructType getGIProductNameStructType(ProductNameStruct productNameStruct)
            throws IllegalStateException
    {
        try
        {
            GIProductNameStructType giProductNameStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIProductNameStructType();
            giProductNameStructType.setExercisePrice(getGIPriceStructType(productNameStruct.exercisePrice));
            giProductNameStructType.setExpirationDate(getGIDateStructType(productNameStruct.expirationDate));
            giProductNameStructType.setOptionType(productNameStruct.optionType);
            giProductNameStructType.setProductSymbol(productNameStruct.productSymbol);
            giProductNameStructType.setReportingClassSymbol(productNameStruct.reportingClass);
            return giProductNameStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIProductStructType getGIProductStructType(ProductStruct productStruct)
            throws IllegalStateException
    {
        try
        {
            GIProductStructType giProductStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIProductStructType();
            giProductStructType.setActivationDate(getGIDateStructType(productStruct.activationDate));
            giProductStructType.setCompanyName(productStruct.companyName);
            giProductStructType.setCreatedTime(getGIDateTimeStructType(productStruct.createdTime));
            giProductStructType.setDescription(productStruct.description);
            giProductStructType.setInactivationDate(getGIDateStructType(productStruct.inactivationDate));
            giProductStructType.setLastModifiedTime(getGIDateTimeStructType(productStruct.lastModifiedTime));
            giProductStructType.setListingState(productStruct.listingState);
            giProductStructType.setMaturityDate(getGIDateStructType(productStruct.maturityDate));
            giProductStructType.setOpraMonthCode(productStruct.opraMonthCode);
            giProductStructType.setOpraPriceCode(productStruct.opraPriceCode);
            giProductStructType.setProductKeys(getGIProductKeysStructType(productStruct.productKeys));
            giProductStructType.setProductName(getGIProductNameStructType(productStruct.productName));
            giProductStructType.setStandardQuantity(productStruct.standardQuantity);
            giProductStructType.setUnitMeasure(productStruct.unitMeasure);
            return giProductStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GISessionProductStructType getGISessionProductStructType(SessionProductStruct sessionProductStruct)
            throws IllegalStateException
    {
        try
        {
            GISessionProductStructType giSessionProductStructType = XmlBindingFacade.getInstance().getObjectFactory().createGISessionProductStructType();
            giSessionProductStructType.setProductState(sessionProductStruct.productState);
            giSessionProductStructType.setProductStateTransactionSequenceNumber(sessionProductStruct.productStateTransactionSequenceNumber);
            giSessionProductStructType.setProductStruct(getGIProductStructType(sessionProductStruct.productStruct));
            giSessionProductStructType.setSessionName(sessionProductStruct.sessionName);
            return giSessionProductStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIClassStructType getGIClassStructType(ClassStruct classStruct)
            throws IllegalStateException
    {
        try
        {
            GIClassStructType giClassStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIClassStructType();
            giClassStructType.setActivationDate(getGIDateStructType(classStruct.activationDate));
            giClassStructType.setClassKey(classStruct.classKey);
            giClassStructType.setClassSymbol(classStruct.classSymbol);
            giClassStructType.setCreatedTime(getGIDateTimeStructType(classStruct.createdTime));
            giClassStructType.setEpwFastMarketMultiplier(classStruct.epwFastMarketMultiplier);
            giClassStructType.setEpwValueSequence(getGIEpwValuesSequence(classStruct.epwValues));
            giClassStructType.setLastModifiedTime(getGIDateTimeStructType(classStruct.lastModifiedTime));
            giClassStructType.setListingState(classStruct.listingState);
            giClassStructType.setInactivationDate(getGIDateStructType(classStruct.inactivationDate));
            giClassStructType.setPrimaryExchange(classStruct.primaryExchange);
            giClassStructType.setProductDescription(getGIProductDescriptionStructType(classStruct.productDescription));
            giClassStructType.setProductType(classStruct.productType);
            giClassStructType.setReportingClassesSequence(getGIReportingClassSequence(classStruct.reportingClasses));
            giClassStructType.setTestClass(classStruct.testClass);
            giClassStructType.setUnderlyingProduct(getGIProductStructType(classStruct.underlyingProduct));
            return giClassStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIReportingClassStructType getGIReportingClassStructType(ReportingClassStruct reportingClassStruct)
            throws IllegalStateException
    {
        try
        {
            GIReportingClassStructType giReportingClassStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIReportingClassStructType();
            giReportingClassStructType.setActivationDate(getGIDateStructType(reportingClassStruct.activationDate));
            giReportingClassStructType.setClassKey(reportingClassStruct.classKey);
            giReportingClassStructType.setContractSize(reportingClassStruct.contractSize);
            giReportingClassStructType.setCreatedTime(getGIDateTimeStructType(reportingClassStruct.createdTime));
            giReportingClassStructType.setInactivationDate(getGIDateStructType(reportingClassStruct.inactivationDate));
            giReportingClassStructType.setLastModifiedTime(getGIDateTimeStructType(reportingClassStruct.lastModifiedTime));
            giReportingClassStructType.setListingState(reportingClassStruct.listingState);
            giReportingClassStructType.setProductClassKey(reportingClassStruct.productClassKey);
            giReportingClassStructType.setProductClassSymbol(reportingClassStruct.productClassSymbol);
            giReportingClassStructType.setProductType(reportingClassStruct.productType);
            giReportingClassStructType.setReportingClassSymbol(reportingClassStruct.reportingClassSymbol);
            giReportingClassStructType.setTransactionFeeCode(reportingClassStruct.transactionFeeCode);
            return giReportingClassStructType;

        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIReportingClassSequence getGIReportingClassSequence(ReportingClassStruct[] reportingClassStructs)
            throws IllegalStateException
    {
        try
        {
            GIReportingClassSequence giReportingClassSequence = XmlBindingFacade.getInstance().getObjectFactory().createGIReportingClassSequence();
            if(reportingClassStructs != null && reportingClassStructs.length>0)
            {
                GIReportingClassStructType[] giReportingClassStructTypes = new GIReportingClassStructType[reportingClassStructs.length];
                for (int i = 0; i < reportingClassStructs.length; i++)
                {
                    giReportingClassStructTypes[i] = getGIReportingClassStructType(reportingClassStructs[i]);
                }
                giReportingClassSequence.setReportingClasses(giReportingClassStructTypes);
            }
            return giReportingClassSequence;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIEpwValueType getGIEpwValueType(EPWStruct epwStruct)
            throws IllegalStateException
    {
        try
        {
            GIEpwValueType giEpwValueType = XmlBindingFacade.getInstance().getObjectFactory().createGIEpwValueType();
            giEpwValueType.setMaximumAllowableSpread(epwStruct.maximumAllowableSpread);
            giEpwValueType.setMaximumBidRange(epwStruct.maximumBidRange);
            giEpwValueType.setMinimumBidRange(epwStruct.minimumBidRange);
            return giEpwValueType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GIEpwValuesSequence getGIEpwValuesSequence(EPWStruct[] epwStructs)
            throws IllegalStateException
    {
        try
        {
            GIEpwValuesSequence giEpwValuesSequence = XmlBindingFacade.getInstance().getObjectFactory().createGIEpwValuesSequence();
            if(epwStructs != null && epwStructs.length>0)
            {
                GIEpwValueType[] giEpwValueTypes = new GIEpwValueType[epwStructs.length];
                for (int i = 0; i < epwStructs.length; i++)
                {
                    giEpwValueTypes[i] = getGIEpwValueType(epwStructs[i]);
                }
                giEpwValuesSequence.setEpwValues(giEpwValueTypes);
            }
            return giEpwValuesSequence;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIProductDescriptionStructType getGIProductDescriptionStructType(ProductDescriptionStruct productDescriptionStruct)
            throws IllegalStateException
    {
        try
        {
            GIProductDescriptionStructType giProductDescriptionStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIProductDescriptionStructType();
            giProductDescriptionStructType.setBaseDescriptionName(productDescriptionStruct.baseDescriptionName);
            giProductDescriptionStructType.setMaxStrikePrice(getGIPriceStructType(productDescriptionStruct.maxStrikePrice));
            giProductDescriptionStructType.setMinimumAbovePremiumFraction(getGIPriceStructType(productDescriptionStruct.minimumAbovePremiumFraction));
            giProductDescriptionStructType.setMinimumBelowPremiumFraction(getGIPriceStructType(productDescriptionStruct.minimumBelowPremiumFraction));
            giProductDescriptionStructType.setMinimumStrikePriceFraction(getGIPriceStructType(productDescriptionStruct.minimumStrikePriceFraction));
            giProductDescriptionStructType.setName(productDescriptionStruct.name);
            giProductDescriptionStructType.setPremiumBreakPoint(getGIPriceStructType(productDescriptionStruct.premiumBreakPoint));
            giProductDescriptionStructType.setPremiumPriceFormat(productDescriptionStruct.premiumPriceFormat);
            giProductDescriptionStructType.setPriceDisplayType(productDescriptionStruct.priceDisplayType);
            giProductDescriptionStructType.setStrikePriceFormat(productDescriptionStruct.strikePriceFormat);
            giProductDescriptionStructType.setUnderlyingPriceFormat(productDescriptionStruct.underlyingPriceFormat);
            return giProductDescriptionStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    public GIProductTypeStructType getGIProductTypeStructType(ProductTypeStruct productTypeStruct)
            throws IllegalStateException
    {
        try
        {
            GIProductTypeStructType giProductTypeStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIProductTypeStructType();
            giProductTypeStructType.setCreatedTime(getGIDateTimeStructType(productTypeStruct.createdTime));
            giProductTypeStructType.setDescription(productTypeStruct.description);
            giProductTypeStructType.setLastModifiedTime(getGIDateTimeStructType(productTypeStruct.lastModifiedTime));
            giProductTypeStructType.setName(productTypeStruct.name);
            giProductTypeStructType.setType(productTypeStruct.type);
            return giProductTypeStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GISessionClassStructType getGISessionClassStructType(SessionClassStruct sessionClassStruct)
            throws IllegalStateException
    {
        try
        {
            GISessionClassStructType giSessionClassStructType = XmlBindingFacade.getInstance().getObjectFactory().createGISessionClassStructType();
            giSessionClassStructType.setClassState(sessionClassStruct.classState);
            giSessionClassStructType.setClassStateTransactionSequenceNumber(sessionClassStruct.classStateTransactionSequenceNumber);
            giSessionClassStructType.setClassStruct(getGIClassStructType(sessionClassStruct.classStruct));
            giSessionClassStructType.setSessionName(sessionClassStruct.sessionName);
            giSessionClassStructType.setUnderlyingSessionName(sessionClassStruct.underlyingSessionName);
            giSessionClassStructType.setEligibleSessionSequence(getGIEligibleSessionsSequence(sessionClassStruct.eligibleSessions));
            return giSessionClassStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }

    public GIEligibleSessionsSequence getGIEligibleSessionsSequence(String[] eligibleSessions)
    {
        try
        {
            GIEligibleSessionsSequence giEligibleSessionsSequence = XmlBindingFacade.getInstance().getObjectFactory().createGIEligibleSessionsSequence();
            giEligibleSessionsSequence.setEligibleSessions(eligibleSessions);
            return giEligibleSessionsSequence;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }

    }

    public GISessionStrategyStructType getGISessionStrategyStructType(SessionStrategyStruct sessionStrategyStruct)
    {
        try
        {
            GISessionStrategyStructType giSessionStrategyStructType = XmlBindingFacade.getInstance().getObjectFactory().createGISessionStrategyStructType();
            giSessionStrategyStructType.setSessionProductStruct(getGISessionProductStructType(sessionStrategyStruct.sessionProductStruct));
            giSessionStrategyStructType.setSessionStrategyLegSequence(getGISessionStrategyLegSequence(sessionStrategyStruct.sessionStrategyLegs));
            giSessionStrategyStructType.setStrategyType(sessionStrategyStruct.strategyType);
            return giSessionStrategyStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    
    public GISessionStrategyLegSequence getGISessionStrategyLegSequence(SessionStrategyLegStruct[] sessionStrategyLegStructs)
    {
        try
        {
            GISessionStrategyLegSequence giSessionStrategyLegSequence = XmlBindingFacade.getInstance().getObjectFactory().createGISessionStrategyLegSequence();
            if(sessionStrategyLegStructs != null && sessionStrategyLegStructs.length>0)
            {
                GISessionStrategyLegStructType[] giSessionStrategyLegStructTypes = new GISessionStrategyLegStructType[sessionStrategyLegStructs.length];
                for(int i = 0; i < sessionStrategyLegStructs.length; i++)
                {
                    giSessionStrategyLegStructTypes[i] = getGISessionStrategyLegStructType(sessionStrategyLegStructs[i]);
                }
                giSessionStrategyLegSequence.setSessionStrategyLegs(giSessionStrategyLegStructTypes);
            }
            return giSessionStrategyLegSequence;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    
    public GISessionStrategyLegStructType getGISessionStrategyLegStructType(SessionStrategyLegStruct sessionStrategyLeg)
    {
        try
        {
            GISessionStrategyLegStructType giSessionStrategyLegStructType = XmlBindingFacade.getInstance().getObjectFactory().createGISessionStrategyLegStructType();
            giSessionStrategyLegStructType.setProduct(sessionStrategyLeg.product);
            giSessionStrategyLegStructType.setRatioQuantity(sessionStrategyLeg.ratioQuantity);
            giSessionStrategyLegStructType.setSessionName(sessionStrategyLeg.sessionName);
            giSessionStrategyLegStructType.setSide(sessionStrategyLeg.side);
            return giSessionStrategyLegStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    
    public GIStrategyStructType getGIStrategyStructType(StrategyStruct strategyStruct)
    {
        try
        {
            GIStrategyStructType giStrategyStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIStrategyStructType();
            giStrategyStructType.setProductStruct(getGIProductStructType(strategyStruct.product));
            giStrategyStructType.setStrategyLegSequence(getGIStrategyLegSequence(strategyStruct.strategyLegs));
            giStrategyStructType.setStrategyType(strategyStruct.strategyType);
            return giStrategyStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    
    public GIStrategyLegSequence getGIStrategyLegSequence(StrategyLegStruct[] strategyLegStructs)
    {
        try
        {
            GIStrategyLegSequence giStrategyLegSequence = XmlBindingFacade.getInstance().getObjectFactory().createGIStrategyLegSequence();
            if(strategyLegStructs != null && strategyLegStructs.length>0)
            {
                GIStrategyLegStructType[] giStrategyLegStructTypes = new GIStrategyLegStructType[strategyLegStructs.length];
                for(int i = 0; i < strategyLegStructs.length; i++)
                {
                    giStrategyLegStructTypes[i] = getGIStrategyLegStructType(strategyLegStructs[i]);
                }
                giStrategyLegSequence.setStrategyLegs(giStrategyLegStructTypes);
            }
            return giStrategyLegSequence;
            
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
    
    public GIStrategyLegStructType getGIStrategyLegStructType(StrategyLegStruct strategyLegStruct)
    {
        try
        {
            GIStrategyLegStructType strategyLegStructType = XmlBindingFacade.getInstance().getObjectFactory().createGIStrategyLegStructType();
            strategyLegStructType.setProduct(strategyLegStruct.product);
            strategyLegStructType.setRatioQuantity(strategyLegStruct.ratioQuantity);
            strategyLegStructType.setSide(strategyLegStruct.side);
            return strategyLegStructType;
        }
        catch (JAXBException e)
        {
            IllegalStateException ise = new IllegalStateException(e.getMessage());
            ise.initCause(e);
            throw ise;
        }
    }
}

