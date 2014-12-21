package com.cboe.ffDomain;

import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.ffInterfaces.ExecutionReport;
import com.cboe.ffInterfaces.ExecutionReportHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import java.util.Collection;

public class ExecutionReportHomeImpl
    extends BOHome
    implements ExecutionReportHome
{
    protected ExecutionReportImpl createImpl()
    {
        ExecutionReportImpl newReport = new ExecutionReportImpl();
        addToContainer(newReport);
        return newReport;
    }

    public ExecutionReport create()
    {
        return createImpl();
    }

    public ExecutionReport create(ExecutionReportStruct struct)
    {
        return create(struct.user, struct.symbol, struct.price, struct.quantity, struct.side, struct.sentTime);
    }

    public ExecutionReport create(String acronym, String prodSym, float price, int vol, char side, TimeStruct sentTime)
    {
        ExecutionReport newReport = createImpl();
        newReport.setAcronym(acronym);
        newReport.setProductSymbol(prodSym);
        newReport.setPrice(price);
        newReport.setVolume(vol);
        newReport.setSide(side);
        newReport.setSentTime(sentTime);
        return newReport;
    }

    public Collection findForAcronym(String acronym) throws PersistenceException
    {
        ExecutionReportImpl queryExample = createImpl();
        ObjectQuery query = new ObjectQuery(queryExample);
        queryExample.setAcronym(acronym);
        return query.find();
    }
}
