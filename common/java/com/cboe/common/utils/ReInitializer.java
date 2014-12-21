package com.cboe.common.utils;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import com.cboe.common.log.Logger;

/* This class is a singleton which keeps track of objects that can be reinitialized and
	calls their reinitialize method as required.

	The methods in this class are not expected to get called frequently, so, the simplest 
	approach will be used rather than trying to optimize for high throughput.

*/
public class ReInitializer
{

	static private final String myName = "ReInitializer";
	static private ReInitializer reInitializer;
	static public synchronized ReInitializer getReInitializer()
	{
		if ( null == reInitializer )
		{
			reInitializer = new ReInitializer();
		}
		return reInitializer;
	}
	private ReInitializer()
	{
		reInitSet = new HashMap<ReInitializable, Info>();
	}

	synchronized public void register( ReInitializable r, String group, String name )
	{
		/* Check to see if this object is already registered */
		Info info = reInitSet.get( r );
		if ( null != info )
		{
			if ( group.equals( info.group ) && name.equals( info.name ) )
			{
				/* The same object registered the same way multiple times */
				return;
			}
			else
			{
				Logger.sysNotify( myName + " object registered as (" + info.group + ", " + info.name + 
				 ") is now re-registering as (" + group + ", " + name + "). The original registration will be replaced." );
			}
		}
		info = new Info( group, name );
		reInitSet.put( r, info );
	}
	synchronized public boolean unRegister( ReInitializable r )
	{
		/* Return false if we do not find this object in our map */
		return ( null == reInitSet.remove( r ))?false:true;
	}
	private void reInitialize( ReInitializable r, Info i )
	{
		Logger.sysNotify( myName + " calling reInitialize on (" + i.group +
		", " + i.name + ")." );
		try
		{
			r.reInitialize();
		}
		catch ( Throwable t )
		{
			Logger.sysAlarm( myName + " error calling reInitialize on (" + i.group +
			", " + i.name + "):" + t, t );
		}
	}
	synchronized public short reInitialize()
	{
		Iterator<ReInitializable> i = reInitSet.keySet().iterator();
		short count = 0;
		while( i.hasNext() )
		{
			ReInitializable r = i.next();
			Info info = reInitSet.get( r );
			reInitialize( r, info );
			count++;
		}
		return count;
	}
	synchronized public short reInitialize( String group )
	{
		Iterator<ReInitializable> i = reInitSet.keySet().iterator();
		short count = 0;
		while( i.hasNext() )
		{
			ReInitializable r = i.next();
			Info info = reInitSet.get( r );
			if ( group.equals( info.group ) )
			{
			   reInitialize( r, info );
				count++;
			}
		}
		return count;
	}
	synchronized public String[] getGroups()
	{
		Iterator<ReInitializable> i = reInitSet.keySet().iterator();
		Set<String> groupSet = new HashSet<String>();
		while( i.hasNext() )
		{
			groupSet.add( reInitSet.get( i.next() ).group );
		}
		return groupSet.toArray( new String[ 0 ] );
	}

	private class Info
	{
		String group;
		String name;
		Info( String g, String n )
		{
			group = g;
			name = n;
		}
	};
	private HashMap<ReInitializable, Info> reInitSet;
}
