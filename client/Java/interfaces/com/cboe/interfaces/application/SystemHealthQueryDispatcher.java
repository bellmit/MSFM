package com.cboe.interfaces.application;

public interface SystemHealthQueryDispatcher
{
    public String getContextDetail(String xmlInput);
    public String getProductData(String xmlInput);
    public String getConfigurationData(String xmlInput);
}
