//
// -----------------------------------------------------------------------------------
// Source file: FirmCache.java
//
// PACKAGE: com.cboe.internalPresentation.firm
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.firm;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.idl.internalBusinessServices.FirmMaintenanceService;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.internalPresentation.FirmMaintenanceServiceAPI;
import com.cboe.interfaces.internalPresentation.firm.FirmModel;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

public class FirmCache implements EventChannelListener
{
    public static final int DEFAULT_INITIAL_CACHE_SIZE = 500;

    private FirmMaintenanceServiceAPI subscriptionService;
    private FirmMaintenanceService apiService;

    private Map firmsByKeyMap;
    private Map firmsByNumber;
    private Map firmsByAcronym;
    private Map activeFirmsByKeyMap;

    public FirmCache(FirmMaintenanceService apiService, FirmMaintenanceServiceAPI subscriptionService)
    {
        if(subscriptionService == null)
        {
            throw new IllegalArgumentException("FirmMaintenanceServiceAPI may not be null.");
        }
        this.subscriptionService = subscriptionService;

        if(apiService == null)
        {
            throw new IllegalArgumentException("FirmMaintenanceService may not be null.");
        }
        this.apiService = apiService;

        firmsByKeyMap = new HashMap(DEFAULT_INITIAL_CACHE_SIZE);
        firmsByNumber = new HashMap(DEFAULT_INITIAL_CACHE_SIZE);
        firmsByAcronym = new HashMap(DEFAULT_INITIAL_CACHE_SIZE);
        activeFirmsByKeyMap = new HashMap(DEFAULT_INITIAL_CACHE_SIZE);

        synchronized(this)
        {
            try
            {
                subscriptionService.subscribeFirmEvents(this);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not subscribe for Firm Events. Firms will be invalid.");
            }

            try
            {
                FirmStruct[] allStructs = apiService.getFirms();
                add(allStructs);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not obtain the initial set of Firms. Firms will be invalid.");
            }
        }
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        Object eventData = event.getEventData();

        if(channelType == ChannelType.CB_USER_EVENT_ADD_FIRM)
        {
            GUILoggerHome.find().debug(getClass().getName(),
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       "Processing API Firm Event Add.");

            FirmStruct firmStruct = (FirmStruct) eventData;
            add(firmStruct);
        }
        else if(channelType == ChannelType.CB_USER_EVENT_DELETE_FIRM)
        {
            GUILoggerHome.find().debug(getClass().getName(),
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       "Processing API Firm Event Delete.");

            FirmStruct firmStruct = (FirmStruct) eventData;
            remove(firmStruct);
        }
    }

    public void cleanup()
    {
        firmsByKeyMap.clear();
        firmsByAcronym.clear();
        firmsByNumber.clear();
        activeFirmsByKeyMap.clear();
    }

    public FirmModel getFirmByKey(int firmKey)
            throws NotFoundException
    {
        FirmModel model;
        Integer firmKeyObject = new Integer(firmKey);
        if(firmsByKeyMap.containsKey(firmKeyObject))
        {
            model = (FirmModel) firmsByKeyMap.get(firmKeyObject);
        }
        else
        {
            throw ExceptionBuilder.notFoundException("Firm Not Found by Key:" + firmKey + ".",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return model;
    }

    public FirmModel getFirmByNumber(ExchangeFirmStruct firmStruct)
            throws DataValidationException, NotFoundException
    {
        if(firmStruct == null)
        {
            throw ExceptionBuilder.dataValidationException("ExchangeFirmStruct may not be null.", 0);
        }
        if(firmStruct.firmNumber == null || firmStruct.firmNumber.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("firmNumber was not specified.", 0);
        }
        if(firmStruct.exchange == null || firmStruct.exchange.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("exchange was not specified.",
                                                           DataValidationCodes.INVALID_EXCHANGE);
        }

        FirmModel model;
        String firmNumberKey = firmStruct.exchange + firmStruct.firmNumber;
        if(firmsByNumber.containsKey(firmNumberKey))
        {
            model = (FirmModel) firmsByNumber.get(firmNumberKey);
        }
        else
        {
            throw ExceptionBuilder.notFoundException("Firm Not Found by Number:" + firmStruct.exchange + ':' +
                                                     firmStruct.firmNumber + ".", NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return model;
    }

    public FirmModel getFirmByAcronym(String firmAcronym, String exchange)
            throws DataValidationException, NotFoundException
    {
        if(firmAcronym == null || firmAcronym.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("firmAcronym was not specified.", 0);
        }
        if(exchange == null || exchange.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("exchange was not specified.",
                                                           DataValidationCodes.INVALID_EXCHANGE);
        }

        FirmModel model;
        String firmAcronymKey = firmAcronym + exchange;
        if(firmsByAcronym.containsKey(firmAcronymKey))
        {
            model = (FirmModel) firmsByAcronym.get(firmAcronymKey);
        }
        else
        {
            throw ExceptionBuilder.notFoundException("Firm Not Found by Acronym:" + firmAcronym + ", Exchange:" +
                                                     exchange + ".", NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return model;
    }

    public FirmModel[] getFirms()
    {
        Collection values = firmsByKeyMap.values();
        FirmModel[] models = new FirmModel[values.size()];
        models = (FirmModel[]) values.toArray(models);
        return models;
    }

    public FirmModel[] getActiveFirms()
    {
        Collection values = activeFirmsByKeyMap.values();
        FirmModel[] models = new FirmModel[values.size()];
        models = (FirmModel[]) values.toArray(models);
        return models;
    }

    protected synchronized void add(FirmStruct[] firmStructs)
    {
        for(int i = 0; i < firmStructs.length; i++)
        {
            FirmStruct firmStruct = firmStructs[i];
            Integer firmKey = new Integer(firmStruct.firmKey);

            boolean shouldAdd = false;
            if(firmsByKeyMap.containsKey(firmKey))
            {
                FirmModel containedModel = (FirmModel) firmsByKeyMap.get(firmKey);
                if(firmStruct.versionNumber > containedModel.getFirmStruct().versionNumber)
                {
                    remove(containedModel.getFirmStruct());
                    shouldAdd = true;
                }
            }
            else
            {
                shouldAdd = true;
            }

            if(shouldAdd)
            {
                FirmModel newModel = createFirmModel(firmStruct);

                firmsByKeyMap.put(firmKey, newModel);

                ExchangeFirmStruct firmNumberStruct = firmStruct.firmNumber;
                String firmAcronymKey = firmStruct.firmAcronym + firmNumberStruct.exchange;
                firmsByAcronym.put(firmAcronymKey, newModel);

                // SEDL6536:  don't add inactive firms to the firms-by-number cache, or to the active firms cache.
                // (An inactive firm may have a firm number that duplicates an active firm number.)
                if(firmStruct.isActive)
                {
                    String firmNumberKey = firmNumberStruct.exchange + firmNumberStruct.firmNumber;
                    firmsByNumber.put(firmNumberKey, newModel);
                    activeFirmsByKeyMap.put(firmKey, newModel);
                }
            }
        }
    }

    protected void add(FirmStruct firmStruct)
    {
        FirmStruct[] structs = {firmStruct};
        add(structs);
    }

    protected synchronized void remove(FirmStruct firmStruct)
    {
        Integer firmKey = new Integer(firmStruct.firmKey);

        Object removedFirm = firmsByKeyMap.remove(firmKey);

        if(removedFirm != null)
        {
            activeFirmsByKeyMap.remove(firmKey);
            ExchangeFirmStruct firmNumberStruct = firmStruct.firmNumber;
            String firmAcronymKey = firmStruct.firmAcronym + firmNumberStruct.exchange;
            firmsByAcronym.remove(firmAcronymKey);

            // SEDL6536:  before removing a firm from the firms-by-number cache,
            //            verify that it has the same acronym.  
            // (An inactive firm may have a firm number that duplicates an active firm number.)
            String firmNumberKey = firmNumberStruct.exchange + firmNumberStruct.firmNumber;
            FirmModel numberFirm = (FirmModel) firmsByNumber.get(firmNumberKey);
            if (numberFirm != null && numberFirm.getAcronym().equals(firmStruct.firmAcronym))
            {
                firmsByNumber.remove(firmNumberKey);
            }
        }
    }

    protected FirmModel createFirmModel(FirmStruct struct)
    {
        return new FirmStructModel(struct);
    }
}
