/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.user;

import junit.framework.*;

import com.cboe.presentation.common.comparators.ExchangeFirmComparator;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeFirmModel;
import com.cboe.interfaces.presentation.common.formatters.ExchangeFirmFormatStrategy;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.FormatFactory;

/**
 *  Description of the Class
 *
 *@author     Nick DePasquale
 *@created    January 31, 2002
 */
public class ExchangeFirmModelImpl extends AbstractMutableBusinessModel implements ExchangeFirmModel
{
    static ExchangeFirmComparator comparator = new ExchangeFirmComparator();
    String renderString = "";

    // Warning use setters to change values!!!
    private String firm = "";
    private String exchange = "";
    /**
     *  Description of the Field
     */
    public final static String PROPERTY_EXCHANGE = "PROPERTY_EXCHANGE";
    /**
     *  Description of the Field
     */
    public final static String PROPERTY_FIRM = "PROPERTY_FIRM";

    /**
     *  Constructor for the ExchangeFirmModelImpl object
     */
    protected ExchangeFirmModelImpl()
    {
    }

    /**
     *  Constructor for the ExchangeFirmModelImpl object
     *
     *@param  anExchange  Description of Parameter
     *@param  aFirm       Description of Parameter
     */
    protected ExchangeFirmModelImpl(String anExchange, String aFirm)
    {
        super();
        setFirm(aFirm);
        setExchange(anExchange);

    }

    /**
     *  Constructor for the ExchangeFirmModelImpl object
     *
     *@param  anExchangeFirmStruct  Description of Parameter
     */
    protected ExchangeFirmModelImpl(ExchangeFirmStruct anExchangeFirmStruct)
    {
        this(anExchangeFirmStruct == null ? "" : anExchangeFirmStruct.exchange, anExchangeFirmStruct == null ? "" : anExchangeFirmStruct.firmNumber);

    }

    /**
     *  Sets the Firm attribute of the ExchangeFirmModelImpl object
     *
     *@param  aFirm  The new Firm value
     */
    public void setFirm(String aFirm)
    {
        String old = this.firm;
        this.firm = aFirm;
        this.renderString = "";
        firePropertyChange(PROPERTY_FIRM, old, this.firm);

    }


    /**
     *  Sets the Exchange attribute of the ExchangeFirmModelImpl object
     *
     *@param  anExchange  The new Exchange value
     */
    public void setExchange(String anExchange)
    {
        String old = this.exchange;
        if(anExchange == null)
            this.exchange = "";
        else
            this.exchange = anExchange;
        this.renderString = "";
        firePropertyChange(PROPERTY_EXCHANGE, old, this.exchange);
    }

    /**
     *  Gets the Firm attribute of the ExchangeFirmModelImpl object
     *
     *@return    The Firm value
     */
    public String getFirm()
    {
        return firm;
    }

    /**
     *  Gets the Exchange attribute of the ExchangeFirmModelImpl object
     *
     *@return    The Exchange value
     */
    public String getExchange()
    {
        return exchange;
    }

    /**
     *  Gets the ExchangeFirmStruct attribute of the ExchangeFirmModelImpl
     *  object
     *
     *@return    The ExchangeFirmStruct value
     */
    public ExchangeFirmStruct getExchangeFirmStruct()
    {
        return new ExchangeFirmStruct(getExchange(), getFirm());
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Returned Value
     */
    public String toString()
    {
        if(renderString == "")
        {
            renderString = FormatFactory.getExchangeFirmFormatStrategy().format(this, ExchangeFirmFormatStrategy.BRIEF);
        }
        return renderString;
    }

    /**
     *  Description of the Method
     *
     *@param  o  Description of Parameter
     *@return    Description of the Returned Value
     */
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
        else if(o instanceof ExchangeFirmModel)
        {
            ExchangeFirmModel aExchangeFirmModel = (ExchangeFirmModel)o;
//System.out.println("*****************************************\nExchangeFirmModelImpl.equals() -- this.exchange = '"+this.getExchange()+"'\n*****************************************");
            if(this.getExchange().equals(aExchangeFirmModel.getExchange()))
            {
                if(this.getFirm().equals(aExchangeFirmModel.getFirm()))
                {
                    result = true;
                }
            }

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
        ExchangeFirmModelImpl aNewExchangeFirmModel = new ExchangeFirmModelImpl(this.getExchange(), this.getFirm());

        return aNewExchangeFirmModel;
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Returned Value
     */
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     *  Remember to add the import import junit.framework.*; To run - java
     *  junit.swingui.TestRunner and pass [package.SomeClass]$TestImpl as a
     *  parameter You will have access to all privates in this class
     *
     *@author     Nick DePasquale
     *@created    October 25, 2000
     */
    public static class TestImpl extends TestCase
    {

        private ExchangeFirmModelImpl exchangeFirm;
        private ExchangeFirmStruct defaultExchangeFirmStruct;
        private ExchangeFirmModelImpl differentExchangeFirm;
        private String defaultExchange = "CBOE";
        private String defaultFirm = "FFF";

        /**
         *  Constructor for the TestImpl object
         *
         *@param  name  Name of the test
         */
        public TestImpl(String name)
        {
            super(name);
        }

        /**
         *  A sample unit test for JUnit
         */
        public void testExchangeAcronymCreation()
        {
            assertTrue("Testing ExchangeFirm wrapper's Exchange", getBaseExchangeFirmModel().getExchange().equals(defaultExchange));
            assertTrue("Testing ExchangeFirm wrapper's Acronym", getBaseExchangeFirmModel().getFirm().equals(defaultFirm));

            try
            {
                assertTrue("Testing ExchangeFirm wrapper's Clone and Equals", getBaseExchangeFirmModel().equals(getBaseExchangeFirmModel().clone()));
            }
            catch(CloneNotSupportedException e)
            {
                fail("Clone not supported: " + e.toString());
            }

            try
            {
                differentExchangeFirm = (ExchangeFirmModelImpl)getBaseExchangeFirmModel().clone();
            }
            catch(CloneNotSupportedException e)
            {
                fail("Clone not supported: " + e.toString());
            }

            differentExchangeFirm.firm = "GGG";

            assertTrue("Testing ExchangeFirm wrapper's Equals - different firm", getBaseExchangeFirmModel().equals(differentExchangeFirm) == false);

            differentExchangeFirm.firm = defaultFirm;
            differentExchangeFirm.exchange = "DDDD";

            assertTrue("Testing ExchangeFirm wrapper's Equals - different exchange", getBaseExchangeFirmModel().equals(differentExchangeFirm) == false);

        }

        /**
         *  The JUnit setup method
         */
        protected void setUp()
        {

        }

        /**
         *  Gets the BaseExchangeFirmModel attribute of the TestImpl object
         *
         *@return    The BaseExchangeFirmModel value
         */
        private ExchangeFirm getBaseExchangeFirmModel()
        {
            if(exchangeFirm == null)
            {
                exchangeFirm = new ExchangeFirmModelImpl(defaultExchange, defaultFirm);
            }
            return exchangeFirm;
        }

        /**
         *  A unit test suite for JUnit This constructor creates a suite with
         *  all the methods starting with "test" that take no arguments.
         *
         *@return    The test suite
         */
        public static Test suite()
        {
            //This constructor creates a suite with all the methods starting with "test" that take no arguments.
            return new TestSuite(TestImpl.class);
        }

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
