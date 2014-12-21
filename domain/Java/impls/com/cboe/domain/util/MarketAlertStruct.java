package com.cboe.domain.util;
final public class MarketAlertStruct {
    public java.lang.String sessionName;
    public short productType;
    public int classKey;
    public int productKey;
    public char side;
    public com.cboe.idl.cmiUtil.PriceStruct price;
    public int quantity;
    public com.cboe.idl.cmiUtil.DateTimeStruct alertTime;
    public java.lang.String extensions;
    public java.lang.String[] awayExchanges = new java.lang.String[0];
    public short marketAlertType;

    public MarketAlertStruct() {
    }
    public MarketAlertStruct(    // constructor
                java.lang.String sessionName,
                short productType,
                int classKey,
                int productKey,
                char side,
                com.cboe.idl.cmiUtil.PriceStruct price,
                int quantity,
                com.cboe.idl.cmiUtil.DateTimeStruct alertTime,
                java.lang.String extensions,
                java.lang.String[] awayExchanges,
                short marketAlertType)
    {
       this.sessionName = sessionName;
       this.productType = productType;
       this.classKey = classKey;
       this.productKey = productKey;
       this.side = side;
       this.price = price;
       this.quantity = quantity;
       this.alertTime = alertTime;
       this.extensions = extensions;
       this.awayExchanges = awayExchanges;
       this.marketAlertType = marketAlertType;
    }
}
