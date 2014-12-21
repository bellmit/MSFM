/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.internalPresentation.common.formatters
 * User: torresl
 * Date: Jan 9, 2003 11:20:48 AM
 */
package com.cboe.presentation.common.formatters;

import java.util.*;

public class InstrumentorTypes {
    public static final short QUEUE = com.cboe.interfaces.instrumentation.InstrumentorTypes.QUEUE;
    public static final short NETWORK_CONNECTION = com.cboe.interfaces.instrumentation.InstrumentorTypes.NETWORK_CONNECTION;
    public static final short HEAP = com.cboe.interfaces.instrumentation.InstrumentorTypes.HEAP;
    public static final short METHOD = com.cboe.interfaces.instrumentation.InstrumentorTypes.METHOD;
    public static final short THREAD = com.cboe.interfaces.instrumentation.InstrumentorTypes.THREAD;
    public static final short COUNT = com.cboe.interfaces.instrumentation.InstrumentorTypes.COUNT;
    public static final short EVENT = com.cboe.interfaces.instrumentation.InstrumentorTypes.EVENT;
    public static final short JMX = com.cboe.interfaces.instrumentation.InstrumentorTypes.JMX;
    public static final short JSTAT = com.cboe.interfaces.instrumentation.InstrumentorTypes.JSTAT;
    public static final short KEY_VALUE = com.cboe.interfaces.instrumentation.InstrumentorTypes.KEY_VALUE;

    public static final String QUEUE_STRING = "QI";
    public static final String NETWORK_CONNECTION_STRING = "NI";
    public static final String HEAP_STRING = "HI";
    public static final String METHOD_STRING = "MI";
    public static final String THREAD_STRING = "TI";
    public static final String EVENT_STRING = "EI";
    public static final String COUNT_STRING = "CI";
    public static final String JMX_STRING = "XI";
    public static final String JSTAT_STRING = "TI";

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE = "INVALID_TYPE";
    public static final short INVALID_TEXT = -1;
    private static final Map<String, Short> map = new HashMap<String, Short>(5);

    static {
        map.put(QUEUE_STRING, new Short(QUEUE));
        map.put(NETWORK_CONNECTION_STRING, new Short(NETWORK_CONNECTION));
        map.put(HEAP_STRING, new Short(HEAP));
        map.put(METHOD_STRING, new Short(METHOD));
        map.put(THREAD_STRING, new Short(THREAD));
        map.put(EVENT_STRING, new Short(EVENT));
        map.put(COUNT_STRING, new Short(COUNT));
        map.put(COUNT_STRING, new Short(JMX));
        map.put(COUNT_STRING, new Short(JSTAT));
    }

    private InstrumentorTypes() {
    }

    public static short getValue(String formatted) {
        Short value = map.get(formatted);
        if (value != null) {
            return value.shortValue();
        }
        return INVALID_TEXT;
    }

    public static String toString(short instrumentorType) {
        return toString(instrumentorType, TRADERS_FORMAT);
    }

    public static String toString(short instrumentorType, String format) {
        if (format.equals(TRADERS_FORMAT)) {
            switch (instrumentorType) {
                case QUEUE:
                    return QUEUE_STRING;
                case NETWORK_CONNECTION:
                    return NETWORK_CONNECTION_STRING;
                case HEAP:
                    return HEAP_STRING;
                case METHOD:
                    return METHOD_STRING;
                case THREAD:
                    return THREAD_STRING;
                case EVENT:
                    return EVENT_STRING;
                case COUNT:
                    return COUNT_STRING;
                case JMX:
                    return JMX_STRING;
                case JSTAT:
                    return JSTAT_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(instrumentorType).toString();
            }
        } else {
            return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(format).toString();
        }
    }
}
