//
// -----------------------------------------------------------------------------------
// Source file: AffiliatedFirmCache.java
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

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;
import com.cboe.interfaces.internalPresentation.user.UserFirmAffiliation;
import com.cboe.interfaces.internalPresentation.SystemAdminAPI;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.routingProperty.common.AffiliatedFirmAcronymImpl;

public class AffiliatedFirmCache
{
    private SortedSet<FirmModel> affiliatedFirmModelsSet;
    private SortedSet<AffiliatedFirmAcronym> affiliatedFirmAcronymsSet;
    private List<AffiliatedFirmAcronym> affiliatedFirmAcronymsList;
    private UserFirmAffiliation[] affiliatedFirmsData;

    public AffiliatedFirmCache(SystemAdminAPI systemAdminAPI)
    {

        Comparator<FirmModel> comparator = new Comparator<FirmModel>()
        {
            public int compare(FirmModel o1, FirmModel o2)
            {
                return o1.getFirmNumber().compareTo(o2.getFirmNumber());
            }
        };

        Comparator<AffiliatedFirmAcronym> afComparator = new Comparator<AffiliatedFirmAcronym>()
        {
            public int compare(AffiliatedFirmAcronym o1, AffiliatedFirmAcronym o2)
            {
                return o1.getFirmAcronym().compareTo(o2.getFirmAcronym());
            }
        };

        affiliatedFirmModelsSet = new TreeSet<FirmModel>(comparator);
        affiliatedFirmAcronymsSet = new TreeSet<AffiliatedFirmAcronym>(afComparator);
   
        synchronized(this)
        {
            try
            {
                affiliatedFirmsData = systemAdminAPI.getAllUserAffiliatedFirms();
                createFirms(affiliatedFirmsData);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not obtain the initial set of Affiliated Firms. Affiliated Firms will be invalid.");
            }
        }
    }



    public void createFirms(UserFirmAffiliation[] firmData)
    {
        for(UserFirmAffiliation ufa : firmData)
        {
            FirmStruct struct = new FirmStruct(-1, -1,
                                               new ExchangeFirmStruct(ufa.getExchange(), ufa.getAffiliatedFirm()),
                                               "<Unknown>", "<Unknown>", false, false,
                                               StructBuilder.buildDateTimeStruct(), 0);
            // wrap the new struct
            affiliatedFirmModelsSet.add(new FirmStructModel(struct));
            affiliatedFirmAcronymsSet.add(new AffiliatedFirmAcronymImpl(ufa.getAffiliatedFirm(),ufa.getExchange()));
        }
    }

    public void cleanup()
    {
        affiliatedFirmModelsSet.clear();
    }


    public FirmModel getFirmByAcronym(String affiliatedFirmAcronym, String exchange)
            throws DataValidationException, NotFoundException
    {
        if(affiliatedFirmAcronym == null || affiliatedFirmAcronym.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("affiliatedFirmAcronym was not specified.", 0);
        }
        if(exchange == null || exchange.length() == 0)
        {
            throw ExceptionBuilder.dataValidationException("exchange was not specified.",
                                                           DataValidationCodes.INVALID_EXCHANGE);
        }

        FirmModel foundModel = null;
        for(FirmModel model : affiliatedFirmModelsSet)
        {
            if(affiliatedFirmAcronym.equals(model.getFirmNumber()) && exchange.equals(model.getFirmExchange()))
            {
                foundModel = model;
            }
        }

        if(foundModel == null)
        {
            throw ExceptionBuilder.notFoundException("Affiliated Firm Not Found by Acronym:" +
                                                     affiliatedFirmAcronym + ", Exchange:" +
                                                     exchange + ".", NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return foundModel;
    }

    public List<AffiliatedFirmAcronym> getAffiliatedFirmAcronyms()
    {
        if(affiliatedFirmAcronymsList == null)
        {
            affiliatedFirmAcronymsList = new ArrayList<AffiliatedFirmAcronym>(affiliatedFirmAcronymsSet);
        }
        return affiliatedFirmAcronymsList;
    }



}
