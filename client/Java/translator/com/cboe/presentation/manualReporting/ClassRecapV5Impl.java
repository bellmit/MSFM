package com.cboe.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.ClassRecapStructV5;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.manualReporting.ClassRecap;
import com.cboe.interfaces.presentation.manualReporting.ClassRecapV5;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
/**
 * ClassRecapV5 implementation for a ClassRecapStructV5 from the API.
 */
public class ClassRecapV5Impl extends AbstractBusinessModel implements ClassRecapV5 {

    protected ClassRecapStructV5 classRecapStructV5 = null;
    private int productKey = -1;
    ClassRecapImpl  productRecap	= new ClassRecapImpl();
	ClassRecapImpl	underlyingRecap	= new ClassRecapImpl();

    private DisplayPriceWrapper underlyingHighPrice = new DisplayPriceWrapper();
    private DisplayPriceWrapper underlyingLowPrice = new DisplayPriceWrapper();
    private DisplayPriceWrapper	underlyingLastSalePrice =  new DisplayPriceWrapper();

    /**
     * Constructors
     * @param classVolumeStruct to represent
     */
    public ClassRecapV5Impl(ClassRecapStructV5 classRecapStructV5)
    {
        super();
        this.classRecapStructV5 = classRecapStructV5;
    	this.productRecap.setValue( getStruct().productRecaps[0].aRecapStruct);
    	this.underlyingRecap.setValue(getStruct().underlyingRecap.aRecapStruct);

    	this.underlyingHighPrice.setPrice(getStruct().underlyingRecap.aRecapStruct.highPrice);
    	this.underlyingLowPrice.setPrice(getStruct().underlyingRecap.aRecapStruct.lowPrice);
    	this.underlyingLastSalePrice.setPrice(getStruct().underlyingRecap.aRecapStruct.lastSalePrice);
    }
    
    public int getProductKey() {
        return this.productKey;
    }
    
    // helper methods to struct attributes
    public ClassRecap getProductRecap() {
        return this.productRecap;
    }

    public DateTimeStruct getLowPriceTime() {
       return getStruct().productRecaps[0].lowPriceTime;
    }

    public DateTimeStruct getHighPriceTime() {
        return getStruct().productRecaps[0].highPriceTime;
     }

    public DateTimeStruct getOpeningPriceTime() {
        return getStruct().productRecaps[0].openingPriceTime;
     }

    public String getRecapSuffix() {
        return getStruct().productRecaps[0].aRecapSuffix;
     }

    public ClassRecapStructV5 getStruct() {
        return this.classRecapStructV5;
    }
    
    public void setProductKey(int productKey)
    {
    	this.productKey = productKey;
    }
    
    public Price getUnderlyingHighPrice()
    {
    	return underlyingHighPrice.getPrice();
    }

    public Price getUnderlyingLowPrice()
    {
    	return underlyingLowPrice.getPrice();
    	
    }
    
    public Price getUnderlyingLastSalePrice()
    {
    	return underlyingLastSalePrice.getPrice();
    }
}
