package com.cboe.interfaces.domain.session;

import com.cboe.idl.cmiUtil.CallbackInformationStruct;

public interface CallbackDeregistrationInfo {

    public CallbackInformationStruct getCallbackInformationStruct();

    public String getReason();

    public int getErrorCode();

}

