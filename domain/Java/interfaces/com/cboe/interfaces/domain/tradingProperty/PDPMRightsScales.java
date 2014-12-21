package com.cboe.interfaces.domain.tradingProperty;

/**
 * Created by IntelliJ IDEA.
 * User: saborm
 * Date: May 19, 2005
 * Time: 1:59:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PDPMRightsScales extends TradingProperty {
    int getLowNbrParticipants();
    void setLowNbrParticipants(int lowNbrParticipants);
    int getHighNbrParticipants();
    void setHighNbrParticipants(int highNbrParticipants);
    double getScalePercentage();
    void setScalePercentage(double scalePercentage);
}
