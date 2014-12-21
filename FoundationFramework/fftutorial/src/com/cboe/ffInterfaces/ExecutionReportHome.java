package com.cboe.ffInterfaces;

import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import java.util.Collection;

public interface ExecutionReportHome
{
    static final String HOME_NAME="ExecutionReportHome";

    ExecutionReport create();
    ExecutionReport create(ExecutionReportStruct struct);
    ExecutionReport create(String acronym, String prodSym, float price, int vol, char side, TimeStruct sentTime);

    Collection findForAcronym(String acronym) throws PersistenceException;
}
