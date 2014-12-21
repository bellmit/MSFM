package com.cboe.internalPresentation.product;

import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.idl.product.PriceAdjustmentItemStruct;
import com.cboe.idl.product.PriceAdjustmentClassStruct;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustmentClassModel;

public class PriceAdjustmentClassModelImpl implements PriceAdjustmentClassModel
{

    PriceAdjustmentClassStruct priceAdjustmentClassStruct;

    private PriceAdjustmentClassModelImpl()
    {
    }

    protected PriceAdjustmentClassModelImpl(PriceAdjustmentClassStruct struct)
    {
        super();
        this.setPriceAdjustmentClassStruct(struct);
    }

    public String toString()
    {
        return this.getCurrentClassSymbol() + " ("+ProductTypes.toString(this.getProductType())+")";
    }

    public void setPriceAdjustmentClassStruct(PriceAdjustmentClassStruct struct)
    {
        this.priceAdjustmentClassStruct = struct;
    }

    public PriceAdjustmentClassStruct getPriceAdjustmentClassStruct()
    {
        return this.priceAdjustmentClassStruct;
    }

    public short getAction()
    {
        return this.getPriceAdjustmentClassStruct().action;
    }

    public int getClassKey()
    {
        return this.getPriceAdjustmentClassStruct().classKey;
    }

    public String getCurrentClassSymbol()
    {
        return this.getPriceAdjustmentClassStruct().currentClassSymbol;
    }

    public short getProductType()
    {
        return this.getPriceAdjustmentClassStruct().productType;
    }

    public String getNewClassSymbol()
    {
        return this.getPriceAdjustmentClassStruct().newClassSymbol;
    }

    public int getBeforeContractSize()
    {
        return this.getPriceAdjustmentClassStruct().beforeContractSize;
    }

    public int getAfterContractSize()
    {
        return this.getPriceAdjustmentClassStruct().afterContractSize;
    }

    public PriceAdjustmentItemStruct[] getItems()
    {
        return this.getPriceAdjustmentClassStruct().items;
    }

    public void setAction(short action)
    {
        this.getPriceAdjustmentClassStruct().action = action;
    }

    public void setClassKey(int classKey)
    {
        this.getPriceAdjustmentClassStruct().classKey = classKey;
    }

    public void setCurrentClassSymbol(String currentClassSymbol)
    {
        this.getPriceAdjustmentClassStruct().currentClassSymbol = currentClassSymbol;
    }

    public void setProductType(short productType)
    {
        this.getPriceAdjustmentClassStruct().productType = productType;
    }

    public void setNewClassSymbol(String newClassSymbol)
    {
        this.getPriceAdjustmentClassStruct().newClassSymbol = newClassSymbol;
    }

    public void setBeforeContractSize(int beforeContractSize)
    {
        this.getPriceAdjustmentClassStruct().beforeContractSize = beforeContractSize;
    }

    public void setAfterContractSize(int afterContractSize)
    {
        this.getPriceAdjustmentClassStruct().afterContractSize = afterContractSize;
    }

    public void setItems(PriceAdjustmentItemStruct[] items)
    {
        this.getPriceAdjustmentClassStruct().items = items;
    }

//
//    public ValidationResult validateData()
//    {
//        return null;
//    }

    /**
     * Clones this PriceAdjustment by returning another instance that represents a
     * PriceAdjustmentStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        PriceAdjustmentClassModel dest = null;
        if (getPriceAdjustmentClassStruct() != null)
        {
            PriceAdjustmentClassStruct clonedStruct = ProductStructBuilder.clonePriceAdjustmentClassStruct(getPriceAdjustmentClassStruct());
            dest = PriceAdjustmentClassModelFactory.create(clonedStruct);
        }
        return dest;
    }

    public int hashCode()
    {
        return this.getClassKey();
    }
}

