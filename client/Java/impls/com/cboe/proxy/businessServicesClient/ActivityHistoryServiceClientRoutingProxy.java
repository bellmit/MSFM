package com.cboe.proxy.businessServicesClient;


import java.util.concurrent.atomic.AtomicInteger;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.internalBusinessServices.ActivityHistoryService;
import com.cboe.idl.terminalActivity.HistoryRequestStruct;
import com.cboe.idl.terminalActivity.HistoryResultStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;



public class ActivityHistoryServiceClientRoutingProxy extends NonGlobalServiceClientRoutingProxy
        implements com.cboe.interfaces.internalBusinessServices.ActivityHistoryService
{
    private static AtomicInteger currentProcess = new AtomicInteger(0);
    private static AtomicInteger previousProcess = new AtomicInteger(0);
    private int size;
   
    /**
     * Default constructor
     */

    public ActivityHistoryServiceClientRoutingProxy()
    {
    }

    /**
     * Default create method
     */
    public void create(String name)
    {
        super.create(name);

    }
   
    public void shutdown()
    {
    }
    
    @Override
    protected String getHelperClassName()
    {
        // TODO Auto-generated method stub
        return "com.cboe.idl.internalBusinessServices.ActivityHistoryServiceHelper";
    }
    
    public HistoryResultStruct queryActivityHistory(
            HistoryRequestStruct historyRequestStruct,
            String queryID,
            DateStruct date,
            int relativeDay,
            long startTime,
            short direction)
    throws SystemException,CommunicationException,AuthorizationException,DataValidationException
    {
        HistoryResultStruct historyResultStruct = null;
        ActivityHistoryService service = (ActivityHistoryService)getService(checkServicesAvailability());
        if (service != null)
        {
            try
            {
                Log.information(this, " calling AHS for queryActivityHistory on "+currentProcess.intValue());
                historyResultStruct = service.queryActivityHistory(historyRequestStruct, queryID, date, relativeDay, startTime, direction);
                Log.information(this," returning from AHS for queryActivityHistory on "+currentProcess.intValue()); 
            }catch (SystemException e)
            {
                Log.exception(this," fail to queryActivityHistory from Activity History Service",e);
                throw e;

            }catch (CommunicationException e)
            {
                Log.exception(this," fail to queryActivityHistory from Activity History Service",e);
                throw e;
            
            }catch (AuthorizationException e)
            {
                Log.exception(this," fail to queryActivityHistory from Activity History Service",e);
                throw e;
            
            }catch (DataValidationException e)
            {
                Log.exception(this," fail to queryActivityHistory from Activity History Service",e);
                throw e;
            }
            catch (Exception e)
            {
                Log.exception(this," fail to queryActivityHistory from Activity History Service",e);
                throw ExceptionBuilder.systemException("Fatal exception during queryActivityHistory query", 0);
            }
        }else
        {
            Log.debug(this,"Activity History Service is null");
        }
        return historyResultStruct;
    }  
    
    
    
    public HistoryResultStruct queryActivityTradeHistory(
           HistoryRequestStruct historyRequestStruct,
           String queryID,
           DateStruct date,
           int relativeDay,
           long startTime,
           short direction)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        HistoryResultStruct historyResultStruct = null;
        ActivityHistoryService service = (ActivityHistoryService)getService(checkServicesAvailability());
        if (service != null)
        {
            try
            {
                Log.information(this," calling AHS for queryActivityTradeHistory on "+currentProcess.intValue());
                historyResultStruct = service.queryActivityTradeHistory(historyRequestStruct, queryID, date, relativeDay, startTime, direction);
                previousProcess = currentProcess;
                Log.information(this," returning from AHS for queryActivityTradeHistory on "+currentProcess.intValue()); 
                   
            }catch (SystemException e)
            {
                Log.exception(this," fail to queryActivityTradeHistory from Activity History Service",e);
                throw e;

            }catch (CommunicationException e)
            {
                Log.exception(this," fail to queryActivityTradeHistory from Activity History Service",e);
                throw e;
            
            }catch (AuthorizationException e)
            {
                Log.exception(this," fail to queryActivityTradeHistory from Activity History Service",e);
                throw e;
            
            }catch (DataValidationException e)
            {
                Log.exception(this," fail to queryActivityTradeHistory from Activity History Service",e);
                throw e;
            }
            catch (Exception e)
            {
                Log.exception(this," fail to queryActivityTradeHistory from Activity History Service",e);
                throw ExceptionBuilder.systemException("Fatal exception during queryActivityTradeHistory query", 0);
            }
        }else
        {
            Log.debug(this," can't access Activity History Service");
        }
       
        return historyResultStruct;
    }
    
    private int checkServicesAvailability()
    {
        
        size = ahsRouteMap.size();    
        if (size==0){return 0;}
        
        // First round
        if ((currentProcess.get()==0)&&(previousProcess.get()==0))
        {
            currentProcess.set(0); // the first service in the map
            previousProcess.set(size-1); // the last service in the map 
          
        }else 
        {
            previousProcess.set(currentProcess.get());
            if (currentProcess.get() < (size-1)) // get the next service in the map
            {
                currentProcess.getAndIncrement();
            }else
            {
                currentProcess.set(0);
            }
        }   
        return currentProcess.intValue();
    }

}
