package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.order.Order;

/**
 * Created by IntelliJ IDEA.
 * User: Davisson
 * Date: Jan 30, 2003
 * Time: 1:58:19 PM
 * To change this template use Options | File Templates.
 */
public interface OrderExtensionsFormatStrategy
{
    public static final String FULL_INFO_NAME = "Full Key/Value Information";
    public static final String FULL_INFO_DESCRIPTION = "Full Information including both the key/value pair from an Order's 'extensions' field";

    public static final String BRIEF_INFO_NAME = "Brief - Value Only";
    public static final String BRIEF_INFO_DESCRIPTION = "Brief Information including only the value for a specified key";

    /**
     * Defines a method for returning a formatted String representation of a key/value pair parsed from an Order's
     * extensions field.
     *
     * @param order
     * @param extKey
     * @return formatted String
     */
    public String format(Order order, String extKey);

    /**
     * Defines a method for returning a formatted String representation of a key/value pair parsed from an Order's
     * extensions field.
     *
     * @param extensions
     * @param extKey
     * @return formatted String
     */
    public String format(String extensions, int extKey);

    /**
     * Defines a method for returning a formatted String representation of a key/value pair parsed from an Order's
     * extensions field.
     *
     * @param extensions
     * @param extKey
     * @return formatted String
     */
    public String format(String extensions, String extKey);

    /**
     * Defines a method for returning a formatted String representation of a key/value pair parsed from an Order's 
     * extensions field.
     *
     * @param extensions
     * @param extKey
     * @param styleName
     * @return formatted String
     */
    public String format(String extensions, String extKey, String styleName);

    /**
     * Defines a method for returning a formatted String representation of a constant in
     * com.cboe.idl.cmiConstants.ExtensionFields.  Those idl constants are used to store values in an Order's extensions
     * field.
     *
     * @param extKey
     * @return formatted String
     */
    public String formatKey(String extKey);
}
