package com.cboe.interfaces.application;

/**
 * This is the common interface for the IROMakerHome
 * @author Jimmy Wang
 */
public interface IORMakerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "IORMakerHome";

  /**
   * Creates an instance of the market data generator home.
   *
   * @return reference to IORMaker
   *
   * @author Jimmy Wang
   */
  public IORMaker create();
}
