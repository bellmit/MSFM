// Source file: d:/cboe/java/com/cboe/loggingService/SystemAlarmMsgCategory.java

package com.cboe.loggingService;

/**
   Information to be used for warning system administration of an event or condition threatening the integrity or functionality of the system.
   @author David Houlding
   @version 4.0
 */
class SystemAlarmMsgCategory extends MsgCategory {
    static final String uniqueName = "systemAlarm";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 3;
    
    /**
       @roseuid 365B197201AF
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A0B24B0072
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof SystemAlarmMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C43AD10235
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
