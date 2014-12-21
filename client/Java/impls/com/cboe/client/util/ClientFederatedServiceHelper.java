//
// -----------------------------------------------------------------------------------
// Source file: FederatedServiceHelper2.java
//
// PACKAGE: com.cboe.server.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.client.util;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.constants.PropertyFederatedBulkOperation;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.idl.util.SummaryStruct;
import com.cboe.idl.constants.FederatedOperationType;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.interfaces.domain.groupService.BulkActionRequest;
import com.cboe.interfaces.domain.groupService.BulkActionRequestHome;
import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiConstants.TimesInForce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.cboe.idl.cmiConstants.TimesInForce;

/**
 * Base Helper class to render all common functionalities to process federated responses.
 *
 * @author Cherian Mathew
 * @see com.cboe.ohs.receivers.ohs.OrderHandlingServiceHelper
 * @see com.cboe.businessServices.marketMakerQuoteService.MarketMakerQuoteServiceHelper
 */
public final class ClientFederatedServiceHelper
{
    public static final String EMPTY_STRING = "";
    public static final String CANCEL_BULK_SIZE = "cancelBulkSize";
    public static final String CANCEL_BULK_WAIT = "cancelBulkWaitMillis";
    public static final String CANCEL_FAILED = "X";
    public static final String CANCEL_ROUTED_AWAY = "R";
    public static final String CANCEL_SUCCESSFUL = "C";
    public static final String CANCEL_ID_HELP_DESK = "Help Desk";
    public static final String REQ_SUBMITTED = "Request submitted to the Thread Pool";


    /**
     * Utility method to fetch the value of a given key from the <code>KeyValueStruct</code> array.
     *
     * @param properties - The source <code>KeyValueStruct</code> array
     * @param key        - The <code>key</code> to find
     * @return Returns the <code>value</code> of the given <code>key</code>
     */
    public static String getValueFromKeyValueStruct(final KeyValueStruct[] properties, final String key)
    {
        String value = EMPTY_STRING;
        if (null != properties)
        {
            for (KeyValueStruct keyValueStruct : properties)
            {
                if (keyValueStruct.key.equals(key))
                {
                    value = keyValueStruct.value;
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Utility method to prepare the optional text for a certain federated request
     *
     * @param properties - The source <code>KeyValueStruct</code> array
     * @return Returns an optional text for a given federated request
     */
    private static String prepareOptionalText(final KeyValueStruct[] properties)
    {
        if (null == properties)
        {
            return EMPTY_STRING;
        }

        StringBuilder optionalText = new StringBuilder(128);
        for (KeyValueStruct keyValueStruct : properties)
        {
            optionalText.append("[").append(keyValueStruct.key).append("=");
            optionalText.append(keyValueStruct.value).append("] ");
        }
        return optionalText.toString();
    }

    /**
     * Utility method to prepare the <code>SummaryStruct</code> for federated responses through even channel
     *
     * @param successfull    - The total number of successfull request or the result
     * @param activeQuantity - The total number of actual request
     * @param failed         - The total number of failed request
     * @param transactionId  - The unique identifier of the transaction
     * @param serverName     - The name of the server in which the request was processed
     * @param properties     - The <code>KeyValueStruct<code> array of properties
     * @param operationType  - The <code>operationType</code>
     * @return Returns an instance of <code>SummaryStruct</code> with all the results
     */
    public static SummaryStruct prepareFederatedResponseSummaryStruct(final int successfull,
                                                                      final int activeQuantity,
                                                                      final int failed,
                                                                      final String transactionId,
                                                                      final String serverName,
                                                                      final KeyValueStruct[] properties,
                                                                      final short operationType)
    {
        SummaryStruct summaryStruct = new SummaryStruct();
        summaryStruct.transactionId = transactionId;
        summaryStruct.serverId = serverName;
        summaryStruct.groupKey = -1;
        try
        {
            String groupKey = getValueFromKeyValueStruct(properties, PropertyFederatedBulkOperation.GROUP_KEY);
            if (!isStringEmpty(groupKey))
            {
                summaryStruct.groupKey = Long.parseLong(groupKey);
            }
        }
        catch (NumberFormatException nfe)
        {
            Log.exception(
                    "<<<FederatedServiceHelper>>> NumberFormatException occurred while parsing the group key :",
                    nfe);
        }
        summaryStruct.successfull = successfull;
        summaryStruct.activeQuantity = activeQuantity;
        summaryStruct.failed = failed;
        summaryStruct.operationType = operationType;

        return summaryStruct;
    }


    /**
     * Utility method to save the request information to the persistence layer
     *
     * @param timeStamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param requestType            - The type of action being requested
     * @param transactionId          - The unique identifier of the transaction
     * @param serverName             - The name of the server in which the request was processed
     * @param properties             - The <code>KeyValueStruct<code> array of properties
     * @return Returns a handle to the <code>BulkActionRequest</code>
     */
    public static BulkActionRequest persistBulkActionRequest(final BulkActionRequestHome bulkActionRequestHome,
                                                             final DateTimeStruct timeStamp,
                                                             final String userIdRequestingCancel,
                                                             final String requestType, final String transactionId,
                                                             final String serverName,
                                                             final KeyValueStruct[] properties)
    {
        boolean committed = false;
        DateWrapper wrapper = new DateWrapper(timeStamp);
        BulkActionRequest request = null;
        try
        {
            Transaction.startTransaction();
            request = bulkActionRequestHome.create(requestType, userIdRequestingCancel, transactionId, serverName,
                                                        prepareOptionalText(properties), wrapper.getTimeInMillis());
            committed = Transaction.commit();
        }
        catch (SystemException se)
        {
            Log.exception(
                    "<<<FederatedServiceHelper>>> SystemException occurred while creating the BulkActionRequest entry :",
                    se);
        }
        finally
        {
            if (!committed)
            {
                Log.information("<<<FederatedServiceHelper>>> The request information couldn't be saved !!!");
                Transaction.rollback();
            }
        }
        return request;
    }

    /**
     * Utility method to get the list of BC's and eliminate the underlying service.
     *
     * @param routeMap - The list of all BC's
     * @return Returns the list of BC's after eliminating the underlying service
     */
    public static List<String> getServiceRoutes(Map routeMap)
    {
        if(routeMap==null)
        {
            throw new IllegalArgumentException("RouteMap[the list of BC's] is null");
        }

        Set<String> keys = routeMap.keySet();
        List<String> serviceRoutes = new ArrayList<String>(keys.size());
        for (String route : keys)
        {
            //String route = (String) o;
            // check for not calling underlying service
            if (isValidBCCall(route))
            {
                serviceRoutes.add(route);
            }
        }

        return serviceRoutes;
    }

    /**
     * Internal helper method check whether a given service name is that of an
     * underlying session.
     *
     * @param serviceRoute - The service route name
     * @return Returns "true" if the given service name is not of an underlying session, otherwise "false".
     */
    private static boolean isValidBCCall(String serviceRoute)
    {
        StringTokenizer st = new StringTokenizer(serviceRoute, ":");
        String strSessionName = st.nextToken();
        return !TradingSessionNameHelper.isUnderlyingSession(strSessionName);
    }

    /**
     * Helper method to check if a given string value is empty
     *
     * @param value - The string to check
     * @return Returns "true" if the string is empty, otherwise "false"
     */
    public static boolean isStringEmpty(String value)
    {
        boolean isStringEmpty=false;
        if (value==null||value.trim().length() == 0)
        {
            isStringEmpty= true;
        }
        return isStringEmpty;
    }

    /**
     * Helper method to construct the <code>ServerResponseStruct</code> with the given input parameters.
     *
     * @param serverId    - The actual BC name / the RouteName on which the call is made
     * @param errorCode   - The error code related to the call
     * @param description - The description of the call
     * @return Returns a new <code>ServerResponseStruct</code>
     */
    public static ServerResponseStruct getServerResponseStruct(String serverId, short errorCode, String description)
    {
        ServerResponseStruct serverResponseStruct = new ServerResponseStruct();
        serverResponseStruct.serverId = serverId;
        serverResponseStruct.errorCode = errorCode;
        serverResponseStruct.description = description;
        return serverResponseStruct;
    }

    /**
     * Helper method to validate the unique identifier of the user
     *
     * @param userIdRequestingCancel - The unique identifier of the user
     * @throws com.cboe.exceptions.DataValidationException - If the unique identifier of the user is not found
     */
    public static void validateUserIdRequestingCancel(final String userIdRequestingCancel)
            throws DataValidationException
    {
        if (isStringEmpty(userIdRequestingCancel))
        {
            throw ExceptionBuilder.dataValidationException("Invalid UserId Requesting Cancel",
                                                           DataValidationCodes.INVALID_USERID_REQUESTING_CANCEL);
        }
    }

    /**
     * Helper method to validate the unique identifier of the users whose orders/quotes bulk request is recieved
     *
     * @param userIds - The unique identifier of the users whose orders/quotes bulk request is recieved
     * @throws com.cboe.exceptions.DataValidationException - If the unique identifier of the users does not exists
     */
    public static void validateUserIds(final String[] userIds) throws DataValidationException
    {
        if (null == userIds || userIds.length == 0)
        {
            throw ExceptionBuilder.dataValidationException("UserIds list is empty",
                                                           DataValidationCodes.INVALID_USERID_LIST);
        }
    }

    /**
     * Helper method to validate the workstartionID for a bulk request
     *
     * @param properties - The extended property array <code>KeyValueStruct</code>
     * @param key        - The key to find from the <code>KeyValueStruct</code>
     * @throws com.cboe.exceptions.DataValidationException - If a valid <code>workstationID</code> is not found in the extended properties
     */
    public static void validateWorkstationID(final KeyValueStruct[] properties, final String key)
            throws DataValidationException
    {
        if (isStringEmpty(getValueFromKeyValueStruct(properties, key)))
        {
            throw ExceptionBuilder
                    .dataValidationException("Invalid WorkStation ID", DataValidationCodes.INVALID_WORKSTATION_ID);
        }
    }
    /**
     * Utility method to validate the operation type
     *
     * @param operationType - The operation type
     * @throws com.cboe.exceptions.DataValidationException - If the operation type is invalid
     */
    public static void validateOperationType(short operationType) throws DataValidationException
    {
        if ((operationType != FederatedOperationType.ORDERS) && (operationType != FederatedOperationType.IORDERS))
        {
            throw ExceptionBuilder
                    .dataValidationException("Invalid operation type", DataValidationCodes.INVALID_OPERATION_TYPE);
        }
    }

    /**
     * Utility method to validate the order type
     *
     * @param orderType - The operation type
     * @throws com.cboe.exceptions.DataValidationException - If the operation type is invalid
     *
     * @author Cognizant Technology Solutions.
     */
    public static void validateOrderTypes(final char[] orderTypes) throws DataValidationException
    {
        if(orderTypes != null && orderTypes.length > 0)
        {
            for(char orderType : orderTypes)
            {
                if(orderType != TimesInForce.DAY && orderType != TimesInForce.GTC && orderType != TimesInForce.GTD)
                {
                    throw ExceptionBuilder
                    .dataValidationException("Invalid order type ", DataValidationCodes.INVALID_TIME_IN_FORCE);
                }
            }
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Invalid order type", DataValidationCodes.INVALID_TIME_IN_FORCE);
        }
    }

    /**
     * Utility method to validate the correspondent Firms.
     *
     * @param corrrespondentFirmValues
     * @throws com.cboe.exceptions.DataValidationException
     *
     * @author Cognizant Technology Solutions.
     */
    public static void validateCorrespondentFirm(String[] corrrespondentFirmValues) throws DataValidationException
    {
        if(corrrespondentFirmValues == null)
        {
            throw ExceptionBuilder.dataValidationException("Invalid corrrespondentFirmValues", 0);//FIXME: Appropriate error code not found.
        }
    }

    /**
     * Utility method to validate the classKeys.
     *
     * @param classKeys - array of classKeys
     * @throws com.cboe.exceptions.DataValidationException
     *
     * @author Cognizant Technology Solutions.
     */
    public static void validateClassKeys(int[] classKeys) throws DataValidationException
    {
        if(classKeys == null)
        {
            throw ExceptionBuilder.dataValidationException("Invalid classKeys", DataValidationCodes.INVALID_PRODUCT_CLASS);
        }
    }

}
