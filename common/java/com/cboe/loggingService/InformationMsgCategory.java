// Source file: d:/cboe/java/com/cboe/loggingService/InformationMsgCategory.java

package com.cboe.loggingService;

/**
   Information that may not be classified under any of the other categories.
   @author David Houlding
   @version 4.0
 */
class InformationMsgCategory extends MsgCategory {
    static final String uniqueName = "information";
    /**
       The index associated with this category.
     */
    static final int categoryIndex = 0;
    
    /**
       @roseuid 365B19990313
     */
    public String getUniqueName() {
        return uniqueName;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 37A09A9403DE
     */
    public boolean equals(Object category) {
        // If they are the same object then they must be equal.
        if( this == category ) {
            return true;
        }
        
        // If they are not the same object see if they are equal by looking
        // at their contents.
        if( category instanceof InformationMsgCategory ) {
            return true;
        }
        
        return false;
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C439460093
     */
    public int getCategoryIndex() {
        return categoryIndex;
    }
}
