// -----------------------------------------------------------------------------------
// Source file: ProductSettlementOpenInterestModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.product;

import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.internalPresentation.product.ProductSettlement;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementModel;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterest;
import com.cboe.interfaces.internalPresentation.product.ProductOpenInterestModel;
import com.cboe.interfaces.internalPresentation.product.ProductSettlementOpenInterestModel;
import com.cboe.interfaces.domain.Price;
import com.cboe.internalPresentation.product.ProductSettlementFactory;
import com.cboe.internalPresentation.product.ProductOpenInterestFactory;
import org.omg.CORBA.UserException;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;


public class ProductSettlementOpenInterestModelImpl extends AbstractMutableBusinessModel implements ProductSettlementOpenInterestModel
{
    private ProductSettlementModel productSettlementModel = null;
    private ProductOpenInterestModel productOpenInterestModel = null;
    private boolean isOpenInterstEditable = true;

    /**
     * Constructor
     * @param struct ProductSettlementStruct to represent
     * @param struct ProductOpenInterestStruct to represent
     */
    protected ProductSettlementOpenInterestModelImpl(ProductSettlementStruct settlementStruct, ProductOpenInterestStruct openInterestStruct)
    {
        this(ProductSettlementFactory.createProductSettlementModel(settlementStruct), ProductOpenInterestFactory.createProductOpenInterestModel(openInterestStruct));
    }

    /**
     * Constructor
     * @param struct ProductSettlement to represent
     * @param struct ProductOpenInterest to represent
     */
//    protected ProductSettlementOpenInterestModelImpl(ProductSettlement settlement, ProductOpenInterest openInterest)
//    {
//        this(ProductSettlementFactory.createProductSettlementModel(settlement), ProductOpenInterestFactory.createProductOpenInterestModel(openInterest));
//    }

    /**
     *  Constructor.
     *  @param ProductSettlementModel to represent
     *  @param ProductOpenInterestModel to represent
     */
    protected ProductSettlementOpenInterestModelImpl(ProductSettlementModel productSettlement, ProductOpenInterestModel openInterest)
    {
        super();
        setProductSettlementOpenInterest(productSettlement, openInterest);
    }


    public ProductSettlementModel getProductSettlementModel()
    {
        return productSettlementModel;
    }

    public ProductOpenInterestModel getProductOpenInterestModel()
    {
        return productOpenInterestModel;
    }

    public ProductSettlement getProductSettlement()
    {
        return (ProductSettlement)productSettlementModel;
    }

    public ProductOpenInterest getProductOpenInterest()
    {
        return (ProductOpenInterest)productOpenInterestModel;
    }

//    public ProductOpenInterestStruct getProductOpenInterestStruct()
//    {
//        return getProductOpenInterestModel().getProductOpenInterestStruct();
//    }

    public Integer getOpenInterest()
    {
        return getProductOpenInterestModel().getOpenInterest();
    }
    /**
     *  Sets models and performs nessesary validation
     *  @param ProductSettlementModel to represent
     *  @param ProductOpenInterestModel to represent
     */
    private void setProductSettlementOpenInterest(ProductSettlementModel settlement, ProductOpenInterestModel openInterest)
    {
        if (settlement == null)
        {
            throw new IllegalArgumentException("ProductSettlementModel can not be NULL");
        }
        if (openInterest == null)
        {
            throw new IllegalArgumentException("ProductOpenInterestModel can not be NULL");
        }

        if ( settlement.getProduct().equals(openInterest.getProduct()) )
        {
            productSettlementModel = settlement;
            productOpenInterestModel = openInterest;
        }
        else
        {
            throw new IllegalArgumentException("ProductSettlementModel and ProductOpenInterestModel represent different products: "+settlement.getProduct()+", "+openInterest.getProduct());
        }

    }

    public void setOpenInterest(Integer interest)
    {
        Integer oldOpenInterest = getOpenInterest();
        getProductOpenInterestModel().setOpenInterest(interest);

        if ( getProductOpenInterestModel().isModified() )
        {
            setModified(true);
            Integer newOpenInterest = interest;
            firePropertyChange(DATA_CHANGE_EVENT, oldOpenInterest, newOpenInterest);
        }
    }

    public ProductSettlementStruct getProductSettlementStruct()
    {
        return getProductSettlementModel().getProductSettlementStruct();
    }

    public ProductOpenInterestStruct getProductOpenInterestStruct()
    {
        return getProductOpenInterestModel().getProductOpenInterestStruct();
    }

    public int getProductKey()
    {
        return getProductSettlementModel().getProductKey();
    }

    public int getClassKey()
    {
        return getProductSettlementModel().getClassKey();
    }

    public short getProductType()
    {
        return getProductSettlementModel().getProductType();
    }
    public int getReportingClassKey()
    {
        return getProductSettlementModel().getReportingClassKey();
    }

    public ProductKeysStruct getProductKeysStruct()
    {
        return getProductSettlementModel().getProductKeysStruct();
    }

    public PriceStruct getSettlementPriceStruct()
    {
        return getProductSettlementModel().getSettlementPriceStruct();
    }

    public Price getSettlementPrice()
    {
        return getProductSettlementModel().getSettlementPrice();
    }

    public Product getProduct()
    {
        return getProductSettlementModel().getProduct();
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProductSettlementModel clonedProductSettlement = (ProductSettlementModel)getProductSettlementModel().clone();
        ProductOpenInterestModel clonedProductOpenInterest = (ProductOpenInterestModel)getProductOpenInterestModel().clone();
        return new ProductSettlementOpenInterestModelImpl(clonedProductSettlement, clonedProductOpenInterest);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof ProductSettlementOpenInterestModel )
        {
            isEqual = (this.getProductKey() == ((ProductSettlementOpenInterestModel)obj).getProductKey());
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getProductKey();
    }

    public void setProduct(Product product)
    {
        if ( product == null )
        {
            throw new IllegalArgumentException("Product can not be NULL");
        }
        getProductSettlementModel().setProduct(product);
        getProductOpenInterestModel().setProduct(product);
    }

//    public void setProductKeysStruct(ProductKeysStruct keysStruct)
//    {
//        if ( keysStruct == null )
//        {
//            throw new IllegalArgumentException("ProductKeysStruct can not be NULL");
//        }
//        ProductKeysStruct oldStruct = getProductKeysStruct();
//
//        getProductSettlementModel().setProductKeysStruct(keysStruct);
//        getProductOpenInterestModel().setProductKeysStruct(keysStruct);
//
//        if ( getProductSettlementModel().isModified() )
//        {
//            setModified(true);
//            firePropertyChange(DATA_CHANGE_EVENT, oldStruct, keysStruct);
//        }
//    }

//    public void setSettlementPriceStruct(PriceStruct priceStruct)
//    {
//        PriceStruct oldPrice = getProductSettlementModel().getSettlementPriceStruct();
//
//        getProductSettlementModel().setSettlementPriceStruct(priceStruct);
//
//        if ( getProductSettlementModel().isModified() )
//        {
//            setModified(true);
//            firePropertyChange(DATA_CHANGE_EVENT, oldPrice, priceStruct);
//        }
//
//    }
    public void setSettlementPrice(Price price)
    {
        if ( price == null )
        {
            throw new IllegalArgumentException("Price can not be NULL");
        }
        getProductSettlementModel().setSettlementPrice(price);
    }

    /**
     *  Saves any mods made to the business model
     */
    public void saveChanges() throws UserException
    {
        getProductSettlementModel().saveChanges();
        if ( isOpenInterstEditable )
        {
            getProductOpenInterestModel().saveChanges();
        }
        firePropertyChange(SAVED_EVENT,null,this);
    }

    /**
     *  Reloads data from the server
     */
    public void refreshData() throws UserException
    {
        getProductSettlementModel().refreshData();
        getProductOpenInterestModel().refreshData();
        firePropertyChange(RELOADED_EVENT,null,this);
    }

    public boolean isModified()
    {
        return getProductSettlementModel().isModified() || getProductOpenInterestModel().isModified();
    }

    public boolean isOpenInterestEditable()
    {
        return isOpenInterstEditable;
    }

    public void setOpenInterestEditable(boolean flag)
    {
        isOpenInterstEditable = flag;
    }

//    protected void setProductSettlementModel(ProductSettlementModel productSettlementModel, boolean fireEvent)
//    {
//        this.productSettlementModel = productSettlementModel;
//        if (fireEvent)
//        {
//            firePropertyChange(DATA_CHANGE_EVENT,null,this);
//        }
//    }

}