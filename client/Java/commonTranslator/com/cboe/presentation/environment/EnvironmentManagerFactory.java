package com.cboe.presentation.environment;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

/**
 * This class is responsible for providing a Singleton EnvironmentManager.  The default environment manager is
 * com.cboe.infra.presentation.AbstractEnvironmentManager, but custom EnvironmentManagers can be specified by setting
 * either the System or application property "ENVIRONMENT_MANAGER_IMPL".  This factory checks for a non-null value for
 * the System property, and failing that, then checks for a non-null value for the application property.  If both of
 * those checks fail, or if there are errors finding/instantiating the custom EnvironmentManagers, this class falls back
 * to the default environment manager.
 */
public class EnvironmentManagerFactory
{
    private static final String ENVIRONMENT_MANAGER_IMPL = "ENVIRONMENT_MANAGER_IMPL";
    private static final String PROPERTIES_SECTION_NAME = "Defaults";

    private static EnvironmentManager manager = null;

    /**
     * Obtain the single EnvironmentManager for this process.
     */
    public static EnvironmentManager find()
    {
        if (manager != null)
        {
            return manager;
        }
        else
        {
            throw new IllegalStateException("EnvironmentManager: Create has not been called yet.");
        }

    }

    public static void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.environment.EnvironmentManager.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                manager = (EnvironmentManager) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("EnvironmentManagerFactory: Does not support interface com.cboe.presentation.environment.EnvironmentManager. className = " + theClass);
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public static void create()
    {
        String managerImpl = System.getProperty(ENVIRONMENT_MANAGER_IMPL);

        if (managerImpl == null)
        {
            managerImpl = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, ENVIRONMENT_MANAGER_IMPL);
        }

        create(managerImpl);
    }
}