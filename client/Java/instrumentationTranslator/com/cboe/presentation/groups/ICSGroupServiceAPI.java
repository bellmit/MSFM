//
// -----------------------------------------------------------------------------------
// Source file: InstrumentationMonitorAPI.java
//
// PACKAGE: com.cboe.interfaces.instrumentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.groups;

import com.cboe.exceptions.*;
import java.util.Set;
import java.util.List;
import java.util.EventListener;

public interface ICSGroupServiceAPI
{
    //group service
    public void initializeGroupCache() throws CacheInitializationException;
    public void subscribeGroupElementCacheEvents(EventListener groupElementListener);
    public void unsubscribeGroupElementCacheEvents(EventListener groupElementListener);
    public GroupElementModel getRootGroupForGroupType(short groupType) throws SystemException, CommunicationException, NotFoundException;
    public Set<GroupElementModel> getAllGroupsForGroupType(short groupType) throws SystemException, CommunicationException, NotFoundException;
    public Set<GroupElementModel> getSubgroupsForGroup(long groupKey) throws SystemException, CommunicationException;
    public Set<GroupElementModel> getParentsForGroupElement(long elementKey);
    public Set<GroupElementModel> getLeafElementsForGroup(long groupKey) throws SystemException, CommunicationException;
    public Set<GroupElementModel> getAllParentGroupsForGroupElement(long groupElementKey);
    public Set<GroupElementModel> getAllLeafElementsForGroup(long groupElementKey);
    public GroupElementModel findElementKeyByUniqueSearchHashCode(GroupElementModel groupModel);
    public void createElementsForGroup(long groupKey, List<GroupElementModel> newGroupElements)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException;
    public void addElementsToGroup(long groupKey, Set<GroupElementModel> groupElements)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException;
    public void  updateElement(GroupElementModel groupElement)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException;
    public void cloneToGroup(long newGroupKey, GroupElementModel cloneGroup, long[] groupKeys)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException;
    public void removeElementsFromGroup(long groupKey, Set<GroupElementModel> groupElements)
            throws SystemException, TransactionFailedException, DataValidationException, CommunicationException, AuthorizationException;
    public void moveToGroup(long currentGroupKey, long newGroupKey, long[] groupKeys)
             throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException;
 }