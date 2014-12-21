package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.common.formatters.OrderExtensionsFormatStrategy;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.formatters.Formatter;

import java.text.ParseException;

public class OrderExtensionsFormatter extends Formatter implements OrderExtensionsFormatStrategy
{

    private ExtensionsHelper eh;

    private final String Category = this.getClass().getName();

    public OrderExtensionsFormatter()
    {
        super();

        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);

        setDefaultStyle(FULL_INFO_NAME);
    }


    public String format(Order order, String extKey)
    {
        return format(order.getExtensions(), extKey);
    }

    public String format(String extensions, int extKey)
    {
        return format(extensions, Integer.toString(extKey));
    }

    public String format(String extensions, String extKey)
    {
        return format(extensions, extKey, getDefaultStyle());
    }

    /**
     * If styleName is BRIEF_INFO_NAME, the returned String will be formatted: "value_string".
     * If styleName is FULL_INFO_NAME, the returned String will be formatted: "key_string: value_string";
     * @param extensions
     * @param extKey
     * @param styleName
     * @return String value from order.extensions field
     */
    public String format(String extensions, String extKey, String styleName)
	{
        StringBuffer sb = new StringBuffer(25);
        String extValue = "";
        try
        {
            getExtensionsHelper().setExtensions(extensions);
            extValue = getExtensionsHelper().getValue(extKey);
        }
        catch(ParseException e)
        {
            GUILoggerHome.find().exception(e);
        }

        if(styleName.equalsIgnoreCase(FULL_INFO_NAME))
        {
            sb.append(formatKey(extKey)).append(": ");
        }
        sb.append(extValue);

        return sb.toString();
	}

    /**
     * Returns a String representing the com.cboe.idl.cmiConstants.ExtensionFields, which are used as the keys to extract
     * information from the order.extensions field.  The order.extensions can be parsed using a
     * com.cboe.domain.util.ExtensionsHelper.
     *
     * @param extKey
     * @return String representation of cmiConstant extKey
     */
    public String formatKey(String extKey)
    {
        String retVal = "Unknown Extension Key";
        String extString = ExtensionFields.toString(extKey, ExtensionFields.TRADERS_FORMAT);
        if(! ExtensionFields.INVALID_TYPE.equals(extString))
        {
            retVal = extString;
        }
        else
        {
            retVal = new StringBuffer(25).append("Unknown Extension Key ").append(extKey).toString();
        }
        return retVal;
    }

    private ExtensionsHelper getExtensionsHelper()
    {
        if(eh == null)
        {
            eh = new ExtensionsHelper();
        }
        return eh;
    }
}
