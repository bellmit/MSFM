package com.cboe.util;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;


/**
 * @(#)CorbaCopier.java
 * Copyright (c) 1999 Chicago Board of Options Exchange. All Rights Reserved.
 * @author C. William Binko <cwbinko@trcinc.com>
 * @version 1.0
 */

/**
 * This class is used to perform a deep copy of a Corba object.
 * Example usage: 
 *      SomeStructClassHolder holder = new SomeStructClassHolder(foo)
 *      CorbaCopier.deepCopy(holder, orb);
 *      SomeStructClass foo2 = holder.value;
 */
public class CorbaCopier
{
  private ORB _orb;
  public CorbaCopier(ORB orb)
    {
      _orb = orb;
    }

  public void deepCopy(Streamable s)
    {
      deepCopy(s, _orb);
    }

  public static void deepCopy(Streamable s, ORB orb)
    {
      OutputStream os = orb.create_output_stream();
      s._write(os);
      s._read(os.create_input_stream());
    }
}
