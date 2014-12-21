package com.cboe.uuidServiceCommon;

import com.cboe.idl.uuidService.IdService;
import com.cboe.idl.uuidService.IdServiceAdmin;

public interface IdServiceClientWrapper {
    public IdService getIdService();
    public IdServiceAdmin getIdAdminService();
};
