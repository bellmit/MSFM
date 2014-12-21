package com.cboe.interfaces.domain;

/**
 * A home for <code>BrokerProcessor</code> instances.  This home was added to
 * give the ability to configure interceptors for the BrokerProcessor.
 *
 * @author John Wickberg
 */

public interface BrokerProcessorHome {

    /**
     * Name of the home for the foundation framework.
     */
    public static final String HOME_NAME = "BrokerProcessorHome";
    
    public static final String HOME_NAME_DEPRECATED = "BrokerProcessorDeprecatedHome";

    /**
     * Creates a processor for a broker.
     *
     * @param broker the broker that will use the processor.
     * @return a processor for the broker
     */
    BrokerProcessor create(Broker broker);

    /**
     * Finds a processor for a broker.
     *
     * @param broker the broker that will use the processor.
     * @return processor for the broker
     */
    /* TODO, remove
    BrokerProcessor find(Broker broker);
    */
}
