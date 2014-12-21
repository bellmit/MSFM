package com.cboe.internalPresentation.product;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.interfaces.internalPresentation.product.ProductDescriptionModel;
import com.cboe.internalPresentation.product.ProductDescriptionFactory;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.exceptions.*;
import com.cboe.domain.util.ProductStructBuilder;
import org.omg.CORBA.UserException;

public class ProductDescriptionModelImpl extends AbstractMutableBusinessModel implements ProductDescriptionModel
{
    ProductDescriptionStruct productDescriptionStruct;


    Boolean isBaseDescription;
//    // this 'key' is a work around because we have to implement the abstract hashcode() method in AbstractMutableBusinessModel; since it's abstract, we can't just use super.hashcode() (Object.hashcode())
//    private int key = -1;

    /**
     * Constructor
     * @param productDescriptionStruct to represent
     */
    protected ProductDescriptionModelImpl(ProductDescriptionStruct productDescriptionStruct)//, int uniqueKey)
    {
        this();

        // The struct doesn't have any unique int to use as hashCode.
        // Will count on ProductDescriptionFactory to assign a unique key for each impl.
//        this.key = uniqueKey;

        this.setProductDescriptionStruct(productDescriptionStruct, false);
    }

    /**
     *  Default constructor.
     */
    private ProductDescriptionModelImpl()
    {
        super();
    }

    public Boolean isBaseDescription()
    {
        if ( isBaseDescription == null )
        {
            boolean isBase = false;
            String name = getName();
            String baseDescName = getBaseDescriptionName();

            if ( name != null && name.length() > 0 &&
                 baseDescName != null && baseDescName.length()>0 &&
                 name.equals(baseDescName) )
            {
                isBase = true;
            }

            isBaseDescription = new Boolean(isBase);
        }
        return isBaseDescription;
    }

    public String getName()
    {
        return this.productDescriptionStruct.name;
    }

    public String getBaseDescriptionName()
    {
        return this.productDescriptionStruct.baseDescriptionName;
    }

    public PriceStruct getMinimumStrikePriceFraction()
    {
        return this.productDescriptionStruct.minimumStrikePriceFraction;
    }

    public PriceStruct getMaxStrikePrice()
    {
        return this.productDescriptionStruct.maxStrikePrice;
    }

    public PriceStruct getPremiumBreakPoint()
    {
        return this.productDescriptionStruct.premiumBreakPoint;
    }

    public PriceStruct getMinimumAbovePremiumFraction()
    {
        return this.productDescriptionStruct.minimumAbovePremiumFraction;
    }

    public PriceStruct getMinimumBelowPremiumFraction()
    {
        return this.productDescriptionStruct.minimumBelowPremiumFraction;
    }

    public short getPriceDisplayType()
    {
        return this.productDescriptionStruct.priceDisplayType;
    }

    public short getPremiumPriceFormat()
    {
        return this.productDescriptionStruct.premiumPriceFormat;
    }

    public short getStrikePriceFormat()
    {
        return this.productDescriptionStruct.strikePriceFormat;
    }

    public short getUnderlyingPriceFormat()
    {
        return this.productDescriptionStruct.underlyingPriceFormat;
    }

    public String toString()
    {
        return this.getName();
    }

    /**
     * get the ProductDescriptionStruct this wraps
     */
    public ProductDescriptionStruct getProductDescriptionStruct()
    {
        return this.productDescriptionStruct;
    }

    public void setProductDescriptionStruct(ProductDescriptionStruct struct)
    {
        this.setProductDescriptionStruct(struct, true);
    }

    public void setProductDescriptionStruct(ProductDescriptionStruct struct, boolean fireEvent)
    {
        if ( struct == null )
        {
            throw new IllegalArgumentException("ProductDescriptionStruct can not be null");
        }
        else
        {
            ProductDescriptionStruct oldValue = this.getProductDescriptionStruct();
            this.productDescriptionStruct = struct;
            this.isBaseDescription = null;
            if(fireEvent)
            {
                setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, struct);
            }
        }
    }

    // key for SortedProductDescriptionModelCollection is Name
    public Object getKey()
    {
        return this.getName();
    }

    // set struct fields
    public void setName(String name)
    {
        if(name == null)
            throw new IllegalArgumentException("ProductDescription name can not be null");
        else if(name.equals(""))
            throw new IllegalArgumentException("ProductDescription name can not be blank");
        else if(!name.equals(this.getName()))
        {
            this.productDescriptionStruct.name = name;
            super.setModified(true);
        }
    }

    public void setBaseDescriptionName(String baseDescriptionName)
    {
        if(baseDescriptionName == null)
            throw new IllegalArgumentException("ProductDescription baseDescriptionName can not be null");
        else if(baseDescriptionName.equals(""))
            throw new IllegalArgumentException("ProductDescription baseDescriptionName can not be blank");
        else if(!baseDescriptionName.equals(this.getName()))
        {
            this.productDescriptionStruct.baseDescriptionName = baseDescriptionName;
            super.setModified(true);
        }
    }

    public void setMinimumStrikePriceFraction(PriceStruct minimumStrikePriceFraction)
    {
        if(minimumStrikePriceFraction == null)
            throw new IllegalArgumentException("ProductDescription minimumStrikePriceFraction can not be null");
        else
        {
            this.productDescriptionStruct.minimumStrikePriceFraction = minimumStrikePriceFraction;
            super.setModified(true);
        }
    }

    public void setMaxStrikePrice(PriceStruct maxStrikePrice)
    {
        if(maxStrikePrice == null)
            throw new IllegalArgumentException("ProductDescription maxStrikePrice can not be null");
        else
        {
            this.productDescriptionStruct.maxStrikePrice = maxStrikePrice;
            super.setModified(true);
        }
    }

    public void setPremiumBreakPoint(PriceStruct premiumBreakPoint)
    {
        if(premiumBreakPoint == null)
            throw new IllegalArgumentException("ProductDescription premiumBreakPoint can not be null");
        else
        {
            this.productDescriptionStruct.premiumBreakPoint = premiumBreakPoint;
            super.setModified(true);
        }
    }

    public void setMinimumAbovePremiumFraction(PriceStruct minimumAbovePremiumFraction)
    {
        if(minimumAbovePremiumFraction == null)
            throw new IllegalArgumentException("ProductDescription minimumAbovePremiumFraction can not be null");
        else
        {
            this.productDescriptionStruct.minimumAbovePremiumFraction = minimumAbovePremiumFraction;
            super.setModified(true);
        }
    }

    public void setMinimumBelowPremiumFraction(PriceStruct minimumBelowPremiumFraction)
    {
        if(minimumBelowPremiumFraction == null)
            throw new IllegalArgumentException("ProductDescription minimumBelowPremiumFraction can not be null");
        else
        {
            this.productDescriptionStruct.minimumBelowPremiumFraction = minimumBelowPremiumFraction;
            super.setModified(true);
        }
    }

    public void setPriceDisplayType(short priceDisplayType)
    {
        if(priceDisplayType != this.getPriceDisplayType())
        {
            this.productDescriptionStruct.priceDisplayType = priceDisplayType;
            super.setModified(true);
        }
    }

    public void setPremiumPriceFormat(short premiumPriceFormat)
    {
        if(premiumPriceFormat != this.getPremiumPriceFormat())
        {
            this.productDescriptionStruct.premiumPriceFormat = premiumPriceFormat;
            super.setModified(true);
        }
    }

    public void setStrikePriceFormat(short strikePriceFormat)
    {
        if(strikePriceFormat != this.getStrikePriceFormat())
        {
            this.productDescriptionStruct.strikePriceFormat = strikePriceFormat;
            super.setModified(true);
        }
    }

    public void setUnderlyingPriceFormat(short underlyingPriceFormat)
    {
        if(underlyingPriceFormat != this.getUnderlyingPriceFormat())
        {
            this.productDescriptionStruct.underlyingPriceFormat = underlyingPriceFormat;
            super.setModified(true);
        }
    }

    public int hashCode()
    {
        return super.hashCode();
//        return this.key;
    }

    public Object clone() throws CloneNotSupportedException
    {
        ProductDescriptionStruct clonedStruct = ProductStructBuilder.cloneProductDescriptionStruct(this.productDescriptionStruct);
        return ProductDescriptionFactory.createProductDescriptionModel(clonedStruct);
    }

    public boolean equals(Object obj)
    {
        return compareTo(obj)==0;
    }

    public int compareTo(Object obj)
    {
        int retVal = -1;

        if ( this == obj )
        {
            retVal = 0;
        }
        else if ( obj instanceof  ProductDescriptionModel )
        {
            retVal = getName().compareTo(((ProductDescriptionModel)obj).getName());
        }

        return retVal;
    }

    /**
     *  Saves any mods made to the business model
     */
    public void saveChanges()
            throws UserException, SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        if (isModified())
        {
            if(GUILoggerHome.find().isDebugOn())
                GUILoggerHome.find().debug("ProductDescriptionImpl.saveChanges() Updating ProductDescription...", GUILoggerBusinessProperty.PRODUCT_DEFINITION, getProductDescriptionStruct());

            ProductDescriptionStruct updatedStruct = SystemAdminAPIFactory.find().updateProductDescription(this.getProductDescriptionStruct());

            if(GUILoggerHome.find().isDebugOn())
                GUILoggerHome.find().debug("ProductDescriptionImpl.saveChanges() ProductDescription saved.  Updated ProductDescriptionStruct:", GUILoggerBusinessProperty.PRODUCT_DEFINITION, updatedStruct);

            this.setProductDescriptionStruct(updatedStruct, false);
            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    /**
     *  Reloads data from the server
     */
    public void refreshData()
            throws UserException
    {
        // don't do anything -- there are no server api methods to get a specific ProductDescription; can only get all descriptions
    }

}

