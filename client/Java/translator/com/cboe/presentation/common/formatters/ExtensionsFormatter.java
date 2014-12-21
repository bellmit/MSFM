//
// ------------------------------------------------------------------------
// FILE: ExtensionsFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExtensionsFormatStrategy;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.presentation.common.logging.GUILoggerHome;

import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author torresl@cboe.com
 */
class ExtensionsFormatter extends AbstractCommonStylesFormatter implements ExtensionsFormatStrategy
{
    private Comparator comparator;
    public ExtensionsFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public String format(String extensionsField)
    {
        return format(extensionsField, getDefaultStyle());
    }

    public String format(String extensionsField, String style)
    {
        validateStyle(style);
        StringBuffer buffer = new StringBuffer();
        String delimiter = getDelimiterForStyle(style);
        if (extensionsField.length() > 0)
        {
            ExtensionsHelper helper = null;
            try
            {
                helper = new ExtensionsHelper(extensionsField);
                TreeSet set = new TreeSet(getComparator());
                set.addAll(helper.getKeys());
                for (Iterator iterator = set.iterator(); iterator.hasNext();)
                {
                    String key = (String) iterator.next();
                    buffer.append(ExtensionFields.toString(key));
                    buffer.append(ExtensionsHelper.DEFAULT_TAG_DELIMITER);
                    buffer.append(helper.getValue(key));
                    buffer.append(delimiter);
                }
            }
            catch (java.text.ParseException e)
            {
                GUILoggerHome.find().exception(e, "Parse extensions field "+extensionsField);
            }
        }

        return buffer.toString();
    }
    protected Comparator getComparator()
    {
        if(comparator == null)
        {
            comparator = new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    // return in alphabetical order
                    return o1.toString().compareTo(o2.toString());
                }
            };
        }
        return comparator;
    }
}
