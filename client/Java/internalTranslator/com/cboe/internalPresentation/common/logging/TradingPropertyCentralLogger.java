//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyCentralLogger.java
//
// PACKAGE: com.cboe.internalPresentation.common.logging
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.logging;

import java.lang.reflect.InvocationTargetException;

import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradingPropertyFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.userSession.UserSessionFactory;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;

/**
 * Helper class to prepare the necessary trading property information.
 * @author Cherian Mathew
 */
public class TradingPropertyCentralLogger
{
    public static final String OPERATION_ADD_UPDATE = "ADD/UPDATE";
    public static final String OPERATION_REMOVE = "REMOVE";
    public static final String NONE = "NONE";
    public static final String TRADING_PROPERTY_AUDIT = "TradingPropertyAudit";

    public static final String FAILED = "Failed with exception";

    private static final String PIPE = "\u007C";
    private static final TradingPropertyFormatStrategy FORMATTER =
            CommonFormatFactory.getTradingPropertyFormatStrategy();
    private static final String DEFAULT_TEXT_FORMAT =
            TradingPropertyFormatStrategy.PROPERTY_DEFINITION_NAME_VALUE_STYLE_NAME;

    private static final ProductClassFormatStrategy PC_FORMATTER =
            CommonFormatFactory.getProductClassFormatStrategy();

    /**
     * Prepares a formatted <code>String</code> representation of the trading properties
     * @param struct - The <code>PropertyGroupStruct</code> representing the trading properties
     * @return Returns a formatted <code>String<code> representation of the trading properties
     */
    public static String getFormattedTradingProperties(PropertyGroupStruct struct)
    {
        if(struct == null)
        {
            return NONE;
        }

        return getFormattedTradingProperties(getPropertyItem(struct.propertyKey, 1),
                                             getTradingPropertyKeyClassKey(struct.propertyKey),
                                             getPropertyItem(struct.propertyKey, 0));
    }

    /**
     * Prepares a formatted <code>String</code> representation of the trading properties
     * @param sessionName - The name of the trading session
     * @param tradingPropertyName - The name of the trading property
     * @return Returns a formatted <code>String</code> representation of the trading properties
     */
    public static String getFormattedTradingProperties(String sessionName,
                                                       String tradingPropertyName)
    {
        TradingPropertyGroup tpg = null;
        try
        {
            tpg = getTradingPropertyGroup(sessionName, ProductClass.DEFAULT_CLASS_KEY,
                                          tradingPropertyName);
        }
        catch(Exception ite)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getFormattedTradingProperties(sessionName, tradingPropertyName)",
                                           " Exception while finding the TradingPropertyGroup :",
                                           ite);
            return NONE;
        }
        return getFormattedTradingProperties(tpg);
    }

    /**
     * Prepares a formatted <code>String</code> representation of the trading properties
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the product class
     * @param propertyType - The type of the trading property
     * @return Returns a formatted <code>String</code> representation of the trading properties
     */
    public static String getFormattedTradingProperties(String sessionName, int classKey,
                                                       int propertyType)
    {
        return getFormattedTradingProperties(sessionName, classKey,
                                             TradingPropertyFactoryHome.find().getPropertyName(
                                                     propertyType));
    }

    /**
     * Prepares a formatted <code>String</code> representation of the trading properties
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the product class
     * @param tradingPropertyName - The name of the trading property
     * @return Returns a formatted <code>String</code> representation of the trading properties
     */
    public static String getFormattedTradingProperties(String sessionName, int classKey,
                                                       String tradingPropertyName)
    {
        TradingPropertyGroup tpg = null;
        try
        {
            tpg = getTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getFormattedTradingProperties(sessionName, classKey, tradingPropertyName)",
                                           " Exception while finding the TradingPropertyGroup :",
                                           e);
            return NONE;
        }
        return getFormattedTradingProperties(tpg);
    }

    /**
     * Prepares a formatted <code>String</code> representation of the trading properties
     * @param sessionName - The name of the trading session
     * @param propertyType - The type of the trading property
     * @return Returns a formatted <code>String</code> representation of the trading properties
     */
    public static String getFormattedTradingProperties(String sessionName, int propertyType)
    {
        TradingPropertyGroup tpg = null;
        try
        {
            tpg = getTradingPropertyGroup(sessionName, ProductClass.DEFAULT_CLASS_KEY,
                                          TradingPropertyFactoryHome
                                                  .find().getPropertyName(propertyType));
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getFormattedTradingProperties(sessionName, propertyType)",
                                           " Exception while finding the TradingPropertyGroup :",
                                           e);
            return NONE;
        }
        return getFormattedTradingProperties(tpg);
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the old and the new properties.
     * @param operationName - The operation name (ADD/UPDATE/REMOVE)
     * @param oldProperties - A formatted <code>String</code> represenation of the old properties
     * @param struct - The <code>PropertyGroupStruct</code> representing the new values of the
     * trading property
     * @return Return a delimeted <code>String</code> representation of the old and the new
     *         properties.
     */
    public static String getMessageText(String operationName, String oldProperties,
                                        PropertyGroupStruct struct)
    {
        TradingPropertyGroup tpg = null;
        String messageText = FAILED;
        try
        {
            tpg = getTradingPropertyGroupForPGS(struct);
            messageText = getMessageText(operationName, oldProperties, tpg);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getMessageText(operationName, oldProperties, struct)",
                                           " Exception while finding the TradingPropertyGroup :",
                                           e);
        }
        return messageText;
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the trading property this is
     * removed
     * @param struct - The <code>PropertyGroupStruct</code> representing the values of the trading
     * property being removed
     * @return Returns a delimeted <code>String</code> representation of the trading property that
     *         is removed
     */
    public static String getMessageTextForRemoveProperties(PropertyGroupStruct struct)
    {
        if(struct == null)
        {
            return NONE;
        }
        TradingPropertyGroup tpg = null;
        String messageText = FAILED;
        try
        {
            tpg = getTradingPropertyGroupForPGS(struct);
            messageText = getMessageTextForRemove(tpg);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getMessageTextForRemoveProperties(struct)",
                                           " Exception while finding the TradingPropertyGroup :",
                                           e);
        }
        return messageText;
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the old and the new properties.
     * @param operationName - The name of the operation (ADD/UPDATE/REMOVE)
     * @param oldProperties - The formatted old property string
     * @param sessionName - The name of the trading session
     * @param tradingPropertyName - The name of thr trading property
     * @return Returns a delimeted <code>String</code> representation of the trading property
     */
    public static String getMessageText(String operationName, String oldProperties,
                                        String sessionName, String tradingPropertyName)
    {
        return getMessageText(operationName, oldProperties, sessionName,
                              ProductClass.DEFAULT_CLASS_KEY, tradingPropertyName);
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the old and the new properties.
     * @param operationName - The name of the operation (ADD/UPDATE/REMOVE)
     * @param oldProperties - The formatted old property string
     * @param sessionName - The name of the trading session
     * @param propertyType - The type of the trading property
     * @return Returns a delimeted <code>String</code> representation of the trading property
     */
    public static String getMessageText(String operationName, String oldProperties,
                                        String sessionName, int propertyType)
    {
        return getMessageText(operationName, oldProperties, sessionName,
                              ProductClass.DEFAULT_CLASS_KEY, propertyType);
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the old and the new properties.
     * @param operationName - The name of the operation (ADD/UPDATE/REMOVE)
     * @param oldProperties - The formatted old property string
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the product class
     * @param propertyType - The type of the trading property
     * @return Returns a delimeted <code>String</code> representation of the trading property
     */
    public static String getMessageText(String operationName, String oldProperties,
                                        String sessionName, int classKey, int propertyType)
    {
        return getMessageText(operationName, oldProperties, sessionName, classKey,
                              TradingPropertyFactoryHome.find().getPropertyName(propertyType));
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the old and the new properties.
     * @param operationName - The name of the operation (ADD/UPDATE/REMOVE)
     * @param oldProperties - The formatted old property string
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the product class
     * @param tradingPropertyName - The name of the trading property
     * @return Returns a delimeted <code>String</code> representation of the trading property
     */
    public static String getMessageText(String operationName, String oldProperties,
                                        String sessionName, int classKey,
                                        String tradingPropertyName)
    {
        TradingPropertyGroup tpg = null;
        String messageText = null;
        try
        {
            tpg = getTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
            messageText = getMessageText(operationName, oldProperties, tpg);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getMessageText(operationName, oldProperties, sessionName, classKey, tradingPropertyName)",
                                           " Exception while formatting the message text :", e);
            return FAILED;
        }
        return messageText;
    }

    /**
     * Prepares a delimeted <code>String</code> representation of the trading that is removed.
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the product class
     * @param propertyType - The type of the trading property
     * @return Returns a delimeted <code>String</code> representation of the trading that is
     *         removed.
     */
    public static String getMessageTextForRemoveTradingProperties(String sessionName, int classKey,
                                                                  int propertyType)
    {
        TradingPropertyGroup tpg = null;
        String messageText = null;
        try
        {
            tpg = getTradingPropertyGroup(sessionName, classKey, TradingPropertyFactoryHome
                    .find().getPropertyName(propertyType));
            messageText = getMessageTextForRemove(tpg);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getMessageTextForRemoveTradingProperties(sessionName, classKey, propertyType)",
                                           " Exception while formatting the message text :", e);
            return FAILED;
        }

        return messageText;
    }

    /**
     * Internal helper method to find the <code>TradingPropertyGroup</code>
     * @param sessionName - The name of the trading session
     * @param classKey - The key of the prodct class
     * @param tradingPropertyName - The name of the trading property
     * @return Returns the <code>TradingPropertyGroup</code> for the given sessionName, classKey and
     *         tradingPropertyName.
     * @throws com.cboe.exceptions.DataValidationException - If there was an DataValidationException
     * while finding
     * @throws com.cboe.exceptions.CommunicationException - If there was an CommunicationException
     * while finding
     * @throws com.cboe.exceptions.AuthorizationException - If there was an AuthorizationException
     * while finding
     * @throws com.cboe.exceptions.SystemException - If there was an SystemException while finding
     * @throws com.cboe.exceptions.NotFoundException - If there was an NotFoundException while
     * finding
     * @throws com.cboe.exceptions.TransactionFailedException - If there was an
     * TransactionFailedException while finding
     * @throws java.lang.reflect.InvocationTargetException - If there was an
     * InvocationTargetException while finding
     */
    private static TradingPropertyGroup getTradingPropertyGroup(String sessionName, int classKey,
                                                                String tradingPropertyName) throws
            DataValidationException, CommunicationException, AuthorizationException,
            SystemException, NotFoundException, TransactionFailedException,
            InvocationTargetException
    {
        TradingPropertyGroup tpg = TradingPropertyFactoryHome.find()
                .getTradingPropertyGroup(sessionName, classKey, tradingPropertyName);
        return tpg;
    }

    /**
     * Internal helper method to prepare a formatted <code>String</code> representation of the given
     * <code>TradingPropertyGroup</code>
     * @param tpg - The <code>TradingPropertyGroup</code> to be formatted
     * @return Returns a <code>String</code> representation of the <code>TradingPropertyGroup</code>
     */
    private static String getFormattedTradingProperties(TradingPropertyGroup tpg)
    {
        if(null == tpg)
        {
            return NONE;
        }

        try
        {
            return FORMATTER.format(tpg, DEFAULT_TEXT_FORMAT);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                           ".getFormattedTradingProperties(tpg)",
                                           " Exception while formatting the TradingPropertyGroup :",
                                           e);
            return FAILED;
        }
    }

    /**
     * Internal helper method to prepare formatted <code>String</code> representation of the given
     * <code>SessionProductClass</code>
     * @param spc - The <code>SessionProductClass</code> to be formatted
     * @return Returns a <code>String</code> representation of the <code>SessionProductClass</code>
     */
    private static String getFormattedProductClass(SessionProductClass spc)
    {
        if(null == spc)
        {
            return NONE;
        }

        try
        {
            return PC_FORMATTER.format(spc, PC_FORMATTER.CLASS_TYPE_NAME);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(
                    GUILoggerSABusinessProperty.PROPERTY_SERVICE + ".getFormattedProductClass(spc)",
                    " Exception while formatting the SessionProductClass :", e);
            return FAILED;
        }
    }

    /**
     * Internal helper method to prepare a <code>TradingPropertyGroup</code> for the given
     * <code>PropertyGroupStruct</code>
     * @param struct - The <code>PropertyGroupStruct</code> for which the
     * <code>TradingPropertyGroup</code> has to be created
     * @return Returns a <code>TradingPropertyGroup</code> for the given
     *         <code>PropertyGroupStruct</code>
     * @throws DataValidationException - If there was an DataValidationException while preparing
     * @throws CommunicationException - If there was an CommunicationException while preparing
     * @throws AuthorizationException - If there was an AuthorizationException while preparing
     * @throws SystemException - If there was an SystemException while preparing
     * @throws NotFoundException - If there was an NotFoundException while preparing
     * @throws TransactionFailedException - If there was an TransactionFailedException while
     * preparing
     * @throws InvocationTargetException - If there was an InvocationTargetException while
     * preparing
     */
    private static TradingPropertyGroup getTradingPropertyGroupForPGS(PropertyGroupStruct struct)
            throws DataValidationException, CommunicationException, AuthorizationException,
            SystemException, NotFoundException, TransactionFailedException,
            InvocationTargetException
    {
        TradingPropertyGroup tpg = getTradingPropertyGroup(getPropertyItem(struct.propertyKey, 1),
                                                           getTradingPropertyKeyClassKey(
                                                                   struct.propertyKey),
                                                           getPropertyItem(struct.propertyKey, 0));
        PropertyServicePropertyGroup pspg = PropertyFactory.createPropertyGroup(struct);
        tpg.setPropertyGroup(pspg);
        return tpg;
    }

    /**
     * Internal helper method to prepare a delimeted <code>String</code> representation of the old
     * and the new properties.
     * @param operationName - The name of the operation (ADD/UDPATE/REMOVE)
     * @param oldProperties - The formatted old property string
     * @param tpg - The <code>TradingPropertyGroup<code> representing the new properties
     * @return Returns a <code>String</code> representation of the old and the new properties.
     * @throws SystemException - If there was an SystemException while preparing
     * @throws DataValidationException - If there was an DataValidationException while preparing
     * @throws CommunicationException - If there was an CommunicationException while preparing
     * @throws AuthorizationException - If there was an AuthorizationException while preparing
     */
    private static String getMessageText(String operationName, String oldProperties,
                                         TradingPropertyGroup tpg) throws SystemException,
            DataValidationException, CommunicationException, AuthorizationException
    {
        SessionProductClass spc = ProductHelper
                .getSessionProductClassCheckInvalid(tpg.getSessionName(), tpg.getClassKey());

        return getMessageText(spc.getTradingSessionName(),
                              tpg.getTradingPropertyType().getFullName(),
                              getFormattedProductClass(spc), operationName, oldProperties,
                              getFormattedTradingProperties(tpg));
    }

    /**
     * Internal helper method to prepare a delimeted <code>String</code> representation of the
     * <code>TradingPropertyGroup</code> of the property that is being removed
     * @param tpg - The <code>TradingPropertyGroup</code> of the property that is being removed
     * @return Returns a <code>String</code> representation of the <code>TradingPropertyGroup</code>
     *         of the property that is being removed
     * @throws SystemException - If there was an SystemException while preparing
     * @throws DataValidationException - If there was an DataValidationException while preparing
     * @throws CommunicationException - If there was an CommunicationException while preparing
     * @throws AuthorizationException - If there was an AuthorizationException while preparing
     */
    private static String getMessageTextForRemove(TradingPropertyGroup tpg) throws SystemException,
            DataValidationException, CommunicationException, AuthorizationException
    {
        SessionProductClass spc = ProductHelper
                .getSessionProductClassCheckInvalid(tpg.getSessionName(), tpg.getClassKey());

        return getMessageText(spc.getTradingSessionName(),
                              tpg.getTradingPropertyType().getFullName(),
                              getFormattedProductClass(spc), OPERATION_REMOVE,
                              getFormattedTradingProperties(tpg), NONE);
    }

    /**
     * Internal helper method to prepare a delimeted <code>String</code> representation of the old
     * and the new trading properties along with the sessionName, propertyName, productClass,
     * operationName, oldProperties, newProperties
     * @param sessionName - The trading session name
     * @param propertyName - The name of the property
     * @param productClass - The product class to which the property is associated with.
     * @param operationName - The name of the operation (ADD/UPDATE/REMOVE)
     * @param oldProperties - The old formatted property string
     * @param newProperties - The new formatted property string
     * @return Returns a delimeted <code>String</code> representation of the old and the new trading
     *         properties along with the sessionName, propertyName, productClass, operationName,
     *         oldProperties, newProperties
     */
    private static String getMessageText(String sessionName, String propertyName,
                                         String productClass, String operationName,
                                         String oldProperties, String newProperties)
    {
        TradingPropertyParser parser = new TradingPropertyParser();
        String[] parsedValues = parser.validateAndSendForParsing(oldProperties, newProperties);

        StringBuilder messageText = new StringBuilder(sessionName);
        messageText.append(PIPE).append(propertyName).append(PIPE);
        messageText.append(productClass).append(PIPE).append(operationName);
        messageText.append(PIPE).append(parsedValues[0]).append(PIPE);
        messageText.append(parsedValues[1]).append(PIPE);
        messageText.append(UserSessionFactory.findUserSession().getUserModel().getUserId());
        return messageText.toString();
    }

    /**
     * Internal helper method to extract a value from the trading property key
     * @param tradingPropertyKey - The key of the trading property from which the value should be
     * extracted
     * @param offset - The index position
     * @return Returns the extracted value
     */
    private static String getPropertyItem(String tradingPropertyKey, int offset)
    {
        String[] parsedKeys = BasicPropertyParser.parseArray(tradingPropertyKey);
        if(parsedKeys.length > offset)
        {
            return parsedKeys[offset];
        }
        else
        {
            return "";
        }
    }

    /**
     * Internal helper method to extract the class key from a trading property key
     * @param tradingPropertyKey - The key of the trading property from which the class key should
     * be extracted
     * @return Returns the class key if found, otherwise '0'
     */
    private static int getTradingPropertyKeyClassKey(String tradingPropertyKey)
    {
        String returnClassKey = getPropertyItem(tradingPropertyKey, 2);
        if(returnClassKey.length() == 0)
        {
            return 0;
        }
        else
        {
            try
            {
                return Integer.parseInt(returnClassKey);
            }
            catch(NumberFormatException e)
            {
                GUILoggerHome.find().exception(GUILoggerSABusinessProperty.PROPERTY_SERVICE +
                                               ".getTradingPropertyKeyClassKey(tradingPropertyKey)",
                                               " Exception while parsing the classKey :", e);
                return 0;
            }
        }
    }
}
