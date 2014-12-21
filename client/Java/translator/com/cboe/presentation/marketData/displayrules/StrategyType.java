/**
 * 
 */
package com.cboe.presentation.marketData.displayrules;

/**
 * Enum that lists the different strategies that currently have some special 
 * rules to display some values.
 * 
 * @author Eric Maheo
 * @author Shawn Khosravani - moved from \gui\Java\commonBusiness\com\cboe\presentation\marketDisplay\displayrules
 */
public enum StrategyType
{
	/**
	 * Time strategy.
	 * The enum value is {@value}.
	 */
	TIME("Time"),
	/**
	 * Vertical strategy.
	 * The enum value is {@value}.
	 */
	VERTICAL("Vertical"),
	/**
	 * Butterfly strategy.
	 * The enum value is {@value}.
	 */
	BUTTERFLY("Butterfly"),
	/**
	 * Spread with 3 legs.
	 * Lack of consistency in the naming of this strategy... most of them strart with an uppercase but not this one!
	 * The enum value is {@value}.
	 */
	SPREAD_WITH_3_LEGS("spread with 3 legs"),
	/**
	 * Unknown strategy is a catch-all recipient for non listed strategies.
	 * The enum value is {@value}.
	 */
	UNKNOWN("Unknown");
	
	private String value;
	
	StrategyType(String value){
		this.value = value;
	}
	/**
	 * Get the string of this enum.
	 */
	public String toString(){
		return value;
	}
}
