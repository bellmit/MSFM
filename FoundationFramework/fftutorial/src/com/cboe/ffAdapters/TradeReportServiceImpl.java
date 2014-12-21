package com.cboe.externalIntegrationServices.pdsAdapter;

// java classes
import java.io.*;
import java.util.*;

// cboe classes
import com.cboe.ffidl.ffExceptions.SystemException;
import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffInterfaces.*;
import com.cboe.ffUtil.*;
import com.cboe.connectionServer.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.util.ReflectiveObjectWriter;

public class TradeReportServiceImpl
    extends com.cboe.connectionServer.DefaultService
    implements TradeReportConsumer
{
   /**
    * Name used for logging purposes.
    */
    private static final String myClassName = "PDSMarketDataUpdateImpl";

    private OutputStream outStream;
    private OutputStreamWriter outWriter;

    /**
     * Return an instance of this class. Note that
     * different calls to this method may return a reference to the same
     * service instance.
     *
     * @return Service, an instance of this serice class.
     */
    public com.cboe.connectionServer.Service getInstance()
    {
        return this;
    }

    public void acceptTradeReport(String sessionCode, TradeReportStruct report)
    {
        Log.debug("Accepting trade report");
        try
        {
            writeReport(report);
            Log.debug("Wrote trade report");
        }
        catch (IOException ex)
        {
            Log.exception("Failed to write report", ex);
        }
    }

    protected void writeReport(TradeReportStruct report)
        throws IOException
    {
        TradeReportHelper.writeReport(outWriter, report);
    }

    /**
     * Called when the process is becoming master.
     * @param boolean true if we are failing over.
     */
    public synchronized void goMaster(boolean failingOver)
    {
        internalStart();
    }
    /**
     * Called when the process is going in slave mode.
     * Try and be ready i.e. get event publishers and other needed services.
     */
    public synchronized void goSlave()
    {
        internalStart();
    }

    public void initialize(String aConfigPrefix, LogService aLogService) throws com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException
    {
        super.initialize(aConfigPrefix, aLogService);
    }

    /**
     * Get event publishers and get hold of other services.
     */
    private void internalStart()
    {
        BOHome home;
        try
        {
            home = HomeFactory.getInstance().findHome(TradeReportConsumerHome.HOME_NAME);
            ((TradeReportConsumerHome)home).addConsumer(this);
            Log.debug("Registered adapter as consumer of TradeReport events.");
        }
        catch (CBOELoggableException ex)
        {
            Log.exception("Failed to find " + TradeReportConsumerHome.HOME_NAME, ex);
        }
        catch (SystemException ex)
        {
            Log.exception("Failed to register as event consumer", ex);
        }
    }

    /**
     * Read messages, format them and disseminate tickers and recaps.
     * Also send product state events out.
     *
     * @param in java.io.InputStream
     * @param out java.io.OutputStream
     * @param connectionName java.lang.String
     */
    public void serve(java.io.InputStream in, java.io.OutputStream out, String connectionName)
        throws IOException
    {
        // Get publishers and other needed services.
        internalStart();

        outStream = out;
        outWriter = new OutputStreamWriter(outStream);
        Log.debug("outStream and outWriter has been set.");
        Log.debug("Sleeping the serve thread, we never expect anything.  Write-only.");
        try
        {
            while (true)
            {
                Thread.currentThread().sleep(Integer.MAX_VALUE);
            }
        }
        catch (InterruptedException ex)
        {
        }
        Log.debug("Exiting serve() method");
    }

    /**
     * For compilation sake. Not used here.
     */
    public void start()
    {
    }
    /**
     * Ignored but needed for compile.
     */
    public void stop()
    {
    }

    public String toString(String prefix)
    {
        return prefix + "[TradeReportServiceImpl: nothing to report.]";
    }
}
