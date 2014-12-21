//
// -----------------------------------------------------------------------------------
// Source file: LinkageStateCache.java
//
// PACKAGE: com.cboe.internalPresentation.product.models
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import java.util.*;

import com.cboe.idl.cmiConstants.LinkageIndicatorReturnTypes;
import com.cboe.idl.product.LinkageIndicatorResultStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.interfaces.presentation.preferences.BusinessPreferenceManager;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.common.properties.PropertiesFile;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

public class LinkageStateCache implements EventChannelListener
{
    private Map<String, Map<Integer, Boolean>> sessionMap =
            new HashMap<String, Map<Integer, Boolean>>(16);
    private List<LinkageStateCacheListener> cacheListeners =
            new ArrayList<LinkageStateCacheListener>(16);

    public static final String APP_PROPERTIES_SESSION_SECTION_NAME = "Session";
    private static final String TRADING_SESSION_PREF_NAME = "DefaultTradingSession";

    public LinkageStateCache() throws SystemException, CommunicationException,
            DataValidationException, AuthorizationException
    {
        SystemAdminAPIFactory.find().subscribeLinkageStatus(this);
    }

    public synchronized boolean[] getLinkageState(String sessionName, int[] productClasses,
                                                  boolean isRefresh) throws SystemException,
            TransactionFailedException, DataValidationException, CommunicationException,
            AuthorizationException
    {
        Map<Integer, Boolean> linkageStateMap = getSessionMap(sessionName);
        int[] refresh = null;
        if(isRefresh)
        {
            refresh = productClasses;
        }
        else
        {
            List<Integer> refreshList = new ArrayList<Integer>(16);
            for(int classKey : productClasses)
            {
                if(!linkageStateMap.containsKey(classKey))
                {
                    refreshList.add(classKey);
                }
            }
            refresh = new int[refreshList.size()];
            for(int index = 0; index < refreshList.size(); index++)
            {
                refresh[index] = refreshList.get(index);
            }
        }

        if(refresh.length > 0)
        {
            String userid = UserSessionFactory.findUserSession().getUserModel().getUserId();
            LinkageIndicatorResultStruct[] linkageResults = SystemAdminAPIFactory.find()
                    .getLinkageIndicators(userid, sessionName, productClasses);
            setLinkageState(linkageResults, false);
        }

        boolean[] results = new boolean[productClasses.length];
        for(int index = 0; index < productClasses.length; index++)
        {
            Boolean enabled = linkageStateMap.get(productClasses[index]);
            results[index] = ((enabled != null) && enabled);
        }
        return results;
    }

    public synchronized boolean[] getLinkageState(String sessionName, int[] productClasses) throws
            SystemException, TransactionFailedException, DataValidationException,
            CommunicationException, AuthorizationException
    {
        return getLinkageState(sessionName, productClasses, false);
    }

    public synchronized void setLinkageState(LinkageIndicatorResultStruct[] linkageStructs)
    {
        setLinkageState(linkageStructs, true);
    }

    private void setLinkageState(LinkageIndicatorResultStruct[] linkageStructs, boolean isNotify)
    {
        for(LinkageIndicatorResultStruct struct : linkageStructs)
        {
            if(struct.linkageIndicatorReturnType == LinkageIndicatorReturnTypes.SUCCESS)
            {
                String retVal = null;
                PropertiesFile propertiesFile = AppPropertiesFileFactory.find();
                retVal = propertiesFile.getValue(APP_PROPERTIES_SESSION_SECTION_NAME, TRADING_SESSION_PREF_NAME);
                Map<Integer, Boolean> linkageStateMap = getSessionMap(retVal);
//                Map<Integer, Boolean> linkageStateMap = getSessionMap(BusinessPreferenceManager.getDefaultTradingSession());
//                Map<Integer, Boolean> linkageStateMap = getSessionMap(struct.sessionName);
                linkageStateMap.put(struct.classKey, struct.linkageIndicator);
            }
        }

        if(isNotify)
        {
            notifyCacheListeners();
        }
    }

    public synchronized void addLinkageStateCacheListener(LinkageStateCacheListener listener)
    {
        cacheListeners.add(listener);
    }

    public synchronized void removeLinkageStateCacheListener(LinkageStateCacheListener listener)
    {
        cacheListeners.remove(listener);
    }

    private void notifyCacheListeners()
    {
        for(LinkageStateCacheListener listener : cacheListeners)
        {
            listener.linkageCacheUpdated();
        }
    }

    private Map<Integer, Boolean> getSessionMap(String sessionName)
    {
        Map<Integer, Boolean> linkageStateMap = sessionMap.get(sessionName);
        if(linkageStateMap == null)
        {
            linkageStateMap = new HashMap<Integer, Boolean>(1024);
            sessionMap.put(sessionName, linkageStateMap);
        }
        return linkageStateMap;
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        if(channelType == ChannelType.CB_UPDATE_LINKAGE_INDICATOR)
        {
            LinkageIndicatorResultStruct[] structs =
                    (LinkageIndicatorResultStruct[]) event.getEventData();
            setLinkageState(structs);
        }
    }

}
