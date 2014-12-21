package com.cboe.interfaces.domain.tradingProperty;

/**
 * Created by IntelliJ IDEA.
 * User: saborm
 * Date: May 19, 2005
 * Time: 1:59:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RangeScale extends TradingProperty {
    int getLowerRange();
    void setLowerRange(int lowRange);
    int getUpperRange();
    void setUpperRange(int highRange);
    double getPercentage();
    void setPercentage(double percentage);
}
