package com.cboe.interfaces.application;

public interface ProductQueryServiceAdapterHome
{
    public static final String HOME_NAME = "ProductQueryServiceAdapterHome";
    public ProductQueryServiceAdapter find();
    public ProductQueryServiceAdapter create();
}