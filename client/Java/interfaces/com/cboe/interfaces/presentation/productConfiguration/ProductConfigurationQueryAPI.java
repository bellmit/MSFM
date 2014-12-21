//
// -----------------------------------------------------------------------------------
// Source file: ProductConfigurationQueryAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.productConfiguration
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.productConfiguration;

import com.cboe.idl.internalBusinessServices.ProductConfigurationQueryServiceOperations;
import com.cboe.idl.product.GroupStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.presentation.product.ProductClass;

public interface ProductConfigurationQueryAPI extends ProductConfigurationQueryServiceOperations
{
    String TRANSLATOR_NAME = "PCQS_TRANSLATOR";
    String ALLOW_PCQS_ACCESS_PROPERTY_NAME = "AllowPCQSAPIAccess";

    int DEFAULT_POST_ASSIGNMENT_GROUP_TYPE = 40;
    String DEFAULT_POST_ASSIGNMENT_GROUP_TYPE_DESC = "PostAssignmentsGroup";

    int DEFAULT_PROCESS_GROUP_TYPE = 2;
    String DEFAULT_PROCESS_GROUP_TYPE_DESC = "ProcessGroup";
    
    public boolean isPostGroup(GroupStruct group);
    public boolean isStationGroup(GroupStruct group);

    public GroupStruct getPostGroupForStation(GroupStruct stationGroup)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public GroupStruct getGroup(int groupKey, int groupType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public int getPostStationGroupType();

    public int getProcessGroupType();
    
    public ProductClass[] getProductClassesByGroupKey(int groupKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    public GroupStruct[] getGroupsForProductClass(int productClassKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
}