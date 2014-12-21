// Source file: d:/cboe/java/com/cboe/loggingService/MediumMsgPriority.java

package com.cboe.loggingService;

/**
   Use for messages that are of interest and should be handled on an as time permits basis.
   @author David Houlding
   @version 4.0
 */
class MediumMsgPriority extends MsgPriority {
    static final String uniqueName = "medium";
    static final int priorityIndex = 1;
    
    /**
       @roseuid 365C7E5D0385
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       Returns an integer index such that 0 is the lowest priority and higher priorities have successively higher indices.
       @roseuid 36640FC30245
     */
    public int getPriorityIndex() {
        return priorityIndex;
    }
    
    /**
       @return True if the priority of this object matches the priority of the given object.
       @roseuid 37A098870389
     */
    public boolean equals(Object otherPriority) {
        // If they are the same object then they must be equal.
        if( this == otherPriority ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( otherPriority instanceof MediumMsgPriority ) {
            return true;
        }
        
        return false;
    }
}
