/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.order
 * User: torresl
 * Date: Dec 31, 2002 2:24:57 PM
 */
package com.cboe.presentation.order;

import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.FilledReport;
import com.cboe.interfaces.presentation.order.ContraParty;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.presentation.util.StringCache;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.StructBuilder;

class FilledReportImpl implements FilledReport
{
    protected CBOEId            tradeId;
    protected Short             fillReportType;
    protected ExchangeFirm      executingOrGiveUpFirm;
    protected String            userId;
    protected ExchangeAcronym   userAcronym;
    protected Integer           productKey;
    protected String            sessionName;
    protected Integer           tradedQuantity;
    protected Integer           leavesQuantity;
    protected Price             price;
    protected Character         side;
    protected String            orsId;
    protected String            executingBroker;
    protected ExchangeFirm      cmta;
    protected String            account;
    protected String            subaccount;
    protected ExchangeAcronym   originator;
    protected String            optionalData;
    protected String            userAssignedId;
    protected ContraParty[]     contraParties;
    protected ContraPartyStruct[] contraPartyStructs;
    protected DateTime          timeSent;
    protected Character         positionEffect;
    protected Integer           transactionSequenceNumber;
    protected ExtensionsHelper  extensionsHelper;

    private PriceStruct priceStruct;

    public FilledReportImpl(FilledReportStruct filledReportStruct)
    {
        initialize(filledReportStruct);
    }

    private void initialize(FilledReportStruct filledReportStruct)
    {
        tradeId = new CBOEIdImpl(filledReportStruct.tradeId);
        fillReportType = filledReportStruct.fillReportType;
        executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm(StringCache.get(filledReportStruct.executingOrGiveUpFirm.exchange), StringCache.get(filledReportStruct.executingOrGiveUpFirm.firmNumber));
        userId = StringCache.get(filledReportStruct.userId);
        userAcronym = ExchangeAcronymFactory.createExchangeAcronym(StringCache.get(filledReportStruct.userAcronym.exchange), StringCache.get(filledReportStruct.userAcronym.acronym));
        productKey = filledReportStruct.productKey;
        sessionName = StringCache.get(filledReportStruct.sessionName);
        tradedQuantity = filledReportStruct.tradedQuantity;
        leavesQuantity = filledReportStruct.leavesQuantity;
        price = DisplayPriceFactory.create(filledReportStruct.price);
        priceStruct = StructBuilder.clonePrice(filledReportStruct.price);
        side = new Character(filledReportStruct.side);
        orsId = StringCache.get(filledReportStruct.orsId);
        executingBroker = StringCache.get(filledReportStruct.executingBroker);
        cmta = ExchangeFirmFactory.createExchangeFirm(StringCache.get(filledReportStruct.cmta.exchange), StringCache.get(filledReportStruct.cmta.firmNumber));
        account = StringCache.get(filledReportStruct.account);
        subaccount = StringCache.get(filledReportStruct.subaccount);
        originator = ExchangeAcronymFactory.createExchangeAcronym(StringCache.get(filledReportStruct.originator.exchange), StringCache.get(filledReportStruct.originator.acronym));
        optionalData = StringCache.get(filledReportStruct.optionalData);
        userAssignedId = StringCache.get(filledReportStruct.userAssignedId);
        timeSent = new DateTimeImpl(filledReportStruct.timeSent);
        positionEffect = new Character(filledReportStruct.positionEffect);
        transactionSequenceNumber = filledReportStruct.transactionSequenceNumber;
        // keep copies of the ContraStructs; create the impls only when requested
        contraPartyStructs = new ContraPartyStruct[filledReportStruct.contraParties.length];
        for (int i = 0; i < filledReportStruct.contraParties.length; i++)
        {
            ExchangeAcronymStruct tmpEA = new ExchangeAcronymStruct(StringCache.get(filledReportStruct.contraParties[i].user.exchange), StringCache.get(filledReportStruct.contraParties[i].user.acronym));
            ExchangeFirmStruct tmpEF = new ExchangeFirmStruct(StringCache.get(filledReportStruct.contraParties[i].firm.exchange), StringCache.get(filledReportStruct.contraParties[i].firm.firmNumber));
            contraPartyStructs[i] = new ContraPartyStruct(tmpEA, tmpEF, filledReportStruct.contraParties[i].quantity);
        }

        setupExtensions(filledReportStruct.extensions);
    }

    /**
     * Gets the underlying struct
     * @return FilledReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public FilledReportStruct getStruct()
    {
        FilledReportStruct retVal = new FilledReportStruct();
        retVal.tradeId = getTradeId().getStruct();
        retVal.fillReportType = getFillReportType();
        retVal.executingOrGiveUpFirm = getExecutingOrGiveUpFirm().getExchangeFirmStruct();
        retVal.userId = getUserId();
        retVal.userAcronym = getUserAcronym().getExchangeAcronymStruct();
        retVal.productKey = getProductKey();
        retVal.sessionName = getSessionName();
        retVal.tradedQuantity = getTradedQuantity();
        retVal.leavesQuantity = getLeavesQuantity();
        retVal.price = priceStruct;
        retVal.side = getSide();
        retVal.orsId = getOrsId();
        retVal.executingBroker = getExecutingBroker();
        retVal.cmta = getCmta().getExchangeFirmStruct();
        retVal.account = getAccount();
        retVal.subaccount = getSubaccount();
        retVal.originator = getOriginator().getExchangeAcronymStruct();
        retVal.optionalData = getOptionalData();
        retVal.userAssignedId = getUserAssignedId();
        retVal.extensions = getExtensions();
        retVal.contraParties = contraPartyStructs;
        retVal.timeSent = getTimeSent().getDateTimeStruct();
        retVal.positionEffect = getPositionEffect();
        retVal.transactionSequenceNumber = getTransactionSequenceNumber();

        return retVal;
    }

    public CBOEId getTradeId()
    {
        return tradeId;
    }

    public Short getFillReportType()
    {
        return fillReportType;
    }

    public ExchangeFirm getExecutingOrGiveUpFirm()
    {
        return executingOrGiveUpFirm;
    }

    public String getUserId()
    {
        return userId;
    }

    public ExchangeAcronym getUserAcronym()
    {
        return userAcronym;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public Integer getTradedQuantity()
    {
        return tradedQuantity;
    }

    public Integer getLeavesQuantity()
    {
        return leavesQuantity;
    }

    public Price getPrice()
    {
        return price;
    }

    public Character getSide()
    {
        return side;
    }

    public String getOrsId()
    {
        return orsId;
    }

    public String getExecutingBroker()
    {
        return executingBroker;
    }

    public ExchangeFirm getCmta()
    {
        return cmta;
    }

    public String getAccount()
    {
        return account;
    }

    public String getSubaccount()
    {
        return subaccount;
    }

    public ExchangeAcronym getOriginator()
    {
        return originator;
    }

    public String getOptionalData()
    {
        return optionalData;
    }

    public String getUserAssignedId()
    {
        return userAssignedId;
    }

    public String getExtensions()
    {
        return getExtensionsHelper().toString();
    }

    public ContraParty[] getContraParties()
    {
        if(contraParties == null)
        {
            contraParties = new ContraParty[contraPartyStructs.length];
            for (int i = 0; i < contraPartyStructs.length; i++)
            {
                contraParties[i] = ContraPartyFactory.createContraParty(contraPartyStructs[i]);
            }
        }
        return contraParties;
    }

    public DateTime getTimeSent()
    {
        return timeSent;
    }

    public Character getPositionEffect()
    {
        return positionEffect;
    }

    public Integer getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    protected void setupExtensions(String extensions)
    {
        try
        {
            getExtensionsHelper().setExtensions(extensions);
        }
        catch (java.text.ParseException e)
        {
            GUILoggerHome.find().exception(e, e.getMessage());
        }
    }
    protected ExtensionsHelper getExtensionsHelper()
    {
        if(extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    public String getExtensionField(String extensionField)
    {
        return getExtensionsHelper().getValue(extensionField);
    }
}
