/*
 * Created by IntelliJ IDEA.
 * User: depasqun
 * Date: Apr 17, 2002
 * Time: 11:40:14 AM
 */
package com.cboe.interfaces.presentation.quote;

import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;


public interface QuoteCancelReport extends BusinessModel
{
    public int getQuoteKey();

    public ProductKeysStruct getProductKeysStruct();
    public ProductNameStruct getProductNameStruct();
    public short getCancelReason();
    public short getStatusChange();

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public QuoteCancelReportStruct getQuoteCancelReportStruct();
}
