// Source file: d:/cboe/java/com/cboe/loggingService/SystemNotificationMsgCategory.java

package com.cboe.loggingService;

/**
   Information to be used for general monitoring of the system.
   @author David Houlding
   @version 4.0
 */
class SystemNotificationMsgCategory extends MsgCategory {
    static final String uniqueName = "systemNotification";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 5;
    
    /**
       @roseuid 365B198B0269
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A0B32C03D3
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof SystemNotificationMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C43B790269
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
