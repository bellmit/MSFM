package com.cboe.presentation.common.formatters;

//
// -----------------------------------------------------------------------------------
// Source file: Utility.java
//
// PACKAGE: com.cboe.presentation.commonBusiness;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;
import java.text.*;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiUtil.DateTimeStruct;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;

import com.cboe.domain.util.DateWrapper;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.api.APIHome;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.exceptions.*;

/**
 * Provides static utility methods.
 */
public class Utility
{
    public static final int MM_ORDER_TYPE = 0;
    public static final int CUST_ORDER_TYPE = 1;
    public static final int BD_ORDER_TYPE = 2;
    public static final int CONT_ORDER_TYPE = 3;

    private static ThreadLocal<DateWrapper> dateWrapperTL = new ThreadLocal<DateWrapper>()
    {
        public DateWrapper initialValue()
        {
            return new DateWrapper();
        }
    };
    private static final DateFormatThreadLocal standardDateFormatterTL = new DateFormatThreadLocal("yyyy/MM/dd hh:mm:ss aa");

    /**
     * @author Nick DePasquale
     * @return java.lang.String
     * @param contingency int
     *
     * Given a contingency number (int), this method returns a string containing
     * the contingency abbreviation.
     *
     * @deprecated  use a {@link com.cboe.presentation.common.formatters.ContingencyFormatter} to format an OrderContingencyStruct.
     */
//    public static String contingencyToString(OrderContingencyStruct contingency)
//    {
//        return FormatFactory.getContingencyFormatStrategy().format(contingency);
//    }
    /**
     * This method was created by Michael Hatmaker.
     * @param order to determine type of.
     * @return int
     * @deprecated
     */
    public static int getOrderType(OrderStruct order)
    {
        //?? This method should examine the order and tell what type of member
        //generated the order (Broker/Dealer, Marketmaker, etc.)
        return MM_ORDER_TYPE;
    }
    /**
     * Calculates the remaining quantity on an order.
     * @param order OrderDetailStruct
     * @return int
     */
    public static int getRemainingQuantity(OrderDetailStruct order)
    {
        return getRemainingQuantity(order.orderStruct);
    }
    /**
     * Calculates the remaining quantity on an order.
     * @param order OrderDetailStruct
     * @return int
     */
    public static int getRemainingQuantity(OrderStruct order)
    {
        //return getRemainingQuantity(order.originalQuantity, order.addedQuantity, order.tradedQuantity, order.cancelledQuantity, order.bustedQuantity);
        return order.leavesQuantity;
    }

    /**
     * This method was created by Michael Hatmaker.
     * @return boolean
     * @param order com.cboe.idl.cmiOrder.OrderDetailStruct
     */
//    static public boolean isOpenOrder(OrderDetailStruct order)
//    {
//        return (getRemainingQuantity(order) > 0);
//    }
    /**
     * This method inspects order state and returns true if passed in
     * order considered to be "active".
     *
     * @author Alex Brazhnichenko
     *
     * @return boolean
     * @param order com.cboe.idl.cmiOrder.OrderDetailStruct
     */
//    public static boolean isOrderActive(OrderDetailStruct order)
//    {
//        boolean isActive;
//        if (order != null)
//        {
//            if (order.orderStruct.state == OrderStates.BOOKED || order.orderStruct.state == OrderStates.OPEN_OUTCRY || order.orderStruct.state == OrderStates.ACTIVE)
//            {
//                isActive = true;
//            }
//            else
//            {
//                isActive = false;
//            }
//        }
//        else
//        {
//            isActive = false;
//        }
//        return isActive;
//    }
    /**
     * @return java.lang.String
     * @param state short
     * @deprecated Use TradingSessionFormatStrategy from FormatFactory in com.cboe.presentation.utility package.
     */
    public static String tradingSessionStateToString(short state)
    {
        String _state = TradingSessionStates.toString(state);
        try
        {
            Short.parseShort(_state);
            // if no exception is thrown, then it's a valid session state
            GUILoggerHome.find().debug("com.cboe.presentation.common.formatters.Utility.tradingSessionStateToString()",
                    GUILoggerBusinessProperty.TRADING_SESSION,
                                       "Invalid Trading Session State. State = " + state);
        }
        catch ( NumberFormatException e)
        {
            // do nothing;
        }
        return _state;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     * @param state short
     * @deprecated Use ProductFormatStrategy from FormatFactory in com.cboe.presentation.utility package.
     */
    public static String productStateToString(short state)
    {
        return ProductStates.toString(state, ProductStates.BRIEF_FORMAT);
    }

    /**
     * Will return a text String representing the passed char side.
     * @param side to convert to String
     * @return user representation of char, will return empty String if
     * passed side is not valid. See Sides interface for valid options.
     * @see com.cboe.presentation.common.formatters.Sides
     */
    public static String sideToString(char side)
    {
        return Sides.toString(side, Sides.NO_BID_FORMAT);
    }
    /**
     * Will return a text String representing the passed char time in force.
     * @param timeInForce to convert to String
     * @return user representation of char, will return empty String if
     * passed timeInForce is not valid. See TimesInForce interface for valid options.
     * @see com.cboe.presentation.common.formatters.TimesInForce.
     */
    public static String timeInForceToString(char timeInForce)
    {
        return TimesInForce.toString(timeInForce, TimesInForce.BRIEF_FORMAT);
    }
    /**
     * Converts the passed DateTimeStruct to a standard String.
     * @param dateTime to convert
     * @return String in pretty format
     */
    public static String toString(DateTimeStruct dateTime)
    {
        DateWrapper dateWrapper = dateWrapperTL.get();
        dateWrapper.setDateTime(dateTime);
        return standardDateFormatterTL.get().format(dateWrapper.getDate());
    }
    /**
     * Converts the passed DateTimeStruct to a standard String.
     * @param dateTime to convert
     * @param formatSequence to pass to SimpleDateFormat to provide your own format code
     * for how SimpleDateFormat builds its pretty name.
     * @return String in pretty format
     */
    public static String toString(DateTimeStruct dateTime, String formatSequence)
    {
        SimpleDateFormat specialFormatter = new SimpleDateFormat(formatSequence);
        DateWrapper dateWrapper = dateWrapperTL.get();
        dateWrapper.setDateTime(dateTime);
        return specialFormatter.format(dateWrapper.getDate());
    }

    public static boolean isStrategy(int classKey)
    {
        boolean isStrategy = false;
        try
        {
            isStrategy = APIHome.findProductQueryAPI().isStrategy(classKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return isStrategy;
    }

    public static SessionProduct getProductByKeyForSession(String sessionName, int productKey)
    {
        SessionProduct product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(sessionName, productKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return product;
    }

    public static Product getProductByKey(int productKey)
    {
        Product product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(productKey);
        }
        catch( UserException e )
        {
            GUILoggerHome.find().exception(e);
        }
        return product;
    }

    public static Product getProduct(int productKey)
    {
        Product product = null;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(productKey);
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        return product;
    }

    public static ProductClass getProductClass(int classKey)
    {
        ProductClass productClass = null;
        try
        {
            productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e.details.message, e);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        return productClass;
    }

    // this is used temporarily while we port all report formatters to Jasper. It should be removed when all are ported
    public static void portWarningPorted(String caller)
    {
        StringBuffer alarmText = new StringBuffer("Jasper port warning type 1: this report has been ported to Jasper.\n"
                                                + "The format method was called unexpectedly by " + caller);
        alarmText.append(getStackTrace());
        GUILoggerHome.find().debug(alarmText.toString(), GUILoggerBusinessProperty.REPORT_GENERATION);
    }
    // this is used temporarily while we port all report formatters to Jasper. It should be removed when all are ported
    public static void portWarningToBePorted(String caller)
    {
        StringBuffer alarmText = new StringBuffer("Jasper port warning type 2: this report not yet been ported to Jasper.\n"
                                                + "The format method was called unexpectedly by " + caller);
        alarmText.append(getStackTrace());
        GUILoggerHome.find().debug(alarmText.toString(), GUILoggerBusinessProperty.REPORT_GENERATION);
    }

    public static String getStackTrace()
    {
        Exception exception = null;

        try
        {
            throw new Exception();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        String s = getStackTrace(exception, "\n");

        return s.substring(s.indexOf(',', s.indexOf(',') + 1) + 1);
    }

    public static String getStackTrace(Throwable e, String delimiter)
    {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String s = sw.toString();

        StringTokenizer tokenizer = new StringTokenizer(s, "\n");
        StringBuffer buffer = new StringBuffer(s.length());

        boolean b = tokenizer.hasMoreTokens();
        while (b)
        {
            s = tokenizer.nextToken().trim();

            if (s.startsWith("at "))
            {
                buffer.append(s.substring(3));
            }
            else
            {
                buffer.append(s);
            }

            b = tokenizer.hasMoreTokens();
            if (b == true)
            {
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }

}






