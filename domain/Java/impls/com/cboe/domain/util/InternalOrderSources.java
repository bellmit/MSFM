package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.SourcesOperations;


public interface InternalOrderSources extends SourcesOperations {

   public static final char STOCKFIXLINKAGE = (char) 'F';
   public static final char MANUAL = (char) 'M';
   public static final char PAR = (char) 'P';
   public static final char MMHH = (char) 'H';


}