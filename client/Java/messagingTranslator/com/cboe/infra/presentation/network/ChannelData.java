//
// -----------------------------------------------------------------------------------
// Source file: ChannelData.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.utils.monitoringService.EventChannel;
import com.cboe.utils.monitoringService.ECInterface;

import java.util.*;

/**
 *  A static copy of an EventChannel object.  These objects are initialized
 * with "live" EventChannel objects, from which they copy their data, but after
 * initialization, no connection exists between the ChannelData object and its original
 * source, creating in effect, a "snap shot" of the EventChannel object.
 */
public class ChannelData
{
    public static final String INCLUDE_FILTERS = "inclFilters";
    public static final String EXCLUDE_FILTERS = "exclFilters";
    String name;
    Map interfaces = new HashMap();

    /**
     * Create a ChannelData and initialize its data from the EventChannel source.
     * After creation, the ChannelData object is independent of the source; that is
     * if an interface is added or removed from the source, it has no effect on the ChannelData
     * object.
     * @param source
     */
    public ChannelData(EventChannel source)
    {
        name = source.getEcName();
        Collection ifs = source.getECInterfaces();
        for (Iterator iterator = ifs.iterator(); iterator.hasNext();) {
            ECInterface ecInterface = (ECInterface) iterator.next();
            HashMap filters = new HashMap();
            filters.put( INCLUDE_FILTERS, ecInterface.getInclusionFilters() );
            filters.put( EXCLUDE_FILTERS, ecInterface.getExclusionFilters() );
            interfaces.put( ecInterface.getInterfaceName(), filters );
        }
    }

    /**
     * The collection of interfaces for this channel.
     * Each interface has a name that serves as a key in this map.
     * The value to that key is another Map.  That map should have two
     * keys in it, INCLUDE_FILTERS and EXCLUDE_FILTERS, each of which
     * have String[] with the filter names.
     * Note that the EventChannel filters concept is deprecated, and that
     * CmdConsUtil should be used to get filter information in general, since
     * it includes historical filters, and not just the filters added after the node
     * object was created.
     * @return Map  A Map with interface names as the keys, and Maps of inclusion, exclusion
     * filters as the values.
     */
    public Map getInterfaces() {
        return interfaces;
    }

    /**
     * Return the name of this channel
     * @return String the name.
     */
    public String getName() {
        return name;
    }
}
