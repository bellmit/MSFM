package com.cboe.domain.util;

import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

public class IntermarketAdminMessageContainer
{
    private ProductKeysStruct productKeys;
    private AdminStruct adminMessage;
    private String  srcExchange;
    private String session;

    public IntermarketAdminMessageContainer(ProductKeysStruct productKeysStruct, AdminStruct adminStruct,  String sourceExchange, String sessionName )
    {
        this.productKeys = productKeysStruct;
        this.adminMessage = adminStruct;
        this.srcExchange = sourceExchange;
        this.session = sessionName;

    }
    public ProductKeysStruct getProductKeysStruct()
    {
        return productKeys;
    }

    public AdminStruct getAdminStruct()
    {
        return adminMessage;
    }

    public String getSourceExchange()
    {
        return srcExchange;
    }

    public String getSessionName()
    {
        return session;
    }
}
