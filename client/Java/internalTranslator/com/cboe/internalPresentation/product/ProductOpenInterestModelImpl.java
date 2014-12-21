// -----------------------------------------------------------------------------------
// Source file: ProductOpenInterestModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterest;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterestModel;
import com.cboe.interfaces.domain.Price;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.internalPresentation.product.ProductOpenInterestFactory;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.api.APIHome;
import org.omg.CORBA.UserException;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
//import java.beans.PropertyChangeListener;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;


public class ProductOpenInterestModelImpl extends AbstractMutableBusinessModel implements ProductOpenInterestModel
{
    private ProductOpenInterestStruct openInterestStruct = null;
    private Product product = null;
    private Integer interest = null;

    /**
     * Constructor
     * @param struct ProductOpenInterestStruct to represent
     */
    protected ProductOpenInterestModelImpl(ProductOpenInterestStruct struct)
    {
        super();
        setProductOpenInterestStruct(struct, false);
    }

    /**
     *  Constructor.
     *  @param productOpenInterest to represent
     */
//    protected ProductOpenInterestModelImpl(ProductOpenInterest productOpenInterest)
//    {
//        super();
//        this.openInterestStruct = productOpenInterest.getProductOpenInterestStruct();
//    }
///////////////////////////////////////////////////////////////////////////////////
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
        if ( openInterestStruct == null )
        {
            throw new IllegalStateException("ProductOpenInterstStruct can not be null.");
        }

        return openInterestStruct.productKeys;
    }

    public Integer getOpenInterest()
    {
        if (interest == null)
        {
            if ( openInterestStruct == null )
            {
                throw new IllegalStateException("ProductOpenInterstStruct can not be null.");
            }
            interest = new Integer(openInterestStruct.openInterest);
        }
        return interest;
    }

    public Product getProduct()
    {
        if ( product == null )
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

    public int hashCode()
    {
        return getProductKey();
    }

    public ProductOpenInterestStruct getProductOpenInterestStruct()
    {
        return this.openInterestStruct;
    }

///////////////////////////////////////////////////////////////////////////////////
    public Object clone() throws CloneNotSupportedException
    {
        ProductOpenInterestStruct clonedStruct = ProductStructBuilder.cloneProductOpenInterestStruct(this.openInterestStruct);
        return new ProductOpenInterestModelImpl(clonedStruct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof ProductOpenInterestModel )
        {
            isEqual = (this.getProductKey() == ((ProductOpenInterestModel)obj).getProductKey());
        }

        return isEqual;
    }

    public void setProduct(Product newProduct)
    {
        if ( newProduct == null )
        {
            throw new IllegalArgumentException("Product can not be NULL");
        }

        Product oldProduct = getProduct();
        if ( !oldProduct.equals(newProduct) )
        {
            setProductKeysStruct(newProduct.getProductKeysStruct());
            this.product = newProduct;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldProduct, product);
        }
    }

    private void setProductKeysStruct(ProductKeysStruct keysStruct)
    {
        if ( keysStruct == null )
        {
            throw new IllegalArgumentException("ProductKeysStruct can not be NULL");
        }

        ProductOpenInterestStruct openInterestStruct = getProductOpenInterestStruct();
        if ( openInterestStruct == null )
        {
            throw new IllegalStateException("ProductOpenInterestModel Illegal State: ProductOpenInterestStruct is null");
        }
        openInterestStruct.productKeys = keysStruct;
    }

    public void setOpenInterest(Integer openInterest)
    {
        if ( openInterest == null )
        {
            throw new IllegalArgumentException("OpenInterest can not be NULL");
        }

        if ( openInterest.intValue() < 0 )
        {
            throw new IllegalArgumentException("OpenInterest can not be negative, ("+openInterest.intValue()+")");
        }

        ProductOpenInterestStruct openInterestStruct = getProductOpenInterestStruct();
        if ( openInterestStruct == null )
        {
            throw new IllegalStateException("ProductOpenInterestModel Illegal State: ProductOpenInterestStruct is null");
        }

        // If new open interest is different from old open interest do the update
        if (getOpenInterest().intValue() != openInterest.intValue())
        {
            Integer oldOpenInterest = getOpenInterest();
            interest = openInterest;
            openInterestStruct.openInterest = openInterest.intValue();
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldOpenInterest, openInterest);
        }

    }

    /**
     *  Saves any mods made to the business model
     */
    public void saveChanges() throws UserException
    {
        if ( isModified() )
        {
            GUILoggerHome.find().debug("Updating OpenInterest...", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, getProductOpenInterestStruct());
            ProductOpenInterestStruct [] openInterestStructs = new ProductOpenInterestStruct[1];
            openInterestStructs[0] = getProductOpenInterestStruct();
            ProductOpenInterestStruct [] savedOpenInterestStructs = SystemAdminAPIFactory.find().updateOpenInterestByProduct(openInterestStructs);

            GUILoggerHome.find().debug("OpenInterest Saved. New Struct: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, savedOpenInterestStructs);

            setProductOpenInterestStruct(savedOpenInterestStructs[0], false);
            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    /**
     *  Reloads data from the server
     */
    public void refreshData() throws UserException
    {
            GUILoggerHome.find().debug("Reloading OpenInterest...", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, getProductOpenInterestStruct());
            ProductKeysStruct [] productKeys = new ProductKeysStruct[1];
            productKeys[0] = getProductKeysStruct();
            ProductOpenInterestStruct [] newOpenInterestStructs = SystemAdminAPIFactory.find().getOpenInterestForProducts(productKeys);

            GUILoggerHome.find().debug("OpenInterest Reloaded. New Struct: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, newOpenInterestStructs);

            setProductOpenInterestStruct(newOpenInterestStructs[0], false);
            firePropertyChange(RELOADED_EVENT,null,this);
    }

    protected void setProductOpenInterestStruct(ProductOpenInterestStruct newStruct, boolean fireEvent)
    {
        this.openInterestStruct = newStruct;
        product = null;
        interest = null;
        if (fireEvent)
        {
            firePropertyChange(DATA_CHANGE_EVENT,null,this);
        }
    }

}