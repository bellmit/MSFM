/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.user;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.interfaces.presentation.user.ExchangeFirmModel;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.comparators.ExchangeFirmComparator;

final public class ExchangeFirmModelNullImpl extends AbstractMutableBusinessModel implements ExchangeFirmModel
{
    private final static ExchangeFirmComparator comparator = new ExchangeFirmComparator();

    private final static String firm = "";
    private final static String exchange = "";

    private static String renderString = "NO_FIRM_DEFINED";
    private final ExchangeFirmStruct exchangeFirmStruct;

    protected ExchangeFirmModelNullImpl()
    {
        super();
        exchangeFirmStruct = new ExchangeFirmStruct(exchange, firm);
    }

    protected ExchangeFirmModelNullImpl(String pRenderString){
        this();
        renderString = pRenderString;
    }

    public void setFirm(String aFirm)
    {
        throw new IllegalArgumentException("EchangeFirmModelNullImpl does not allow setting the firm");
    }

    public void setExchange(String anExchange)
    {
        throw new IllegalArgumentException("EchangeFirmModelNullImpl does not allow setting the exchange");
    }
    public String getFirm()
    {
        return firm;
    }

    public String getExchange()
    {
        return exchange;
    }

    public ExchangeFirmStruct getExchangeFirmStruct()
    {
        return exchangeFirmStruct;
    }

    public String toString()
    {

        return renderString;
    }

    public boolean equals(Object o)
    {
        boolean result = false;

        if(this == o)
        {
            result = true;
        }
        else if(o == null)
        {
            result = false;
        }
        else if(o instanceof ExchangeFirmModelNullImpl)
        {
            result = true;
        }
        return result;
    }

    /**
     *  Implements Cloneable
     *
     *@return                                 Description of the Returned Value
     *@exception  CloneNotSupportedException  Description of Exception
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new ExchangeFirmModelNullImpl();
    }

    /**
     * implement SortedListElement interface
     */
    public Object getKey()
    {
        return super.getKey();// this.toString();
    }

    /**
     * implement SortedListElement interface
     */
    public int compareTo(Object obj)
    {
        return comparator.compare(this, obj);
    }

}
