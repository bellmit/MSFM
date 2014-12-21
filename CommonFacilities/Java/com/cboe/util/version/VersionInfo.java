//
// -----------------------------------------------------------------------------------
// Source file: VersionInfo.java
//
// PACKAGE: com.cboe.util.version
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.util.version;

import java.net.URL;
import java.util.*;

/*
 * Provides version information about the specific runtime.
 */
public class VersionInfo
{
    public static final String[] VM_VERSION_PROPERTY_NAMES = {"java.compiler",
                                                              "java.home",
                                                              "java.specification.name",
                                                              "java.specification.vendor",
                                                              "java.specification.version",
                                                              "java.vendor",
                                                              "java.version",
                                                              "java.vm.info",
                                                              "java.vm.name",
                                                              "java.vm.specification.name",
                                                              "java.vm.specification.vendor",
                                                              "java.vm.specification.version",
                                                              "java.vm.version"};

    public static final String VERSION_EXTENSION = ".version";
    public static final String VERSION_PATH = "version";

    public static final String IMPLEMENTATION_VERSION_PROPERTY = "Implementation-Version";
    public static final String IMPLEMENTATION_TITLE_PROPERTY = "Implementation-Title";
    public static final String IMPLEMENTATION_VENDOR_PROPERTY = "Implementation-Vendor";
    public static final String SPECIFICATION_VERSION_PROPERTY = "Specification-Version";
    public static final String SPECIFICATION_TITLE_PROPERTY = "Specification-Title";
    public static final String SPECIFICATION_VENDOR_PROPERTY = "Specification-Vendor";

    private static final String BOOT_CLASSPATH_PROPERTY_NAME = "sun.boot.class.path";
    private static final String CLASSPATH_PROPERTY_NAME = "java.class.path";
    private static final String CLASSPATH_SEPARATOR = "path.separator";
    private static final String END_OF_PATH_SEPARATOR = "file.separator";

    private static String[] versionResourceNames = null;

    /**
     * Default Constructor. This class need not be instantiated
     */
    private VersionInfo()
    {}

    /**
     * Gets the version information from the version file for a specific jar.
     * @param jarName to get version information from. Can be fully qualified or only the jar name with
     * or without ".jar" extension.
     * @return SortedMap that contains the name=value pairs from the version information.
     * Each name and value will be of the type String. The SortedMap will be unmodifiable.
     * @exception NoSuchRuntimeJarException if the jarName does not exist in the runtime.
     * @exception NoSuchVersionResourceException if the jarName does not contain a resource with version information.
     */
    public static SortedMap getVersionInfo(String jarName) throws NoSuchRuntimeJarException, NoSuchVersionResourceException
    {
        SortedMap versionProperties = new TreeMap();
        String resourceName = jarToResourceName(jarName);

        if(Arrays.binarySearch(getRuntimeVersionResourceNames(), resourceName) < 0)
        {
            throw new NoSuchRuntimeJarException("The JAR Name:" + resourceName + " is not listed as a runtime JAR.", jarName);
        }

        StringBuilder qualifiedResourceName = new StringBuilder(30);
        qualifiedResourceName.append('/');
        qualifiedResourceName.append(VERSION_PATH);
        qualifiedResourceName.append('/');
        qualifiedResourceName.append(resourceName);
        qualifiedResourceName.append(VERSION_EXTENSION);

        try
        {
            URL resourceURL = VersionInfo.class.getResource(qualifiedResourceName.toString());

            if(resourceURL != null)
            {
                Properties properties = new Properties();
                properties.load(resourceURL.openStream());
                versionProperties.putAll(properties);
            }
            else
            {
                throw new NoSuchVersionResourceException("Version resource could not be found for:" + qualifiedResourceName.toString(), jarName);
            }
        }
        catch(Exception e)
        {
            throw new NoSuchVersionResourceException("Version resource could not be found for:" + qualifiedResourceName.toString(), jarName);
        }

        return Collections.unmodifiableSortedMap(versionProperties);
    }
    
    /**
     * Gets the version information from the version file for a specific jar, but doesn't check to see if the jar
     * exists in the environment's ClassPath
     * @param jarName to get version information from. Can be fully qualified or only the jar name with
     * or without ".jar" extension.
     * @return SortedMap that contains the name=value pairs from the version information.
     * Each name and value will be of the type String. The SortedMap will be unmodifiable.
     * @exception NoSuchVersionResourceException if the jarName does not contain a resource with version information.
     */
    public static SortedMap getJarVersionInfo(String jarName) throws NoSuchVersionResourceException
    {
        SortedMap versionProperties = new TreeMap();
        String resourceName = jarToResourceName(jarName);

        StringBuilder qualifiedResourceName = new StringBuilder(30);
        qualifiedResourceName.append('/');
        qualifiedResourceName.append(VERSION_PATH);
        qualifiedResourceName.append('/');
        qualifiedResourceName.append(resourceName);
        qualifiedResourceName.append(VERSION_EXTENSION);

        try
        {
            URL resourceURL = VersionInfo.class.getResource(qualifiedResourceName.toString());

            if(resourceURL != null)
            {
                Properties properties = new Properties();
                properties.load(resourceURL.openStream());
                versionProperties.putAll(properties);
            }
            else
            {
                throw new NoSuchVersionResourceException("Version resource could not be found for:" + qualifiedResourceName.toString(), jarName);
            }
        }
        catch(Exception e)
        {
            throw new NoSuchVersionResourceException("Version resource could not be found for:" + qualifiedResourceName.toString(), jarName);
        }

        return Collections.unmodifiableSortedMap(versionProperties);
    }
    

    /**
     * Gets the version information from the version files for all jars in this runtime.
     * @return SortedMap where each key is the jar name and each value is a SortedMap.
     * (see getVersionInfo(String):SortedMap) The collection will be unmodifiable.
     * It will not contain the VM version info
     */
    public static SortedMap getAllVersionInfo()
    {
        SortedMap versionInfo = new TreeMap();

        String[] jarNames = getRuntimeVersionResourceNames();

        for(int i = 0; i < jarNames.length; i++)
        {
            try
            {
                versionInfo.put(jarNames[i], getVersionInfo(jarNames[i]));
            }
            catch(NoSuchVersionResourceException e)
            {}
            catch(NoSuchRuntimeJarException e)
            {}
        }

        return Collections.unmodifiableSortedMap(versionInfo);
    }

    /**
     * Get VM version information
     * @return SortedMap that contains the name=value pairs from the version information.
     * Each name and value will be of the type String. The SortedMap will be unmodifiable.
     */
    public static SortedMap getVMVersionInfo()
    {
        SortedMap versionProperties = new TreeMap();

        for(int i = 0; i < VM_VERSION_PROPERTY_NAMES.length; i++)
        {
            versionProperties.put(VM_VERSION_PROPERTY_NAMES[i], System.getProperty(VM_VERSION_PROPERTY_NAMES[i]));
        }

        return Collections.unmodifiableSortedMap(versionProperties);
    }

    /**
     * Gets all the version resource names for all jars that make up this runtime.
     * Each element can be used as a resource name to get version information.
     * @return Each version resource name for all jars that makes up this runtime as an element in the String array.
     */
    public static String[] getRuntimeVersionResourceNames()
    {
        if(versionResourceNames == null)
        {
            ArrayList jarNamesList = new ArrayList(5);

            StringTokenizer classPathTokens = new StringTokenizer(System.getProperty(CLASSPATH_PROPERTY_NAME), System.getProperty(CLASSPATH_SEPARATOR), false);
            while(classPathTokens.hasMoreTokens())
            {
                String nextToken = classPathTokens.nextToken();

                if(isNameAJar(nextToken))
                {
                    String resourceName = jarToResourceName(nextToken);

                    if(!jarNamesList.contains(resourceName))
                    {
                        jarNamesList.add(resourceName);
                    }
                }
            }

            StringTokenizer bootClassPathTokens = new StringTokenizer(System.getProperty(BOOT_CLASSPATH_PROPERTY_NAME), System.getProperty(CLASSPATH_SEPARATOR), false);
            while(bootClassPathTokens.hasMoreTokens())
            {
                String nextToken = bootClassPathTokens.nextToken();

                if(isNameAJar(nextToken))
                {
                    String resourceName = jarToResourceName(nextToken);

                    if(!jarNamesList.contains(resourceName))
                    {
                        jarNamesList.add(resourceName);
                    }
                }
            }

            if(jarNamesList.size() > 0)
            {
                Collections.sort(jarNamesList);
                versionResourceNames = (String[])jarNamesList.toArray(new String[0]);
            }
        }

        return versionResourceNames;
    }

    /**
     * Determines if passed String represents a recognizable jar name format
     * @param jarName to check
     * @return True if format follows jar names, false otherwise.
     */
    private static boolean isNameAJar(String jarName)
    {
        boolean isJarName = false;

        String strippedJarName = jarName.trim();
        if(strippedJarName.endsWith(".jar") && strippedJarName.length() > 5)
        {
            isJarName = true;
        }

        return isJarName;
    }

    /**
     * Converts a jarName to the associated resource name by stripping the ".jar" and the path information.
     * @param jarName to convert
     * @return qualified jar name stripped to just the associated resource name
     */
    private static String jarToResourceName(String jarName)
    {
        String resourceName = jarName.trim();

        int endOfPathInfo = resourceName.lastIndexOf(System.getProperty(END_OF_PATH_SEPARATOR));
        if(endOfPathInfo > -1)
        {
            resourceName = resourceName.substring(endOfPathInfo + 1);
        }

        int locationOfJarExt = resourceName.lastIndexOf(".jar");
        if(locationOfJarExt > 0)
        {
            resourceName = resourceName.substring(0, locationOfJarExt);
        }

        return resourceName;
    }

    /**
     * Test method
     */
    public static void main(String[] args)
    {
        StringBuilder buffer = new StringBuilder(1000);

        SortedMap map = getVMVersionInfo();
        for(Iterator i = map.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            buffer.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        System.out.println(buffer.toString());

        System.out.println("--------------------- JAR Names ------------------------");

        String[] jarNames = getRuntimeVersionResourceNames();
        for(int i = 0; i < jarNames.length; i++)
        {
            System.out.println(jarNames[i]);
        }

        System.out.println();
        System.out.println("--------------------- gui_common Version Info -----------");
        buffer = new StringBuilder(1000);
        try
        {
            map = getVersionInfo("gui_common");
            for(Iterator i = map.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();

                buffer.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
            }

            System.out.println(buffer.toString());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        System.out.println("--------------------- All Version Info ------------------");
        buffer = new StringBuilder(1000);
        map = getAllVersionInfo();
        for(Iterator i = map.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();

            buffer.append("-- For: ").append(entry.getKey()).append('\n');
            SortedMap map2 = (SortedMap)entry.getValue();
            for(Iterator i2 = map2.entrySet().iterator(); i2.hasNext();)
            {
                Map.Entry entry2 = (Map.Entry) i2.next();

                buffer.append(entry2.getKey()).append(": ").append(entry2.getValue()).append('\n');
            }

            buffer.append('\n');
        }

        System.out.println(buffer.toString());

        System.exit(0);
    }
}
