package com.cboe.interfaces.presentation.product;

/**
 This is the interface implemented by business objects that provide and maintain
 a cached formatted string of their product value. The main purpose of this class
 is to allow a common renderer to be used for tables that have to display a rendered
 String representing a product. 
 @author Will McNabb
*/
public interface ProductContainer
{
    public String getProductRenderString();
    public Product getContainedProduct();   
    public ProductClass getContainedProductClass();
}

