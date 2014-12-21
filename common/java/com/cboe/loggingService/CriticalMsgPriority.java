// Source file: d:/cboe/java/com/cboe/loggingService/CriticalMsgPriority.java

package com.cboe.loggingService;

/**
   Use for messages that should be handled before performing any other system function. These messages typically concern issues posing a threat to the correct functioning or integrity of the system.
   @author David Houlding
   @version 4.0
 */
class CriticalMsgPriority extends MsgPriority {
    static final String uniqueName = "critical";
    static final int priorityIndex = 3;
    
    /**
       @roseuid 365C7E0E0291
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       Returns an integer index such that 0 is the lowest priority and higher priorities have successively higher indices.
       @roseuid 36640F6601FB
     */
    public int getPriorityIndex() {
        return priorityIndex;
    }
    
    /**
       @return True if the priority of this object matches the priority of the given object.
       @roseuid 37A097DF00B6
     */
    public boolean equals(Object otherPriority) {
        // If they are the same object then they must be equal.
        if( this == otherPriority ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( otherPriority instanceof CriticalMsgPriority ) {
            return true;
        }
        
        return false;
    }
}
