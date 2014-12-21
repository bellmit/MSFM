package com.cboe.interfaces.application.subscription;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.interfaces.domain.ExchangeFirmStructWrapper;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;

public interface SubscriptionCollectionService
{
    public UserSubscriptionCollection getUserSubscriptionCollection(SessionProfileUserStructV2 userStruct);
    public SessionClassSubscriptionCollection getSessionClassSubscriptionCollection(SessionKeyWrapper sessionClass);
    public FirmSubscriptionCollection getFirmSubscriptionCollection(ExchangeFirmStructWrapper exchangeFirm);
    public FirmSubscriptionCollection getTradingFirmSubscriptionCollection(TradingFirmGroupWrapper tradingFirm);
    public ProductTypeSubscriptionCollection getProductTypeSubscriptionCollection(short productType);
    public void setDefaultSubscriptonFlag(boolean defaultSubscriptionOn);
}
