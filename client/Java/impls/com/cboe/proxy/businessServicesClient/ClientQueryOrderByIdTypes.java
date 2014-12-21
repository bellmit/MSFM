package com.cboe.proxy.businessServicesClient;

public enum ClientQueryOrderByIdTypes 
{
    UNKNOWN,
    QUERY_ORDER_BY_ID,
    QUERY_ORDER_BY_ID_V2,
    QUERY_ORDER_BY_ORSID;

    public static ClientQueryOrderByIdTypes findEnum(int p_type)
    {
        ClientQueryOrderByIdTypes result = ClientQueryOrderByIdTypes.UNKNOWN;
        for(ClientQueryOrderByIdTypes type : ClientQueryOrderByIdTypes.values())
        {
            if(p_type == type.ordinal())
            {
                result = type;
                break;
            }
        }
        return result;
    }
        
    public static ClientQueryOrderByIdTypes findEnum(String p_name)
    {
        ClientQueryOrderByIdTypes result = ClientQueryOrderByIdTypes.UNKNOWN;
        if(p_name != null && p_name.length() > 0)
        {
            for(ClientQueryOrderByIdTypes type : ClientQueryOrderByIdTypes.values())
            {
                if(type.name().equals(p_name))
                {
                    result = type;
                    break;
                }
            }
        }
        return result;
    }
}
