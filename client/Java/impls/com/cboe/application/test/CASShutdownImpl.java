package com.cboe.application.test;

import com.cboe.interfaces.application.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class CASShutdownImpl extends BObject implements CASShutdown
{
    CASShutdownFrame casShutdownFrame;

    protected int       delay;
    protected String    title;
    protected int       showButton;

    private static boolean shutdownInProgress = false;

    public CASShutdownImpl(int delay, String title, int showButton)
    {
        super();
        this.delay              = delay;
        this.title              = title;
        this.showButton         = showButton;
        this.shutdownInProgress = false;
    }

    public void create(String name)
    {
        super.create(name);

        if ( showButton != 0 )
        {
            casShutdownFrame = new CASShutdownFrame(this, title);
        }
    }

    public void setVisible(boolean value)
    {
        if ( showButton != 0 )
        {
            casShutdownFrame.setVisible(value);
        }
    }

    public void shutdownCAS()
    {
        if ( ! shutdownInProgress )
        {
            shutdownInProgress = true;

            // Wait 'delay' seconds and then start the CAS shutdown via FF.
            try
            {
                Log.alarm("\n\n!!! CAS Shutting Down in " + delay + " Seconds !!!\n\n");
                Thread.sleep(delay * 1000);
            }
            catch(Exception e) { }

            try
            {
                Log.alarm("\n\n!!! CAS shutdown started.... !!!\n\n");
                FoundationFramework ff = FoundationFramework.getInstance();
                // should be a blocking call until all are finished.
                ff.shutdownProcess();
                Log.alarm("\n\n!!! CAS shutdown complete !!!\n\n");
            }
            catch(Exception e)
            {
                Log.exception(e);
            }
        }

    }

}
