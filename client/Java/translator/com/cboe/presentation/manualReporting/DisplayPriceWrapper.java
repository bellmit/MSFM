package com.cboe.presentation.manualReporting;

import com.cboe.domain.util.StructBuilder;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

public class DisplayPriceWrapper 
{
	public static final Price   NO_PRICE = DisplayPriceFactory.createNoPrice();
    public static final PriceStruct NO_PRICE_STRUCT = StructBuilder.buildPriceStruct();

    private Price   price;
    private Price   oldPrice;

    private PriceStruct oldPriceStruct;
    private PriceStruct newPriceStruct;

    public DisplayPriceWrapper() {
        super();
        price    = NO_PRICE;
        oldPrice = NO_PRICE;
        oldPriceStruct = NO_PRICE_STRUCT;
        newPriceStruct = NO_PRICE_STRUCT;
        
    }

    public Price getPrice() {
        if (price == null){
            if (isEqual(newPriceStruct, oldPriceStruct)) {
                price = oldPrice;
            }
            else {
                price = DisplayPriceFactory.create(newPriceStruct);
                oldPrice = price;
                oldPriceStruct = newPriceStruct;
            }
        }
        
        return price;
    }

    private boolean isEqual(PriceStruct base, PriceStruct compare) {
        if(base != null && compare != null) {
            if(base == compare) {
                return true;
            }
            return (base.fraction == compare.fraction &&
                    base.whole == compare.whole &&
                    base.type == compare.type);
        }
        return false;
    }

    public void setPrice(PriceStruct p){
        if (p==null){
            newPriceStruct = NO_PRICE_STRUCT;
        }
        else {
            newPriceStruct = p;
        }
        this.price = null;
    }
    
    public void setNoPrice(){
        setPrice(NO_PRICE_STRUCT);
    }
}