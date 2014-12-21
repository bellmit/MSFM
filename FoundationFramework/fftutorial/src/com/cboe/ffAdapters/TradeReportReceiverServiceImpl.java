package com.cboe.externalIntegrationServices.pdsAdapter;

// java classes
import java.io.*;
import java.text.ParseException;
import java.util.*;

// cboe classes
import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffUtil.*;
import com.cboe.connectionServer.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.util.ReflectiveObjectWriter;

public class TradeReportReceiverServiceImpl
    extends com.cboe.connectionServer.DefaultService
{
   /**
    * Name used for logging purposes.
    */
    private static final String myClassName = "TradeReportReceiverServiceImpl";

    private InputStream inStream;
    private InputStreamReader inReader;

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
    }

    /**
     * Read messages, format them and disseminate tickers and recaps.
     * Also send product state events in.
     *
     * @param in java.io.InputStream
     * @param in java.io.InputStream
     * @param connectionName java.lang.String
     */
    public void serve(java.io.InputStream in, java.io.OutputStream out, String connectionName)
        throws IOException
    {
        // Get publishers and other needed services.
        internalStart();

        inStream = in;
        inReader = new InputStreamReader(inStream);
        Log.debug("inStream and inReader has been set.");
        Log.debug("Sleeping the serve thread, we never expect anything.  Write-only.");
        try
        {
            while (true)
            {
                TradeReportStruct report;
                try
                {
                    report = TradeReportHelper.readReport(inReader);
                }
                catch (IOException ex)
                {
                    Log.exception("Error reading report: abort adapter's server thread", ex);
                    break;
                }
                catch (ParseException ex)
                {
                    Log.exception("Error parsing report: ignoring message and continuing", ex);
                    continue;
                }
                System.out.println("Received report at " + new Date());
                ReflectiveObjectWriter.writeObject(report, "tradeReport");
                System.out.println();
            }
        }
        catch (Exception ex)
        {
            Log.exception("Exception in serve() method", ex);
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
