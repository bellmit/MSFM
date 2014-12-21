// Source file: d:/cboe/java/com/cboe/loggingService/LowMsgPriority.java

package com.cboe.loggingService;

/**
   Use for messages that are for informational purposes only.  No action on the part of the system or an external entity is needed.
   @author David Houlding
   @version 4.0
 */
class LowMsgPriority extends MsgPriority {
    static final String uniqueName = "low";
    static final int priorityIndex = 0;
    
    /**
       @roseuid 365C7E8E012D
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       Returns an integer index such that 0 is the lowest priority and higher priorities have successively higher indices.
       @roseuid 36640FE102CA
     */
    public int getPriorityIndex() {
        return priorityIndex;
    }
    
    /**
       @return True if the priority of this object matches the priority of the given object.
       @roseuid 37A098C10328
     */
    public boolean equals(Object otherPriority) {
        // If they are the same object then they must be equal.
        if( this == otherPriority ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( otherPriority instanceof LowMsgPriority ) {
            return true;
        }
        
        return false;
    }
}
