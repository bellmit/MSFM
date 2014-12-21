package com.cboe.testDrive;

import com.cboe.testDrive.*;
import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.delegates.callback.*;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.application.cas.TestCallback;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.domain.util.*;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.exceptions.*;

public class CASTestProdFileBuilder
{
    protected SessionManagerStructV2 sessionManagerStructV2 = null;
    Hashtable breakdown = new Hashtable();
    Hashtable allClasses = new Hashtable();
    Hashtable usableClassesByTs = new Hashtable();
    int numOfSplits = 0;
//    int numOfAgents = 10;
//    int userThreadsPerAgent = 10;

    TestParameter parm;

    //  private ArrayList productKeys = null;

    public CASTestProdFileBuilder(TestParameter parm, SessionManagerStructV2 sessionManagerStructV2)
        throws Exception
    {
        this.sessionManagerStructV2 = sessionManagerStructV2;
        this.parm = parm;
    }

    private void updateUsableClassByTS(String TS)
    {
        if (!usableClassesByTs.containsKey(TS))
        {
            usableClassesByTs.put(TS, new Counter());
        }
        ((Counter)usableClassesByTs.get(TS)).updateCount();
    }

    private int getUsableClassCountByTS(String TS)
    {
        int count;
        if (!usableClassesByTs.containsKey(TS))
        {
            count = 0;
        }
        else
        {
            count = ((Counter)usableClassesByTs.get(TS)).getCount();
        }
        return count;
    }

    public class Counter
    {
        int count = 0;
        public void updateCount()
        {
            count++;
        }

        public int getCount()
        {
            return count;
        }
    }

    public void createHybridOptionFile()
    {
        int numberOfProdsToUse = 0;
        String tmpClassKey;

        getClassByTS(parm.TSBreakdownFile);
        numOfSplits = parm.numOfSplits;

        try
        {
            System.out.println("Using CAS at " + parm.host + ":" + parm.port);

            UserSessionManager userSessionManager = sessionManagerStructV2.sessionManager;

            System.out.println("user name = " + (String)parm.userNames.get(0));

            PrintWriter writer =  new PrintWriter(new FileWriter(parm.productKeyFile));

            PrintWriter summaryWriter = new PrintWriter(new FileWriter("prodDownloadSummary.out"));
            com.cboe.application.cas.TestCallback callback = new com.cboe.application.cas.TestCallback();

            TradingSessionStruct[] sessions = userSessionManager.getTradingSession().getCurrentTradingSessions(null);

            PrintWriter[] splitWriters = new PrintWriter[numOfSplits];
            if (this.numOfSplits > 0)
            {
                for (int i = 0; i < numOfSplits; i++)
                {
                    splitWriters[i] = new PrintWriter(new FileWriter(i+1 + ".prd", true));
                }
            }

            int splits = 0;

            for (int i = 0; i < sessions.length; i++)
            {
                if ((sessions[i].sessionName).trim().equals("W_MAIN"))
                {
                    System.out.println("Session="+sessions[i].sessionName);

                    ClassStatusConsumerDelegate classStatus = new ClassStatusConsumerDelegate(callback);
                    ProductStatusConsumerDelegate productStatus = new ProductStatusConsumerDelegate(callback);

                    org.omg.CORBA.Object orbObject =
                            (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(classStatus);
                    com.cboe.idl.cmiCallback.CMIClassStatusConsumer cmiObject = CMIClassStatusConsumerHelper.narrow(orbObject);

                    org.omg.CORBA.Object prodObject =
                            (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(productStatus);
                    com.cboe.idl.cmiCallback.CMIProductStatusConsumer productObject = CMIProductStatusConsumerHelper.narrow(prodObject);

                    SessionClassStruct classes[] = userSessionManager.getTradingSession().
                            getClassesForSession(sessions[i].sessionName, parm.productType, cmiObject);
                    System.out.println("ProductType input for server is: " + parm.productType + " ************");
                    for (int j = 0; j < classes.length; j++)
                    {
                        tmpClassKey = Integer.toString(classes[j].classStruct.classKey);
                        if (this.allClasses.containsKey(tmpClassKey))
                        {
                            SessionProductStruct products[] = userSessionManager.getTradingSession().getProductsForSession(sessions[i].sessionName,
                                classes[j].classStruct.classKey, productObject);
                            if (parm.prodsPerHybridClass == 0)  // All products in a class
                            {
                                numberOfProdsToUse = products.length;
                            }
                            else
                            {
                                numberOfProdsToUse = parm.prodsPerHybridClass;
                            }

                            if (products.length >= numberOfProdsToUse)
                            {
                                this.updateUsableClassByTS((String)allClasses.get(tmpClassKey));
                                for (int k = 0; k < numberOfProdsToUse; k++)
                                {
                                    writer.println(allClasses.get(Integer.toString(classes[j].classStruct.classKey)) + ","
                                        + classes[j].classStruct.classKey + "," + classes[j].classStruct.classSymbol + ","
                                        + products[k].productStruct.productKeys.productKey + "," +  sessions[i].sessionName);
                                    if (numOfSplits > 0)
                                    {
                                        splitWriters[splits].println(allClasses.get(Integer.toString(classes[j].classStruct.classKey)) + ","
                                            + classes[j].classStruct.classKey + "," + classes[j].classStruct.classSymbol + ","
                                            + products[k].productStruct.productKeys.productKey + "," +  sessions[i].sessionName);
                                    }
                                }
                                splits = splits + 1;
                                if (splits >= numOfSplits)
                                {
                                    splits = 0;
                                }
                            }
                            else
                            {
                                numberOfProdsToUse = 0;
                            }

                            summaryWriter.println("Written out " + numberOfProdsToUse + " of " + products.length + " Hybrid products for " + classes[j].classStruct.classSymbol + ".");
                            System.out.println("Written out " +  numberOfProdsToUse + " of " + products.length + " Hybrid products for " + classes[j].classStruct.classSymbol + ".");
                        }
                    }
                }
            }

            Enumeration en = breakdown.keys();
            String[] TSs = new String[breakdown.size()];
            TSs = (String[])breakdown.keySet().toArray(TSs);

            String TS = "";
            int tmpUsableCount = 0;
            int tmpCountByTs = 0;
            int tmpMaxClassCount = 0;

            while (en.hasMoreElements())
            {
                TS = (String)en.nextElement();
                tmpUsableCount = getUsableClassCountByTS(TS);
                tmpCountByTs = ((ArrayList)breakdown.get(TS)).size();
                if (tmpUsableCount > tmpMaxClassCount)
                {
                    tmpMaxClassCount = tmpUsableCount;
                }
                summaryWriter.println("Written out " + tmpUsableCount + " classes from " + tmpCountByTs + " for " + TS);
            }
            writer.close();
            summaryWriter.close();

            if (this.numOfSplits > 0)
            {
                for (int i = 0; i < numOfSplits; i++)
                {
                    splitWriters[i].flush();
                    splitWriters[i].close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace() ;
            return ;
        }
    }


    public void getClassByTS(String breakdownFile)
    {

        FileReader aFR;
        String tmpClassKey;
        String tmpTSName;

        try {
            aFR = new FileReader(breakdownFile);
            BufferedReader br = new BufferedReader(aFR);
            String s;
            while (( s = br.readLine()) != null)
            {
                tmpClassKey = "";
                tmpTSName = "";
                StringTokenizer parser = new StringTokenizer(s, ",");

                if (parser.hasMoreTokens())
                {
                    tmpTSName = parser.nextToken().trim();
                }
                if (parser.hasMoreTokens())
                {
                    tmpClassKey = parser.nextToken().trim();
                }

                if (!tmpTSName.equals("") && !tmpClassKey.equals(""))
                {
                    if ( ! this.breakdown.containsKey(tmpTSName))
                    {
                        breakdown.put(tmpTSName, new ArrayList());
                    }
                    ((ArrayList)(breakdown.get(tmpTSName))).add(tmpClassKey);
                    allClasses.put(tmpClassKey,tmpTSName);
                }
                else
                {
                    System.out.println("Bad product/class read from file...");
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error obtaining breakdown keys. " + e);
        }
        System.out.println("file : " + breakdownFile + "...");
    }
}
