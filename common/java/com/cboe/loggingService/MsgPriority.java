// Source file: d:/cboe/java/com/cboe/loggingService/MsgPriority.java

package com.cboe.loggingService;

import java.io.Serializable;


/**
   Defines the valid priority values of log messages.
   @author David Houlding
   @version 4.0
 */
public abstract class MsgPriority implements Serializable {
    /**
       Use for messages that should be handled before performing any other system function. These messages typically concern issues posing a threat to the correct functioning or integrity of the system.
     */
    public static final MsgPriority critical = new CriticalMsgPriority();
    public static final int _critical = CriticalMsgPriority.priorityIndex;
    /**
       Use for messages that require the attention of the system or an external entity as soon as possible.
     */
    public static final MsgPriority high = new HighMsgPriority();
    public static final int _high = HighMsgPriority.priorityIndex;
    /**
       Use for messages that are of interest and should be handled on an as time permits basis.
     */
    public static final MsgPriority medium = new MediumMsgPriority();
    public static final int _medium = MediumMsgPriority.priorityIndex;
    /**
       Use for messages that are for informational purposes only.  No action on the part of the system or an external entity is needed.
     */
    public static final MsgPriority low = new LowMsgPriority();
    public static final int _low = LowMsgPriority.priorityIndex;    
    /**
       The serialization version of this class.
     */
    static final long serialVersionUID = -6276922862430327835L;
    
    /**
       @return True if this message priority instance is of type "critical".
       @roseuid 3659CE5F0135
     */
    public boolean isCritical() {
        return this instanceof CriticalMsgPriority;
    }
    
    /**
       @return True if this message priority instance is of type "high".
       @roseuid 3653384302C8
     */
    public boolean isHigh() {
        return this instanceof HighMsgPriority;
    }
    
    /**
       @return True if this message priority instance is of type "medium".
       @roseuid 3653384801B6
     */
    public boolean isMedium() {
        return this instanceof MediumMsgPriority;
    }
    
    /**
       @return True if this message priority instance is of type "low".
       @roseuid 3653384C02F3
     */
    public boolean isLow() {
        return this instanceof LowMsgPriority;
    }
    
    /**
       @return The name of this message priority which is unique across all message priorities.
       @roseuid 365C7DF6011A
     */
    public abstract String getUniqueName();
    
    /**
       Return the message priority associated with the given name.
       @exception InvalidMessagePriorityException The named priority does not exist.
       @roseuid 365C7F6A01DE
     */
    public static MsgPriority fromUniqueName(String name) throws InvalidMessagePriorityException {
        MsgPriority priority = null;
        if( name.equalsIgnoreCase( LowMsgPriority.uniqueName ) ) {
            priority = low;
        }
        else if( name.equalsIgnoreCase( MediumMsgPriority.uniqueName ) ) {
            priority = medium;
        }
        else if( name.equalsIgnoreCase( HighMsgPriority.uniqueName ) ) {
            priority = high;
        }
        else if( name.equalsIgnoreCase( CriticalMsgPriority.uniqueName ) ) {
            priority = critical;
        }
        else {
            throw new InvalidMessagePriorityException( "Message priority name \"" + name + "\" does not exist." );
        }

        return priority;
    }
    
    /**
       @return An array of strings representing the complete set of unique names for valid message priorities.
       @roseuid 365C829C037E
     */
    public static String[] getAllUniqueNames() {
        String[] output = {
                LowMsgPriority.uniqueName,
                MediumMsgPriority.uniqueName,
                HighMsgPriority.uniqueName,
                CriticalMsgPriority.uniqueName };

        return output;
    }
    
    /**
       @return True if the this priority is greater than or equal to the given priority.
       @roseuid 36640E250037
     */
    public boolean greaterThanOrEqualTo(MsgPriority priority) {
        return getPriorityIndex() >= priority.getPriorityIndex();
    }
    
    /**
    @return True if the this priority is greater than or equal to the given priority.
    @roseuid 36640E250037
  */
    public boolean lessThanOrEqualTo(MsgPriority priority) {
        return getPriorityIndex() <= priority.getPriorityIndex();
    }
 
    /**
       @return An integer index such that 0 is the lowest priority and higher priorities have successively higher indices.
       @roseuid 36640F0C02CE
     */
    public abstract int getPriorityIndex();
    
    /**
       Create a new instance of a message priority.
       @roseuid 37614FC5007C
     */
    protected MsgPriority() {
    }
    
    /**
       @return True if the priority of this object matches the priority of the given object.
       @roseuid 37A0976301E4
     */
    public abstract boolean equals(Object otherPriority);
    
    /**
       @return The priority object corresponding to the given priority index.
       @roseuid 37C4376E0206
     */
    static MsgPriority fromPriorityIndex(int priorityIndex) throws InvalidMessagePriorityException {
        MsgPriority returnedPriority;
        switch( priorityIndex ) {
            case CriticalMsgPriority.priorityIndex : {
                returnedPriority = critical;
                break;
            }
            case HighMsgPriority.priorityIndex : {
                returnedPriority = high;
                break;
            }
            case MediumMsgPriority.priorityIndex : {
                returnedPriority = medium;
                break;
            }
            case LowMsgPriority.priorityIndex : {
                returnedPriority = low;
                break;
            }
            default : {
                throw new InvalidMessagePriorityException( "Priority index does not correspond to a valid priority." );
            }
        }
        
        return returnedPriority;
    }
}
