/*
 * Created by IntelliJ IDEA.
 * User: BRAZHNI
 * Date: Feb 4, 2002
 * Time: 3:47:40 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.TransactionFeeCode;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.idl.product.TransactionFeeCodeStruct;
import com.cboe.domain.util.ProductStructBuilder;

public class TransactionFeeCodeImpl extends AbstractBusinessModel implements TransactionFeeCode
{
    private TransactionFeeCodeStruct feeCodeStruct;

    public TransactionFeeCodeImpl()
    {
        super();
    }

    public TransactionFeeCodeImpl(TransactionFeeCodeStruct struct)
    {
        this();
        setTransactionFeeCodeStruct(struct);
    }

    private void setTransactionFeeCodeStruct(TransactionFeeCodeStruct feeCodeStruct)
    {
        this.feeCodeStruct = feeCodeStruct;
    }

    public TransactionFeeCodeStruct getTransactionFeeCodeStruct()
    {
        return feeCodeStruct;
    }

    public String getDescription()
    {
        return getTransactionFeeCodeStruct().description;
    }

    public int hashCode()
    {
        return getTransactionFeeCode().hashCode();
    }

    public String getTransactionFeeCode()
    {
        return getTransactionFeeCodeStruct().transactionFeeCode;
    }

    public Object clone() throws CloneNotSupportedException
    {
        TransactionFeeCodeImpl clonedObject = (TransactionFeeCodeImpl)super.clone();
        if (getTransactionFeeCodeStruct() != null)
        {
            clonedObject.feeCodeStruct = ProductStructBuilder.cloneTransactionFeeCodeStruct(getTransactionFeeCodeStruct());
        }
        return clonedObject;
    }

    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if (this == obj)
        {
            retVal = true;
        }
        else if ( obj != null && obj instanceof TransactionFeeCode )
        {
            TransactionFeeCode otherFeeCode = (TransactionFeeCode)obj;
            retVal = this.getTransactionFeeCode().equals(otherFeeCode.getTransactionFeeCode());
        }
        return retVal;
    }

    public String toString()
    {
        return getDescription();
    }
}
