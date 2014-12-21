// Source file: d:/cboe/java/com/cboe/loggingService/NonRepudiationMsgCategory.java

package com.cboe.loggingService;

/**
   Information to be used for non-repudiation.
   @author David Houlding
   @version 4.0
 */
class NonRepudiationMsgCategory extends MsgCategory {
    static final String uniqueName = "nonRepudiation";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 4;
    
    /**
       @roseuid 365B197E03AB
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A0B2EE0257
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof NonRepudiationMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C43B300340
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
