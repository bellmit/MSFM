//
// -----------------------------------------------------------------------------------
// Source file: ProductClassFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.FormatterCache;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.TradingSessionFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Implements the ProductClassFormatStrategy
 */
class ProductClassFormatter extends Formatter implements ProductClassFormatStrategy
{
    FormatterCache cache;
    /**
     * Constructor, defines styles and sets initial default style
     */
    public ProductClassFormatter()
    {
        super();

        addStyle(PLAIN_CLASS_NAME, PLAIN_CLASS_NAME_DESCRIPTION);
        addStyle(CLASS_TYPE_NAME, CLASS_TYPE_NAME_DESCRIPTION);
        addStyle(CLASS_TYPE_NAME_KEY, CLASS_TYPE_NAME_KEY_DESCRIPTION);
        addStyle(CLASS_TYPE_SESSION_NAME, CLASS_TYPE_SESSION_DESCRIPTION);
        addStyle(CLASS_SESSION_NAME, CLASS_SESSION_NAME_DESCRIPTION);
        addStyle(CLASS_TYPE_SESSION_NAME_INVALID, CLASS_TYPE_SESSION_INVALID_DESCRIPTION);
        addStyle(CLASS_TYPE_NAME_INVALID, CLASS_TYPE_NAME_INVALID_DESCRIPTION);
        addStyle(CLASS_POST_STN_NAME, CLASS_POST_STN_NAME_DESCRIPTION);
        addStyle(DROP_COPY_CLASS_TYPE_NAME, DROP_COPY_CLASS_TYPE_NAME_DESCRIPTION);

        setDefaultStyle(PLAIN_CLASS_NAME);
        cache = FormatterCacheFactory.create(1000, 3);
    }

    public ProductClassFormatter(boolean cacheEnabled)
    {
        this();
        cache.setCacheEnabled(cacheEnabled);
    }

    /**
     * Formats a Product Class
     * @param productClass to format
     */
    public String format(ProductClass productClass)
    {
        return format(productClass, getDefaultStyle(), true);
    }

    /**
     * Defines a method for formatting ProductClasses
     * @param productClass to format
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(ProductClass productClass, boolean useCache)
    {
        return format(productClass, getDefaultStyle(), true);
    }

    /**
     * Formats a Product Class
     * @param productClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(ProductClass productClass, String styleName)
    {
        return format(productClass, styleName, true);
    }

    /**
     * Formats a Product Class
     * @param productClass to format
     * @param styleName to use for formatting
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(ProductClass productClass, String styleName, boolean useCache)
    {
        validateStyle(styleName);
        String value = null;
        if (useCache)
        {
            value = cache.get(productClass, styleName);
        }
        if (value == null)
        {
            StringBuffer classText = new StringBuffer(15);

            if(styleName.equals(PLAIN_CLASS_NAME))
            {
                classText.append(getClassSymbol(productClass));
            }
            else if(styleName.equals(CLASS_TYPE_NAME) || styleName.equals(CLASS_TYPE_SESSION_NAME))
            {
                classText.append(getClassSymbol(productClass));
                classText.append(' ');
                classText.append(getClassType(productClass));
            }
            else if (styleName.equals(CLASS_TYPE_NAME_KEY))
            {
                classText.append(getClassSymbol(productClass));
                classText.append(' ');
                classText.append(getClassType(productClass));
                classText.append("   Key: ");
                classText.append(getClassKey(productClass));
            }
            else if(styleName.equals(CLASS_TYPE_NAME_INVALID) || styleName.equals(CLASS_TYPE_SESSION_NAME_INVALID))
            {
                classText.append(getClassSymbol(productClass));
                classText.append(' ');
                classText.append(getClassType(productClass));
                if (!productClass.isValid())
                {
                    classText.append(" (");
                    classText.append("Invalid");
                    classText.append(") ");
                }
            } 
            else if(styleName.equals(CLASS_POST_STN_NAME))
            {
                classText.append(getClassSymbol(productClass));
                if(!getPost(productClass).equals("") || !getStation(productClass).equals("")){
                    classText.append("(");
                    classText.append(getPost(productClass));
                    classText.append("/");
                    classText.append(getStation(productClass));
                    classText.append(")");
                }
            }
            else if(styleName.equals(DROP_COPY_CLASS_TYPE_NAME))
            {
                classText.append(getClassSymbol(productClass));
                classText.append(' ');
                classText.append(getClassType(productClass, ProductTypes.DROP_COPY_FORMAT));
            }

            value = classText.toString();

            cache.put(productClass, styleName, value);
        }
        return value;
    }


    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass)
    {
        return format(sessionProductClass, getDefaultStyle(), true);
    }

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, boolean useCache)
    {
        return format(sessionProductClass, getDefaultStyle(), useCache);
    }
    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, String styleName)
    {
        return format(sessionProductClass, styleName, true);
    }

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param styleName to use for formatting
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, String styleName, boolean useCache)
    {
        validateStyle(styleName);

        String retVal = null;
        if (useCache)
        {
            retVal = cache.get(sessionProductClass, styleName);
        }
        if (retVal == null)
        {
            StringBuffer classText = new StringBuffer(20);

            if(styleName.equals(PLAIN_CLASS_NAME))
            {
                classText.append(getClassSymbol(sessionProductClass));
            }
            else if(styleName.equals(CLASS_TYPE_NAME))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(' ');
                classText.append(getClassType(sessionProductClass));
            }
            else if(styleName.equals(CLASS_TYPE_SESSION_NAME))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(" (");
                classText.append(getClassSession(sessionProductClass));
                classText.append(") ");
                classText.append(getClassType(sessionProductClass));
            }
            else if(styleName.equals(CLASS_SESSION_NAME))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(" (");
                classText.append(getClassSession(sessionProductClass));
                classText.append(")");
            }
            else if (styleName.equals(CLASS_TYPE_NAME_KEY))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(' ');
                classText.append(getClassType(sessionProductClass));
                classText.append("   Key: ");
                classText.append(getClassKey(sessionProductClass));
            }
            else if(styleName.equals(CLASS_TYPE_SESSION_NAME_INVALID))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(" (");
                classText.append(getClassSession(sessionProductClass));
                classText.append(") ");
                classText.append(getClassType(sessionProductClass));
                if (!sessionProductClass.isValid())
                {
                    classText.append(" (");
                    classText.append("Invalid");
                    classText.append(") ");
                }
            }
            else if(styleName.equals(CLASS_TYPE_NAME_INVALID))
            {
                classText.append(getClassSymbol(sessionProductClass));
                classText.append(' ');
                classText.append(getClassType(sessionProductClass));
                if (!sessionProductClass.isValid())
                {
                    classText.append(" (");
                    classText.append("Invalid");
                    classText.append(") ");
                }
            }
            else if(styleName.equals(CLASS_POST_STN_NAME))
            {
                classText.append(getClassSymbol(sessionProductClass));
                if (!getPost(sessionProductClass).equals("") || !getStation(sessionProductClass).equals("")) {
                    classText.append("(");
                    classText.append(getPost(sessionProductClass));
                    classText.append("/");
                    classText.append(getStation(sessionProductClass));
                    classText.append(")");
                }
            }

            retVal = classText.toString();
            cache.put(sessionProductClass, styleName, retVal);
        }
        return retVal;
    }

    /**
     * Gets the class symbol
     * @param productClass to get symbol from
     * @return Class symbol
     */
    private String getClassSymbol(ProductClass productClass)
    {
        return productClass.getClassSymbol();
    }

    /**
     * Gets the post location
     * @param productClass to get post from
     * @return Post
     */
    private String getPost(ProductClass productClass)
    {
        return productClass.getPost() != null ? productClass.getPost(): "";
    }

    /**
     * Gets the station location
     * @param productClass to get station from
     * @return Post
     */
    private String getStation(ProductClass productClass)
    {
        return productClass.getStation() != null ? productClass.getStation(): "";
    }

    /**
     * Gets the class type
     * @param productClass to get type from
     * @return class type as text
     */
    private String getClassType(ProductClass productClass)
    {
        return getClassType(productClass, ProductTypes.TRADERS_FORMAT);
    }

    /**
     * Gets the class type
     * @param productClass to get type from
     * @param String formatting style name
     * @return class type as text
     */
    private String getClassType(ProductClass productClass, String style)
    {
        StringBuffer type = new StringBuffer(25);
        if(!productClass.isAllSelectedProductClass() && !productClass.isDefaultProductClass())
        {
            type.append('(');
            type.append(ProductTypes.toString(productClass.getProductType(), style));
            type.append(')');
        }

        return type.toString();
    }

    /**
     * Gets the session name of the class
     * @param sessionProductClass to get session name from
     * @return session name
     */
    private String getClassSession(SessionProductClass sessionProductClass)
    {
        if( sessionProductClass.isDefaultSession() )
        {
            return TradingSessionFormatStrategy.ALL_SESSIONS_FORMATTED_NAME;
        }
        else
        {
            return sessionProductClass.getTradingSessionName();
        }
    }

    /**
     * Gets the class key of the class
     * @param productClass to get key from
     * @return key
     */
    private String getClassKey(ProductClass productClass)
    {
        return String.valueOf(productClass.getClassKey());
    }


}
