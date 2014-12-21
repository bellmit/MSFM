package com.cboe.application.test;

/**
 * This type was created in VisualAge.
 */
import com.cboe.interfaces.application.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class CASShutdownHomeImpl extends BOHome implements CASShutdownHome
{
    CASShutdownImpl casShutdownImpl;

    public final static String SMA_TYPE_PATH           = "GlobalCASShutdownHome.CASShutdownHomeImpl";
    public final static String SHUTDOWN_DELAY          = "ShutdownDelay";
    public final static String TITLE                   = "Title";
    public final static String ENABLE_SHUTDOWN_BUTTON  = "EnableShutdownButton";

    private final static int    DEFAULT_SHUTDOWN_DELAY = 10;
    private final static String DEFAULT_TITLE          = "";
    private final static int    DEFAULT_BUTTON_ENABLE  = 0;     //false

    private int     delay;
    private String  title;
    private int     showButton;

    private static boolean shutdownInProgress = false;

    public CASShutdownHomeImpl()
    {
        super();
        setSmaType(SMA_TYPE_PATH);
        this.shutdownInProgress = false;
    }

    public CASShutdown create()
    {
        if (casShutdownImpl == null)
        {
            casShutdownImpl = new CASShutdownImpl(delay, title, showButton);
            //Every bo object must be added to the container.
            addToContainer(casShutdownImpl);
            casShutdownImpl.create(String.valueOf(casShutdownImpl.hashCode()));
        }

        return casShutdownImpl;
    }

    public void initialize()
    {
        delay       = Integer.parseInt(getProperty(SHUTDOWN_DELAY, Integer.toString(DEFAULT_SHUTDOWN_DELAY)));
        title       = getProperty(TITLE, DEFAULT_TITLE);
        showButton  = Integer.parseInt(getProperty(ENABLE_SHUTDOWN_BUTTON, Integer.toString(DEFAULT_BUTTON_ENABLE)));
    }

    public CASShutdown find()
    {
        return create();
    }

    public void start()
    {
        create();
    }

    public void shutdown()
    {
        if ( ! shutdownInProgress )
        {
            shutdownInProgress = true;
            casShutdownImpl.shutdownCAS();
        }
    }

}
