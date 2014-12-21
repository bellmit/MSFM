package com.cboe.presentation.common.comparators;

import java.util.*;

/**
  ProductComparator is a comparison used to compare products. The comparison is customizable
  in that it supports a user defined product field comparison order. By default, this comparator
  will compare products by option type, then by activation date and then by strike price.
  @author Will McNabb
*/
public class ProductComparator implements Comparator
{
///////////////////////////////////////////////////////////////////////////////
// CONSTANTS

    public static final String SYMBOL = "SYMBOL";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String OPTION_TYPE = "TYPE";
    public static final String ACTIVATION_DATE = "ACTIVATIONDATE";
    public static final String MATURITY_DATE = "MATURITYDATE";
    public static final String STRIKE_PRICE = "PRICE";
    public static final String CLASS = "CLASS";
    public static final String EXPIRATIONCLASS = "EXPIRATIONCLASS";

///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    protected String[] comparisonOrder;
    protected Comparator[] comparatorList;

///////////////////////////////////////////////////////////////////////////////
// STATIC

    protected static ProductPriceComparator priceComparator = new ProductPriceComparator();
    protected static ProductActivationDateComparator activationDateComparator = new ProductActivationDateComparator();
    protected static ProductMaturityDateComparator maturityDateComparator = new ProductMaturityDateComparator();
    protected static ProductOptionTypeComparator optionTypeComparator = new ProductOptionTypeComparator();
    protected static ProductDescriptionComparator descriptionComparator = new ProductDescriptionComparator();
    protected static ProductSymbolComparator symbolComparator = new ProductSymbolComparator();
    protected static ProductClassComparator classComparator = new ProductClassComparator();
    protected static ProductExpirationTypeComparator expirationComparator = new ProductExpirationTypeComparator();
    protected static HashMap comparators;

    static
    {
        comparators = new HashMap();
        comparators.put(OPTION_TYPE, optionTypeComparator);
        comparators.put(ACTIVATION_DATE, activationDateComparator);
        comparators.put(MATURITY_DATE, maturityDateComparator);
        comparators.put(STRIKE_PRICE, priceComparator);
        comparators.put(DESCRIPTION, descriptionComparator);
        comparators.put(SYMBOL, symbolComparator);
        comparators.put(CLASS, classComparator);
        comparators.put(EXPIRATIONCLASS, expirationComparator);
    }

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
      Creates a ProductComparator which will compare fields in the given order
      of importance.<br>
      Symbol<br>
      <&nbsp><&nbsp>Option Type<br>
      <&nbsp><&nbsp><&nbsp><&nbsp>Maturity Date<br>
      <&nbsp><&nbsp><&nbsp><&nbsp><&nbsp><&nbsp>Strike Price<br>
    */
    public ProductComparator()
    {
        super();
     //   String[] defaultOrder = {CLASS, EXPIRATIONCLASS, OPTION_TYPE, MATURITY_DATE, STRIKE_PRICE};
        String[] defaultOrder = {EXPIRATIONCLASS, CLASS, OPTION_TYPE, MATURITY_DATE, STRIKE_PRICE};
        setComparisonOrder(defaultOrder);
    }
    /**
      Creates a ProductComparator which will compare fields in the default order
      of importance.
      @param String[] comparisonOrder
    */
    public ProductComparator(String[] comparisonOrder)
    {
        setComparisonOrder(comparisonOrder);
    }

///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    /**
      Implements Comparator
    */
    public int compare(Object product1, Object product2)
    {
        int result = -1;
        for (int i=0; i<this.comparisonOrder.length; i++)
        {
            Comparator comp = comparatorList[i];
            result = comp.compare(product1, product2);
            //If the comparison result did not return equal, we have the final result
            //- break out of the loop.
            if (result != 0)
            {
                break;
            }
        }
        return result;
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
      Set the product field comparison order.
      @param String[] comparisonOrder
    */
    public void setComparisonOrder(String[] comparisonOrder)
    {
        this.comparisonOrder = comparisonOrder;
        this.comparatorList = new Comparator[this.comparisonOrder.length];
        for (int i=0; i<this.comparatorList.length; i++)
        {
            this.comparatorList[i] = (Comparator)comparators.get(this.comparisonOrder[i]);
        }
    }

}

