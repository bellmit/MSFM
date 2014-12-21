// -----------------------------------------------------------------------------------
// Source file: GroupErrorCodeResultFormatter
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// Created: Mar 9, 2004 1:08:14 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.interfaces.internalPresentation.common.formatters.GroupErrorCodeResultFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.idl.product.GroupErrorCodeResultStruct;
import com.cboe.idl.product.ErrorCodeResultStruct;
import com.cboe.idl.cmiUtil.KeyDescriptionStruct;
import com.cboe.presentation.common.formatters.Formatter;
import com.cboe.presentation.product.ProductHelper;

public class GroupErrorCodeResultFormatter extends Formatter implements GroupErrorCodeResultFormatStrategy
{
    public GroupErrorCodeResultFormatter()
    {
        addStyle(GROUP_ERRORCODERESULT_MESSAGE, GROUP_ERRORCODERESULT_MESSAGE_DESCRIPTION);
        addStyle(GROUP_ERRORCODERESULT, GROUP_ERRORCODERESULT_DESCRIPTION);
        addStyle(GROUP_ERRORCODERESULT_KEYDESCRIPTION, GROUP_ERRORCODERESULT_KEYDESCRIPTION_DESCRIPTION);

        addStyle(ERRORCODERESULT_MESSAGE, ERRORCODERESULT_MESSAGE_DESCRIPTION);
        addStyle(ERRORCODERESULT_KEYDESCRIPTION, ERRORCODERESULT_KEYDESCRIPTION_DESCRIPTION);

        setDefaultStyle(GROUP_ERRORCODERESULT_MESSAGE);
    }

    public String format(GroupErrorCodeResultStruct struct, String sessionName)
    {
        return format(struct, sessionName, getDefaultStyle());
    }

    public String format(GroupErrorCodeResultStruct struct, String sessionName, String styleName)
    {
        String retVal = "";

        if(!containsStyle(styleName))
        {
            throw new IllegalArgumentException("GroupErrorCodeResultFormatter - Unknown Style: '" + styleName + "'");
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("Group: ");
        buffer.append(struct.group.groupName);
        buffer.append('\n');

        if (styleName.equals(GROUP_ERRORCODERESULT_MESSAGE))
        {
            buffer.append(format(struct.errorResults[0], sessionName, ERRORCODERESULT_MESSAGE));
            buffer.append('\n');
            retVal = buffer.toString();
        }
        else if (styleName.equals(GROUP_ERRORCODERESULT))
        {
            for (int i=0; i<struct.errorResults.length; i++)
            {
                buffer.append(format(struct.errorResults[i], sessionName));
                buffer.append('\n');
            }

            retVal = buffer.toString();
        }
        else if (styleName.equals(GROUP_ERRORCODERESULT_KEYDESCRIPTION))
        {

            for (int i=0; i<struct.errorResults.length; i++)
            {
                buffer.append(format(struct.errorResults[i], sessionName, ERRORCODERESULT_KEYDESCRIPTION));
            }

            retVal = buffer.toString();
        }

        return retVal;
    }

    public String format(ErrorCodeResultStruct struct, String sessionName, String styleName)
    {
        String retVal = "";

        if(!containsStyle(styleName))
        {
            throw new IllegalArgumentException("GroupErrorCodeResultFormatter - Unknown Style: '" + styleName + "'");
        }

        if (styleName.equals(ERRORCODERESULT_MESSAGE))
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Message: ");
            buffer.append(struct.exceptionDescription);

            retVal = buffer.toString();
        }
        else if (styleName.equals(ERRORCODERESULT_KEYDESCRIPTION))
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append(format(struct, sessionName));
            buffer.append('\n');

            if (struct.failedProducts != null)
            {
                for (int i=0; i<struct.failedProducts.length; i++)
                {
                    buffer.append(format(struct.failedProducts[i], sessionName));
                    buffer.append('\n');
                }
            }

            retVal = buffer.toString();
        }

        return retVal;
    }

    public String format(ErrorCodeResultStruct struct, String sessionName)
    {
        String retVal = "";

        if(struct != null)
        {
            StringBuffer buffer = new StringBuffer();

            SessionProductClass productClass = ProductHelper.getSessionProductClass(sessionName, struct.classKey);
            if (productClass != null)
            {
                ProductClassFormatStrategy formatter = com.cboe.presentation.common.formatters.FormatFactory.getProductClassFormatStrategy();
                buffer.append("Class: ");
                buffer.append(formatter.format(productClass, ProductClassFormatStrategy.CLASS_TYPE_NAME_KEY));
                buffer.append(' ');
            }
            else
            {
                buffer.append("ClassKey: ");
                buffer.append(struct.classKey);
                buffer.append(" ");
            }

            buffer.append("Message: ");
            buffer.append(struct.exceptionDescription);
            buffer.append(' ');

            buffer.append("Error code: ");
            buffer.append(struct.errorCode);

            retVal = buffer.toString();
        }

        return retVal;
    }

    public String format(KeyDescriptionStruct struct, String sessionName)
    {
        String retVal = "";

        if (struct != null)
        {
            StringBuffer buffer = new StringBuffer();

            SessionProduct product = ProductHelper.getSessionProduct(sessionName,struct.key);
            if (product != null)
            {
                ProductFormatStrategy formatter = com.cboe.presentation.common.formatters.FormatFactory.getProductFormatStrategy();
                buffer.append("Product: ");
                buffer.append(formatter.format(product, ProductFormatStrategy.FULL_PRODUCT_NAME_WITH_KEY));
                buffer.append(' ');
            }
            else
            {
                buffer.append("ProductKey: ");
                buffer.append(struct.key);
                buffer.append(' ');
            }

            buffer.append("Message: ");
            buffer.append(struct.description);

            retVal = buffer.toString();
        }

        return retVal;
    }

} // -- end of class GroupErrorCodeResultFormatter
