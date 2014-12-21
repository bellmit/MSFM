//
// -----------------------------------------------------------------------------------
// Source file: ProductKeysImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import java.util.*;

import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.interfaces.presentation.common.formatters.Formattable;

import com.cboe.interfaces.presentation.product.ProductKeys;

/********************************************************************************
 * Contains product identity information
 *
 * @see com.cboe.idl.cmiProduct.ProductKeysStruct
 */
public class ProductKeysImpl implements Formattable, ProductKeys
{
//*** Public Attributes

    // Format constants
    public static final String NAME_VALUE_FORMAT = new String( "NAME_VALUE_FORMAT" );


//*** Private Attributes

    private Map               m_formattedStrings  = null;
    private ProductKeysStruct m_productKeysStruct = null;


//*** Public Methods

    /*****************************************************************************
     * Memberwise Constructor
     *
     * @see com.cboe.idl.cmiProduct.ProductKeysStruct
     */
    public ProductKeysImpl( ProductKeysStruct productKeysStruct )
    {
        m_formattedStrings  = new HashMap( );
        m_productKeysStruct = productKeysStruct;
    }


    /*****************************************************************************
     * Determines whether another ProductKeysImpl is equal to this one
     */
    public boolean equals(ProductKeys anotherProductKeys)
    {
        boolean result = false;

        if(anotherProductKeys != null &&
                getClassKey() == anotherProductKeys.getClassKey() &&
                getProductKey() == anotherProductKeys.getProductKey() &&
                getProductType() == anotherProductKeys.getProductType() &&
                getReportingClassKey() == anotherProductKeys.getReportingClassKey())
        {
            result = true;
        }
        return result;
    }

    public boolean equals(Object obj)
    {
        boolean equal = super.equals(obj);

        if(!equal)
        {
            if(obj != null && obj instanceof ProductKeys)
            {
                ProductKeys productKeys = (ProductKeys)obj;
                equal = equals(productKeys);
            }
        }
        return equal;
    }

    /*****************************************************************************
     * Returns the class key
     */
    public int getClassKey( )
    {
        return m_productKeysStruct.classKey;
    }


    /*****************************************************************************
     * Returns the product key
     */
    public int getProductKey( )
    {
        return m_productKeysStruct.productKey;
    }

    /**
     * Gets the underlying struct
     * @return ProductKeysStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ProductKeysStruct getStruct()
    {
        return m_productKeysStruct;
    }

    /*****************************************************************************
     * Returns the product type identifier
     */
    public short getProductTypeCode( )
    {
        return m_productKeysStruct.productType;
    }


    /*****************************************************************************
     * Returns the reporting class
     */
    public int getReportingClassKey( )
    {
        return m_productKeysStruct.reportingClass;
    }


    /*****************************************************************************
     * Returns a string representation of the object in NAME_VALUE_FORMAT format
     *
     * @return a string representation of the object
     */
    public String toString( )
    {
        return toString( NAME_VALUE_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the object
     * @see java.text.SimpleDateFormat
     */
    public String toString( String formatSpecifier )
    {
        // Check to see if we've already rendered this string
        String formattedString = ( String )m_formattedStrings.get( formatSpecifier );
        if( formattedString == null )
        {
            if( formatSpecifier.equals( NAME_VALUE_FORMAT ))
        {
                formattedString = new String( "Product Key: " + String.valueOf( getProductKey( )) +
                                              ", Class Key: " + String.valueOf( getClassKey( )) +
                                              ", Product Type Code: " + String.valueOf( getProductTypeCode( )) +
                                              ", Reporting Class Key: " + String.valueOf( getReportingClassKey( )));
            }
            else
            {
                formattedString = new String( "ERROR: Format not supported" );
            }
            m_formattedStrings.put( formatSpecifier, formattedString );
        }
        return formattedString;
    }


//*** Private Methods
    public short getProductType()
    {
        return getProductTypeCode();
    }

    public ProductKeysStruct toProductKeysStruct()
    {
        return m_productKeysStruct;
    }

    public Object getKey()
    {
        return new Integer(getProductKey());
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProductKeysStruct productKeysStruct = new ProductKeysStruct(
                m_productKeysStruct.productKey,
                m_productKeysStruct.classKey,
                m_productKeysStruct.productType,
                m_productKeysStruct.reportingClass);
        // don't worry about the m_formattedStrings maps, as it will be rebuilt
        // if the toString(formatSpecified) method is called.
        return new ProductKeysImpl(productKeysStruct);
    }
}