// Source file: d:/cboe/java/com/cboe/loggingService/AuditMsgCategory.java

package com.cboe.loggingService;

/**
   Information to be used for auditing.
   @author David Houlding
   @version 4.0
 */
class AuditMsgCategory extends MsgCategory {
    static final String uniqueName = "audit";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 1;
    
    /**
       @roseuid 365B1954035A
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A0B1C2036E
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof AuditMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C43A0102B8
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
