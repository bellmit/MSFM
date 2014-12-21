//
// -----------------------------------------------------------------------------------
// Source file: ActivityRecord.java
//
// PACKAGE: com.cboe.presentation.traderActivity;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.traderActivity;

import java.util.*;

import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;
import com.cboe.idl.cmiTraderActivity.ActivityRecordStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.activity.ActivityField;
import com.cboe.interfaces.presentation.common.formatters.Formattable;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.formatters.ActivityFieldTypes;
import com.cboe.presentation.common.formatters.ActivityTypes;
import com.cboe.presentation.common.formatters.ContingencyTypes;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.OrderStates;
import com.cboe.presentation.common.formatters.ProductStates;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.formatters.RfqTypes;
import com.cboe.presentation.common.formatters.Sides;
import com.cboe.presentation.common.formatters.TimesInForce;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;

/**
 * ****************************************************************************** Describes an activity event
 *
 * @see com.cboe.idl.cmiTraderActivity.ActivityRecordStruct
 */
@SuppressWarnings({"UnusedCatchParameter"})
public class ActivityRecord implements Formattable
{

//*** Public Attributes

    // Format constants
    public static final String NAME_VALUE_FORMAT = "NAME_VALUE_FORMAT";
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String ALL_FIELDS_FORMAT = "ALL_FIELDS_FORMAT";
    public static final String ALL_FIELDS_MULTILINE_FORMAT = "ALL_FIELDS_MULTILINE_FORMAT";
    public static final String PRIORITY_FIELD_FORMAT = "PRIORITY_FIELD_FORMAT";
    public static final String PRIORITY_DETAILS_FORMAT = "PRIORITY_DETAILS_FORMAT";
    private final String Category = getClass().getName();

//*** Private Attributes

    private List<ActivityField> m_activityFields = null;
    private ActivityRecordStruct m_activityRecordStruct = null;
    private DateTime m_eventTime = null;
    private Map<String,String> m_formattedStrings = null;
    private String m_productName = null;
    private List<ActivityField> m_priorityDetailFields = null;
    private List<ActivityField> m_priorityTableRowFields = null;
    private List<ActivityField> m_unwantedFields = null;

    private ProductFormatStrategy productFormatter;

 //*** Public Methods

    /**
     * ************************************************************************** Memberwise constructor
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityRecordStruct
     */
    public ActivityRecord(ActivityRecordStruct activityRecordStruct)
    {
        m_activityRecordStruct = activityRecordStruct;
        m_eventTime = null;
        m_formattedStrings = new HashMap<String,String>(16);

        m_priorityDetailFields = new ArrayList<ActivityField>(32);
        m_priorityTableRowFields = new ArrayList<ActivityField>(32);
        m_unwantedFields = new ArrayList<ActivityField>(10);

    }

    /**
     *
     * @param pFieldTypes
     */
    public void setPriorityDetailFields(short [] pFieldTypes){
        if (m_priorityDetailFields.isEmpty())
        {
            for(short pFieldType : pFieldTypes)
            {
                addPriorityField(pFieldType, m_priorityDetailFields);
            }
        }
    }

    /**
     *
     * @param pFieldTypes
     */
    public void setPriorityTableRowFields(short[] pFieldTypes)
    {
        if(m_priorityTableRowFields.isEmpty())
        {
            for(short pFieldType : pFieldTypes)
            {
                addPriorityField(pFieldType, m_priorityTableRowFields);
            }
        }
    }

    /**
     *
     * @param pFieldTypes
     */
    public void setUnwantedFields(short[] pFieldTypes)
    {
        if(m_unwantedFields.isEmpty())
        {
            for(short pFieldType : pFieldTypes)
            {
                addPriorityField(pFieldType, m_unwantedFields);
            }
        }
    }

    /**
     *  This method adds a field to a list of priority Activity Fields.
     */
    private void addPriorityField(short pFieldType, List<ActivityField> list){
        ActivityField af;

        // Check to see if the Field exists. Don't report error if not found, since not all fields are expected
        // to be in every activity record.
        af = findField(pFieldType,false);

        // If the field is found,
        // make sure only prioritize Activity Fields with valid values
        if(af.isValid() && af.isPriorityEnforceable()){
            list.add(af);
        }
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public ActivityField findField(short fieldType, boolean logError)
    {
        for(ActivityField af : getActivityFields())
        {
            if(af.getFieldTypeCode() == fieldType)
            {
                return af;
            }
        }
        //Activity field not found
        if(logError)
        {
            String errMsg =  "ACTIVITY FIELD TYPE " + fieldType + " NOT FOUND BY ActivityField.findField(short fieldType)";
            GUILoggerHome.find().alarm(errMsg);
            GUILoggerHome.find().alarm(Category + ".createTradersFormat()" + toString(NAME_VALUE_FORMAT));
        }

        return new NotFoundActivityFieldImpl();
    }

    /**
     * **************************************************************************
     * Overloaded version of findField method that defaults the second parm to true, so that by default,
     * not finding the specified activity field type causes the error to be logged.
     *
     * @return the requested ActivityField. If the requested ActivityField could not be found, then throws an excpetion
     */

    public ActivityField findField(short fieldType)
    {
        return findField(fieldType, true);
    }

    /**
     * ************************************************************************** Returns the ActivityRecordStruct that
     * this object represents
     * <p/>
     * Note: This method exists primarily for backwards compatability reasons. Please use the wrapper objects whenever
     * possible
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityRecordStruct
     */
    public ActivityRecordStruct getActivityRecordStruct()
    {
        return m_activityRecordStruct;
    }


    /**
     * ************************************************************************** Returns the activity fields
     *
     * @return a List of ActivityFields
     */
    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public List<ActivityField> getActivityFields()
    {
        if (m_activityFields == null)
        {
            m_activityFields = new ArrayList<ActivityField>(92);//92 is the current number of fields in an activityRecord.
            for(ActivityFieldStruct activityField : m_activityRecordStruct.activityFields)
            {
                m_activityFields.add(new ActivityFieldImpl(activityField));
            }
        }
        return m_activityFields;
    }

    /**
     * ************************************************************************** Returns the time the activity
     * occurred
     */
    public DateTime getEventTime()
    {
        if (m_eventTime == null)
        {
            m_eventTime = new DateTimeImpl(m_activityRecordStruct.eventTime);
        }
        return m_eventTime;
    }


    /**
     * ************************************************************************** Returns the activity type code
     */
    public short getEventTypeCode()
    {
        return m_activityRecordStruct.entryType;
    }


    /**
     * ************************************************************************** Returns the key of the product that
     * this activity record represents
     */
    public int getProductKey()
    {
        return m_activityRecordStruct.productKey;
    }


    /**
     * ************************************************************************** Returns the name of the product that
     * this activity record represents
     */
    public String getProductName()
    {
        if (m_productName == null)
        {
            m_productName = formatProduct(m_activityRecordStruct.productKey);
        }
        return m_productName;
    }

    /**
     * ************************************************************************** Returns a string representation of the
     * object in NAME_VALUE_FORMAT format
     *
     * @return a string representation of the object
     */
    public String toString()
    {
        return toString(NAME_VALUE_FORMAT);
    }

    /**
     * ************************************************************************** Returns a string representation of the
     * object in the given format
     *
     * @param formatSpecifier - a string that specifies how the object should format itself.
     * @return a string representation of the object
     * @see java.text.SimpleDateFormat
     */
    public String toString(String formatSpecifier)
    {
        // Check to see if we've already rendered this string
        String formattedString = m_formattedStrings.get(formatSpecifier);
        if (formattedString == null)
        {
            if (formatSpecifier.equals(NAME_VALUE_FORMAT))
            {
                formattedString = "Event Time: " + getEventTime().toString() +
                        ", Product Key: " + String.valueOf(getProductKey()) +
                        ", Event Type: " + ActivityTypes.toString(getEventTypeCode()) + ' ' + getActivityFields().toString();
            }
            else if (formatSpecifier.equals(TRADERS_FORMAT))
            {
                formattedString = createTradersFormat(getEventTypeCode());
            }
            else if (formatSpecifier.equals(ALL_FIELDS_FORMAT))
            {
                //create comma separated list of all name value pairs
                formattedString = createAllFieldsFormat(",", null);
            }
            else if (formatSpecifier.equals(ALL_FIELDS_MULTILINE_FORMAT))
            {
                //create list of all name value pairs with each pair on a new line
                formattedString = createAllFieldsFormat("\n", null);
            }
            else if(formatSpecifier.equals(PRIORITY_FIELD_FORMAT)){
                //create comma separated list of all name value pairs with
                // priority pairs displayed first.
                formattedString = createAllFieldsFormat(",", m_priorityTableRowFields);

            }
            else if(formatSpecifier.equals(PRIORITY_DETAILS_FORMAT))
            {
                //create list of all name value pairs with each pair on a new line
                // with priority pairs displayed first.
                formattedString = createAllFieldsFormat("\n", m_priorityDetailFields);

            }
            else
            {
                formattedString = "ERROR: Format not supported";
            }
            m_formattedStrings.put(formatSpecifier, formattedString);
        }
        return formattedString;
    }



//*** Private Methods

    /**
     * ************************************************************************** Hide the default constructor from the
     * public interface
     */
    private ActivityRecord()
    {
    }

    private String formatProduct(int productKey)
    {
        return getProductFormatter().format(ProductHelper.getProduct(productKey));
    }

    /**
     * Format a product by its style.
     * @param productKey to format.
     * @param style to apply to the product format. @see ProductFormatStrategy.
     * @return the format product applied with its style.
     */
    private String formatProduct(int productKey, String style){
        return getProductFormatter().format(ProductHelper.getProduct(productKey), style);        
    }

    private ProductFormatStrategy getProductFormatter()
    {
        if (productFormatter == null)
        {
            productFormatter = FormatFactory.getProductFormatStrategy();
        }

        return productFormatter;
    }

    /**
     * ************************************************************************** Renders all name value pairs present
     * in the Activity Record, in ascending Field Name order.
     *
     * @param inSeparator - a string that specifies how the name value pairs should be separated (e.g. comma or new
     *                    line)
     * @param priorityList (may be null) - if not null, contains priority fields to be displayed before other fields
     *
     * @return a String containing all name/value pairs in the activity record
     */
    private String createAllFieldsFormat(String inSeparator, List<ActivityField> priorityList)
    {
        String separator = inSeparator;
        StringBuilder output = new StringBuilder(2000);
        SortedSet<String> textComponents = new TreeSet<String>();
        List<ActivityField> a_activityFields = new ArrayList<ActivityField>(10);
        a_activityFields.addAll(getActivityFields());

        if (separator == null)
        {
            separator = ",";    //default to comma separated list
        }

        // First, format the priority fields (if any), and remove them from the list of all fields
        if(priorityList != null){
            for(ActivityField priorityField : priorityList){
                a_activityFields.remove(priorityField);
                if(priorityField.getFieldValue().length() > 0){
                    StringBuilder nameValuePair = new StringBuilder(50);
                    nameValuePair.append(
                            priorityField.toString(ActivityField.DETAILED_EQUALS_FORMAT));
                    nameValuePair.append(separator);
                    output.append(nameValuePair.toString());
                }
            }
        }

        // Next, remove any unwanted fields from the list of remaining fields
        if(m_unwantedFields != null)
        {
            for(ActivityField unwantedField : m_unwantedFields)
            {
                a_activityFields.remove(unwantedField);
            }
        }

        // Then, format the remaining fields in alphabetical order
        for (ActivityField af : a_activityFields)
        {
            //only display lines having a non-empty value field
            if (af.getFieldValue().length() > 0)
            {
                StringBuilder nameValuePair = new StringBuilder(50);
                nameValuePair.append(af.toString(ActivityField.DETAILED_EQUALS_FORMAT));
                nameValuePair.append(separator);
                textComponents.add(nameValuePair.toString());
            }
        }

        for (String s : textComponents)
        {
            output.append(s);
        }

        //Remove last separator since it's not needed and should not be displayed
        output.delete(output.lastIndexOf(separator), output.length());

        return output.toString();
    }

    private static final String NO_PRICE="NP";

    /**
     * ************************************************************************** Renders various events in a format
     * that traders are familiar with
     *
     * @return a formatted String
     */
    @SuppressWarnings({"OverlyComplexMethod", "MethodWithMultipleReturnPoints", "OverlyLongMethod"})
    private String createTradersFormat(short eventTypeCode)
    {

//        GUILoggerHome.find().debug( "In NEW_ORDER_STRATEGY_LEG data = "+  m_activityRecordStruct );
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = eventTypeCode;
            argObj[1] = ActivityTypes.toString(eventTypeCode);
            argObj[2] = m_activityRecordStruct;

            GUILoggerHome.find().debug(Category + ".createTradersFormat() ", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }
        StringBuilder buffer;
        switch (eventTypeCode)
        {

            case ActivityTypes.NEW_ORDER:
            {
                char timeInForce =
                        findField(ActivityFieldTypes.TIME_IN_FORCE).getFieldValue().charAt(0);
                buffer = new StringBuilder(50);

                Product product = ProductHelper.getProduct(getProductKey());

                if(product.getProductType() != ProductTypes.STRATEGY){
                    //display the side if it's a simple order.
                    char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                    buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                }

                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue())
                        .append(" @ ");
                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);
                if(priceAF.isValid())
                {
                    Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                    buffer.append(price.toString()).append(' ');
                }
                buffer.append(getContingencyTypeString());
                buffer.append(TimesInForce.toString(timeInForce, TimesInForce.TRADERS_FORMAT));

                if(product.getProductType() == ProductTypes.STRATEGY)
                {
                    buffer.append(" DSM ");
                    buffer.append(getPriceRange(ActivityFieldTypes.DSM_BID_PRICE, ActivityFieldTypes.DSM_ASK_PRICE));
                }
                else
                {
                    buffer.append(" CBOE ");
                    buffer.append(getPriceRange(ActivityFieldTypes.BBO_BID_PRICE, ActivityFieldTypes.BBO_ASK_PRICE));
                    buffer.append(" NBBO ");
                    buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE,
                                                ActivityFieldTypes.NBBO_ASK_PRICE));
                }
                buffer.append(' ');
                buffer.append(formatRelatedOrder());
                return buffer.toString();
            }
            case ActivityTypes.FILL_ORDER:
            case ActivityTypes.FILL_QUOTE:
            {
                buffer = new StringBuilder(50);
                buffer.append(findField(ActivityFieldTypes.TRADED_QUANTITY).getFieldValue())
                        .append(" @ ");
                ActivityField priceField = findField(ActivityFieldTypes.PRICE); 
                Product product = ProductHelper.getProduct(getProductKey());
                if(product.getProductType() == ProductTypes.STRATEGY)
                {
                    Price price = DisplayPriceFactory.create(priceField.getFieldValue());
                    buffer.append(priceField.getFieldValue());
                    //check if the price is credit or debit.
                    if (price.isValuedPrice()){
                        //Todo: case where price value is 0.00
                        if (price.toDouble() >= 0.0){
                            buffer.append(" cr");                        
                        } else {
                            buffer.append(" db");
                        }
                    }
                } else { //Simple case
                    buffer.append(priceField.getFieldValue());
                }
                buffer.append(" lvs ")
                        .append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue());
                buffer.append(' ').append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_ORDER:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue())
                        .append(" @ ");
                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);
                Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                buffer.append(price.toString());
                buffer.append(" cancelled ")
                        .append(findField(ActivityFieldTypes.CANCELLED_QUANTITY).getFieldValue());
                buffer.append(" lvs ")
                        .append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue());
                buffer.append(" tlc ")
                        .append(findField(ActivityFieldTypes.TLC_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.CANCEL_REASON).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.ORDER_ROUTED:
            {
                buffer = new StringBuilder(20);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                buffer.append("; ");
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_REQUEST_ROUTED:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }                
            case ActivityTypes.CANCEL_REPLACE_ORDER_REQUEST:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                buffer.append(' ');
                buffer.append(formatRelatedOrder());
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_REPLACE_ORDER:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                char timeInForce =
                        findField(ActivityFieldTypes.TIME_IN_FORCE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append("Cancel Order ID: ")
                        .append(findField(ActivityFieldTypes.REPLACE_ORDERID).getFieldValue());
                buffer.append(" cancelled ")
                        .append(findField(ActivityFieldTypes.CANCELLED_QUANTITY).getFieldValue());
                buffer.append(" tlc ")
                        .append(findField(ActivityFieldTypes.TLC_QUANTITY).getFieldValue());
                buffer.append(" mismatched ")
                        .append(findField(ActivityFieldTypes.MISMATCHED_QUANTITY).getFieldValue());
                buffer.append(" ReOrd: ").append(Sides.toString(side, Sides.BUY_SELL_FORMAT))
                        .append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue())
                        .append('@');

                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);
                if(priceAF.isValid())
                {
                    Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                        buffer.append(' ');
                    }
                    else
                    {
                        buffer.append(priceAF.getFieldValue()).append(' ');
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }

                buffer.append(getContingencyTypeString()).append(' ');
                buffer.append(TimesInForce.toString(timeInForce, TimesInForce.TRADERS_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.BUST_ORDER_FILL:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append("Order Bust Quantity: ").append(findField(ActivityFieldTypes.BUSTED_QUANTITY).getFieldValue());
                buffer.append(' ').append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT)).append(" @");
                buffer.append(findField(ActivityFieldTypes.PRICE).getFieldValue()).append(' ');
                buffer.append(" Trade ID: ").append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.BUST_QUOTE_FILL:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer = new StringBuilder(30);
                ActivityField bustQtyAF = findField(ActivityFieldTypes.BUSTED_QUANTITY);
                
                if(bustQtyAF.isValid())
                {
                    buffer.append("Quote Bust Quantity: ").append(bustQtyAF.getFieldValue());
                }
                else
                {
                    buffer.append("[No Quote Bust Quantity in ActivityRecord]");
                }

                buffer.append(' ').append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT)).append(" @");
                Price price = DisplayPriceFactory.create(findField(ActivityFieldTypes.PRICE).getFieldValue());
                if(price.isNoPrice())
                {
                    buffer.append(NO_PRICE);
                }
                else
                {
                    buffer.append(findField(ActivityFieldTypes.PRICE).getFieldValue()).append(' ');
                }
                buffer.append(" Trade ID: ").append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.BUST_REINSTATE_ORDER:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append("Bust quantity: ").append(findField(ActivityFieldTypes.BUSTED_QUANTITY).getFieldValue());
                buffer.append(" reinstate quantity: ").append(findField(ActivityFieldTypes.REINSTATED_QUANTITY).getFieldValue());
                buffer.append(' ').append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT)).append(" @");

                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);
                if(priceAF.isValid())
                {
                    Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(priceAF.getFieldValue()).append(' ');
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }

                buffer.append(" Trade ID: ").append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.UPDATE_ORDER:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue()).append('@');

                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);
                if(priceAF.isValid())
                {
                    Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(priceAF.getFieldValue()).append(' ');
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }

                buffer.append(" account ").append(findField(ActivityFieldTypes.ACCOUNT).getFieldValue());
                buffer.append(" subaccount ").append(findField(ActivityFieldTypes.SUB_ACCOUNT).getFieldValue());
                buffer.append(" CMTA ").append(findField(ActivityFieldTypes.CMTA).getFieldValue());
                buffer.append(" Optional Data ").append(findField(ActivityFieldTypes.OPTIONAL_DATA).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.BOOK_ORDER:
            {
                return "";
            }
            case ActivityTypes.STATE_CHANGE_ORDER:
            {
                buffer = new StringBuilder(30);
                buffer.append("Order State: ").append(getOrderStateString());
                return buffer.toString();
            }
            case ActivityTypes.PRICE_ADJUST_ORDER:
            {
                buffer = new StringBuilder(50);
                buffer.append("Original Quantity ").append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append(", Product ").append(findField(ActivityFieldTypes.PRODUCT).getFieldValue());

                ActivityField priceAF = findField(ActivityFieldTypes.PRICE);

                if(priceAF.isValid())
                {
                    Price price = DisplayPriceFactory.create(priceAF.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(", ").append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(", Price ").append(priceAF.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }

                return buffer.toString();
            }
            case ActivityTypes.CANCEL_ALL_ORDERS:
            {
                buffer = new StringBuilder(30);
                buffer.append("All Orders Cancelled");
                return buffer.toString();
            }
            case ActivityTypes.NEW_QUOTE:
            case ActivityTypes.UPDATE_QUOTE:
            case ActivityTypes.CANCEL_QUOTE:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.BID_PRICE).getFieldValue()).append(" - ");
                buffer.append(findField(ActivityFieldTypes.ASK_PRICE).getFieldValue()).append("   ");
                buffer.append(findField(ActivityFieldTypes.BID_QTY).getFieldValue()).append(" x ");
                buffer.append(findField(ActivityFieldTypes.ASK_QTY).getFieldValue());

                ActivityField af = findField(ActivityFieldTypes.QUOTE_UPDATE_CONTROL_ID);
                if (af.isValid())
                {
                    buffer.append(" (");
                    buffer.append(ActivityFieldTypes.toString(ActivityFieldTypes.QUOTE_UPDATE_CONTROL_ID));
                    buffer.append(": ").append(af.getFieldValue());
                    buffer.append(')');
                }
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_ALL_QUOTES:
            {
                return "All Quotes Cancelled by User";
            }
            case ActivityTypes.SYSTEM_CANCEL_QUOTE:
            {
                 return "All Quotes Cancelled by System";
            }
            case ActivityTypes.NEW_RFQ:
            {
                buffer = new StringBuilder(50);
                buffer.append(getRFQTypeString());
                buffer.append(", Original Quantity ").append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append(", Product State ").append(getProductTypeString());
                buffer.append(", Time to Live ").append(findField(ActivityFieldTypes.TIME_TO_LIVE).getFieldValue());
                return buffer.toString();
            }
// Strategy Additions....

            case ActivityTypes.NEW_ORDER_STRATEGY_LEG:
            {
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer = new StringBuilder(40);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(formatProduct(getProductKey(),
                                            ProductFormatStrategy.FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS));
                return buffer.toString();
            }
            case ActivityTypes.QUOTE_LEG_FILL:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(30);
                buffer.append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT));
                buffer.append(' ');
                buffer.append(productName);
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.TRADED_QUANTITY).getFieldValue());
                buffer.append('@');

                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                buffer.append(" lvs ");
                buffer.append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue());
                buffer.append(" Trade ID: ");
                buffer.append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_STRATEGY_LEG:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);

                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer = new StringBuilder(50);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT));
                buffer.append(' ');
                buffer.append(productName);
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append('@');
                Price price = DisplayPriceFactory.create(findField(ActivityFieldTypes.PRICE).getFieldValue());
                if(price.isNoPrice())
                {
                    buffer.append(NO_PRICE);
                }
                else
                {
                    buffer.append(findField(ActivityFieldTypes.PRICE).getFieldValue());
                }

                buffer.append(' ');
                buffer.append(" cancelled ");
                buffer.append(findField(ActivityFieldTypes.CANCELLED_QUANTITY).getFieldValue());
                buffer.append(" lvs ");
                buffer.append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue());
                buffer.append(" tlc ");
                buffer.append(findField(ActivityFieldTypes.TLC_QUANTITY).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.BUST_STRATEGY_LEG_FILL:
            case ActivityTypes.BUST_QUOTE_LEG_FILL:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer = new StringBuilder(50);
                buffer.append(productName);
                buffer.append(' ');
                buffer.append("Bust quantity: ");
                buffer.append(findField(ActivityFieldTypes.BUSTED_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT));
                buffer.append(" @");
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                buffer.append(' ');
                buffer.append(" Trade ID: ");
                buffer.append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.BUST_REINSTATE_STRATEGY_LEG:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer = new StringBuilder(50);
                buffer.append(productName);
                buffer.append(' ');
                buffer.append("Bust quantity: ");
                buffer.append(findField(ActivityFieldTypes.BUSTED_QUANTITY).getFieldValue());
                buffer.append(" reinstate quantity: ");
                buffer.append(findField(ActivityFieldTypes.REINSTATED_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT));
                buffer.append('@');
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                buffer.append(' ');
                buffer.append(" Trade ID: ").append(findField(ActivityFieldTypes.TRADEID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.UPDATE_STRATEGY_LEG:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer = new StringBuilder(50);
                buffer.append(productName);
                buffer.append(' ');
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append('@');
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                buffer.append(' ');
                buffer.append(" account ");
                buffer.append(findField(ActivityFieldTypes.ACCOUNT).getFieldValue());
                buffer.append(" CMTA ");
                buffer.append(findField(ActivityFieldTypes.CMTA).getFieldValue());
                buffer.append(" Optional Data ");
                buffer.append(findField(ActivityFieldTypes.OPTIONAL_DATA).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.PRICE_ADJUST_ORDER_LEG:
            {
                int productKey = getActivityRecordStruct().productKey;
                String productName = formatProduct(productKey);
                buffer = new StringBuilder(50);
                buffer.append("Original Quantity ");
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue());
                buffer.append(", Product ");
                buffer.append(productName);
                buffer.append(", Price ");
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                return buffer.toString();
            }
            case ActivityTypes.CANCEL_ORDER_REQUEST:
            case ActivityTypes.CANCEL_ORDER_REQUEST_REJECT:
            case ActivityTypes.CANCEL_ORDER_REQUEST_REJECT_REJECTED:
            case ActivityTypes.CANCEL_REPORT_REJECT:
            case ActivityTypes.CANCEL_REPORT_REJECT_REJECTED:
            case ActivityTypes.NEW_ORDER_REJECT_REJECTED:
            case ActivityTypes.NEW_ORDER_REJECT:
            {
                buffer = new StringBuilder(200);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.ORIGINAL_QUANTITY).getFieldValue()).append('@');
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord]");
                }
                buffer.append(" cancelled ").append(findField(ActivityFieldTypes.CANCELLED_QUANTITY).getFieldValue()).append(' ');
                buffer.append("lvs ").append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue()).append(' ');
                buffer.append("tlc ").append(findField(ActivityFieldTypes.TLC_QUANTITY).getFieldValue()).append(' ');
                String cancelReason = findField(ActivityFieldTypes.CANCEL_REASON).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT);
                if (cancelReason.length() > 0)
                {
                    buffer.append("Cancel Reason: ").append(cancelReason);
                }
//                    ActivityFieldTypes.TRANSACTION_SEQUENCE_NUMBER  -- NOT DISPLAYED
                return buffer.toString();
            }
            case ActivityTypes.FILL_REJECT:
            case ActivityTypes.FILL_REJECT_REJECTED:
            {
                buffer = new StringBuilder(50);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);

                buffer.append("Order ID: ").append(findField(ActivityFieldTypes.ORDERID).getFieldValue()).append(' ');
                buffer.append(Sides.toString(side, Sides.BOUGHT_SOLD_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.TRADED_QUANTITY).getFieldValue()).append(' ');
                ActivityField af = findField(ActivityFieldTypes.PRICE);
                if(af.isValid())
                {
                    Price price = DisplayPriceFactory.create(af.getFieldValue());
                    if(price.isNoPrice())
                    {
                        buffer.append(NO_PRICE);
                    }
                    else
                    {
                        buffer.append(af.getFieldValue());
                    }
                }
                else
                {
                    buffer.append("[No Price in ActivityRecord] ");
                }
                buffer.append("lvs ").append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue()).append(' ');
                buffer.append("Trade ID: ").append(findField(ActivityFieldTypes.TRADEID).getFieldValue()).append(' ');
                buffer.append("Exec Broker: ").append(findField(ActivityFieldTypes.EXEC_BROKER).getFieldValue()).append(' ');
                buffer.append("User Assigned ID: ").append(findField(ActivityFieldTypes.USER_ASSIGNED_ID).getFieldValue()).append(' ');
//                    ActivityFieldTypes.TRANSACTION_SEQUENCE_NUMBER  --- NOT DISPLAYED
                return buffer.toString();
            }
            case ActivityTypes.HELD_FOR_IPP_PROTECTION:
            {
                buffer = new StringBuilder(50);
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                buffer.append(" #");
                buffer.append(findField(ActivityFieldTypes.ROUTED_QUANTITY).getFieldValue());
                buffer.append(" @ ");
                buffer.append(findField(ActivityFieldTypes.PRICE).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.EXCHANGE_INDICATORS).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_ORSID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.ROUTE_TO_AWAY_EXCHANGE:
            { 
                buffer = new StringBuilder(50);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.ROUTED_QUANTITY).getFieldValue());
                buffer.append(" @ ");
                buffer.append(findField(ActivityFieldTypes.PRICE).getFieldValue());
//                buffer.append(' ');
//                buffer.append(findField(ActivityFieldTypes.EXCHANGE_INDICATORS).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                buffer.append(' ');
                buffer.append(formatRelatedOrder());
                return buffer.toString();
            }
            case ActivityTypes.MANUAL_ORDER_TA:
            case ActivityTypes.MANUAL_ORDER_TB:
            {
                buffer = new StringBuilder(50);
                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer.append(Sides.toString(side, Sides.BUY_SELL_FORMAT)).append(' ');
                buffer.append(findField(ActivityFieldTypes.TRADED_QUANTITY).getFieldValue());
                buffer.append(" CBOE ");
                buffer.append(getPriceRange(ActivityFieldTypes.BBO_BID_PRICE, ActivityFieldTypes.BBO_ASK_PRICE));
                buffer.append(" NBBO ");
                buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE, ActivityFieldTypes.NBBO_ASK_PRICE));
                return buffer.toString();
            }
            case ActivityTypes.MANUAL_ORDER_BOOK:
            {
                buffer = new StringBuilder(50);
                buffer.append(" CBOE ");
                buffer.append(getPriceRange(ActivityFieldTypes.BBO_BID_PRICE,
                                            ActivityFieldTypes.BBO_ASK_PRICE));
                buffer.append(" NBBO ");
                buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE,
                                            ActivityFieldTypes.NBBO_ASK_PRICE));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.PAR_BROKER_LEG_MKT:
            {
                //Product bbo bid/ask nbbo bid/ask
                buffer = new StringBuilder(30);
                buffer.append(formatProduct(getProductKey(),
                                            ProductFormatStrategy.FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS));
                buffer.append(" CBOE ");
                buffer.append(getPriceRange(ActivityFieldTypes.BBO_BID_PRICE,
                                            ActivityFieldTypes.BBO_ASK_PRICE));
                buffer.append(" NBBO ");
                buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE,
                                            ActivityFieldTypes.NBBO_ASK_PRICE));
                return buffer.toString();
            }
            case ActivityTypes.PAR_BROKER_USED_MKT_DATA:
            case ActivityTypes.PAR_BROKER_MKT_DATA:
            {
                buffer = new StringBuilder(50);
                buffer.append(findField(ActivityFieldTypes.SUBEVENT_TYPE).getFormattedFieldValue(
                                                          ActivityFieldTypes.BOOTH_FORMAT));

                buffer.append(" CBOE ");
                buffer.append(getPriceRange(ActivityFieldTypes.BBO_BID_PRICE,
                                                  ActivityFieldTypes.BBO_ASK_PRICE));
                buffer.append(" NBBO ");
                buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE,
                                                  ActivityFieldTypes.NBBO_ASK_PRICE));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.LINKAGE_ORDER_RELATIONSHIP:
            {
                buffer = new StringBuilder(70);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_ORSID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.HYBRID_REQUEST_RETURNED:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.RETURN_CODE).getFormattedFieldValue(
                                                          ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.FILL_STRATEGY_LEG:
            {
                buffer = new StringBuilder(40);

                char side = findField(ActivityFieldTypes.SIDE).getFieldValue().charAt(0);
                buffer.append(Sides.toString(side, Sides.BOT_SOLD_FORMAT));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.TRADED_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(formatProduct(getProductKey(),
                                            ProductFormatStrategy.FULL_PRODUCT_WITH_CLASS_AND_REPORTING_CLASS));
                buffer.append(' ').append(findField(ActivityFieldTypes.PRICE).getFormattedFieldValue());
                buffer.append(' ').append("lvs ").append(findField(ActivityFieldTypes.LEAVES_QUANTITY).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.EXECUTION_REPORT_ROUTED:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.SOURCE_FIELD).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.EXECUTION_REPORT_ON_LINKED_ORDER:
            {
                buffer = new StringBuilder(70);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.SOURCE_FIELD).getFieldValue());
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_ORSID).getFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.AUCTION_END:
            {
                return "";
            }
            case ActivityTypes.AUCTION_START:
            {
                buffer = new StringBuilder(70);
                buffer.append(findField(ActivityFieldTypes.AUCTION_TYPE).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
                Product product = ProductHelper.getProduct(getProductKey());
                if(product.getProductType() == ProductTypes.STRATEGY)
                {
                    buffer.append(" DSM ");
                    buffer.append(getPriceRange(ActivityFieldTypes.DSM_BID_PRICE,
                                                ActivityFieldTypes.DSM_ASK_PRICE));
                } else {
                    buffer.append(" NBBO ");
                    buffer.append(getPriceRange(ActivityFieldTypes.NBBO_BID_PRICE,
                                                ActivityFieldTypes.NBBO_ASK_PRICE));
                }
                return buffer.toString();
            }
            case ActivityTypes.TSB_REQUEST:
            {
                buffer = new StringBuilder(50);
                buffer.append("COB ");
                buffer.append(getPriceRange(ActivityFieldTypes.BOOK_BID_PRICE, ActivityFieldTypes.BOOK_ASK_PRICE));
                buffer.append(' ');
                buffer.append(getSizeFormat(ActivityFieldTypes.BOOK_BID_SIZE, ActivityFieldTypes.BOOK_ASK_SIZE));
                buffer.append(' ');
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                return buffer.toString();
            }
            case ActivityTypes.FAILED_ROUTE:
            {
                buffer = new StringBuilder(50);
                buffer.append(findField(ActivityFieldTypes.ROUTE_REASON).getFormattedFieldValue());
                buffer.append(' ').append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                return buffer.toString();
            }
            case ActivityTypes.MANUAL_ORDER_AUCTION:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.LOCATION).getFormattedFieldValue(
                        ActivityFieldTypes.BOOTH_FORMAT));
                buffer.append(" : ");
                Product product = ProductHelper.getProduct(getProductKey());
                //Todo: Test for complex : if(product.getProductType() == ProductTypes.STRATEGY) -- Is this the only test to do? Equity?
                if(product.getProductType() == ProductTypes.STRATEGY)
                {
                    buffer.append(" DSM ");
                    buffer.append(getPriceRange(ActivityFieldTypes.DSM_BID_PRICE, ActivityFieldTypes.DSM_ASK_PRICE));
                }
                else
                {
                    GUILoggerHome.find().exception("A Manual Order Auction occurred when productType wasn't a Strategy.", new IllegalStateException(
                            "Product type state: " + product.getProductType()).fillInStackTrace());
                }
                return buffer.toString();
            }
            case ActivityTypes.CPS_SPLIT_ORDER_TIMEOUT:
            case ActivityTypes.CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT:
            case ActivityTypes.CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT:
            case ActivityTypes.CPS_SPLIT_DERIVED_ORDER_NEW:
            case ActivityTypes.CPS_SPLIT_DERIVED_ORDER_FILL:
            case ActivityTypes.CPS_SPLIT_DERIVED_ORDER_CANCEL:
            {
                buffer = new StringBuilder(30);
                buffer.append(findField(ActivityFieldTypes.ROUTE_DESCRIPTION).getFormattedFieldValue());
                return buffer.toString();
            }

            /*
            case ActivityTypes.CROSSING_ORDER_ROUTED:
            case ActivityTypes.AUCTION_TRIGGER_START:
            case ActivityTypes.AUCTION_TRIGGER_END:
            case ActivityTypes.AWAY_EXCHANGE_MARKET:
            case ActivityTypes.LINKAGE_DISQUALIFIED_EXCHANGE:
            case ActivityTypes.VOL_MAINTENANCE:
            {
                return createAllFieldsFormat(",", null);   //default handling is to display all fields comma separated
            }
            */

            default:
                return createAllFieldsFormat(",", m_priorityTableRowFields);
        }

    }

//**** Private methods
    private String getContingencyTypeString()
    {
        StringBuilder sbContingencyType = new StringBuilder(10);
        short contingencyType;

        try
        {
            contingencyType = Short.parseShort(findField(ActivityFieldTypes.CONTINGENCY_TYPE).getFieldValue());
            sbContingencyType.append(ContingencyTypes.toString(contingencyType, ContingencyTypes.TRADERS_FORMAT)).append(' ');
        }
        catch (NumberFormatException e)
        {
            sbContingencyType.append(ActivityField.ACTIVITY_FIELD_NOT_FOUND);
        }
        return sbContingencyType.toString();
    }

    private String getOrderStateString()
    {
        short orderState;
        StringBuilder sbOrderState = new StringBuilder(10);
        try
        {
            orderState = Short.parseShort(findField(ActivityFieldTypes.ORDER_STATE).getFieldValue());
            sbOrderState.append(OrderStates.toString(orderState, OrderStates.TRADERS_FORMAT));
        }
        catch(NumberFormatException e)
        {
            sbOrderState.append(ActivityField.ACTIVITY_FIELD_NOT_FOUND);
        }
        return sbOrderState.toString();
    }

    private String getRFQTypeString()
    {
        short rfqType;
        StringBuilder sbRFQ = new StringBuilder(10);
        try
        {
            rfqType = Short.parseShort(findField(ActivityFieldTypes.RFQ_TYPE).getFieldValue());
            sbRFQ.append(RfqTypes.toString(rfqType, RfqTypes.TRADERS_FORMAT));
        }
        catch(NumberFormatException e)
        {
            sbRFQ.append(ActivityField.ACTIVITY_FIELD_NOT_FOUND);
        }

        return sbRFQ.toString();
    }

    private String getProductTypeString()
    {
        short productState;
        StringBuilder sbProductState = new StringBuilder(10);

        try
        {
            productState = Short.parseShort(findField(ActivityFieldTypes.PRODUCT_STATE).getFieldValue());
            sbProductState.append(ProductStates.toString(productState, ProductStates.TRADERS_FORMAT));
        }
        catch(NumberFormatException e)
        {
            sbProductState.append(ActivityField.ACTIVITY_FIELD_NOT_FOUND);
        }
        return sbProductState.toString();
    }

    /**
     * Get the price range format.
     * @param bid price
     * @param ask price
     * @return a price formatted like ##0.00 x ##0.00
     */
    private String getPriceRange(Price bid, Price ask)
    {
        StringBuilder sb = new StringBuilder(20);
        if (bid.isNoPrice()){
            sb.append(NO_PRICE);
        }
        else {
            sb.append(bid.toString());
        }
        sb.append(" x ");
        if(ask.isNoPrice())
        {
           sb.append(NO_PRICE);
        }
        else {
            sb.append(ask.toString());
        }
        return sb.toString();
    }

    /**
     * Get the price range format.
     * @param ask @{link ActivityFieldTypes#ASK_PRICE}
     * @param bid @see ActivityFieldTypes
     * @return a formatted range price for a market like: ##0.00 x ##0.00
     */
    private String getPriceRange(short bid, short ask)
    {
        StringBuilder buffer = new StringBuilder(20);
        ActivityField askPrice = findField(ask);
        ActivityField bidPrice = findField(bid);

        if(askPrice.isValid() && bidPrice.isValid())
        {
            Price priceAsk = DisplayPriceFactory.create(askPrice.getFieldValue());
            Price priceBid = DisplayPriceFactory.create(bidPrice.getFieldValue());
            buffer.append(getPriceRange(priceBid, priceAsk));
        }
        else
        {
            GUILoggerHome.find().exception("Invalid Price.", new IllegalArgumentException().fillInStackTrace());
        }
        return buffer.toString();
    }

    /**
     * Get the size format of a bid and ask price size.
     * @param bid like DSM_BID_SIZE
     * @param ask like DSM_ASK_SIZE
     * @return a string formatted like SIZE = 2 x 123
     */
    private String getSizeFormat(short bid, short ask)
    {
        StringBuilder buffer = new StringBuilder(30);
        buffer.append("Size = ");
        ActivityField bidSize = findField(bid);
        ActivityField askSize = findField(ask);
        buffer.append(bidSize.getFormattedFieldValue()).append(" x ").append(askSize.getFormattedFieldValue());
        return buffer.toString();
    }

    /**
     * Format some related order fields separated by comma delimiter if present.
     * Format output is:
     * Re: Related order firmnumber, Related order correspond firm,
     *  Related order branch, Related order branch sequence number, Related Order orsid
     *    
     * @return the Related order formatted if present.
     */
    private String formatRelatedOrder(){
        StringBuilder buffer=new StringBuilder(10);
        ActivityField firmNumber = findField(ActivityFieldTypes.RELATED_ORDER_FIRMNUMEBR);
        //Use the firmNumber activityFieldType to test if the related order are present.
        if(firmNumber != null && !firmNumber.getFieldValue().equals(""))
        {
            buffer.append("Re:");
            buffer.append(firmNumber.getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
            buffer.append(',');
            buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_CORRESPONDENTFIRM).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
            buffer.append(',');
            buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_BRANCH).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
            buffer.append(',');
            buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_BRANCHSEQNUMBER).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
            buffer.append(',');
            buffer.append(findField(ActivityFieldTypes.RELATED_ORDER_ORSID).getFormattedFieldValue(ActivityFieldTypes.BOOTH_FORMAT));
        }
        return buffer.toString();
    }
} // End of class

