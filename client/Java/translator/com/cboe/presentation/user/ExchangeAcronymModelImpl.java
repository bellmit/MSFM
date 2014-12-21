//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronymModelImpl.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;

import com.cboe.interfaces.presentation.common.formatters.ExchangeAcronymFormatStrategy;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeAcronymModel;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.comparators.ExchangeAcronymComparator;
import com.cboe.presentation.common.formatters.FormatFactory;

public class ExchangeAcronymModelImpl extends AbstractMutableBusinessModel implements ExchangeAcronymModel
{
    public static final String PROPERTY_ACRONYM = "PROPERTY_ACRONYM";
    public static final String PROPERTY_EXCHANGE = "PROPERTY_EXCHANGE";

    private String acronym = "";
    private String exchange = "";
    private String renderString = null;

    private boolean neverBeenSaved;

    protected ExchangeAcronymModelImpl()
    {
        super(new ExchangeAcronymComparator());
        neverBeenSaved = false;
    }

    protected ExchangeAcronymModelImpl(String anExchange, String anAcronym)
    {
        super(new ExchangeAcronymComparator());
        setAcronym(anAcronym);
        setExchange(anExchange);
        neverBeenSaved = false;
    }

    protected ExchangeAcronymModelImpl(ExchangeAcronymStruct exchangeAcronymStruct)
    {
        this();
        if(exchangeAcronymStruct == null)
        {
            throw new IllegalArgumentException("ExchangeAcronymStruct may not be null.");
        }
        setAcronym(exchangeAcronymStruct.acronym);
        setExchange(exchangeAcronymStruct.exchange);
    }

    public String toString()
    {
        if(renderString == null)
        {
            renderString =
            FormatFactory.getExchangeAcronymFormatStrategy().format(this, ExchangeAcronymFormatStrategy.BRIEF);
        }
        return renderString;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof ExchangeAcronym))
        {
            return false;
        }

        final ExchangeAcronym exchangeAcronymModel = (ExchangeAcronym) o;

        if(getAcronym() != null ?
           !getAcronym().equals(exchangeAcronymModel.getAcronym()) :
           exchangeAcronymModel.getAcronym() != null)
        {
            return false;
        }
        if(getExchange() != null ?
           !getExchange().equals(exchangeAcronymModel.getExchange()) :
           exchangeAcronymModel.getExchange() != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (acronym != null ? acronym.hashCode() : 0);
        result = 29 * result + (exchange != null ? exchange.hashCode() : 0);
        return result;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        ExchangeAcronymModelImpl newExchangeAcronymModel = new ExchangeAcronymModelImpl(getExchange(), getAcronym());

        return newExchangeAcronymModel;
    }

    public void setNeverBeenSaved(boolean neverBeenSaved)
    {
        this.neverBeenSaved = neverBeenSaved;
    }

    public boolean isNeverBeenSaved()
    {
        return neverBeenSaved;
    }

    public void setAcronym(String acronym)
    {
        String oldValue = this.acronym;
        this.acronym = acronym;
        renderString = null;

        firePropertyChange(PROPERTY_ACRONYM, oldValue, acronym);
    }

    public void setExchange(String exchange)
    {
        String oldValue = this.exchange;
        this.exchange = exchange;
        renderString = null;

        firePropertyChange(PROPERTY_EXCHANGE, oldValue, exchange);

    }

    public String getAcronym()
    {
        return acronym;
    }

    public String getExchange()
    {
        return exchange;
    }

    public ExchangeAcronymStruct getExchangeAcronymStruct()
    {
        return new ExchangeAcronymStruct(getExchange(), getAcronym());
    }
}