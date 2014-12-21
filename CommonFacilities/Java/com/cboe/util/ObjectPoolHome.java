package com.cboe.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * To specify object pool cartridge size use -DObjectPoolCartridge.size.<full class name>=<value>
 * Eg: -DObjectPoolCartridge.size.com.util.AcceptQuoteBlockCommand=50
 * 
 * To specify object pool increment use -DObjectPoolCartridge.capacityIncrement.<full class name>=<value>
 * Eg: -DObjectPoolCartridge.capacityIncrement.com.util.AcceptQuoteSequenceCommand=10
 * This value indicates the number of objects that the pool grows by when its empty.
 *
 * @version 0.31
 * @author Kevin Park
 */
public class ObjectPoolHome {
	private static final String defaultHomeType = "persistent";
	private static final String CARTRIDGE_INCREMENT = "10";
	private static final int DEF_CARTRIDGE_INCREMENT = 10;
	private static final int DEF_CARTRIDGE_OVERFLOWFACTOR = 0;
	private static final String USE_ELASTIC_OBJ_POOL = "ObjectPool.useElasticObjectPool";  // "yes","no", or "threadLocal"
	private static String homeType; // "persistent", "null", "transactional", etc.
	private static ObjectPoolHome home;
	private ConcurrentHashMap<String, ObjectPool> objectPoolCollection = new ConcurrentHashMap<String, ObjectPool>();
	private static String CARTRIDGE_CAPACITY = "40";
	private static int DEF_CARTRIDGE_CAPACITY = 0;
	private static boolean useElasticObjectPool;
	private static boolean useElasticObjectPoolThreadLocal;
	
	static
	{
	    String useEOP=System.getProperty(USE_ELASTIC_OBJ_POOL, "no");
	    useElasticObjectPoolThreadLocal = useEOP.compareToIgnoreCase ("threadLocal") == 0;
		useElasticObjectPool = useElasticObjectPoolThreadLocal || useEOP.compareToIgnoreCase ("yes") == 0;
	}
    /**
     * OrderBookHomeImpl constructor comment.
     */
    protected ObjectPoolHome() {
    	super();
    	registerAdminCommand();
    }
    /**
     * Default object pool key is the string name of the object's class.
     */
    public ObjectPool create(Copyable newObject) {
    	return create( newObject.getClass().getName(), newObject );
    }
    /**
     * create method comment.
     */
    public <T extends Copyable> ObjectPool<T> create(String configurationKey, T newObject) {
    
    	ObjectPool<T> rval = objectPoolCollection.get(configurationKey);
    
    	if (rval == null)
    	{
    		synchronized (this)
    		{
    			rval = objectPoolCollection.get(configurationKey);
    			
    			if (rval == null)
    			{
    			    if (getUseElasticObjectPoolThreadLocalFlag())
    			    {
    			        rval = new ElasticObjectPoolThreadLocal<T>(
    			                newObject, getCapacity(newObject), getCapacityIncrement(newObject),
    			                ElasticObjectPoolThreadLocal.ExtraObjectPolicy.NEW_SEGMENT, getCapacityOverFlow(newObject));
    			    }
    			    else if (getUseElasticObjectPoolFlag())
    			    {
    			        rval = new ElasticObjectPool<T>(
    			                newObject, getCapacity(newObject), getCapacityIncrement(newObject));
    			    }
    			    else
    			    {
    			        rval = new ObjectPoolImpl<T>(getCapacity(newObject),
    									getCapacityIncrement(newObject), newObject);
    			    }
    				objectPoolCollection.put(configurationKey, rval);
    			}
    		}
    	}
    
    	return rval;
    }
    
    private int getCapacity(Copyable newObject)
    {
        return getSystemProperty("ObjectPoolCartridge.size.", newObject, DEF_CARTRIDGE_CAPACITY);
    }
    
    private int getCapacityIncrement (Copyable newObject)
    {
        return getSystemProperty("ObjectPoolCartridge.capacityIncrement.", newObject, DEF_CARTRIDGE_INCREMENT);
    }
    
    private int getCapacityOverFlow (Copyable newObject)
    {
        return getSystemProperty("ObjectPoolCartridge.capacityOverflowFactor.", newObject, DEF_CARTRIDGE_OVERFLOWFACTOR);
    }
    
    /**
     * Get system property as either "[prefix] + [full class name]" or "[prefix] + [simple class name]".
     * Returns p_default on any error encountered, or if the property is not defined.
     * 
     * @param p_propertyPrefix
     * @param p_object
     * @param p_default
     * @return
     */
    private int getSystemProperty(String p_propertyPrefix, Copyable p_object, int p_default)
    {
        if (p_object == null)
        {
            System.out.println("ObjectPool config PROCESSING ERROR: passed null object. "
                    + "Returning default "+p_default);
            return p_default;
        }
        String prop="???";
        String value;
        try
        {
            if (!p_propertyPrefix.endsWith("."))
            {
                p_propertyPrefix += ".";
            }
            prop = p_propertyPrefix+p_object.getClass().getName();
            value = System.getProperty(prop);
            if (value == null)
            {
                prop=p_propertyPrefix+p_object.getClass().getSimpleName();
                value = System.getProperty(prop);
            }
            if  (value == null)
            {
                System.out.println("ObjectPool using default property value "+prop+"="+p_default);
                return p_default;
            }
            System.out.println("ObjectPool using system property "+prop+"="+value);
        }
        catch (Exception e1)
        {
            System.out.println("ObjectPool PROPERTY ACCESS ERROR for system property "+prop+": using default "+p_default);
            return p_default;
        }
        try
        {
            return Integer.parseInt(value);
        }
        catch (Exception e)
        {
            System.out.println("ObjectPool INTEGER PARSE ERROR for system property "+prop+"="+value + ": using default "+p_default);
            return p_default;
        }
    }
    
    private boolean getUseElasticObjectPoolFlag ()
    {
    	return useElasticObjectPool;
    }
    
    private boolean getUseElasticObjectPoolThreadLocalFlag ()
    {
        return useElasticObjectPoolThreadLocal;
    }

    /**
     * This method will return the ObjectPool for the specified
     * key.  If the ObjectPool is not found, null is returned.
     */
    public ObjectPool find(String targetKey) {
    	return objectPoolCollection.get(targetKey);
    }
    /**
     * @author Kevin Park
     * @return com.cboe.businessServices.orderBookService.OrderBookHome
     */
    public static ObjectPoolHome getHome() {
    	if (home == null) {
    		synchronized (ObjectPoolHome.class) {
    			if (home == null) {
    				if (homeType == null) {
    					// SPS - or however we get config properties.
    					//
    					homeType = System.getProperty("ObjectPoolHomeType",defaultHomeType);
    				}
    				if (homeType.equalsIgnoreCase("Persistent"))
    					home = new ObjectPoolHome();
    				//else if (homeType.equalsIgnoreCase("null"))
    				//	home = new OrderBookHomeNullImpl();
    				//else
    				//	throw new InvalidConfigException("Invalid OrderBookHomeType value: " + homeType);
    			}
    		}
    	}
    	return home;
    }

    
    /**
     * Show Object Pool Information.
     * @param objectPoolType
     * @return
     */
    public String adminShowObjectPool (String objectPoolType)
    {
        StringBuilder returnString = new StringBuilder(200);
        try
        {
            if(objectPoolType == null || objectPoolType.length() == 0)
            {
                returnString.append("Missing PoolType. Format<poolType> (regex and % supported)");
            }
            else
            {
                final List<String> matchedPoolTypes = getMatchedObjectPools(objectPoolType);
                if (matchedPoolTypes.size() == 0)
                {
                    returnString.append("No Object found that match name '").append(objectPoolType).append("'");
                }
                else
                {
                    returnString.append("showObjectPool Type for Object of PoolType: ").append(objectPoolType).append("\n\n");
                    for (int i = 0; i < matchedPoolTypes.size(); i++)
                    {
                        String objectPool  = matchedPoolTypes.get(i);
                        ObjectPool pooledObject = objectPoolCollection.get(objectPool);
                        returnString.append("  ").append(pooledObject.toString()).append('\n');
                    }
                }
            }
            return returnString.toString();
        }
        catch (Exception ex)
        {
            java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();
            java.io.PrintWriter writer = new java.io.PrintWriter(bytesOut);
            ex.printStackTrace(writer);
            writer.flush();
            return new String(bytesOut.toByteArray());
        }
    }
    
    
    /**
     * Undo Object pooling and revert back to no object pooling mode.
     * @param String
     * @return
     */
    public String adminAddMoreObjects (String objectPoolType, String numberOfObjectsToBeAdded)
    {
        StringBuffer returnString = new StringBuffer(200);
        try
        {
            if(objectPoolType == null || objectPoolType.length() == 0)
            {
                returnString.append("Missing PoolType. Format<poolType> (regex and % supported)");
            }
            else
            {
                final List<String> matchedPoolTypes = getMatchedObjectPools(objectPoolType);
                if (matchedPoolTypes.size() == 0)
                {
                    returnString.append("No Object found that match name '").append(objectPoolType).append("'");
                }
                else
                {
                    returnString.append("Adding NewFullSegments of PoolType: ").append(objectPoolType).append("\n\n");
                    for (int i = 0; i < matchedPoolTypes.size(); i++)
                    {
                        String objectPool  = matchedPoolTypes.get(i);
                        ObjectPool pooledObject = objectPoolCollection.get(objectPool);
                        int currentSegmentSize = pooledObject.getMaxNumSegments();
                        if(pooledObject.getMaxNumSegments() > 0)
                        {
                            int numberOfSegementsToBeAdded = Integer.parseInt(numberOfObjectsToBeAdded)/currentSegmentSize;
                            pooledObject.addMoreSegments(returnString, numberOfSegementsToBeAdded);
                        }
                        else
                        {
                            returnString.append("Cannot add: as the segment size is < 0, actual value is" ).append( currentSegmentSize ).append( "\n\n");
                        }
                    }
                }
            }
            return returnString.toString();
        }
        catch (Exception ex)
        {
            java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();
            java.io.PrintWriter writer = new java.io.PrintWriter(bytesOut);
            ex.printStackTrace(writer);
            writer.flush();
            return new String(bytesOut.toByteArray());
        }
    }
    
    /**
     * Undo Object pooling and revert back to no object pooling mode.
     * @param String
     * @return
     */
    public String adminUndoObjectPool (String objectPoolType)
    {
        StringBuffer returnString = new StringBuffer();
        try
        {
            if(objectPoolType == null || objectPoolType.length() == 0)
            {
                returnString.append("Missing PoolType. Format<poolType> (regex and % supported)");
            }
            else
            {
                final List<String> matchedPoolTypes = getMatchedObjectPools(objectPoolType);
                if (matchedPoolTypes.size() == 0)
                {
                    returnString.append("No Object found that match name '").append(objectPoolType).append("'");
                }
                else
                {
                    returnString.append("Undoing ObjectPooling Object of PoolType: ").append(objectPoolType).append( "\n\n");
                    for (int i = 0; i < matchedPoolTypes.size(); i++)
                    {
                        String objectPool  = matchedPoolTypes.get(i);
                        ObjectPool pooledObject = objectPoolCollection.get(objectPool);
                        pooledObject.undoObjectPooling(returnString);
                    }
                }
            }
            return returnString.toString();
        }
        catch (Exception ex)
        {
            java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();
            java.io.PrintWriter writer = new java.io.PrintWriter(bytesOut);
            ex.printStackTrace(writer);
            writer.flush();
            return new String(bytesOut.toByteArray());
        }
    }


    //{poolTypeRegex} {set|get} {overflow|trace} [true|false]
    public String adminSetObjectPoolLogging(String objectPoolType, String logAction, 
                                            String logLevel,      String logLevelOnOff)
    {
        StringBuilder returnString = new StringBuilder(200);
        try
        {
            if(objectPoolType == null || objectPoolType.length() == 0)
            {
                returnString.append("Missing PoolType. Format<poolType> (regex and % supported), <set | get>, <overflow | trace> , <true | false> ");
            }
            else
            {
                final List<String> matchedPoolTypes = getMatchedObjectPools(objectPoolType);
                if (matchedPoolTypes.size() == 0)
                {
                    returnString.append("No Object found that match name '").append(objectPoolType).append("'");
                }
                else
                {
                    for (int i = 0; i < matchedPoolTypes.size(); i++)
                    {
                        String objectPool  = matchedPoolTypes.get(i);
                        ObjectPool pooledObject = objectPoolCollection.get(objectPool);
                        if(logAction.equalsIgnoreCase("set"))
                        {

                            if(logLevel.equalsIgnoreCase(ObjectPool.LOG_OVERFLOW))
                            {
                                pooledObject.setLogOverflow(Boolean.valueOf(logLevelOnOff));
                            }
                            else if(logLevel.equalsIgnoreCase(ObjectPool.TRACE_MODE))
                            {
                                pooledObject.setTraceMode(Boolean.valueOf(logLevelOnOff));
                            }
                            else
                            {
                                returnString.append("Invalid Log Level. Valid Log Levels are overflow or trace '" );
                                return returnString.toString();
                            }
                        }
                        
                        if(logLevel.equalsIgnoreCase(ObjectPool.LOG_OVERFLOW))
                        {
                            returnString.append("\n").append(objectPool)
                                        .append("\t\t:").append(" The LogOverFlow set to: ")
                                        .append(pooledObject.isLogOverflow());
                        }
                        else if(logLevel.equalsIgnoreCase(ObjectPool.TRACE_MODE))
                        {
                            returnString.append("\n").append(objectPool)
                                        .append("\t\t:").append(" The TraceMode set to: ")
                                        .append(pooledObject.isTraceMode());
                        }
                    }
                }
            }
            
            return returnString.toString();
        }
        catch (Exception ex)
        {
            java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();
            java.io.PrintWriter writer = new java.io.PrintWriter(bytesOut);
            ex.printStackTrace(writer);
            writer.flush();
            return new String(bytesOut.toByteArray());
        }
    }

    
    protected List<String> getMatchedObjectPools(String objectPoolType)
    {
        ArrayList<String> matchedObjectPools = new ArrayList<String>();
        String[] names = objectPoolType.split(",");
        Iterator<String> iter = objectPoolCollection.keySet().iterator();
        for (int i = 0; i < names.length; i++)
        {
            final String nameRegex = names[i].replaceAll("%", ".*");
            while(iter.hasNext())
            {
                String objectPool = iter.next().toString();
                if (objectPool.matches(nameRegex))
                {
                    matchedObjectPools.add(objectPool);
                }
            }
        }
        Collections.sort(matchedObjectPools);
        return matchedObjectPools;
    }

    /**
     * Register the AdminRequest Command.
     */
    private void registerAdminCommand()
    {
        try 
        {
            FoundationFramework ffWork = FoundationFramework.getInstance();
            ffWork.registerCommand(this, "showObjectPool", "adminShowObjectPool",
                                   "Displays the Pool Types for ObjectPool (regex and % supported)",
                                   new String[] { "java.lang.String" },
                                   new String[] { "<objectPoolTypeRegExp>" });
            
           ffWork.registerCommand(this, "objectPoolLogLevel", "adminSetObjectPoolLogging",
                   "Displays or Sets the log levels ObjectPool (regex and % supported)",
                   new String[] { "java.lang.String","java.lang.String","java.lang.String","java.lang.String" },
                   new String[] { "<objectPoolTypeRegExp>", "<overflow|trace>", "<set | get>", " true | false" });
           
           ffWork.registerCommand(this, "undoObjectPooling", "adminUndoObjectPool",
                   "Go back to un-pool mode (regex and % supported), pass the object pool type to undo",
                   new String[] { "java.lang.String"},
                   new String[] { "<objectPoolTypeRegExp>"});
           
           ffWork.registerCommand(this, "addMoreObjectsToPool", "adminAddMoreObjects",
                   "Specify the object pool type (regex and % supported) and the number of objects to be added",
                   new String[] { "java.lang.String", "java.lang.String"},
                   new String[] { "<objectPoolTypeRegExp>", "numberOfObjects to be added"});
        } 
        catch (Exception ex) 
        {
            Log.exception("Exception registering for command callback.", ex);
        }
    }
}

