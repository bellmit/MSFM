package com.cboe.domain.util;

import com.cboe.interfaces.domain.RecapContainerV4IF;

/**
 * User: beniwalv
 */

public class RecapContainerV4 implements RecapContainerV4IF
{
    private int productKey;
    private int classKey;
    private String sessionName;

    private char[] lastSalePrice;
    private int lastSaleVolume;
    private char[] tradeTime;
    private char tickDirection;

    private char[] bidPrice;
    private int bidSize;
    private char[] bidTime;

    private char[] askPrice;
    private int askSize;
    private char[] askTime;

    private char[] lowPrice;
    private char[] highPrice;
    private char[] openPrice;
    private char[] closePrice;
    private char[] previousClosePrice;

    private int openInterest;
    private String recapPrefix;
    private char[] statusCodes;

    private boolean isRecapMapped;
    private boolean isLastSaleMapped;

    private int recapSentTime;
    private int lastSaleSentTime;

    public static final String EMPTY_STR = "";

    // These are used to compare if Recap is as good as LastSale - so that it can be dispatched.
    private int lastSaleRawPrice;
    private int lowRawPrice;
    private int highRawPrice;

    public RecapContainerV4()
    {
        // Initialization -
        this.productKey = 0;
        this.classKey = 0;
        this.sessionName = EMPTY_STR;

        this.lastSalePrice = new char[8];
        this.lastSaleVolume = 0;
        this.tradeTime = new char[8];
        this.tickDirection = '+';

        this.bidPrice = new char[8];
        this.bidSize = 0;
        this.bidTime = new char[8];

        this.askPrice = new char[8];
        this.askSize = 0;
        this.askTime = new char[8];

        this.lowPrice = new char[8];
        this.highPrice = new char[8];
        this.openPrice = new char[8];
        this.closePrice = new char[8];
        this.previousClosePrice = new char[8];

        // Not supported by V4 - by CBOE
        this.openInterest = -1;
        this.recapPrefix = EMPTY_STR;
        this.statusCodes = new char[8];

        this.isLastSaleMapped = false;
        this.isRecapMapped = false;
    }

    public RecapContainerV4(int productKey, int classKey, String sessionName, char[] lastSalePrice, int lastSaleVolume,
                            char[] tradeTime, char tickDirection, char[] bidPrice, int bidSize, char[] bidTime,
                            char[] askPrice, int askSize, char[] askTime, char[] lowPrice, char[] highPrice, char[] openPrice,
                            char[] closePrice, char[] previousClosePrice, int openInterest, String recapPrefix,
                            char[] statusCodes, boolean isLastSaleMapped, boolean isRecapMapped)
    {
        this.productKey = productKey;
        this.classKey = classKey;
        this.sessionName = sessionName;

        this.lastSalePrice = lastSalePrice;
        this.lastSaleVolume = lastSaleVolume;
        this.tradeTime = tradeTime;
        this.tickDirection = tickDirection;

        this.bidPrice = bidPrice;
        this.bidSize = bidSize;
        this.bidTime = bidTime;

        this.askPrice = askPrice;
        this.askSize = askSize;
        this.askTime = askTime;

        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.previousClosePrice = previousClosePrice;

        this.openInterest = openInterest;
        this.recapPrefix = recapPrefix;
        this.statusCodes = statusCodes;

        this.isLastSaleMapped = isLastSaleMapped;
        this.isRecapMapped = isRecapMapped;
    }

    public int getProductKey() {
        return productKey;
    }

    public void setProductKey(int productKey) {
        this.productKey = productKey;
    }

    public int getClassKey() {
        return classKey;
    }

    public void setClassKey(int classKey) {
        this.classKey = classKey;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public char[] getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(char[] lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public int getLastSaleVolume() {
        return lastSaleVolume;
    }

    public void setLastSaleVolume(int lastSaleVolume) {
        this.lastSaleVolume = lastSaleVolume;
    }

    public char[] getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(char[] tradeTime) {
        this.tradeTime = tradeTime;
    }

    public char getTickDirection() {
        return tickDirection;
    }

    public void setTickDirection(char tickDirection) {
        this.tickDirection = tickDirection;
    }

    public char[] getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(char[] bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public char[] getBidTime() {
        return bidTime;
    }

    public void setBidTime(char[] bidTime) {
        this.bidTime = bidTime;
    }

    public char[] getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(char[] askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskSize() {
        return askSize;
    }

    public void setAskSize(int askSize) {
        this.askSize = askSize;
    }

    public char[] getAskTime() {
        return askTime;
    }

    public void setAskTime(char[] askTime) {
        this.askTime = askTime;
    }

    public char[] getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(char[] lowPrice) {
        this.lowPrice = lowPrice;
    }

    public char[] getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(char[] highPrice) {
        this.highPrice = highPrice;
    }

    public char[] getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(char[] openPrice) {
        this.openPrice = openPrice;
    }

    public char[] getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(char[] closePrice) {
        this.closePrice = closePrice;
    }

    public char[] getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(char[] previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public int getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(int openInterest) {
        this.openInterest = openInterest;
    }

    public String getRecapPrefix() {
        return recapPrefix;
    }

    public void setRecapPrefix(String recapPrefix) {
        this.recapPrefix = recapPrefix;
    }

    public char[] getStatusCodes() {
        return statusCodes;
    }

    public void setStatusCodes(char[] statusCodes) {
        this.statusCodes = statusCodes;
    }

    public boolean isRecapMapped() {
        return isRecapMapped;
    }

    public void setRecapMapped(boolean recapMapped) {
        isRecapMapped = recapMapped;
    }

    public boolean isLastSaleMapped() {
        return isLastSaleMapped;
    }

    public void setLastSaleMapped(boolean lastSaleMapped) {
        isLastSaleMapped = lastSaleMapped;
    }

    public int getLastSaleSentTime() {
        return lastSaleSentTime;
    }

    public void setLastSaleSentTime(int lastSaleSentTime) {
        this.lastSaleSentTime = lastSaleSentTime;
    }

    public int getRecapSentTime() {
        return recapSentTime;
    }

    public void setRecapSentTime(int recapSentTime) {
        this.recapSentTime = recapSentTime;
    }

    public int getLastSaleRawPrice() {
        return lastSaleRawPrice;
    }

    public void setLastSaleRawPrice(int lastSaleRawPrice) {
        this.lastSaleRawPrice = lastSaleRawPrice;
    }

    public int getLowRawPrice() {
        return lowRawPrice;
    }

    public void setLowRawPrice(int lowRawPrice) {
        this.lowRawPrice = lowRawPrice;
    }

    public int getHighRawPrice() {
        return highRawPrice;
    }

    public void setHighRawPrice(int highRawPrice) {
        this.highRawPrice = highRawPrice;
    }
}
