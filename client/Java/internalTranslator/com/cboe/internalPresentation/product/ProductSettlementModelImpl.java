// -----------------------------------------------------------------------------------
// Source file: ProductSettlementModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.internalPresentation.product.ProductSettlement;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementModel;
import com.cboe.interfaces.domain.Price;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.internalPresentation.product.ProductSettlementFactory;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.api.APIHome;
import org.omg.CORBA.UserException;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
//import java.beans.PropertyChangeListener;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;


public class ProductSettlementModelImpl extends AbstractMutableBusinessModel implements ProductSettlementModel
{
     private ProductSettlementStruct settlementStruct = null;
     private Price price = null;
     private Product product = null;

    /**
     * Constructor
     * @param struct ProductSettlementStruct to represent
     */
    protected ProductSettlementModelImpl(ProductSettlementStruct struct)
    {
        super();
        this.settlementStruct = struct;
    }

//////////////////////////////////////////////////////////////////////////////////
    public int getProductKey()
    {
        return getProductKeysStruct().productKey;
    }
    public int getClassKey()
    {
        return getProductKeysStruct().classKey;
    }
    public short getProductType()
    {
        return getProductKeysStruct().productType;
    }
    public int getReportingClassKey()
    {
        return getProductKeysStruct().reportingClass;
    }

    public ProductKeysStruct getProductKeysStruct()
    {
        if ( settlementStruct == null )
        {
            throw new IllegalStateException("ProductSettlementStruct can not be null.");
        }

        return settlementStruct.productKeys;
    }

    public PriceStruct getSettlementPriceStruct()
    {
        if ( settlementStruct == null )
        {
            throw new IllegalStateException("ProductSettlementStruct can not be null.");
        }

        return settlementStruct.settlementPrice;
    }

    public Price getSettlementPrice()
    {
        if ( price == null )
        {
            if ( settlementStruct == null )
            {
                throw new IllegalStateException("ProductSettlementStruct can not be null.");
            }

            price = PriceFactory.create(settlementStruct.settlementPrice);
        }
        return price;
    }

    public Product getProduct()
    {
        if( product == null )
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(getProductKey());
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
        return product;
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProductSettlementStruct clonedStruct = ProductStructBuilder.cloneProductSettlementStruct(this.settlementStruct);
        return new ProductSettlementModelImpl(clonedStruct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof ProductSettlementModel )
        {
            isEqual = (this.getProductKey() == ((ProductSettlementModel)obj).getProductKey());
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getProductKey();
    }

    public ProductSettlementStruct getProductSettlementStruct()
    {
        return this.settlementStruct;
    }

//////////////////////////////////////////////////////////////////////////////////

    public void setProduct(Product newProduct)
    {
        if ( newProduct == null )
        {
            throw new IllegalArgumentException("Product can not be NULL");
        }
        if ( !product.equals(newProduct) )
        {
            Product oldProduct = this.product;
            setProductKeysStruct(newProduct.getProductKeysStruct());
            this.product = newProduct;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldProduct, newProduct);
        }
    }

    private void setProductKeysStruct(ProductKeysStruct keysStruct)
    {
        if ( keysStruct == null )
        {
            throw new IllegalArgumentException("ProductKeysStruct can not be NULL");
        }

        ProductSettlementStruct settlementStruct = getProductSettlementStruct();
        if ( settlementStruct == null )
        {
            throw new IllegalStateException("ProductSettlementModel Illegal State: ProductSettlementStruct is null");
        }
        settlementStruct.productKeys = keysStruct;
    }

    private void setSettlementPriceStruct(PriceStruct priceStruct)
    {
        if ( priceStruct == null )
        {
            throw new IllegalArgumentException("PriceStruct can not be NULL");
        }

        ProductSettlementStruct settlementStruct = getProductSettlementStruct();
        if ( settlementStruct == null )
        {
            throw new IllegalStateException("ProductSettlementModel Illegal State: ProductSettlementStruct is null");
        }

        settlementStruct.settlementPrice = priceStruct;
    }

    public void setSettlementPrice(Price newPrice)
    {
        if ( newPrice == null )
        {
            throw new IllegalArgumentException("Price can not be NULL");
        }
        if ( !getSettlementPrice().equals(newPrice) )
        {
            Price oldPrice = price;
            this.price = newPrice;
            setSettlementPriceStruct(newPrice.toStruct());
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldPrice, newPrice);
        }
    }

    /**
     *  Saves any mods made to the business model
     */
    public void saveChanges() throws UserException
    {
        if ( isModified() )
        {
            GUILoggerHome.find().debug("Updating ProductSettlement...", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, getProductSettlementStruct());
            ProductSettlementStruct [] settlements = new ProductSettlementStruct[1];
            settlements[0] = getProductSettlementStruct();
            ProductSettlementStruct [] savedSettlements = SystemAdminAPIFactory.find().updateSettlementByProduct(settlements);

            GUILoggerHome.find().debug("ProductSettlement Saved. New Struct: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, savedSettlements);

            setSettlementStruct(savedSettlements[0], false);
            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    /**
     *  Reloads data from the server
     */
    public void refreshData() throws UserException
    {
            GUILoggerHome.find().debug("Reloading ProductSettlement...", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, getProductSettlementStruct());
            ProductKeysStruct [] productKeys = new ProductKeysStruct[1];
            productKeys[0] = getProductKeysStruct();
            ProductSettlementStruct [] newSettlements = SystemAdminAPIFactory.find().getSettlementForProducts(productKeys);

            GUILoggerHome.find().debug("ProductSettlement Reloaded. New Struct: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, newSettlements);

            setSettlementStruct(newSettlements[0], false);
            firePropertyChange(RELOADED_EVENT,null,this);
    }

    private void setSettlementStruct(ProductSettlementStruct settlementStruct, boolean fireEvent)
    {
        this.settlementStruct = settlementStruct;
        price = null;
        product = null;
        if (fireEvent)
        {
            firePropertyChange(DATA_CHANGE_EVENT,null,this);
        }
    }

}