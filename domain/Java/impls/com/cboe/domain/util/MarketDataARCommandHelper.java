package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
//
// -----------------------------------------------------------------------------------
// Source file: MarkaetDataARCommandHelper.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.

// -----------------------------------------------------------------------------------
public class MarketDataARCommandHelper {
    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String STATUS = "STATUS";    

    public static class PublishEOPData{
        private static boolean publishEOP = true;
        boolean isPublishEnabled(){
            return isPublishEOP();
        }

        public static boolean isPublishEOP() {
            return publishEOP;
        }

        public static void setPublishEOP(boolean enable) {
            publishEOP = enable;
            String s = enable ? "ENABLED" : "DISABLED";
            Log.notification("EOP / EOS Publishing "+s);
        }
    }

    public static class PublishLTLSData{
        private static boolean publishLTLS = true;

        public static boolean isPublishLTLS() {
            return publishLTLS;
        }

        public static void setPublishLTLS(boolean enable) {
            publishLTLS = enable;
            String s = enable?"ENABLED":"DISABLED";
            Log.notification("LTLS Publishing "+s);
        }
    }
}
