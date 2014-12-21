//
// -----------------------------------------------------------------------------------
// Source file: ProfileModelImpl.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import junit.framework.*;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.user.ProfileModel;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.exampleStructs.ExampleClassStruct;
import com.cboe.presentation.product.SessionProductClassFactory;
import com.cboe.presentation.product.ProductHelper;

public class ProfileModelImpl extends AbstractMutableBusinessModel implements ProfileModel
{
    private SessionProductClass productClass;
    private String account;
    private String subAccount;
    private ExchangeFirm executingGiveupFirm;
    private boolean isAccountIgnored;
    private char originCode;

    public final static String PROPERTY_PRODUCT_CLASS = "PROPERTY_PRODUCT_CLASS";
    public final static String PROPERTY_ACCOUNT = "PROPERTY_ACCOUNT";
    public final static String PROPERTY_SUBACCOUNT = "PROPERTY_SUBACCOUNT";
    public final static String PROPERTY_EXCHANGE_FIRM = "PROPERTY_EXCHANGE_FIRM";
    public final static String PROPERTY_ORIGIN_CODE = "PROPERTY_ORIGIN_CODE";

    protected ProfileModelImpl()
    {
        super();
    }

    protected ProfileModelImpl(SessionProfileStruct profileStruct) throws SystemException, DataValidationException,
                                                                          CommunicationException, AuthorizationException
    {
        if( profileStruct == null)
        {
            throw new IllegalArgumentException("ProfileStruct can not be null");
        }
        if( profileStruct.executingGiveupFirm == null)
        {
            throw new IllegalArgumentException("ProfileStruct.executingGiveupFirm can not be null");
        }

        setProductClass(profileStruct.sessionName, profileStruct.classKey);
        setAccount(profileStruct.account);
        setSubAccount(profileStruct.subAccount);
        setExecutingGiveupFirm(profileStruct.executingGiveupFirm);
        setAccountIgnored(profileStruct.isAccountBlanked);
        setOriginCode(profileStruct.originCode);
    }

    public boolean equals(Object o)
    {
        boolean result = false;

        if( this == o )
        {
            result = true;
        }
        else if( o == null )
        {
            result = false;
        }
        else if( o instanceof ProfileModel )
        {
            ProfileModel aProfileModel = ( ProfileModel ) o;
            if( this.getAccount().equals(aProfileModel.getAccount()) )
            {
                if(this.isAccountIgnored() == aProfileModel.isAccountIgnored())
                {
                    if( this.getProductClass().equals(aProfileModel.getProductClass()) )
                    {
                        if( this.getExecutingGiveupFirm().equals(aProfileModel.getExecutingGiveupFirm()) )
                        {
                            if( this.getSubAccount().equals(aProfileModel.getSubAccount()) )
                            {
                                if( this.getOriginCode() == aProfileModel.getOriginCode())
                                {
                                    result = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProfileModelImpl aNewProfileModel = new ProfileModelImpl();
        try
        {
            aNewProfileModel.setProductClass(this.getSessionName(), this.getClassKey());
            aNewProfileModel.setAccount(this.getAccount());
            aNewProfileModel.setAccountIgnored(this.isAccountIgnored());
            aNewProfileModel.setSubAccount(this.getSubAccount());
            aNewProfileModel.setExecutingGiveupFirm(( ExchangeFirm ) this.getExecutingGiveupFirm().clone());
            aNewProfileModel.setOriginCode(this.getOriginCode());
        }
        catch( Exception e )
        {
            DefaultExceptionHandlerHome.find().process(e, "Unable to clone ProfileModelImpl");
        }

        return aNewProfileModel;
    }

    public int hashCode()
    {
        return getProductClass().hashCode();
    }

    public void setAccountIgnored(boolean ignored)
    {
        isAccountIgnored = ignored;
    }

    public boolean isAccountIgnored()
    {
        return isAccountIgnored;
    }

    public void setProductClass(SessionProductClass productClass)
    {
        ProductClass old = this.productClass;
        this.productClass = productClass;
        firePropertyChange(PROPERTY_PRODUCT_CLASS, old, this.productClass);
    }

    public void setProductClass(String sessionName, int classKey) throws SystemException, DataValidationException,
                                                                         CommunicationException, AuthorizationException
    {
        setProductClass(ProductHelper.getSessionProductClassCheckInvalid(sessionName, classKey));
    }

    public void setAccount(String account)
    {
        String old = this.account;
        this.account = account;
        firePropertyChange(PROPERTY_ACCOUNT, old, this.account);
    }

    public void setSubAccount(String subAccount)
    {
        String old = this.subAccount;
        this.subAccount = subAccount;
        firePropertyChange(PROPERTY_SUBACCOUNT, old, this.subAccount);
    }

    public void setExecutingGiveupFirm(ExchangeFirm executingGiveupFirm)
    {
        ExchangeFirm old = this.executingGiveupFirm;
        this.executingGiveupFirm = executingGiveupFirm;
        firePropertyChange(PROPERTY_EXCHANGE_FIRM, old, this.executingGiveupFirm);
    }

    public void setExecutingGiveupFirm(ExchangeFirmStruct executingGiveupFirm)
    {
        setExecutingGiveupFirm(ExchangeFirmFactory.createExchangeFirm(executingGiveupFirm));
    }

    public void setOriginCode(char originCode)
    {
        char oldOrigin = this.originCode;
        this.originCode = originCode;
        firePropertyChange(PROPERTY_ORIGIN_CODE, oldOrigin, originCode);
    }

    public SessionProfileStruct getProfileStruct()
    {
        SessionProfileStruct struct = new SessionProfileStruct();
        struct.account = getAccount();
        struct.classKey = getProductClass().getClassKey();
        struct.executingGiveupFirm = getExecutingGiveupFirm().getExchangeFirmStruct();
        struct.sessionName = getProductClass().getTradingSessionName();
        struct.subAccount = getSubAccount();
        struct.isAccountBlanked = isAccountIgnored();
        struct.originCode = getOriginCode();
        return struct;
    }

    public SessionProductClass getProductClass()
    {
        return productClass;
    }

    public int getClassKey()
    {
        return getProductClass().getClassKey();
    }

    public String getSessionName()
    {
        return getProductClass().getTradingSessionName();
    }

    public String getAccount()
    {
        return account;
    }

    public String getSubAccount()
    {
        return subAccount;
    }

    public ExchangeFirm getExecutingGiveupFirm()
    {
        return executingGiveupFirm;
    }

    public char getOriginCode()
    {
        return originCode;
    }

    public boolean isDefaultProfile()
    {
        boolean isDefault = false;

        SessionProductClass spc = getProductClass();

        if( spc != null )
        {
            isDefault = spc.isDefaultProductClass() && spc.isDefaultSession();
        }

        return isDefault;
    }

    /**
     *  Remember to add the import import junit.framework.*; To run - java
     *  junit.swingui.TestRunner and pass [package.SomeClass]$TestImpl as a
     *  parameter You will have access to all privates in this class
     *
     *@author     Nick DePasquale
     */
    public static class TestImpl extends TestCase
    {
        private ProfileModelImpl aProfileModelImpl;
        private SessionProfileStruct profileStruct;
        private ExchangeFirm exchangeFirm;
        private ExchangeFirmStruct exchangeFirmStruct;
        private int defaultClassKey;
        private boolean defaultIsAccountBlanked = false;
        private String defaultAccount = "AAA";
        private String defaultSubAccount = "SSS";
        private String defaultFirm = "F";
        private String defaultExchange = "CBOE";
        private SessionProductClass defaultProductClass;
        private char defaultOrigin = 'G';

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
         *  A simple unit test of creating a ProfileModelImpl
         */
        public void testProfileCreation()
        {
//          String s = new String ( "ABC" );
//          String t = new String ( "ABC" );
//
//          assertTrue( "Test '=='" , s == s );
//          assertEquals ( "Test equals()" , s , s );
//          assertTrue( "Test equals()" , s.equals ( t ) );

            /* taken out because assert is a reserved word now

            assertTrue("Testing Profile Struct's ClassKey", getProfileStruct().classKey == defaultClassKey);
            assertTrue("Testing Profile Struct's Account", getProfileStruct().account.equals(defaultAccount));
            assertTrue("Testing Profile Struct's SubAccount", getProfileStruct().subAccount.equals(defaultSubAccount));
            assertTrue("Testing Profile Struct's ExchangeFirmStruct's Exchange", getProfileStruct().executingGiveupFirm.exchange == getExchangeFirmStruct().exchange);
            assertTrue("Testing Profile Struct's ExchangeFirmStruct's Firm", getProfileStruct().executingGiveupFirm.firmNumber == getExchangeFirmStruct().firmNumber);


            assertTrue("Testing Profile wrapper's ClassKey", getBaseProfileModel().getClassKey() == getProfileStruct().classKey);
            assertTrue("Testing Profile wrapper's Account", getBaseProfileModel().getAccount() == getProfileStruct().account);
            assertTrue("Testing Profile wrapper's SubAccount", getBaseProfileModel().getSubAccount() == getProfileStruct().subAccount);
            assertTrue("Testing Profile wrapper's ExecutingGiveUpFirm", getBaseProfileModel().getExecutingGiveupFirm().equals(getExchangeFirm()));

            try
            {
                assertTrue("Testing Profile wrapper's equals method", getBaseProfileModel().equals(getBaseProfileModel().clone()));
            }
            catch(CloneNotSupportedException e)
            {
                fail("Clone not supported: " + e.toString());
            }
            */
        }

        /**
         *  The JUnit setup method
         */
        protected void setUp()
        {
            defaultProductClass = SessionProductClassFactory.create(ExampleClassStruct.getExampleSessionClassStruct());
            defaultClassKey = defaultProductClass.getClassKey();

            getBaseProfileModel();
        }


        /**
         *  Gets the ExchangeFirmStruct attribute of the TestImpl object
         *
         *@return    The ExchangeFirmStruct value
         */
        private ExchangeFirmStruct getExchangeFirmStruct()
        {
            if(exchangeFirmStruct == null)
            {
                exchangeFirmStruct = new ExchangeFirmStruct(defaultExchange, defaultFirm);
            }
            return exchangeFirmStruct;
        }

        /**
         *  Gets the ExchangeFirm attribute of the TestImpl object
         *
         *@return    The ExchangeFirm value
         */
        private ExchangeFirm getExchangeFirm()
        {
            if(exchangeFirm == null)
            {
                exchangeFirm = ExchangeFirmFactory.createExchangeFirm(getExchangeFirmStruct());
            }
            return exchangeFirm;
        }

        /**
         *  Gets the ProfileStruct attribute of the TestImpl object
         *
         *@return    The ProfileStruct value
         */
        private SessionProfileStruct getProfileStruct()
        {
            if(profileStruct == null)
            {

                profileStruct = new SessionProfileStruct(defaultClassKey, defaultAccount, defaultSubAccount,
                                                         getExchangeFirm().getExchangeFirmStruct(), "All Sessions", defaultIsAccountBlanked, defaultOrigin);
            }
            return profileStruct;
        }

        /**
         *  Gets the BaseProfileModel attribute of the TestImpl object
         *
         *@return    The BaseProfileModel value
         */
        private ProfileModel getBaseProfileModel()
        {
            if(aProfileModelImpl == null)
            {
                aProfileModelImpl = new ProfileModelImpl();
                aProfileModelImpl.setAccount(getProfileStruct().account);
                aProfileModelImpl.setExecutingGiveupFirm(getExchangeFirm());
                aProfileModelImpl.setProductClass(defaultProductClass);
                aProfileModelImpl.setSubAccount(getProfileStruct().subAccount);
                aProfileModelImpl.setAccountIgnored(getProfileStruct().isAccountBlanked);
                aProfileModelImpl.setOriginCode(getProfileStruct().originCode);
            }

            return aProfileModelImpl;
        }

        /**
         *  Gets the BaseProfile attribute of the TestImpl object
         *
         *@return    The BaseProfile value
         */
        private Profile getBaseProfile()
        {
            return getBaseProfileModel();
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
            return new TestSuite();//TestImpl.class);
        }

    }
}
