// Source file: d:/cboe/java/com/cboe/loggingService/DebugMsgCategory.java

package com.cboe.loggingService;

/**
   Information to be used for debugging.
   @author David Houlding
   @version 4.0
 */
class DebugMsgCategory extends MsgCategory {
    static final String uniqueName = "debug";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 2;
    
    /**
       @roseuid 365B196202CE
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A0B20C03CF
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof DebugMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C43A7303DF
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
