package com.cboe.presentation.common.comparators;

import com.cboe.interfaces.presentation.product.ProductContainer;

/**
  This class allows comparison of product data when given a ProductContainer instance.
  @author Will McNabb
*/
public class ProductContainerComparator extends ProductComparator
{
    public final String Category = this.getClass().getName();

    public ProductContainerComparator()
    {
        super();
    }
    public ProductContainerComparator(String[] comparisonOrder)
    {
        super(comparisonOrder);
    }

    /**
      Implements Comparator
    */
    public int compare(Object pc1, Object pc2)
    {
        int result = -1;
        result = super.compare(((ProductContainer)pc1).getContainedProduct(), ((ProductContainer)pc2).getContainedProduct());
        return result;
    }
}

