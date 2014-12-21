package com.cboe.internalPresentation.product;

import com.cboe.idl.exchange.ExchangeStruct;
import com.cboe.interfaces.internalPresentation.product.Exchange;
import com.cboe.domain.util.ReflectiveStructBuilder;

import java.io.StringWriter;
import java.io.IOException;

public class ExchangeImpl implements Exchange
{
    private ExchangeStruct exchangeStruct;

    /**
     * Constructor
     * @param ExchangeStruct to represent
     */
    protected ExchangeImpl(ExchangeStruct exchangeStruct)
    {
        this();

        this.exchangeStruct = exchangeStruct;
    }

    /**
     *  Default constructor.
     */
    protected ExchangeImpl()
    {
        super();
/*
        if(formatter == null)
        {
            formatter = FormatFactory.getExchangeFormatStrategy();
        }
*/
    }

    public int getExchangeKey()
    {
        return this.exchangeStruct.exchangeKey;
    }

    public String getAcronym()
    {
        return this.exchangeStruct.acronym;
    }

    public String getName()
    {
        return this.exchangeStruct.name;
    }

    public int getMembershipKey()
    {
        return this.exchangeStruct.membershipKey;
    }

    public String getExchange()
    {
        return getAcronym();
    }

    public String getFullName()
    {
        return getName();
    }

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public ExchangeStruct getExchangeStruct()
    {
        return this.exchangeStruct;
    }

    public Object getKey()
    {
        return new Integer(getExchangeKey());
    }

    public Object clone() throws CloneNotSupportedException
    {
        return new ExchangeImpl(new ExchangeStruct(getExchangeKey(), getMembershipKey(), new String(getAcronym()), new String(getName())));
    }

    public boolean equals(Object obj)
    {
        boolean retval = false;
        if (obj instanceof ExchangeImpl)
        {
            ExchangeStruct exStruct = ((ExchangeImpl) obj).getExchangeStruct();
            if (this.exchangeStruct != null && exStruct != null)
            {
                if (this.exchangeStruct.acronym.equals(exStruct.acronym)
                    && this.exchangeStruct.name.equals(exStruct.name)
                    && this.exchangeStruct.exchangeKey == exStruct.exchangeKey
                    && this.exchangeStruct.membershipKey == exStruct.membershipKey)
                {
                    retval = true;
                }
            }
            else if (this.exchangeStruct == null && exStruct == null)
            {
                retval = true;
            }
        }
        return retval;
    }

    /**
     * Returns a String representation of this ReportingClass
     *           toString() is used by ExchangeListCellRenderer, so changing toString() changes
     *                      any combo box or list component that uses this renderer.
     */
    public String toString()
    {
        return this.getAcronym();
    }
        //StringWriter writer = new StringWriter();
        //try
        //{
        //    ReflectiveStructBuilder.printStruct(exchangeStruct, "", writer);
        //}
        //catch (IOException e)
        //{
        //    e.printStackTrace();
        //}
        //return this.getName() + ": ExchangeImpl{" +
        //       "exchangeStruct = " +
        //       writer.toString() +
        //       "}";
}
