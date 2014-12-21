package com.cboe.cfix.startup;

/**
 * CfixHomeImpl.java
 *
 * @author Jing Chen
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.cboe.client.util.*;
import com.cboe.client.util.Pair;
import com.cboe.client.util.threadpool.*;
import com.cboe.domain.startup.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.application.VersionQuery;
import com.cboe.util.*;
import com.cboe.cfix.fix.session.*;
import com.cboe.cfix.cas.shared.CfixServicesHelper;
import com.cboe.application.cas.CASSummaryData;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;

public class CfixHomeImpl extends ClientBOHome implements CfixHome
{
    // The CfixConfigurationFile property defined in the CfixHome.xml points to the cfix ini file.
    public final static String PROPERTY_FILE_NAME = "CfixConfigurationFile";

    public final static Properties cfixProperties  = new Properties();

    protected static PropertiesHelper propertiesHelper = new PropertiesHelper(cfixProperties);

    public static String cfixPropertiesFileName;

    private static boolean isFixConnectionInitialized = false;

    public static final String CFIX_SUB_MODE                                 = "cfixSubscriptionMode";
    private boolean isMDXEnabled                                             = false;

    public static final String C2_SESSION                                    = "isC2Session";
    private boolean isC2Session                                              = false;


    public void clientInitialize() throws Exception
    {
        if (Log.isDebugOn()) Log.debug(this, "SMA Type = " + this.getSmaType());

        String retnVal = getProperty(CFIX_SUB_MODE, "false");
        this.isMDXEnabled = (retnVal.equals("MDX"));
        if (Log.isDebugOn())
        {
            Log.debug(this, "In CfixHomeImpl: CFIX subscription mode is set to: " + retnVal + " - CFIX MDX Enabled is : " + this.isMDXEnabled);
        }
        CfixServicesHelper.setMDXEnabled(isMDXEnabled);

        String retnValC2 = getProperty(C2_SESSION, "false");
        this.isC2Session = (retnValC2.equals("c2"));
        if (Log.isDebugOn())
        {
            Log.debug(this, "In CfixHomeImpl: SBT Environment Variable is set to: " + retnValC2 + " . isC2Session is " + this.isC2Session);
        }
        CfixServicesHelper.setSessionC2(isC2Session);


        cfixPropertiesFileName = getProperty(PROPERTY_FILE_NAME);

        Log.information("Importing CFIX properties from URL[" + cfixPropertiesFileName + "]");

        loadFile(cfixPropertiesFileName);

        VersionQuery versionQuery = new VersionQueryImpl();
        CASSummaryData casSummaryData = new CASSummaryData(versionQuery);
        HeapInstrumentor hi = FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().find(HeapInstrumentor.INSTRUMENTOR_TYPE_NAME);
        if(hi == null)
        {
            hi = FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().create(HeapInstrumentor.INSTRUMENTOR_TYPE_NAME, casSummaryData);
            FoundationFramework.getInstance().getInstrumentationService().getHeapInstrumentorFactory().register(hi);
        }
        else
        {
            hi.setUserData(casSummaryData);
        }
    }

    public static void mergeCfixPropertyFile(InputStream inputStream) throws Exception
    {
        try
        {
            cfixProperties.load(inputStream);
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
        finally
        {
            try
            {
                if (inputStream != null) inputStream.close();
            }
            catch (Exception x)
            {

            }
        }
    }

    public static void mergeCfixSenderPropertyFile(InputStream inputStream) throws Exception
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int index;
            String line;
            String sender;
            StringTokenizer tokenizer;
            StringBuilder propertyName = new StringBuilder(60);

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#"))
                {
                    continue;
                }

                index = line.indexOf("=");
                if (index >= 0)
                {
                    cfixProperties.put(line.substring(0, index), line.substring(index + 1));
                    continue;
                }

                tokenizer = new StringTokenizer(line, ",");

                if (tokenizer.hasMoreTokens())
                {
                    sender = tokenizer.nextToken();

                    propertyName.setLength(0);
                    propertyName.append("session.").append(sender).append(".cfix.fixSession.senderCompID");
                    cfixProperties.put(propertyName.toString(), sender);

                    if (tokenizer.hasMoreTokens())
                    {
                        propertyName.setLength(0);
                        propertyName.append("session.").append(sender).append(".cfix.fixSession.description");
                        cfixProperties.put(propertyName.toString(), line.substring(line.indexOf(',') + 1).trim());
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
        finally
        {
            try
            {
                if (inputStream != null) inputStream.close();
            }
            catch (Exception x)
            {

            }
        }
    }

    public void clientStart() throws Exception
    {
        initializeCfixTouristConnection(); // if TouristHome was not run, this will check the INI file whether to start the Tourist
    }

    public void initializeCfixTouristConnection() throws SystemException
    {
        String                  connectionToStart;
        String                  runnableInitializableClass;
        RunnableInitializableIF runnableInitializable;
        StringBuilder           initMsg = new StringBuilder(100);

        for (int i = 0; i < 1000; i++)
        {
            connectionToStart = propertiesHelper.getPrefixedProperty("start.tourist", Integer.toString(i));
            if (connectionToStart == null)
            {
                break;
            }

            runnableInitializableClass = propertiesHelper.getPrefixedProperty(connectionToStart, "cfix.runnableInitializableClass");
            if (runnableInitializableClass == null)
            {
                throw ExceptionBuilder.systemException("CRITICAL MISCONFIGURATION ERROR: no 'cfix.runnableInitializableClass' defined for " + connectionToStart,0);
            }

            try
            {
                runnableInitializable = (RunnableInitializableIF) ClassHelper.loadClass(runnableInitializableClass);
                runnableInitializable.initialize(connectionToStart, cfixProperties);

                AdaptiveThreadPool.getDefaultThreadPool().execute(runnableInitializable, connectionToStart);

                initMsg.setLength(0);
                initMsg.append(Thread.currentThread().getName())
                       .append(" CfixHomeImpl Initializing Tourist connection(").append(connectionToStart)
                       .append(") using class: ").append(runnableInitializableClass);
                Log.information(initMsg.toString());
            }
            catch (Exception ex)
            {
                Log.exception(ex);
                throw ExceptionBuilder.systemException("Couldn't instantiate and start component: " + runnableInitializableClass, 0);
            }
        }
    }

    /**
     * Method to initialize the Cfix Connection on port defined by "start.fix" -
     * FROM FIME cfix$XXv2$HOSTNAME.ini under the config/properties directory on a cfix host
     * @throws SystemException
     */
    public void initializeCfixConnection() throws SystemException
    {
        if (!isFixConnectionInitialized)
        {
            String                  connectionToStart;
            String                  runnableInitializableClass;
            RunnableInitializableIF runnableInitializable;

            if (propertiesHelper.getPropertyBoolean("log.debug"))
            {
                boolean on = false;

                try
                {
                    Field field = Log.class.getDeclaredField("debugStatus");
                    boolean oldAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(null, Boolean.TRUE);
                    field.setAccessible(oldAccessible);
                    on = true;
                }
                catch (Exception ex)
                {

                }

                if (on)
                {
                    Log.information("CfixHomeImpl turned on Log.debug");
                }
                else
                {
                    Log.information("CfixHomeImpl could not turn on Log.debug");
                }
            }

            int i;
            StringBuilder initMsg = new StringBuilder(100);

            for (i = 0; i < 1000; i++)
            {
                // Gets the port number string - EXAMPLE - cfix01v2$HOSTNAME.ini : start.fix.0=connection.21536
                connectionToStart = propertiesHelper.getPrefixedProperty("start.fix", Integer.toString(i));
                if (connectionToStart == null)
                {
                    break;
                }
                // Gets the runnable class property - FROM FILE - cfix.defaults :
                // defaults.connection.MarketData_Fix42.cfix.runnableInitializableClass=com.cboe.cfix.fix.net.FixNetworkAcceptor
                // or, for the MDX enabled CFIX - defaults.connection.MarketData_Fix42.cfix.runnableInitializableClassMDX=com.cboe.cfix.fix.net.FixMDXNetworkAcceptor
                if (isMDXEnabled)
                    runnableInitializableClass = propertiesHelper.getPrefixedProperty(connectionToStart, "cfix.runnableInitializableClassMDX");
                else
                    runnableInitializableClass = propertiesHelper.getPrefixedProperty(connectionToStart, "cfix.runnableInitializableClass");

                if (runnableInitializableClass == null)
                {
                    if(isMDXEnabled)
                        throw ExceptionBuilder.systemException("CRITICAL MISCONFIGURATION ERROR: no 'cfix.runnableInitializableClassMDX' defined for " + connectionToStart, 0);
                    else
                        throw ExceptionBuilder.systemException("CRITICAL MISCONFIGURATION ERROR: no 'cfix.runnableInitializableClass' defined for " + connectionToStart, 0);
                }

                try
                {
                    runnableInitializable = (RunnableInitializableIF) ClassHelper.loadClass(runnableInitializableClass);
                    runnableInitializable.initialize(connectionToStart, cfixProperties);

                    AdaptiveThreadPool.getDefaultThreadPool().execute(runnableInitializable, connectionToStart);

                    initMsg.setLength(0);
                    initMsg.append(Thread.currentThread().getName())
                           .append(" CfixHomeImpl Initializing FIX connection(").append(connectionToStart)
                           .append(") using class: ").append(runnableInitializableClass);
                    Log.information(initMsg.toString());
                }
                catch (Exception ex)
                {
                    Log.exception(ex);
                    throw ExceptionBuilder.systemException("Couldn't instantiate and start component: " + runnableInitializableClass, 0);
                }

                FixSessionManagerLocator.setEngineNameForPort(propertiesHelper.getPrefixedPropertyInt(connectionToStart, "cfix.fixNetworkAcceptor.port"), propertiesHelper.getPrefixedProperty(connectionToStart, "cfix.fixNetworkAcceptor.targetCompID"));
            }

            if (i == 0)
            {
                throw ExceptionBuilder.systemException("CRITICAL MISCONFIGURATION ERROR: no 'start.fix.0' defined in the ini file", 0);
            }

            isFixConnectionInitialized = true;
        }
    }

    public void stopCfixConnection()
    {

    }

    public void loadFile(String propertyFileName) throws Exception
    {
        InputStream inputStream = null;
        Exception   exception   = null;
        String      originalPropertyFileName = propertyFileName;
        StringBuilder sb = new StringBuilder(100);

        exception = null;

        try
        {
            inputStream = new URL(originalPropertyFileName).openStream();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        if (exception != null)
        {
            if (propertyFileName.indexOf(":") >= 0)
            {
                throw exception;
            }

            try
            {
                sb.append("file:").append(propertyFileName);
                originalPropertyFileName = sb.toString();
                inputStream = new URL(originalPropertyFileName).openStream();
            }
            catch (Exception ex)
            {
                throw exception;
            }
        }

        mergeCfixPropertyFile(inputStream);

        String fileRoot = null;

        try
        {
            int lastSlash  = originalPropertyFileName.lastIndexOf('/');
            int lastPeriod = originalPropertyFileName.lastIndexOf('.');

            if (lastPeriod > lastSlash)
            {
                fileRoot = originalPropertyFileName.substring(lastSlash + 1, lastPeriod);
            }
        }
        catch (Exception ex)
        {

        }

        Pair pair = new Pair();

        for (int i = 0; i < 1000; i++)
        {
            sb.setLength(0);
            sb.append("import.").append(i);
            if ((propertyFileName = (String) cfixProperties.remove(sb.toString())) == null) {break;}

            pair = getUrl(fileRoot, propertyFileName, originalPropertyFileName, pair);
            if (pair.first != null)
            {
                mergeCfixPropertyFile((InputStream) pair.first);
                sb.setLength(0);
                sb.append("Importing additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
            else
            {
                sb.setLength(0);
                sb.append("Could not import additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
        }

        for (int i = 0; i < 1000; i++)
        {
            sb.setLength(0);
            sb.append("importifexists.").append(i);
            if ((propertyFileName = (String) cfixProperties.remove(sb.toString())) == null) {break;}

            pair = getUrl(fileRoot, propertyFileName, originalPropertyFileName, pair);
            if (pair.first != null)
            {
                mergeCfixPropertyFile((InputStream) pair.first);
                sb.setLength(0);
                sb.append("Importing additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
        }

        for (int i = 0; i < 1000; i++)
        {
            sb.setLength(0);
            sb.append("importsenders.").append(i);
            if ((propertyFileName = (String) cfixProperties.remove(sb.toString())) == null) {break;}

            pair = getUrl(fileRoot, propertyFileName, originalPropertyFileName, pair);
            if (pair.first != null)
            {
                mergeCfixSenderPropertyFile((InputStream) pair.first);
                sb.setLength(0);
                sb.append("Importing and merging Sender information as additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
            else
            {
                sb.setLength(0);
                sb.append("Could not importing and merge Sender information as additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
        }

        for (int i = 0; i < 1000; i++)
        {
            sb.setLength(0);
            sb.append("importsendersifexists.").append(i);
            if ((propertyFileName = (String) cfixProperties.remove(sb.toString())) == null) {break;}

            pair = getUrl(fileRoot, propertyFileName, originalPropertyFileName, pair);
            if (pair.first != null)
            {
                mergeCfixSenderPropertyFile((InputStream) pair.first);
                sb.setLength(0);
                sb.append("Importing and merging Sender information as additional CFIX properties from URL[").append(pair.second).append("]");
                Log.information(sb.toString());
            }
        }
    }

    private Pair getUrl(String fileRoot, String propertyFileName, String originalPropertyFileName, Pair pair)
    {
        int offsetStart = propertyFileName.indexOf("${FILENAME}");
        if (offsetStart != -1 && fileRoot != null)
        {
            StringBuilder pfn = new StringBuilder(fileRoot.length()+propertyFileName.length());
            int offsetEnd = offsetStart + 11;
            if (offsetStart == 0)
            {
                pfn.append(fileRoot).append(propertyFileName.substring(offsetEnd));
                propertyFileName = pfn.toString();
            }
            else
            {
                pfn.append(propertyFileName.substring(0, offsetStart)).append(fileRoot).append(propertyFileName.substring(offsetEnd));
                propertyFileName = pfn.toString();
            }
        }

        InputStream inputStream = null;

        try
        {
            inputStream = new URL(propertyFileName).openStream();
        }
        catch (Exception ex)
        {
            if (propertyFileName.indexOf(":") < 0)
            {
                int index = originalPropertyFileName.lastIndexOf('/');
                if (index > -1)
                {
                    try
                    {
                        propertyFileName = originalPropertyFileName.substring(0, index + 1) + propertyFileName;
                        inputStream = new URL(propertyFileName).openStream();
                    }
                    catch (Exception ex2)
                    {

                    }
                }
            }
        }

        return pair.reset(inputStream, propertyFileName);
    }
}
