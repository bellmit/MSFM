package com.cboe.domain.util;

//import com.cboe.interfaces.internalBusinessServices.*;
//import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.exceptions.*;
import java.util.Vector;
import java.lang.reflect.Field;

/**
 * This is the persistent OrderIdSeedImpl.
 *
 * @author Ravi Rade
 */

public class OrderIdSeedImpl extends  PersistentBObject 
{
    public static final String TABLE_NAME = "order_id_seed";

    private static Field _usedBranch;
    private static Vector classDescriptor;

    //Columns :
    String             usedBranch;

    /*
     * This static block will be regenerated if persistence is regenerated.
     */
    static { /*NAME:fieldDefinition:*/
        try{
            _usedBranch = OrderIdSeedImpl.class.getDeclaredField("usedBranch");
            _usedBranch.setAccessible(true);
        } catch (NoSuchFieldException ex) { 
            System.out.println(ex); 
        }
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public void initDescriptor() {

        synchronized(OrderIdSeedImpl.class) {
            if(classDescriptor != null) {
                return;
            }
            classDescriptor = getSuperDescriptor();
            classDescriptor.addElement(AttributeDefinition.getAttributeRelation( "used_branch" , _usedBranch));
        }
    }

   /**
    * Needed to define table name and the description of this class.
    */
    public ObjectChangesIF initializeObjectEditor() {
        final DBAdapter result = (DBAdapter)super.initializeObjectEditor();
        if(classDescriptor == null) {
            initDescriptor();
        }
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

   /**
    * Get method for usedBrnach column
    */
    public String getUsedBranch(){
        String retVal = (String)editor.get( _usedBranch, usedBranch );
        return ( retVal != null )?retVal:"";
    }

   /**
    * Set method for usedBrnach column
    */
    public void setUsedBranch(String aValue) {
        editor.set(_usedBranch, aValue, usedBranch);
    }

}
