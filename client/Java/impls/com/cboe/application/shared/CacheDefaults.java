package com.cboe.application.shared;

/**
  * The resting place for the system cache default sizes.  The 
  * values here should be overridden by the configuration file
  * for the individual service.  
  *!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  *! There should NEVER be a constant here that is not configurable!
  *!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  * @author Tom Lynch
  */
  public interface CacheDefaults {
        public static final int ORDER_CACHE_SIZE = 500;
        

  }
