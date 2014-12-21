package com.cboe.ffInterfaces;

import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffUtil.TimeStruct;

public interface ExecutionReport
{
    String getAcronym();
    String getProductSymbol();
    float getPrice();
    int getVolume();
    char getSide();
    TimeStruct getSentTime();

    void setAcronym(String aValue);
    void setProductSymbol(String aValue);
    void setPrice(float aValue);
    void setVolume(int aValue);
    void setSide(char aValue);
    void setSentTime(TimeStruct aValue);

    ExecutionReportStruct toStruct();
}
