package com.cboe.internalPresentation.product;

import com.cboe.idl.product.TransactionFeeCodeStruct;
import com.cboe.interfaces.internalPresentation.product.TransactionFeeCode;
import com.cboe.domain.util.ProductStructBuilder;

public class TransactionFeeCodeFactory
{
    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private TransactionFeeCodeFactory()
    {}

    /**
     * Creates an instance of a TransactionFeeCodeImpl from a passed in TransactionFeeCodeStruct.
     * @param TransactionFeeCodeStruct to wrap in instance of TransactionFeeCodeImpl
     * @return TransactionFeeCode to represent the TransactionFeeCodeStruct
     */
    public static TransactionFeeCode create(TransactionFeeCodeStruct struct)
    {
        if (struct == null)
        {
            throw new IllegalArgumentException("Passed in struct can not be null.");
        }
        TransactionFeeCode feeCode = new TransactionFeeCodeImpl(struct);

        return feeCode;
    }

    /**
     * Creates an instance of a default TransactionFeeCodeImpl.
     * Default Transaction Fee Code ia an empty String.
     * @param TransactionFeeCodeStruct to wrap in instance of TransactionFeeCodeImpl
     * @return TransactionFeeCode to represent the TransactionFeeCodeStruct
     */
    public static TransactionFeeCode createDefaultFeeCode()
    {
        TransactionFeeCodeStruct struct = ProductStructBuilder.buildTransactionFeeCodeStruct();
        struct.transactionFeeCode = "";
        struct.description = "NONE";
        TransactionFeeCode feeCode = create(struct);
        return feeCode;
    }

}
