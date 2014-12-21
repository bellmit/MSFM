package com.cboe.internalPresentation.product;

import java.util.Comparator;

import org.omg.CORBA.UserException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.PriceFactory;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.PriceAdjustmentTypes;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustment;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustmentModel;
import com.cboe.interfaces.internalPresentation.product.PriceAdjustmentClassModel;
import com.cboe.internalPresentation.product.PriceAdjustmentClassModelFactory;
import com.cboe.internalPresentation.product.PriceAdjustmentFactory;
import com.cboe.interfaces.presentation.validation.ValidationResult;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.product.PriceAdjustmentStruct;
import com.cboe.idl.product.PriceAdjustmentClassStruct;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Chicago Board Options Exchange
 * @author Joel Davisson
 * @version 1.0
 */

public class PriceAdjustmentModelImpl extends AbstractMutableBusinessModel implements PriceAdjustmentModel
{
    PriceAdjustmentClassModel[] adjustedClasses = null;
    private PriceAdjustmentStruct priceAdjustmentStruct = null;
    private Price cashDividend;
    private Price stockDividend;
    private Price persentDividend;
    private Price lowRange;
    private Price highRange;

    public PriceAdjustmentModelImpl(PriceAdjustmentStruct priceAdjustmentStruct)
    {
        super();
        // don't fire an event
        setPriceAdjustmentStruct(priceAdjustmentStruct, false);
    }

    public PriceAdjustmentModelImpl(PriceAdjustmentStruct priceAdjustmentStruct, Comparator comparator)
    {
        super(comparator);
        // don't fire an event
        setPriceAdjustmentStruct(priceAdjustmentStruct, false);
    }

    /**
     * this will have to be changed if more adjustment types are implemented
     */
    public String getDetailString()
    {
        short type = this.getType();
        StringBuffer buffer = new StringBuffer(PriceAdjustmentTypes.toString(type));
        switch(type)
        {
            case PriceAdjustmentTypes.SPLIT:
                buffer.append(": ");
                buffer.append(this.getSplitNumerator()).append(" for ").append(this.getSplitDenominator());
                break;
            // SYMBOL_CHANGE and MERGER only change the product symbol
            case PriceAdjustmentTypes.SYMBOL_CHANGE:
            case PriceAdjustmentTypes.MERGER:
                buffer.append(": ");
                String symbol = getNewProductSymbol();
                if (symbol!=null && symbol.length() > 0)
                {
                    buffer.append("New Symbol: ").append(symbol);
                }
                else
                {
                    buffer.append("No Symbol Change.");
                }
                break;
            case PriceAdjustmentTypes.DIVIDEND_CASH:
                buffer.append(": ");
                buffer.append('$').append(this.getCashDividend().toString());
                break;
            case PriceAdjustmentTypes.DIVIDEND_STOCK:
                buffer.append(": ");
                buffer.append(this.getStockDividend().toString()).append(" shares.");
                break;
            case PriceAdjustmentTypes.DIVIDEND_PERCENT:
                buffer.append(": ");
                buffer.append(this.getStockDividend().toString()).append('%');
                break;
            case PriceAdjustmentTypes.COMMON_DISTRIBUTION:
                break;
            case PriceAdjustmentTypes.LEAP_ROLLOVER:
                break;
            default:
                break;
        }
        return buffer.toString();
    }

    // hide default contructor
    private PriceAdjustmentModelImpl()
    {
    }

    public void setPriceAdjustmentStruct(PriceAdjustmentStruct priceAdjustmentStruct, boolean fireEvent)
    {
        if(priceAdjustmentStruct == null)
            throw new IllegalArgumentException("PriceAdjustmentStruct can not be null");
        else
        {
            PriceAdjustmentStruct oldValue = this.getPriceAdjustmentStruct();
            this.priceAdjustmentStruct = priceAdjustmentStruct;
            GUILoggerHome.find().debug("PriceAdjustmentModelImpl.setPriceAdjustmentStruct()",GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, this.priceAdjustmentStruct);
            if(fireEvent)
            {
                this.setModified(true);
                firePropertyChange(DATA_CHANGE_EVENT, oldValue, priceAdjustmentStruct);
            }
            // wipe out the previous local array of adjustedClasses, so the next time
            //    getAdjustedClasses() is called, the local array will be regenerated
            //    based on the updated struct's adjustedClasses
            this.adjustedClasses = null;
        }
    }

    //implement PriceAdjustmentModel interface
    public PriceAdjustment getPriceAdjustment()
    {
        return (PriceAdjustment)this;
    }

    public PriceAdjustmentStruct getPriceAdjustmentStruct()
    {
            return this.priceAdjustmentStruct;
    }

    public int getAdjustmentNumber()
    {
        return this.getPriceAdjustmentStruct().adjustmentNumber;
    }
    public void setAdjustmentNumber(int adjustmentNumber)
    {
        if(adjustmentNumber != this.getAdjustmentNumber())
        {
            Integer oldValue = new Integer(this.getAdjustmentNumber());
            this.getPriceAdjustmentStruct().adjustmentNumber = adjustmentNumber;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Integer(adjustmentNumber));
        }
    }

    public short getType()
    {
        return this.getPriceAdjustmentStruct().type;
    }

    public short getOrderAction()
    {
        return this.getPriceAdjustmentStruct().orderAction;
    }

    public void setType(short adjustmentType)
    {
        if(this.getType() != adjustmentType)
        {
            Short oldValue = new Short(this.getType());
            this.getPriceAdjustmentStruct().type = adjustmentType;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Short(adjustmentType));
        }
    }

    public void setOrderAction(short orderAction)
    {
        if(this.getOrderAction() != orderAction)
        {
            Short oldValue = new Short(this.getOrderAction());
            this.getPriceAdjustmentStruct().orderAction = orderAction;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Short(orderAction));
        }
    }

    public int getProductKey()
    {
        return this.getPriceAdjustmentStruct().productKey;
    }
    public void setProductKey(int productKey)
    {
        if(this.getProductKey() != productKey)
        {
            Integer oldValue = new Integer(this.getProductKey());
            this.getPriceAdjustmentStruct().productKey = productKey;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Integer(productKey));
        }
    }

    public String getProductSymbol()
    {
        return this.getPriceAdjustmentStruct().productSymbol;
    }
    public void setProductSymbol(String productSymbol)
    {
        if(productSymbol == null)
            throw new IllegalArgumentException("Product Symbol can not be null");
        else if (!this.getProductSymbol().equals(productSymbol))
        {
            String oldValue = this.getProductSymbol();
            this.getPriceAdjustmentStruct().productSymbol = productSymbol;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, productSymbol);
        }
    }

    public String getNewProductSymbol()
    {
        return this.getPriceAdjustmentStruct().newProductSymbol;
    }
    public void setNewProductSymbol(String newProductSymbol)
    {
        if(newProductSymbol == null)
            throw new IllegalArgumentException("New Product Symbol can not be null");
        else if(!this.getNewProductSymbol().equals(newProductSymbol))
        {
            String oldValue = this.getNewProductSymbol();
            this.getPriceAdjustmentStruct().newProductSymbol = newProductSymbol;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, newProductSymbol);
        }
    }

    public DateStruct getEffectiveDate()
    {
        return this.getPriceAdjustmentStruct().effectiveDate;
    }
    public void setEffectiveDate(DateStruct effectiveDate)
    {
        if(effectiveDate == null)
            throw new IllegalArgumentException("Effective Date can not be null");
        else if(!isEqualDate(this.getEffectiveDate(), effectiveDate))
        {
            DateStruct oldValue = this.getEffectiveDate();
            this.getPriceAdjustmentStruct().effectiveDate = effectiveDate;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, effectiveDate);
        }
    }

    public DateStruct getRunDate()
    {
        return this.getPriceAdjustmentStruct().runDate;
    }
    public void setRunDate(DateStruct runDate)
    {
        if(runDate == null)
            throw new IllegalArgumentException("Run Date can not be null");
        else if(!isEqualDate(this.getRunDate(), runDate))
        {
            DateStruct oldValue = this.getRunDate();
            this.getPriceAdjustmentStruct().runDate = runDate;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, runDate);
        }
    }

    public short getSplitNumerator()
    {
        return this.getPriceAdjustmentStruct().splitNumerator;
    }
    public void setSplitNumerator(short splitNumerator)
    {
        if(this.getSplitNumerator() != splitNumerator)
        {
            Short oldValue = new Short(this.getSplitNumerator());
            this.getPriceAdjustmentStruct().splitNumerator = splitNumerator;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Short(splitNumerator));
        }
    }

    public short getSplitDenominator()
    {
        return this.getPriceAdjustmentStruct().splitDenominator;
    }
    public void setSplitDenominator(short splitDenominator)
    {
        if(this.getSplitDenominator() != splitDenominator)
        {
            Short oldValue = new Short(this.getSplitDenominator());
            this.getPriceAdjustmentStruct().splitDenominator = splitDenominator;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, new Short(splitDenominator));
        }
    }

    public Price getCashDividend()
    {
        if(cashDividend == null)
        {
            cashDividend = DisplayPriceFactory.create(this.getPriceAdjustmentStruct().cashDividend);
        }
        return cashDividend;
    }

    public void setCashDividend(Price newValue)
    {
        if(newValue == null)
        {
            throw new IllegalArgumentException("Cash Dividend can not be null");
        }
        else if(!this.getCashDividend().equals(newValue))
        {
            Price oldValue = this.getCashDividend();
            this.getPriceAdjustmentStruct().cashDividend = newValue.toStruct();
            this.cashDividend = null;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, getCashDividend());
        }
    }

    public Price getStockDividend()
    {
        if(stockDividend == null)
        {
            stockDividend = DisplayPriceFactory.create(this.getPriceAdjustmentStruct().stockDividend);
        }
        return stockDividend;
    }
    public void setStockDividend(Price newValue)
    {
        if(newValue == null)
            throw new IllegalArgumentException("Stock Dividend can not be null");
        else if(!this.getStockDividend().equals(newValue))
        {
            Price oldValue = this.getStockDividend();
            this.getPriceAdjustmentStruct().stockDividend = newValue.toStruct();
            this.stockDividend = null;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, getStockDividend());
        }
    }

    public void setSource(short priceAdjustmentSource)
    {
        short oldSource = getPriceAdjustmentStruct().source;
        getPriceAdjustmentStruct().source = priceAdjustmentSource;
        setModified(true);
        firePropertyChange(DATA_CHANGE_EVENT, oldSource, priceAdjustmentSource);
    }

    public Price getLowRange()
    {
        if(lowRange == null)
        {
            lowRange = DisplayPriceFactory.create(this.getPriceAdjustmentStruct().lowRange);
        }
        return lowRange;
    }
    public void setLowRange(Price newValue)
    {
        if (newValue == null)
        {
            throw new IllegalArgumentException("Low Range can not be null");
        }
        else if(!this.getLowRange().equals(newValue))
        {
            Price oldValue = getLowRange();
            getPriceAdjustmentStruct().lowRange = newValue.toStruct();
            this.lowRange = null;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, getLowRange());
        }
    }

    public Price getHighRange()
    {
        if(highRange == null)
        {
            highRange = DisplayPriceFactory.create(this.getPriceAdjustmentStruct().highRange);
        }
        return highRange;
    }
    public void setHighRange(Price newValue)
    {
        if (newValue == null)
        {
            throw new IllegalArgumentException("High Range can not be null");
        }
        else if(!this.getHighRange().equals(newValue))
        {
            Price oldValue = getHighRange();
            getPriceAdjustmentStruct().highRange = newValue.toStruct();
            this.highRange = null;
            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, getHighRange());
        }
    }

    public PriceAdjustmentClassModel[] getAdjustedClasses()
    {
        if(this.adjustedClasses == null)
        {
            PriceAdjustmentClassStruct[] adjustedClassStructs = this.getPriceAdjustmentStruct().adjustedClasses;
            this.adjustedClasses = new PriceAdjustmentClassModel[adjustedClassStructs.length];
            for(int i=0; i<adjustedClassStructs.length; i++)
            {
                this.adjustedClasses[i] = PriceAdjustmentClassModelFactory.create(adjustedClassStructs[i]);
            }
        }
        return this.adjustedClasses;
    }

    public void setAdjustedClasses(PriceAdjustmentClassModel[] adjustedClassModels)
    {
        if(adjustedClassModels == null)
        {
            throw new IllegalArgumentException("AdjustedClasses array cannot be null");
        }
        else
        {
            PriceAdjustmentClassStruct[] oldStructs = this.getPriceAdjustmentStruct().adjustedClasses;

            PriceAdjustmentClassStruct[] newStructs = new PriceAdjustmentClassStruct[adjustedClassModels.length];
            for(int i=0; i<adjustedClassModels.length; i++)
            {
                newStructs[i] = adjustedClassModels[i].getPriceAdjustmentClassStruct();
            }
            this.getPriceAdjustmentStruct().adjustedClasses = newStructs;

            // setting this local array of PriceAdjustmentClassModels to null will cause it to be recreated from
            //   PriceAdjustmentStruct.adjustedClasses the next time getAdjustedClasses() is called
            this.adjustedClasses = null;

            setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldStructs, newStructs);
//            firePropertyChange(DATA_CHANGE_EVENT, null, newStructs);
        }
    }

    public DateTimeStruct getCreatedTime()
    {
        return this.getPriceAdjustmentStruct().createdTime;
    }
    public void setCreatedTime(DateTimeStruct createdTime)
    {
        if(createdTime == null)
            throw new IllegalArgumentException("Created Time can not be null");
        else if(!isEqualDateTime(this.getCreatedTime(), createdTime))
        {
            DateTimeStruct oldValue = this.getCreatedTime();
            this.getPriceAdjustmentStruct().createdTime = createdTime;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, createdTime);
        }
    }

    public DateTimeStruct getLastModifiedTime()
    {
        return this.getPriceAdjustmentStruct().lastModifiedTime;
    }
    public void setLastModifiedTime(DateTimeStruct lastModifiedTime)
    {
        if(lastModifiedTime == null)
            throw new IllegalArgumentException("Last Modified Time can not be null");
        else if(!isEqualDateTime(this.getLastModifiedTime(), lastModifiedTime))
        {
            DateTimeStruct oldValue = this.getLastModifiedTime();
            this.getPriceAdjustmentStruct().lastModifiedTime = lastModifiedTime;
            this.setModified(true);
            firePropertyChange(DATA_CHANGE_EVENT, oldValue, lastModifiedTime);
        }
    }

    public ValidationResult validateData()
    {
        return null;
    }

    public void deleteModel()
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
            GUILoggerHome.find().debug("Deleting PriceAdjustment",GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, this.getPriceAdjustmentStruct());

        SystemAdminAPIFactory.find().removePriceAdjustment(this.getAdjustmentNumber());

        this.firePropertyChange(DELETED_EVENT, this, null);
    }

    public void saveChanges() throws UserException
    {
        if(this.isModified())
        {
            PriceAdjustmentStruct newPriceAdjustmentStruct = null;

            if(GUILoggerHome.find().isDebugOn())
                GUILoggerHome.find().debug("PriceAdjustmentModelImpl.saveChanges() BEFORE server update: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, this.getPriceAdjustmentStruct());

            newPriceAdjustmentStruct = SystemAdminAPIFactory.find().updatePriceAdjustment(this.getPriceAdjustmentStruct());

            if(GUILoggerHome.find().isDebugOn())
                GUILoggerHome.find().debug("PriceAdjustmentModelImpl.saveChanges() AFTER server update: ", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, newPriceAdjustmentStruct);

            this.setPriceAdjustmentStruct(newPriceAdjustmentStruct, false);

            this.setModified(false);
            firePropertyChange(SAVED_EVENT,null,this);
        }
    }

    public void refreshData() throws UserException
    {
            int classKey = SystemAdminAPIFactory.find().getProductByKey(getProductKey()).getProductKeysStruct().classKey;
            PriceAdjustmentStruct updatedStruct = SystemAdminAPIFactory.find().getPriceAdjustment(classKey, true);

            // don't fire DATA_CHANGED_EVENT
            this.setPriceAdjustmentStruct(updatedStruct, true);

            // can't send old PriceAdjustmentStruct as oldValue -- because hashCode would be the same as
            //      updatedStruct's, so PropertyChange wouldn't be fired
            firePropertyChange(RELOADED_EVENT, null, this);
    }

    /**
     * Clones this PriceAdjustment by returning another instance that represents a
     * PriceAdjustmentStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        PriceAdjustmentModel dest = null;
        if (getPriceAdjustmentStruct() != null)
        {
            PriceAdjustmentStruct clonedStruct = ProductStructBuilder.clonePriceAdjustmentStruct(getPriceAdjustmentStruct());
            dest = PriceAdjustmentFactory.createPriceAdjustmentModel(clonedStruct);
        }
        return dest;
    }

    /**
     * productKey has to be used as unique identifier for PriceAdjustments, even though
     *    adjustmentNumber is also always unique.  When adding/updating, the server will
     *    look for existing adjustments by productKey, not adjustmentNumber
     */
    public int hashCode()
    {
//        return this.getAdjustmentNumber();
        return this.getProductKey();
    }

    public boolean equals(Object key)
    {
        return this.hashCode() == key.hashCode();
    }

    // convenience methods - stole them from ProductClassModelImpl
    // ***  move them into a util class ***
    //
    private boolean isEqualDate(DateStruct d1, DateStruct d2)
    {
        boolean isEqual = false;
        if ( d1.year == d2.year && d1.month == d2.month && d1.day == d2.day )
        {
            isEqual = true;
        }
        return isEqual;
    }
    private boolean isEqualTime(TimeStruct t1, TimeStruct t2)
    {
        boolean isEqual = false;
        if ( t1.hour == t2.hour && t1.minute == t2.minute && t1.second == t2.second && t1.fraction == t2.fraction)
        {
            isEqual = true;
        }
        return isEqual;
    }
    private boolean isEqualDateTime(DateTimeStruct dt1, DateTimeStruct dt2)
    {
        boolean isEqual = false;
        if ( isEqualDate(dt1.date, dt2.date) )
        {
            isEqual = isEqualTime(dt1.time, dt2.time);
        }
        return isEqual;
    }
}
