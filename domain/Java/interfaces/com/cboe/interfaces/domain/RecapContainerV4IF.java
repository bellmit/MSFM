package com.cboe.interfaces.domain;

/**
 * User: beniwalv
 */

public interface RecapContainerV4IF {
    public int getProductKey() ;
    public void setProductKey(int productKey) ;
    public int getClassKey() ;
    public void setClassKey(int classKey) ;
    public String getSessionName() ;
    public void setSessionName(String sessionName) ;
    public char[] getLastSalePrice() ;
    public void setLastSalePrice(char[] lastSalePrice) ;
    public int getLastSaleVolume() ;
    public void setLastSaleVolume(int lastSaleVolume) ;
    public char[] getTradeTime() ;
    public void setTradeTime(char[] tradeTime) ;
    public char getTickDirection() ;
    public void setTickDirection(char tickDirection) ;
    public char[] getBidPrice() ;
    public void setBidPrice(char[] bidPrice) ;
    public int getBidSize() ;
    public void setBidSize(int bidSize) ;
    public char[] getBidTime() ;
    public void setBidTime(char[] bidTime) ;
    public char[] getAskPrice() ;
    public void setAskPrice(char[] askPrice) ;
    public int getAskSize() ;
    public void setAskSize(int askSize) ;
    public char[] getAskTime() ;
    public void setAskTime(char[] askTime) ;
    public char[] getLowPrice() ;
    public void setLowPrice(char[] lowPrice) ;
    public char[] getHighPrice() ;
    public void setHighPrice(char[] highPrice) ;
    public char[] getOpenPrice() ;
    public void setOpenPrice(char[] openPrice) ;
    public char[] getClosePrice() ;
    public void setClosePrice(char[] closePrice) ;
    public char[] getPreviousClosePrice() ;
    public void setPreviousClosePrice(char[] previousClosePrice) ;
    public int getOpenInterest() ;
    public void setOpenInterest(int openInterest) ;
    public String getRecapPrefix() ;
    public void setRecapPrefix(String recapPrefix) ;
    public char[] getStatusCodes() ;
    public void setStatusCodes(char[] statusCodes) ;
}
