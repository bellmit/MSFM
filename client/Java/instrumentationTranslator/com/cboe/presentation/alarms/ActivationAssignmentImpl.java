/**
 * 
 */
package com.cboe.presentation.alarms;

import com.cboe.interfaces.instrumentation.alarms.ActivationAssignmentMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

/**
 * An activation assignment maps an activation to an ICS process name which the activation is
 * processed by.
 * 
 * @author morrow
 */
public class ActivationAssignmentImpl extends AbstractMutableBusinessModel<Object> implements ActivationAssignmentMutable
{
	private Integer id;
	private AlarmActivation activation;
	private String logicalName;

	/**
	 * Constructor
	 * 
     * @param activation
     * @param logicalName
     */
    public ActivationAssignmentImpl(AlarmActivation activation, String logicalName)
    {
	    this(0, activation, logicalName);
    }

    /**
     * Constructor
     * 
     * @param id
     * @param activation
     * @param logicalName
     */
    public ActivationAssignmentImpl(Integer id, AlarmActivation activation, String logicalName)
    {
    	this.id = id;
    	this.activation = activation;
    	this.logicalName = logicalName;
    }
    
	/**
	 * Gets the ID of this activation assignment.
	 * 
	 * @return {@link Integer}
	 */
	@Override
	public Integer getId()
	{
		return id;
	}

	/**
	 * Gets the alarm activation.
	 * 
	 * @return {@link AlarmActivation}
	 */
	@Override
	public AlarmActivation getAlarmActivation()
	{
		return activation;
	}

	/**
	 * Gets the logical name.
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getLogicalName()
	{
		return logicalName;
	}

	/**
	 * Sets the logical name.
	 * 
	 * @param name
	 */
	@Override
	public void setLogicalName(String name)
	{
		logicalName = name;
	}

    /**
     * Sets the activation.
     */
    public void setActivation(AlarmActivation newActivation)
    {
        activation = newActivation;
    }

    /**
	 * Returns true if this activation assignment has been persisted.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isSaved()
	{
		return getId() > 0;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
    	String s = "ActivationAssignmentImpl{";
    	s += "Id:" + getId();
    	s += ";Activation:" + getAlarmActivation().getId();
    	s += ";LogicalName:" + getLogicalName();
    	s += "}";
	    return s;
    }
	
}
