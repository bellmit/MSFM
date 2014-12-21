//
// -----------------------------------------------------------------------------------
// Source file: ManualQuoteDetailImpl.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.idl.quote.ManualQuoteDetailStruct;

import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.manualReporting.ManualQuoteDetail;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.FormatFactory;

/**
 * ManualQuoteDetail implementation for a ManualQuoteDetailStruct from the API.
 */
public class ManualQuoteDetailImpl extends AbstractMutableBusinessModel implements ManualQuoteDetail {
    private ManualQuoteDetailStruct manualQuoteDetailStruct;
    public static ManualReportingFormatStrategy formatter = null;

    /**
     * Constructor
     * @param manualQuoteDetailStruct to represent
     */
    protected ManualQuoteDetailImpl(ManualQuoteDetailStruct manualQuoteDetailStruct)
    {
        this();
        this.manualQuoteDetailStruct = manualQuoteDetailStruct;
    }

    /**
     *  Default constructor.
     */
    protected ManualQuoteDetailImpl()
    {
        super();
        if(formatter == null)
        {
            formatter = FormatFactory.getManualReportingFormatStrategy();
        }
    }


    // helper methods to struct attributes
    public String getLocationId()
    {
        return getStruct().locationId;
    }

    public String getParId()
    {
        return getStruct().parId;
    }

    public String getIpAddress()
    {
        return getStruct().ipAddress;
    }

    public void setLocationId(String locationId)
    {
        Object oldValue = getLocationId();
        getStruct().locationId = locationId;
        setModified(true);
        firePropertyChange(PROPERTY_LOCATION_ID, oldValue, locationId);
    }

    public void setParId(String parId)
    {
        Object oldValue = getParId();
        getStruct().parId = parId;
        setModified(true);
        firePropertyChange(PROPERTY_PAR_ID, oldValue, parId);
    }


    public void setIpAddress(String ipAddress)
    {
        Object oldValue = getIpAddress();
        getStruct().ipAddress = ipAddress;
        setModified(true);
        firePropertyChange(PROPERTY_LOCATION_ID, oldValue, ipAddress);
    }

    public ManualQuoteDetailStruct getStruct() {
        return manualQuoteDetailStruct;
    }
}
