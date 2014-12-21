//
// -----------------------------------------------------------------------------------
// Source file: OrderEntryImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.order.MutableOrderEntry;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.order.OrderEntry;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.userSession.UserSessionFactory;

class OrderEntryImpl extends AbstractMutableBusinessModel implements MutableOrderEntry
{
    protected boolean createdFromStruct;

    protected ExchangeFirm executingOrGiveUpFirm;
    protected String branch;
    protected Integer branchSequenceNumber;
    protected String correspondentFirm;
    protected String orderDate;
    protected ExchangeAcronym originator;
    protected Integer originalQuantity;
    protected Integer productKey;
    protected Character side;
    protected Price price;
    protected Character timeInForce;
    protected DateTime expireTime;
    protected OrderContingency contingency;
    protected ExchangeFirm cmta;
    protected String extensions;
    protected String account;
    protected String subaccount;
    protected Character positionEffect;
    protected Boolean cross;
    protected Character orderOriginType;
    protected Character coverage;
    protected Short orderNBBOProtectionType;
    protected String optionalData;
    protected String userAssignedId;
    protected String[] sessionNames;

    protected OrderEntryStruct orderEntryStruct;

    private static final Comparator myStdComparator = new Comparator()
    {
        public int compare(Object object1, Object object2)
        {
            OrderEntry orderEntry1 = (OrderEntry)object1;
            OrderEntry orderEntry2 = (OrderEntry)object2;

            int result = 0;

            if(!orderEntry1.equals(orderEntry2))
            {
                result = orderEntry1.getExecutingOrGiveUpFirm().compareTo(orderEntry2.getExecutingOrGiveUpFirm());
                if(result == 0)
                {
                    result = orderEntry1.getBranch().compareTo(orderEntry2.getBranch());
                    if(result == 0)
                    {
                        int thisVal = orderEntry1.getBranchSequenceNumber().intValue();
                        int anotherVal = orderEntry2.getBranchSequenceNumber().intValue();
                        result = (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
                        if(result == 0)
                        {
                            result = orderEntry1.getCorrespondentFirm().compareTo(orderEntry2.getCorrespondentFirm());
                            if(result == 0)
                            {
                                result = orderEntry1.getOrderDate().compareTo(orderEntry2.getOrderDate());
                            }
                        }
                    }
                }
            }
            return result;
        }
    };

    public OrderEntryImpl(OrderEntryStruct orderEntryStruct)
    {
        this();
        checkParam(orderEntryStruct, "OrderEntryStruct");
        createdFromStruct = true;
        setOrderEntryStruct(orderEntryStruct);
    }

    public OrderEntryImpl()
    {
        super();
        initialize();
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrderEntryImpl newImpl;
        if(isCreatedFromStruct())
        {
            newImpl = new OrderEntryImpl(getStruct());
        }
        else
        {
            newImpl = new OrderEntryImpl();
        }
        return newImpl;
    }

    public boolean equals(Object otherOrderEntry)
    {
        boolean equal = super.equals(otherOrderEntry);

        if(!equal)
        {
            OrderEntry castedObject = (OrderEntry)otherOrderEntry;

            equal = getExecutingOrGiveUpFirm().equals(castedObject.getExecutingOrGiveUpFirm()) &&
                    getBranch().equals(castedObject.getBranch()) &&
                    getBranchSequenceNumber().equals(castedObject.getBranchSequenceNumber()) &&
                    getCorrespondentFirm().equals(castedObject.getCorrespondentFirm()) &&
                    getOrderDate().equals(castedObject.getOrderDate());
        }
        return equal;
    }

    public boolean isCreatedFromStruct()
    {
        return createdFromStruct;
    }

    /**
     * Gets the underlying struct
     * @return OrderEntryStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderEntryStruct getStruct()
    {
        OrderEntryStruct struct;
        if(isCreatedFromStruct())
        {
            struct = orderEntryStruct;
        }
        else
        {
            struct = OrderEntryFactory.createDefaultStruct();
            struct.account = getAccount();
            struct.cmta = getCmta().getExchangeFirmStruct();
            struct.contingency = getContingency().getStruct();
            struct.coverage = getCoverage().charValue();
            struct.cross = getCross().booleanValue();
            struct.expireTime = getExpireTime().getDateTimeStruct();
            struct.extensions = getExtensions();
            struct.optionalData = getOptionalData();
            struct.orderNBBOProtectionType = getOrderNBBOProtectionType().shortValue();
            struct.orderOriginType = getOrderOriginType().charValue();
            struct.originalQuantity = getOriginalQuantity().intValue();
            struct.originator = getOriginator().getExchangeAcronymStruct();
            struct.positionEffect = getPositionEffect().charValue();
            struct.price = getPrice().toStruct();
            struct.productKey = getProductKey().intValue();
            struct.sessionNames = getSessionNames();
            struct.side = getSide().charValue();
            struct.subaccount = getSubaccount();
            struct.timeInForce = getTimeInForce().charValue();
            struct.userAssignedId = getUserAssignedId();

            struct.branch = getBranch();
            struct.branchSequenceNumber = getBranchSequenceNumber().intValue();
            struct.correspondentFirm = getCorrespondentFirm();
            struct.executingOrGiveUpFirm = getExecutingOrGiveUpFirm().getExchangeFirmStruct();
            struct.orderDate = getOrderDate();
        }
        return struct;
    }

    public ExchangeFirm getExecutingOrGiveUpFirm()
    {
        return executingOrGiveUpFirm;
    }

    public String getBranch()
    {
        return branch;
    }

    public Integer getBranchSequenceNumber()
    {
        return branchSequenceNumber;
    }

    public String getCorrespondentFirm()
    {
        return correspondentFirm;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public ExchangeAcronym getOriginator()
    {
        return originator;
    }

    public Integer getOriginalQuantity()
    {
        return originalQuantity;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public Character getSide()
    {
        return side;
    }

    public Price getPrice()
    {
        return price;
    }

    public Character getTimeInForce()
    {
        return timeInForce;
    }

    public DateTime getExpireTime()
    {
        return expireTime;
    }

    public OrderContingency getContingency()
    {
        return contingency;
    }

    public ExchangeFirm getCmta()
    {
        return cmta;
    }

    public String getExtensions()
    {
        return extensions;
    }

    public String getAccount()
    {
        return account;
    }

    public String getSubaccount()
    {
        return subaccount;
    }

    public Character getPositionEffect()
    {
        return positionEffect;
    }

    public Character getOrderOriginType()
    {
        return orderOriginType;
    }

    public Character getCoverage()
    {
        return coverage;
    }

    public Short getOrderNBBOProtectionType()
    {
        return orderNBBOProtectionType;
    }

    public String getOptionalData()
    {
        return optionalData;
    }

    public String getUserAssignedId()
    {
        return userAssignedId;
    }

    public String[] getSessionNames()
    {
        return sessionNames;
    }

    public Boolean getCross()
    {
        return cross;
    }

    public void setBranch(String newValue)
    {
        Object oldValue = getBranch();
        branch = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().branch = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_BRANCH, oldValue, newValue);
    }

    public void setBranchSequenceNumber(Integer newValue)
    {
        Object oldValue = getBranchSequenceNumber();
        branchSequenceNumber = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().branchSequenceNumber = newValue.intValue();
        }
        setModified();
        firePropertyChange(PROPERTY_BRANCH_SEQUENCE, oldValue, newValue);
    }

    public void setCorrespondentFirm(String newValue)
    {
        Object oldValue = getCorrespondentFirm();
        correspondentFirm = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().correspondentFirm = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_CORRESPONDENT_FIRM, oldValue, newValue);
    }

    public void setExecutingOrGiveUpFirm(ExchangeFirm newValue)
    {
        Object oldValue = getExecutingOrGiveUpFirm();
        executingOrGiveUpFirm = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().executingOrGiveUpFirm = newValue.getExchangeFirmStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_EXECUTING_GIVEUP_FIRM, oldValue, newValue);
    }

    public void setOrderDate(String newValue)
    {
        Object oldValue = getOrderDate();
        orderDate = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().orderDate = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_ORDER_DATE, oldValue, newValue);
    }

    public void setOriginator(ExchangeAcronym newValue)
    {
        ExchangeAcronym oldValue = getOriginator();
        originator = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().originator = newValue.getExchangeAcronymStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_ORIGINATOR, oldValue, newValue);
    }

    public void setOriginalQuantity(Integer newValue)
    {
        Integer oldValue = getOriginalQuantity();
        originalQuantity = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().originalQuantity = newValue.intValue();
        }
        setModified();
        firePropertyChange(PROPERTY_ORIGINAL_QUANTITY, oldValue, newValue);
    }

    public void setProductKey(Integer newValue)
    {
        Integer oldValue = getProductKey();
        productKey = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().productKey = newValue.intValue();
        }
        setModified();
        firePropertyChange(PROPERTY_PRODUCT_KEY, oldValue, newValue);
    }

    public void setSide(Character newValue)
    {
        Character oldValue = getSide();
        side = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().side = newValue.charValue();
        }
        setModified();
        firePropertyChange(PROPERTY_SIDE, oldValue, newValue);
    }

    public void setPrice(Price newValue)
    {
        Price oldValue = getPrice();
        price = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().price = newValue.toStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_PRICE, oldValue, newValue);
    }

    public void setTimeInForce(Character newValue)
    {
        Character oldValue = getTimeInForce();
        timeInForce = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().timeInForce = newValue.charValue();
        }
        setModified();
        firePropertyChange(PROPERTY_TIME_IN_FORCE, oldValue, newValue);
    }

    public void setExpireTime(DateTime newValue)
    {
        DateTime oldValue = getExpireTime();
        expireTime = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().expireTime = newValue.getDateTimeStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_EXPIRE_TIME, oldValue, newValue);
    }

    public void setContingency(OrderContingency newValue)
    {
        OrderContingency oldValue = getContingency();
        contingency = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().contingency = newValue.getStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_CONTINGENCY, oldValue, newValue);
    }

    public void setCmta(ExchangeFirm newValue)
    {
        ExchangeFirm oldValue = getCmta();
        cmta = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().cmta = newValue.getExchangeFirmStruct();
        }
        setModified();
        firePropertyChange(PROPERTY_CMTA, oldValue, newValue);
    }

    public void setExtensions(String newValue)
    {
        String oldValue = getExtensions();
        extensions = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().extensions = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_EXTENSIONS, oldValue, newValue);
    }

    public void setAccount(String newValue)
    {
        String oldValue = getAccount();
        account = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().account = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_ACCOUNT, oldValue, newValue);
    }

    public void setSubaccount(String newValue)
    {
        String oldValue = getSubaccount();
        subaccount = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().subaccount = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_SUB_ACCOUNT, oldValue, newValue);
    }

    public void setPositionEffect(Character newValue)
    {
        Character oldValue = getPositionEffect();
        positionEffect = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().positionEffect = newValue.charValue();
        }
        setModified();
        firePropertyChange(PROPERTY_POSITION_EFFECT, oldValue, newValue);
    }

    public void setCross(Boolean newValue)
    {
        Boolean oldValue = getCross();
        cross = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().cross = newValue.booleanValue();
        }
        setModified();
        firePropertyChange(PROPERTY_CROSS, oldValue, newValue);
    }

    public void setOrderOriginType(Character newValue)
    {
        Character oldValue = getOrderOriginType();
        orderOriginType = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().orderOriginType = newValue.charValue();
        }
        setModified();
        firePropertyChange(PROPERTY_ORDER_ORIGIN_TYPE, oldValue, newValue);
    }

    public void setCoverage(Character newValue)
    {
        Character oldValue = getCoverage();
        coverage = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().coverage= newValue.charValue();
        }
        setModified();
        firePropertyChange(PROPERTY_COVERAGE, oldValue, newValue);
    }

    public void setOrderNBBOProtectionType(Short newValue)
    {
        Short oldValue = getOrderNBBOProtectionType();
        orderNBBOProtectionType = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().orderNBBOProtectionType = newValue.shortValue();
        }
        setModified();
        firePropertyChange(PROPERTY_ORDER_NBBO_PROTECTION_TYPE, oldValue, newValue);
    }

    public void setOptionalData(String newValue)
    {
        String oldValue = getOptionalData();
        optionalData = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().optionalData = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_OPTIONAL_DATA, oldValue, newValue);
    }

    public void setUserAssignedId(String newValue)
    {
        String oldValue = getUserAssignedId();
        userAssignedId = newValue;
        if(isCreatedFromStruct())
        {
            getStruct().userAssignedId = newValue;
        }
        setModified();
        firePropertyChange(PROPERTY_USER_ASSIGNED_ID, oldValue, newValue);
    }

    protected void setModified()
    {
        setModified(true);
    }

    private void initialize()
    {
        setComparator(myStdComparator);

        branch = "";
        orderDate = "";
        correspondentFirm = "";

        UserStructModel user = UserSessionFactory.findUserSession().getUserModel();
        ExchangeFirm firm = user.getDefaultProfile().getExecutingGiveupFirm();
        if(firm == null)
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm("", "");
        }
        else
        {
            executingOrGiveUpFirm = firm;
        }
        account = user.getDefaultProfile().getAccount();

        DateTime now = new DateTimeImpl();
        expireTime = now;

        subaccount = "";
        optionalData = "";
        cmta = null;
        side = new Character(Sides.BUY);

        Role role = UserSessionFactory.findUserSession().getUserModel().getRole();
        if(role == Role.MARKET_MAKER || role == Role.DPM)
        {
            orderOriginType = new Character(OrderOrigins.MARKET_MAKER);
        }
        else
        {
            orderOriginType = new Character(OrderOrigins.CUSTOMER);
        }
        coverage = new Character(CoverageTypes.UNCOVERED);
        positionEffect = new Character(PositionEffects.CLOSED);
        price = DisplayPriceFactory.create(0.0);
        timeInForce = new Character(TimesInForce.DAY);
        originalQuantity = new Integer(0);
        cross = Boolean.FALSE;
        userAssignedId = "";
        extensions = "";

        try
        {
            productKey = new Integer(APIHome.findProductQueryAPI().getDefaultSessionProduct().getProductKey());
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    public void setOrderEntryStruct(OrderEntryStruct orderEntryStruct)
    {
        this.orderEntryStruct = orderEntryStruct;
        if(orderEntryStruct.executingOrGiveUpFirm != null)
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm(orderEntryStruct.executingOrGiveUpFirm);
        }
        else
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm("", "");
        }
        if(orderEntryStruct.originator != null)
        {
            originator = ExchangeAcronymFactory.createExchangeAcronym(orderEntryStruct.originator);
        }
        else
        {
            originator = ExchangeAcronymFactory.createExchangeAcronym("", "");
        }
        if(orderEntryStruct.cmta != null)
        {
            cmta = ExchangeFirmFactory.createExchangeFirm(orderEntryStruct.cmta);
        }
        else
        {
            cmta = ExchangeFirmFactory.createExchangeFirm("", "");
        }
        branch = new String(orderEntryStruct.branch);
        branchSequenceNumber = new Integer(orderEntryStruct.branchSequenceNumber);
        correspondentFirm = new String(orderEntryStruct.correspondentFirm);
        orderDate = new String(orderEntryStruct.orderDate);
        originalQuantity = new Integer(orderEntryStruct.originalQuantity);
        productKey = new Integer(orderEntryStruct.productKey);
        side = new Character(orderEntryStruct.side);
        price = DisplayPriceFactory.create(orderEntryStruct.price);
        timeInForce = new Character(orderEntryStruct.timeInForce);
        expireTime = new DateTimeImpl(orderEntryStruct.expireTime);
        contingency = OrderContingencyFactory.createOrderContingency(orderEntryStruct.contingency);
        extensions = orderEntryStruct.extensions;
        account = orderEntryStruct.account;
        subaccount = orderEntryStruct.subaccount;
        positionEffect = new Character(orderEntryStruct.positionEffect);
        cross = new Boolean(orderEntryStruct.cross);
        orderOriginType = new Character(orderEntryStruct.orderOriginType);
        coverage = new Character(orderEntryStruct.coverage);
        orderNBBOProtectionType = new Short(orderEntryStruct.orderNBBOProtectionType);
        optionalData = orderEntryStruct.optionalData;
        userAssignedId = orderEntryStruct.userAssignedId;
        sessionNames = orderEntryStruct.sessionNames;
    }
}