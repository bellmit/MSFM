// Source file: d:/cboe/java/com/cboe/loggingService/MsgCategory.java

package com.cboe.loggingService;

import java.io.Serializable;


/**
   Defines the valid values for the categories that log messages may be classified under.
   @author David Houlding
   @version 4.0
 */
public abstract class MsgCategory implements Serializable {
    /**
       Information to be used for debugging.
     */
    public static final MsgCategory debug = new DebugMsgCategory();
    public static final int _debug = DebugMsgCategory.categoryIndex;
    /**
       Information to be used for auditing.
     */
    public static final MsgCategory audit = new AuditMsgCategory();
    public static final int _audit = AuditMsgCategory.categoryIndex;    
    /**
       Information to be used for non-repudiation.
     */
    public static final MsgCategory nonRepudiation = new NonRepudiationMsgCategory();
    public static final int _nonRepudiation = NonRepudiationMsgCategory.categoryIndex;     
    /**
       Information to be used for general monitoring of the system.
     */
    public static final MsgCategory systemNotification = new SystemNotificationMsgCategory();
    public static final int _systemNotification = SystemNotificationMsgCategory.categoryIndex; 
    /**
       Information to be used for warning system administration of an event or condition threatening the integrity or functionality of the system.
     */
    public static final MsgCategory systemAlarm = new SystemAlarmMsgCategory();
    public static final int _systemAlarm = SystemAlarmMsgCategory.categoryIndex; 
    /**
       Information that may not be classified under any of the other categories.
     */
    public static final MsgCategory information = new InformationMsgCategory();
    public static final int _information = InformationMsgCategory.categoryIndex;     
    /**
       The serialization version of this class.
     */
    static final long serialVersionUID = 5464317936302780732L;
    
    /**
       @return True if this message category is of "debug" type.
       @roseuid 3650A34301C9
     */
    public boolean isDebug() {
        return this instanceof DebugMsgCategory;
    }
    
    /**
       @return True if this message category is of "audit" type.
       @roseuid 3650A6560044
     */
    public boolean isAudit() {
        return this instanceof AuditMsgCategory;
    }
    
    /**
       @return True if this message category is of "non-repudiation" type.
       @roseuid 3650A6780057
     */
    public boolean isNonRepudiation() {
        return this instanceof NonRepudiationMsgCategory;
    }
    
    /**
       @return True if this message category is of "system notification" type.
       @roseuid 3650A6A50214
     */
    public boolean isSystemNotification() {
        return this instanceof SystemNotificationMsgCategory;
    }
    
    /**
       @return True if this message category is of "system alarm" type.
       @roseuid 3650A6C2018A
     */
    public boolean isSystemAlarm() {
        return this instanceof SystemAlarmMsgCategory;
    }
    
    /**
       @return True if this message category is of "information" type.
       @roseuid 3650A6F400C3
     */
    public boolean isInformation() {
        return this instanceof InformationMsgCategory;
    }
    
    /**
       @return The name of this message type which is unique across all message types.
       @roseuid 36531BC40169
     */
    public abstract String getUniqueName();
    
    /**
       @return An array of strings representing the complete set of unique names for valid message categories.
       @roseuid 36533DE100EF
     */
    public static String[] getAllUniqueNames() {
        String[] output = {
                DebugMsgCategory.uniqueName,
                AuditMsgCategory.uniqueName,
                NonRepudiationMsgCategory.uniqueName,
                SystemNotificationMsgCategory.uniqueName,
                SystemAlarmMsgCategory.uniqueName,
                InformationMsgCategory.uniqueName };

        return output;
    }
    
    /**
       @return The message category corresponding to the given name.
       @exception InvalidMessageCategoryException The specified message category does not exist.
       @roseuid 365B15310285
     */
    public static MsgCategory fromUniqueName(String name) throws InvalidMessageCategoryException {
        MsgCategory category = null;
        if( name.equalsIgnoreCase( DebugMsgCategory.uniqueName ) ) {
            category = debug;
        }
        else if( name.equalsIgnoreCase( AuditMsgCategory.uniqueName ) ) {
            category = audit;
        }
        else if( name.equalsIgnoreCase( NonRepudiationMsgCategory.uniqueName ) ) {
            category = nonRepudiation;
        }
        else if( name.equalsIgnoreCase( SystemNotificationMsgCategory.uniqueName ) ) {
            category = systemNotification;
        }
        else if( name.equalsIgnoreCase( SystemAlarmMsgCategory.uniqueName ) ) {
            category = systemAlarm;
        }
        else if( name.equalsIgnoreCase( InformationMsgCategory.uniqueName ) ) {
            category = information;
        }
        else {
            throw new InvalidMessageCategoryException( "Message category name \"" + name + "\" does not exist." );
        }

        return category;
    }
    
    /**
       @return True if this category is the same as the given category.
       @roseuid 36640D7D002B
     */
    public abstract boolean equals(Object category);
    
    /**
       Create a new instance of a message category.
       @roseuid 37614E930144
     */
    protected MsgCategory() {
    }
    
    /**
       @return The index associated with this category.
       @roseuid 37C439160242
     */
    public abstract int getCategoryIndex();
    
    /**
       @return The category object corresponding to the given category index.
       @roseuid 37C43BCB03E3
     */
    static MsgCategory fromCategoryIndex(int categoryIndex) throws InvalidMessageCategoryException {
        MsgCategory returnedCategory;
        switch( categoryIndex ) {
            case InformationMsgCategory.categoryIndex : {
                returnedCategory = information;
                break;
            }
            case AuditMsgCategory.categoryIndex : {
                returnedCategory = audit;
                break;
            }
            case DebugMsgCategory.categoryIndex : {
                returnedCategory = debug;
                break;
            }
            case SystemAlarmMsgCategory.categoryIndex : {
                returnedCategory = systemAlarm;
                break;
            }
            case NonRepudiationMsgCategory.categoryIndex : {
                returnedCategory = nonRepudiation;
                break;
            }
            case SystemNotificationMsgCategory.categoryIndex : {
                returnedCategory = systemNotification;
                break;
            }
            default : {
                throw new InvalidMessageCategoryException( "Category index does not correspond to a valid category." );
            }
        }
        
        return returnedCategory;
    }
}
