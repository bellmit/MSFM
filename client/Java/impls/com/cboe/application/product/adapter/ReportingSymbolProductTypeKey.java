package com.cboe.application.product.adapter;

public class ReportingSymbolProductTypeKey
{
    String reportingClassSymbol;
    short  productType;
    private int hashcode;
    public ReportingSymbolProductTypeKey(String reportingClassSymbol, short productType)
    {
        this.reportingClassSymbol = reportingClassSymbol;
        this.productType = productType;
        this.hashcode = (reportingClassSymbol + productType).hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReportingSymbolProductTypeKey other = (ReportingSymbolProductTypeKey) obj;
        if (!reportingClassSymbol.equals(other.reportingClassSymbol))
            return false;
        if (productType != other.productType)
            return false;
        return true;
    }
    
    @Override
    public int hashCode()
    {     
        return hashcode;
    }   
    
    
}
