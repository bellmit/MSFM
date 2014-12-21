package com.cboe.testDrive;

import com.cboe.testDrive.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.cmiV2.SessionManagerStructV2;
import com.cboe.idl.cmiV2.UserAccessV2;
import com.cboe.idl.cmiV2.UserSessionManagerV2;

import java.util.ArrayList;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.FileWriter;

public class CASTestQRM extends Thread
{
    protected String UserName;
    protected TestParameter testParm = null;
    protected UserAccessV2 userAccess = null;
    protected UserSessionManagerV2 userSessionManagerV2 = null;
    protected UserSessionManager userSessionManager = null;
    protected SessionManagerStructV2 sessionManagerV2;
    protected UserTradingParameters tradingParameters = null;
    ArrayList myClasses = null;

    public CASTestQRM(TestParameter parm, SessionManagerStructV2 sessionManagerStruct) throws Exception
    {
        this.sessionManagerV2 = sessionManagerStruct;
        this.testParm = parm;
        tradingParameters = sessionManagerV2.sessionManager.getUserTradingParameters();
    }


    public  void run()
    {
        int threshold = 100;
        int timeWindow = 0;

        while (!testParm.threadDone)
        {
            try
            {
    //            UserQuoteRiskManagementProfileStruct riskProfileStruct = tradingParameters.getAllQuoteRiskProfiles();
    //            QuoteRiskManagementProfileStruct[] riskProfiles = riskProfileStruct.quoteRiskProfiles;
                System.out.println("Number of class keys : " + testParm.prodKeysByClass.size());
                QuoteRiskManagementProfileStruct[] riskProfiles = new QuoteRiskManagementProfileStruct[testParm.prodKeysByClass.size()];

                Enumeration en = testParm.prodKeysByClass.keys();
    //            System.out.println(testParm.prodKeysByClass.keys().nextElement());
                int count = 0;

                while (en.hasMoreElements())
                {
                    int classKey = ((Integer)(en.nextElement())).intValue();
                    riskProfiles[count] = new QuoteRiskManagementProfileStruct();
                    riskProfiles[count].classKey = classKey ;
                    riskProfiles[count].quoteRiskManagementEnabled = true;
                    riskProfiles[count].timeWindow = timeWindow;
                    riskProfiles[count].volumeThreshold = threshold;
                    System.out.println("Calling setQuoteRiskProfile for user " + sessionManagerV2.sessionManager.getValidUser().fullName);
                    tradingParameters.setQuoteRiskProfile(riskProfiles[count]);
                    count++;
                }


                threshold = threshold + 1;
                if (threshold == 5000)
                {
                    threshold = 100;
                }

                if (timeWindow == 50)
                {
                    timeWindow = 0;
                }
                System.out.println("Sleeping #############################");
                Thread.currentThread().sleep(5000);
    /*            for (int i = 0; i < myClasses.length; i++)
                {
                    int classKey =
                    riskProfiles[i].
                    riskProfiles[i].quoteRiskManagementEnabled = true;
                    riskProfiles[i].timeWindow = 1;
                    riskProfiles[i].volumeThreshold = 100;
                    System.out.println("Calling setQuoteRiskProfile for user " + sessionManagerV2.sessionManager.getValidUser().fullName);
                    tradingParameters.setQuoteRiskProfile(riskProfiles[i]);
                }        */
            }
             catch (Exception e)
            {
                System.out.println("Failed ");
                e.printStackTrace();
                return;
            }
        }

        try
        {
            Thread.currentThread().sleep(120000);
            sessionManagerV2.sessionManager.logout();
        } catch (Exception e) {}

    }
}
