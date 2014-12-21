package com.cboe.infrastructureUtility.configuration;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class ConnectionIdentityTests
{

	@Test
	public void shouldHashToSameValueByString()
	{
		Set<ConnectionIdentity> set = new HashSet<ConnectionIdentity>();
		ConnectionIdentity id1 = new TestConnectionIdentity("one");
		ConnectionIdentity id2 = new TestConnectionIdentity("one");

		set.add(id1);
		set.add(id2);

		Assert.assertTrue(set.size() == 1);
	}

	@Test
	public void shouldHaveEqualityByString()
	{
		ConnectionIdentity id1 = new TestConnectionIdentity("one");
		ConnectionIdentity id2 = new TestConnectionIdentity("one");

		Assert.assertEquals(id1, id2);
	}

	/**
	 * Required because it's abstract
	 * 
	 * @author eccles
	 * 
	 */
	public static final class TestConnectionIdentity extends ConnectionIdentity
	{

		protected TestConnectionIdentity(String name)
		{
			super(name);
		}

	}
}
