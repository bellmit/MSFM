// -----------------------------------------------------------------------------------
// Source file: AbstractInstrumentor.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import java.text.ParseException;
import java.util.*;

import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.instrumentation.Instrumentor;

import com.cboe.util.UserDataTypes;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.formatters.InstrumentorTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GIUserDataType;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.SessionKeyUserDataHelper;

public abstract class AbstractInstrumentor extends AbstractMutableBusinessModel implements Instrumentor
{
    private static final String CATEGORY = AbstractInstrumentor.class.getName();
    protected static final String NAME_PATH_SEPARATOR = "/";
    protected String userData;
    protected String name;
    protected byte[] instrumentorKey;
    protected long lastUpdatedTimeMillis;
    protected Date lastUpdatedTime;
    protected String[] path;
    protected String orbName;
    protected String clusterName;
    protected String channelKey;
    protected short type;
    protected String typeString;
    protected String fullName;
    protected Boolean infraInstrumentor;

    public AbstractInstrumentor()
    {
        super();
        setType();
    }

    /**
     * Sets type of the instrumentor.  Extending classes should provide implementation.
     */
    abstract protected void setType();
    abstract protected String[] getInfraNames();

    protected boolean getInfraType()
    {
        if (infraInstrumentor == null)
        {
            infraInstrumentor = Boolean.FALSE;
            String[] infraNames = getInfraNames();
            if(infraNames != null && infraNames.length > 0)
            {
                for (int i = 0; i < infraNames.length; i++)
                {
                    StringBuffer testName = new StringBuffer(20);
                    testName.append(infraNames[i]).append(NAME_PATH_SEPARATOR);
                    if(getName() != null &&
                            (getName().indexOf(testName.toString())!= -1) ||
                             getName().equals(infraNames[i]))
                    {
                        infraInstrumentor = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        return infraInstrumentor.booleanValue();

    }
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if(!isEqual)
        {
            if(obj instanceof Instrumentor )
            {
                Instrumentor castedObj = ( Instrumentor ) obj;
                if( Arrays.equals(this.getInstrumentorKey(), castedObj.getInstrumentorKey()) )
                {
                    isEqual = true;
                }
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        StringBuffer buff = new StringBuffer(100);
        buff.append(getClass().getName()).append(getName()).append(getOrbName());
        return buff.toString().hashCode();
    }

    public String toString()
    {
        return "ORB Name: " + getOrbName() + "; User Data: " + getUserData();
    }

    /**
     * Returns userData String of the instrumentor object.
     * @return userData String
     */
    public String getUserData()
    {
        return userData;
    }

    protected void setData(String orbName, String clusterName,
                           com.cboe.instrumentationService.instrumentors.Instrumentor instrumentor)
    {
        checkParam(instrumentor, "Instrumentor");
        this.userData = (String)instrumentor.getUserData();
        this.name = instrumentor.getName();
        this.orbName = orbName;
        this.clusterName = clusterName;
        this.instrumentorKey = instrumentor.getKey();
        updateLastUpdateTime();
    }
    /**
     * Returns name String of the instrumentor object.
     * @return name String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns instrumentor key byte array of the instrumentor object.
     * @return instrumentor key byte[]
     */
    public byte[] getInstrumentorKey()
    {
        return instrumentorKey;
    }

    /**
     * Returns last updated time of the instrumentor object.
     * @return last updated time Date
     */
    public Date getLastUpdatedTime()
    {
        if(lastUpdatedTime == null)
        {
            lastUpdatedTime = new Date(lastUpdatedTimeMillis);
        }
        return lastUpdatedTime;
    }

    public long getLastUpdatedTimeMillis()
    {
        return lastUpdatedTimeMillis;
    }

    protected void updateLastUpdateTime()
    {
        lastUpdatedTimeMillis = System.currentTimeMillis();
        lastUpdatedTime = null;
    }

    /**
     * Returns path of the instrumentor object.
     * @return path String[]
     */
    public String[] getPath()
    {
        // lazily initialize the path
        if (path == null)
        {
            StringTokenizer tokens = new StringTokenizer(getName(),NAME_PATH_SEPARATOR);
            path = new String[tokens.countTokens()];
            int index = 0;
            while (tokens.hasMoreTokens())
            {
                path[index++] = tokens.nextToken();
            }
        }
        return path;
    }

    protected void setData(String orbName, String clusterName, String name, GIUserDataType giUserDataType)
    {
        this.orbName = orbName;
        this.clusterName = clusterName;
        this.name = name;
        this.userData = XmlBindingFacade.getInstance().getDelimitedUserData(giUserDataType);
        updateLastUpdateTime();
    }

    /**
     * Returns orbName of the instrumentor object.
     * @return orbName
     */
    public String getOrbName()
    {
        return orbName;
    }

    /**
     * Returns clusterName of the instrumentor object.
     * @return clusterName
     */
    public String getClusterName()
    {
        return clusterName;
    }

    /**
     * Returns a String that can be used as a channel key.
     * @return channelKey
     */
    public String getChannelKey()
    {
        if ( channelKey == null )
        {
            //channelKey = getOrbName()+getInstrumentorType();
            channelKey = getInstrumentorTypeString();
        }
        return channelKey;
    }

    /**
     * Returns an InstrumentorType.
     * @return type
     */
    public short getInstrumentorType()
    {
        return this.type;
    }

    /**
     * Returns an InstrumentorType.
     * @return type
     */
    protected String getInstrumentorTypeString()
    {
        if (typeString == null)
        {
            typeString = InstrumentorTypes.toString(this.type);
        }
        return typeString;
    }

    protected SessionKeyWrapper[] getSessionKeysFromUserData()
    {
        SessionKeyWrapper[] wrappers = new SessionKeyWrapper[0];

        String userData = getUserData();

        if( userData != null && userData.length() > 0 )
        {
            try
            {
                InstrumentorUserData parsedUserData = new InstrumentorUserData(userData);

                String[] values = parsedUserData.getValues(UserDataTypes.SESSION_CLASS);
                if( values != null && values.length > 0 )
                {
                    try
                    {
                        wrappers = SessionKeyUserDataHelper.decode(values);
                    }
                    catch (ParseException e)
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": getSessionKeysFromUserData",
                                "Could not parse a Session Key pairs:" +
                                values, e);
                    }
                }
            }
            catch( ParseException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": getSessionKeysFromUserData",
                                               "Could not parse userData:" + userData, e);
            }
        }

        return wrappers;
    }

    public boolean isInfraInstrumentor()
    {
        return getInfraType();
    }

    public String getFullName()
    {
        if(fullName == null)
        {
            fullName = name;
        }
        return fullName;
    }
}
