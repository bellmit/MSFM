/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.presentation.user;

import junit.framework.*;

import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.user.AccountModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

/**
 *  Description of the Class
 *
 *@author     Nick DePasquale
 *@created    January 31, 2002
 */
public class AccountModelImpl extends AbstractMutableBusinessModel implements AccountModel
{
    private String account = "";
    private ExchangeFirm exchangeFirm;
    /**
     *  Description of the Field
     */
    public final static String PROPERTY_ACCOUNT = "PROPERTY_ACCOUNT";
    /**
     *  Description of the Field
     */
    public final static String PROPERTY_EXCHANGE_FIRM = "PROPERTY_EXCHANGE_FIRM";

    /**
     *  Constructor for the AccountModelImpl object
     */
    protected AccountModelImpl()
    {
    }

    /**
     *  Constructor for the AccountModelImpl object
     *
     *@param  anExchangeFirm  Description of Parameter
     *@param  anAccount       Description of Parameter
     */
    protected AccountModelImpl(ExchangeFirm anExchangeFirm, String anAccount)
    {
        super();
        setAccount(anAccount);
        setExchangeFirm(anExchangeFirm);
    }

    /**
     *  Constructor for the AccountModelImpl object
     *
     *@param  anAccountStruct  Description of Parameter
     */
    protected AccountModelImpl(AccountStruct anAccountStruct)
    {
        super();

        setAccount(anAccountStruct.account);
        setExchangeFirm(anAccountStruct.executingGiveupFirm);
    }

    /**
     *  Sets the Account attribute of the AccountModelImpl object
     *
     *@param  account  The new Account value
     */
    public void setAccount(String account)
    {
        String old = this.account;
        this.account = account;
        firePropertyChange(PROPERTY_ACCOUNT, old, this.account);
    }

    /**
     *  Sets the ExchangeFirm attribute of the AccountModelImpl object
     *
     *@param  exchangeFirm  The new ExchangeFirm value
     */
    public void setExchangeFirm(ExchangeFirm exchangeFirm)
    {
        ExchangeFirm old = this.exchangeFirm;
        this.exchangeFirm = exchangeFirm;
        firePropertyChange(PROPERTY_EXCHANGE_FIRM, old, this.exchangeFirm);
    }

    /**
     *  Sets the ExchangeFirm attribute of the AccountModelImpl object
     *
     *@param  exchangeFirm  The new ExchangeFirm value
     */
    public void setExchangeFirm(ExchangeFirmStruct exchangeFirm)
    {
        setExchangeFirm(ExchangeFirmFactory.createExchangeFirm(exchangeFirm));
    }

    /**
     *  Gets the Account attribute of the AccountModelImpl object
     *
     *@return    The Account value
     */
    public String getAccount()
    {
        return account;
    }

    /**
     *  Gets the ExchangeFirm attribute of the AccountModelImpl object
     *
     *@return    The ExchangeFirm value
     */
    public ExchangeFirm getExchangeFirm()
    {
        return exchangeFirm;
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
        else if(o instanceof AccountModelImpl)
        {
            AccountModel anAccountModelImpl = (AccountModelImpl)o;
            if(this.getExchangeFirm().equals(anAccountModelImpl.getExchangeFirm()))
            {
                if(this.getAccount().equals(anAccountModelImpl.getAccount()))
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
        AccountModelImpl aNewAccountModel = new AccountModelImpl((ExchangeFirm)this.getExchangeFirm().clone(), this.getAccount());

        return aNewAccountModel;
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

        private AccountModelImpl accountModel;
        private AccountModelImpl differentAccountModel;
        private ExchangeFirm defaultExchangeFirm;
        private ExchangeFirm differentExchangeFirm;

        private String defaultAccount = "AAA";

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
        public void testAccountModelCreation()
        {
            assertTrue("Testing AccountModel wrapper's ExchangeFimm", getBaseAccountModel().getExchangeFirm().equals(getDefaultExchangeFirm()));
            assertTrue("Testing AccountModel wrapper's Account", getBaseAccountModel().getAccount().equals(defaultAccount));

            try
            {
                assertTrue("Testing AccountModel wrapper's Clone and Equals", getBaseAccountModel().equals(getBaseAccountModel().clone()));
            }
            catch(CloneNotSupportedException e)
            {
                fail("Clone not supported: " + e.toString());
            }

            assertTrue("Testing AccountModel wrapper's Equals - different exchangefirm", getBaseAccountModel().equals(getDifferentAccountModel()) == true);

            getDifferentAccountModel().account = "BBB";
            getDifferentAccountModel().exchangeFirm = defaultExchangeFirm;

            assertTrue("Testing ExchangeAcronym wrapper's Equals - different account", getBaseAccountModel().equals(getDifferentAccountModel()) == false);

        }

        /**
         *  The JUnit setup method
         */
        protected void setUp()
        {

        }

        /**
         *  Gets the BaseAccountModel attribute of the TestImpl object
         *
         *@return    The BaseAccountModel value
         */
        private AccountModel getBaseAccountModel()
        {
            if(accountModel == null)
            {
                accountModel = new AccountModelImpl(getDefaultExchangeFirm(), defaultAccount);
            }
            return accountModel;
        }

        /**
         *  Gets the DefaultExchangeFirm attribute of the TestImpl object
         *
         *@return    The DefaultExchangeFirm value
         */
        private ExchangeFirm getDefaultExchangeFirm()
        {
            if(defaultExchangeFirm == null)
            {
                defaultExchangeFirm = ExchangeFirmFactory.createExchangeFirm("CBOE", "FFF");
            }

            return defaultExchangeFirm;
        }

        /**
         *  Gets the DifferentExchangeFirm attribute of the TestImpl object
         *
         *@return    The DifferentExchangeFirm value
         */
        private ExchangeFirm getDifferentExchangeFirm()
        {
            if(differentExchangeFirm == null)
            {
                differentExchangeFirm = ExchangeFirmFactory.createExchangeFirm("CBOE", "GGG");
            }

            return differentExchangeFirm;
        }

        /**
         *  Gets the DifferentAccountModel attribute of the TestImpl object
         *
         *@return    The DifferentAccountModel value
         */
        private AccountModelImpl getDifferentAccountModel()
        {
            if(differentAccountModel == null)
            {
                differentAccountModel = new AccountModelImpl(getDifferentExchangeFirm(), defaultAccount);
            }
            return differentAccountModel;
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

}
