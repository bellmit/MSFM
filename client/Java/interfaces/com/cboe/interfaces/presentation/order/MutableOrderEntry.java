//
// -----------------------------------------------------------------------------------
// Source file: MutableOrderEntry.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.domain.Price;

/**
 * Provides a contract for a wrapper of OrderEntryStruct, with write access
 */
public interface MutableOrderEntry extends MutableBusinessModel, OrderEntry
{
    public static final String PROPERTY_EXECUTING_GIVEUP_FIRM = "PROPERTY_EXECUTING_GIVEUP_FIRM";
    public static final String PROPERTY_BRANCH = "PROPERTY_BRANCH";
    public static final String PROPERTY_BRANCH_SEQUENCE = "PROPERTY_BRANCH_SEQUENCE";
    public static final String PROPERTY_CORRESPONDENT_FIRM = "PROPERTY_CORRESPONDENT_FIRM";
    public static final String PROPERTY_ORDER_DATE = "PROPERTY_ORDER_DATE";
    public static final String PROPERTY_ORIGINATOR = "PROPERTY_ORIGINATOR";
    public static final String PROPERTY_ORIGINAL_QUANTITY = "PROPERTY_ORIGINAL_QUANTITY";
    public static final String PROPERTY_PRODUCT_KEY = "PROPERTY_PRODUCT_KEY";
    public static final String PROPERTY_SIDE = "PROPERTY_SIDE";
    public static final String PROPERTY_PRICE = "PROPERTY_PRICE";
    public static final String PROPERTY_TIME_IN_FORCE = "PROPERTY_TIME_IN_FORCE";
    public static final String PROPERTY_EXPIRE_TIME = "PROPERTY_EXPIRE_TIME";
    public static final String PROPERTY_CONTINGENCY = "PROPERTY_CONTINGENCY";
    public static final String PROPERTY_CMTA = "PROPERTY_CMTA";
    public static final String PROPERTY_EXTENSIONS = "PROPERTY_EXTENSIONS";
    public static final String PROPERTY_ACCOUNT = "PROPERTY_ACCOUNT";
    public static final String PROPERTY_SUB_ACCOUNT = "PROPERTY_SUB_ACCOUNT";
    public static final String PROPERTY_POSITION_EFFECT = "PROPERTY_POSITION_EFFECT";
    public static final String PROPERTY_CROSS = "PROPERTY_CROSS";
    public static final String PROPERTY_ORDER_ORIGIN_TYPE = "PROPERTY_ORDER_ORIGIN_TYPE";
    public static final String PROPERTY_COVERAGE = "PROPERTY_COVERAGE";
    public static final String PROPERTY_ORDER_NBBO_PROTECTION_TYPE = "PROPERTY_ORDER_NBBO_PROTECTION_TYPE";
    public static final String PROPERTY_OPTIONAL_DATA = "PROPERTY_OPTIONAL_DATA";
    public static final String PROPERTY_USER_ASSIGNED_ID = "PROPERTY_USER_ASSIGNED_ID";

    public void setExecutingOrGiveUpFirm(ExchangeFirm newValue);

    public void setBranch(String newValue);

    public void setBranchSequenceNumber(Integer newValue);

    public void setCorrespondentFirm(String newValue);

    public void setOrderDate(String newValue);

    public void setOriginator(ExchangeAcronym newValue);

    public void setOriginalQuantity(Integer newValue);

    public void setProductKey(Integer newValue);

    public void setSide(Character newValue);

    public void setPrice(Price newValue);

    public void setTimeInForce(Character newValue);

    public void setExpireTime(DateTime newValue);

    public void setContingency(OrderContingency newValue);

    public void setCmta(ExchangeFirm newValue);

    public void setExtensions(String newValue);

    public void setAccount(String newValue);

    public void setSubaccount(String newValue);

    public void setPositionEffect(Character newValue);

    public void setCross(Boolean newValue);

    public void setOrderOriginType(Character newValue);

    public void setCoverage(Character newValue);

    public void setOrderNBBOProtectionType(Short newValue);

    public void setOptionalData(String newValue);

    public void setUserAssignedId(String newValue);
}