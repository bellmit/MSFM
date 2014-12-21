package com.cboe.domain.util;

import com.cboe.idl.cmiIntermarketMessages.AdminStruct;

public class IntermarketBroadcastMessageContainer
{

    private AdminStruct adminMessage;
    private String  srcExchange;
    private String session;

    public IntermarketBroadcastMessageContainer(AdminStruct adminStruct,  String sourceExchange, String sessionName )
    {
        this.adminMessage = adminStruct;
        this.srcExchange = sourceExchange;
        this.session = sessionName;
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
