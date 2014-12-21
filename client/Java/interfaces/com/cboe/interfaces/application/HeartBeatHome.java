package com.cboe.interfaces.application;

/**
 * This is the common interface for the HeartbeatHome
 * @author Keith A. Korecky
 */
public interface HeartBeatHome
{

    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "HeartBeatHome";


  /**
   * Returns a reference to the market data generator home.
   *
   * @return reference to heartbeat home
   *
   * @author Keith A. Korecky
   */
  public HeartBeat find();

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to heartbeat home
   *
   * @author Keith A. Korecky
   */
  public HeartBeat create();

}
