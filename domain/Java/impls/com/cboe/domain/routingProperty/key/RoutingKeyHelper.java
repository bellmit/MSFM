package com.cboe.domain.routingProperty.key;

import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.businessServices.TradingSessionServiceHome;
import com.cboe.interfaces.domain.property.PropertyDefinition;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyDefinitionCache;
import com.cboe.domain.routingProperty.key.PropertyKeyElementDefinition;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Helper class to parse and build routing property keys
 */
public class RoutingKeyHelper
{
    public static final int SESSION_KEY_POSITION = 0;
    public static final int EXCHANGE_KEY_POSITION = 1;
    public static final int FIRM_KEY_POSITION = 2;

    private static TradingSessionService tradeSessionService;

    private RoutingKeyHelper() { }

    public static SessionFirmClassKey create(String propertyName, String firmAcronym, String exchangeAcronym,
                                    String sessionName, int classkey)
    {
        return new SessionFirmClassKey(propertyName, firmAcronym, exchangeAcronym, sessionName, classkey);
    }

    public static SessionFirmClassOriginKey create(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
                                    int classkey, char origin)
    {
        return new SessionFirmClassOriginKey(propertyName, sessionName, firmAcronym, exchangeAcronym, classkey, origin);
    }

    public static SessionFirmPostStationKey create(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
            int post, int station)
    {
        return new SessionFirmPostStationKey(propertyName, sessionName, firmAcronym, exchangeAcronym, post, station);
    }

    public static SessionPostStationKey create(String propertyName, String sessionName, int post, int station)
    {
        return new SessionPostStationKey(propertyName, sessionName, post, station);
    }
/*
    public static SessionFirmKey create(String propertyName, String sessionName, String firmAcronym,
                                        String exchangeAcronym)
    {
        return new SessionFirmKey(propertyName, sessionName, firmAcronym, exchangeAcronym);
    }
*/

    public static SessionKey create(String propertyName, String sessionName)
    {
        return new SessionKey(propertyName, sessionName);
    }

    public static SessionClassKey create(String propertyName, String sessionName, int classKey)
    {
        return new SessionClassKey(propertyName, sessionName, classKey);
    }

    public static SessionClassOriginKey create(String propertyName, String sessionName, int classKey, char origin)
    {
        return new SessionClassOriginKey(propertyName, sessionName, classKey, origin);
    }

    public static PartialPropertyKey create(String[] arguments)
    {
        return new PartialPropertyKey(arguments);
    }

    public static String[] parsePropertyKey(String propertyKey)
    {
        return BasicPropertyParser.parseArray(propertyKey);
    }

    public static String getKeyElement(String[] keyElements, int offset)
    {
        if (keyElements.length >= offset)
        {
            return keyElements[offset];
        }

        return null;
    }

    public static String getPropertyNameFromKey(String propertyKey)
    {
        String[] elements = RoutingKeyHelper.parsePropertyKey(propertyKey);
        return RoutingKeyHelper.getKeyElement(elements, elements.length-1);
    }

    public static String createBasePropertyKey(String sessionName, String firmAcronym, String exchangeAcronym)
    {
        Object[] elements = new Object[3];
        elements[SESSION_KEY_POSITION] = sessionName;
        elements[EXCHANGE_KEY_POSITION] = exchangeAcronym;
        elements[FIRM_KEY_POSITION] = firmAcronym;

        return createPropertyKey(elements);
    }


    public static String getSessionNameFromKey(String propertyKey)
    {
        String[] elements = RoutingKeyHelper.parsePropertyKey(propertyKey);
        return RoutingKeyHelper.getKeyElement(elements, SESSION_KEY_POSITION);
    }
/*
    public static String getFirmFromKey(String propertyKey)
    {
        String[] elements = RoutingKeyHelper.parsePropertyKey(propertyKey);
        return RoutingKeyHelper.getKeyElement(elements, FIRM_KEY_POSITION);
    }

    public static String getExchangeFromKey(String propertyKey)
    {
        String[] elements = RoutingKeyHelper.parsePropertyKey(propertyKey);
        return RoutingKeyHelper.getKeyElement(elements, EXCHANGE_KEY_POSITION);
    }
*/

    public static String createPropertyKey(Object... elements)
    {
        return BasicPropertyParser.buildCompoundString(elements);
    }

    static TradingSessionService getTradeSessionService()
    {
        if(tradeSessionService == null)
        {
            try
            {
                TradingSessionServiceHome home = (TradingSessionServiceHome) HomeFactory.getInstance().findHome(TradingSessionServiceHome.HOME_NAME);
                tradeSessionService = home.find();
            }
            catch (CBOELoggableException e)
            {
                Log.exception("RoutingPropertyHelper >>> Could not find TradingSessionServiceHome", e);
            }
        }
        return tradeSessionService;
    }


    public static PropertyDefinition getPropertyDefinition(String propertyCategory, String fieldName) throws NotFoundException
    {
        String className = PropertyKeyElementDefinition.class.getSimpleName();
        Object[] elements = {className, fieldName};
        String name = BasicPropertyParser.buildCompoundString(elements);
        PropertyDefinition definition = PropertyDefinitionCache.getInstance().getPropertyDefinition(propertyCategory, name);

        if(definition == null)
        {
            throw ExceptionBuilder.notFoundException("Property Definition does not exist for category.class.field=[ " +
                                                     propertyCategory + "." + className + "." + fieldName + "]",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return definition;
    }

    // TODO remove later, for now keep for Tom's new routing screens
    public static PropertyDefinition findRoutingKeyDefinition(String keyClass, String keyName)
    {
        return null;
    }

}
