package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.interfaces.domain.product.*;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.businessServices.ProductQueryServiceHome;

public class DomainObjectReferenceHelper
{
    /**
    * A reference to the product query service.
    */
    private static ProductQueryService productQueryService;
    /**
    * A reference to the home for product classes
    */
    private static ProductClassHome productClassHome;
    /**
    * A reference to the home for product descriptions.
    */
    private static ProductDescriptionHome productDescriptionHome;
    /**
    * A reference to the home for reporting classes.
    */
    private static ReportingClassHome reportingClassHome;
    /**
    * A reference to the home for products.
    */
    private static ProductHome productHome;
    /**
    * A reference to the home for product types.
    */
    private static ProductTypeHome productTypeHome;
    /**
    * A reference to the home for price adjustments.
    */
    private static PriceAdjustmentHome priceAdjustmentHome;

    /**
    * Gets reference to home for products.
    *
    * @return reference to product home
    */
    public static PriceAdjustmentHome getPriceAdjustmentHome()
    {
        if (priceAdjustmentHome == null)
        {
        try
        {
            priceAdjustmentHome = (PriceAdjustmentHome) HomeFactory.getInstance().findHome(PriceAdjustmentHome.HOME_NAME);
        }
        catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
        {
            throw new NullPointerException("Cannot not find ProductHome");
        }
        }
        return priceAdjustmentHome;
    }

    /**
    * Gets reference to home for product descriptions.
    *
    * @return reference to product class home
    */
    public static ProductDescriptionHome getProductDescriptionHome()
    {
        if (productDescriptionHome == null)
        {
            try
            {
                productDescriptionHome = (ProductDescriptionHome) HomeFactory.getInstance().findHome(ProductDescriptionHome.HOME_NAME);
            }
            catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
            {
                throw new NullPointerException("Cannot not find ProductDescriptionHome");
            }
        }
        return productDescriptionHome;
    }

    /**
    * Gets reference to home for products.
    *
    * @return reference to product home
    */
    public static ProductHome getProductHome()
    {
        if (productHome == null)
        {
            try
            {
                productHome = (ProductHome) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
            }
            catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
            {
                throw new NullPointerException("Cannot not find ProductHome");
            }
        }
        return productHome;
    }

    /**
    * Gets reference to the product query service.
    *
    * @return reference to query service
    */
    public static ProductQueryService getProductQueryService()
    {
        if (productQueryService == null)
        {
            try
            {
                ProductQueryServiceHome home = (ProductQueryServiceHome) HomeFactory.getInstance().findHome(ProductQueryServiceHome.HOME_NAME);
                productQueryService = home.find();
            }
            catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
            {
                throw new NullPointerException("Cannot not find ProductQueryService");
            }
        }
        return productQueryService;
    }

    /**
    * Gets reference to home for product types.
    *
    * @return reference to product type home
    */
    public static ProductTypeHome getProductTypeHome()
    {
        if (productTypeHome == null)
        {
            try
            {
                productTypeHome = (ProductTypeHome) HomeFactory.getInstance().findHome(ProductTypeHome.HOME_NAME);
            }
            catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
            {
                throw new NullPointerException("Cannot not find ProductTypeHome");
            }
        }
        return productTypeHome;
    }

    /**
    * Gets reference to home for reporting classes.
    *
    * @return reference to reporting class home
    */
    public static ReportingClassHome getReportingClassHome()
    {
        if (reportingClassHome == null)
        {
            try
            {
                reportingClassHome = (ReportingClassHome) HomeFactory.getInstance().findHome(ReportingClassHome.HOME_NAME);
            }
            catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
            {
                throw new NullPointerException("Cannot not find ReportingClassHome");
            }
        }
        return reportingClassHome;
    }

    /**
    * Gets reference to home for product classes.
    *
    * @return reference to product class home
    */
    public static ProductClassHome getProductClassHome()
    {
        if (productClassHome == null)
        {
        try
        {
            productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
        }
        catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
        {
            throw new NullPointerException("Cannot not find ProductClassHome");
        }
        }
        return productClassHome;
    }


}
