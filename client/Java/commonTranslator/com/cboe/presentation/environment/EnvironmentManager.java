package com.cboe.presentation.environment;

import java.util.Collection;

/**
 * An EnvironmentManager is responsible for providing all relevant attributes required to connect-to/ monitor an
 * "environment". An environment is a collection of machines/processes acting as a model of the CBOE SBT architecture.
 * An environment would include General Cluster (GC) type processes like ProcessWatcher, Business Cluster type processes
 * like TradeServer, Front end processes, CAS, SACAS, and MDCAS processes, and processes acting as distribution nodes
 * (message routers). Most of the data required to monitor a replica of the SBT architecture is related to Talarian (the
 * underlying message transport) and CORBA service names for important services like ExtentMap, and ProcessWatcher.
 */
public interface EnvironmentManager
{


    /**
     * Create an (incomplete) environment This method does not add the environment to the working set (since it isn't
     * complete). See {@link #addEnvironment(EnvironmentProperties)}
     */
    public EnvironmentProperties createEnvironment(String name);

    /**
     * Create an environment with the minimum "complete" info. This method adds the environment to the working set
     */
    public EnvironmentProperties createEnvironment(String name, String prefix, String iorRef);

    /**
     * Retrieve an EnvironmentProperty by name
     */
    public EnvironmentProperties getEnvironment(String name);

    /**
     * Properties are loaded implicitly by the EnvironmentManager.  This method returns a collection of all loaded
     * EnvironmentProperties objects, or an empty Collection, but never null
     */
    public Collection getEnvironments();

    /**
     * Get the default environment.
     *
     * @return Return the default EnvironmetProperties dataset if it has been specified, null otherwise
     */
    public EnvironmentProperties getDefaultEnvironment();

    /**
     * Calling this method causes the EnvironmentManager to change the default PropertyEnvironment.  There can only ever
     * be a single default environment.
     */
    public void setDefaultEnvironment(EnvironmentProperties env);

    /**
     * Get the current environment.   The current environment is initially equal to the default environment, if set.
     * Otherwise, the current environment can be specified during the Connect To process
     *
     * @see com.cboe.infra.presentation.actions.ConnectToAction
     */
    public EnvironmentProperties getCurrentEnvironment();

    /**
     * Set the current environment.  Typically, the current environment is set by the user selecting options from the
     * ConnectionPropertiesPanel
     */
    public void setCurrentEnvironment(EnvironmentProperties newCurrent);


    /**
     * Add an EnvironmentProperties dataset to the current working set. The duration/scope of this change is for the
     * lifetime of the process only! Use this method when you've created an environment using createEnvironment(String).
     * Creating an environment with createEnvironment(String,String,String,String) automatically adds it to the working
     * set. To persist these changes, call {@link #saveAll() saveAll()}.
     */
    public void addEnvironment(EnvironmentProperties env);

    /**
     * Remove an EnvironmentProperties dataset from the current working set. The duration/scope of this change is for
     * the lifetime of the process only! To persist these changes, call {@link #saveAll() saveAll()}.
     */
    public void removeEnvironment(EnvironmentProperties env);

    /**
     * Environments are loaded implicitly, but must be saved/persisted explicitly
     */
    public void saveAll();

    /**
     * 
     */
    public boolean isAutoConnect();
}