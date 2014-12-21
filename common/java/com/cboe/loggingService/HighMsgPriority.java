// Source file: d:/cboe/java/com/cboe/loggingService/HighMsgPriority.java

package com.cboe.loggingService;

/**
   Use for messages that require the attention of the system or an external entity as soon as possible.
   @author David Houlding
   @version 4.0
 */
class HighMsgPriority extends MsgPriority {
    static final String uniqueName = "high";
    static final int priorityIndex = 2;
    
    /**
       @roseuid 365C7E390370
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       Returns an integer index such that 0 is the lowest priority and higher priorities have successively higher indices.
       @roseuid 36640FA00385
     */
    public int getPriorityIndex() {
        return priorityIndex;
    }
    
    /**
       @return True if the priority of this object matches the priority of the given object.
       @roseuid 37A0984D0169
     */
    public boolean equals(Object otherPriority) {
        // If they are the same object then they must be equal.
        if( this == otherPriority ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( otherPriority instanceof HighMsgPriority ) {
            return true;
        }
        
        return false;
    }
}
