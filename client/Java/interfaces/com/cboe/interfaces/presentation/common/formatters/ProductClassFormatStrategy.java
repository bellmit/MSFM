//
// -----------------------------------------------------------------------------------
// Source file: ProductClassFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProductClass;

/**
 * Defines a contract for a class that formats ProductClass'es
 */
public interface ProductClassFormatStrategy extends FormatStrategy
{
    public static final String PLAIN_CLASS_NAME = "Plain Class Name";
    public static final String CLASS_TYPE_NAME =  "Class Name w/ Type";
    public static final String CLASS_TYPE_NAME_KEY =  "Class Name w/ Type and Key";
    public static final String CLASS_TYPE_SESSION_NAME = "Class Name w/ Type&Session";
    public static final String CLASS_SESSION_NAME = "Class Name w/ Session";
    public static final String CLASS_TYPE_SESSION_NAME_INVALID = "Class Name w/ Type&Session&Invalid";
    public static final String CLASS_TYPE_NAME_INVALID =  "Class Name w/ Type&Invalid";
    public static final String CLASS_POST_STN_NAME =  "Class Name w/ Post&Station";
    public static final String DROP_COPY_CLASS_TYPE_NAME = "Drop Copy Class Name w/ Type";

    public static final String PLAIN_CLASS_NAME_DESCRIPTION = "Plain Class Symbol with nothing added.";
    public static final String CLASS_TYPE_NAME_DESCRIPTION =  "Class Symbol with the class type added.";
    public static final String CLASS_TYPE_NAME_KEY_DESCRIPTION =  "Class Symbol with the class type and key added.";
    public static final String CLASS_TYPE_SESSION_DESCRIPTION = "Class Symbol with the class type and session name added.";
    public static final String CLASS_SESSION_NAME_DESCRIPTION = "Class Symbol with the session name added.";
    public static final String CLASS_TYPE_SESSION_INVALID_DESCRIPTION = "Class Symbol with the class type and session name added and if invalid.";
    public static final String CLASS_TYPE_NAME_INVALID_DESCRIPTION = "Class Symbol with the class type and if invalid.";
    public static final String CLASS_POST_STN_NAME_DESCRIPTION =  "Class Name with the post and station numbers added.";
    public static final String DROP_COPY_CLASS_TYPE_NAME_DESCRIPTION =
            "Drop Copy Class Symbol with the class type added.";

    /**
     * Defines a method for formatting ProductClasses
     * @param productClass to format
     * @return formatted string
     */
    public String format(ProductClass productClass);

    /**
     * Defines a method for formatting ProductClasses
     * @param productClass to format
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(ProductClass productClass, boolean useCache);

    /**
     * Defines a method for formatting ProductClasses
     * @param productClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(ProductClass productClass, String styleName);

    /**
     * Defines a method for formatting ProductClasses
     * @param productClass to format
     * @param styleName to use for formatting
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(ProductClass productClass, String styleName, boolean useCache);

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass);

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, boolean useCache);

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, String styleName);

    /**
     * Defines a method for formatting SessionProductClasses
     * @param sessionProductClass to format
     * @param styleName to use for formatting
     * @param useCache boolean flag that allows to rebild formatted string even if it is already cached
     * @return formatted string
     */
    public String format(SessionProductClass sessionProductClass, String styleName, boolean useCache);
}
