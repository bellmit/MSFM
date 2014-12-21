package com.cboe.common.utils;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * A test to make sure the group naming rules with the delimeter are respected, as well as equals
 * operator on settings
 */
@Ignore("Executing as part of battery suite")
public class WhenAccessingGroups
{
	private final Settings settings;

	public WhenAccessingGroups(Settings settings)
	{
		this.settings = settings;
	}

	@Test
	public void groupMethodShouldEqualDelimiterConstructedGroupName()
	{
		Settings groupA = settings.group("one").group("two").group("three");
		Settings groupB = settings.group("one" + Settings.DELIMETER + "two" + Settings.DELIMETER + "three");

		assertThat(groupA, equalTo(groupB));
	}

	@Test
	public void sameBuildGroupsShouldBeEquivalent()
	{
		Settings groupA = settings.group("one").group("two").group("three");
		Settings groupB = settings.group("one").group("two").group("three");

		assertThat(groupA, equalTo(groupB));
	}

	@Test
	public void differentBuildGroupsShouldNotEqual()
	{
		Settings groupA = settings.group("one").group("two").group("three");
		Settings groupB = settings.group("one").group("two").group("MOOOO");

		assertThat(groupA, not(groupB));
	}

	@Test
	public void sameGroupsAffectSameVariables() throws SettingsException
	{
		Settings groupA = settings.group("one").group("two").group("three");
		Settings groupB = settings.group("one").group("two").group("three");

		int expected = 12345;
		groupA.name("name").set(12345);

		int result = groupB.name("name").requireInteger();

		assertThat(result, is(expected));
	}

}
