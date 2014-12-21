package com.cboe.infrastructureServices.foundationFramework;

/**
 * The BObject is the basic class extended by all Business objects which provide services of some kind.
 * For a BObject to be valid, it must be added to a container after creation.
 * @see com.cboe.infrastructureServices.foundationFramework.BOHome#addToContainer
 * @author Dave Hoag
 * @version 2.1
 */
public abstract class BObject
{
	private String name;
	BOHome boHome;
	/**
	 * Check with my home, which will then check with the container.
	 * If no component name is specified at the home or component level, get the component name
	 * from the FoundationFramework.
	 * @return String The component name to use for logging.
	 */
	public String getComponentName()
	{
		String val= null;
		if(boHome != null)
		{
			val = boHome.getComponentName();		
		}
		return val;
	
	}
	/**
	 */
	protected void associateHome(BOHome home)
	{
		//default to do nothing
	}
	/**
	 * Homes may invoke this method after creating a BObject.
	 * Any initialization code belongs in overridden implementations of this method.
	 * To support inheritance of initialization, the overridden method should include a call
	 * super.create(name)
	 * @param aName String to use as a the BObject name.
	 */
	public void create(String aName)
	{
		setName( aName );
	} 
	/**
	 * Get the home from which this object was created.
	 * @return BOHome The parent home.
	 */
	public BOHome getBOHome()
	{
		return boHome;
	}
	/**
	 * set the home from which this object was created.
	 * Used in the BOHome.addToContainer method.
	 * @param home BOHome that created this BObject.
	 */
	public void setBOHome(BOHome home)
	{
		boHome = home;
	}
	/**
	 * The name of this BObject.
	 * @return String to use as a the BObject name.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * The name of this BObject.
	 * @param aName String to use as a the BObject name.
	 */
	public void setName( String aName )
	{
		name = aName;
	}
}
