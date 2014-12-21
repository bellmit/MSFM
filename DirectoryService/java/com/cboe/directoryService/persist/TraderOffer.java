package com.cboe.directoryService.persist;

//import com.cboe.loggingService.Log;
import com.objectwave.persist.AttributeTypeColumn;
import com.objectwave.persist.DomainObject;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.transactionalSupport.ObjectEditingView;
import java.lang.reflect.Field;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CosTrading.Offer;
import org.omg.CosTrading.Property;
import org.omg.CosTrading.PropertyTypeMismatch;
import org.omg.CosTrading.RegisterPackage.OfferInfo;

import com.cboe.common.log.Logger;
import com.cboe.common.log.InfraLoggingRb;
import java.util.*;


/**
* This class is set up to work with ObjectWave to hold all
* updates to the Trader Property table.
*/
public abstract class TraderOffer extends DomainObject {
    /** 
     * Any specialized variables will need to go in the subclass
     * Offer ID returned by the export operation 
     */
    String offerID;

    /** Service Type */
    String serviceType;

    /** property name sequence */
    String[] propName;

    /**
    * property value sequence
    * Note: using this construct is OK because only primitive TypeCodes are supported
    */
    String[] propValue;

    /** property type sequence (TCKind) */
    int[] propType;

    /** offer informational field */
    String information;

    /** objReference is the stringified IOR (NOT compressed) of the CORBA Object */
    StringWrapper objReference;    
    
    /** ORB reference */
    protected org.omg.CORBA.ORB orb;

    protected static ResourceBundle rb = null;


    // variables to hold reflexive field definitions

    static Field _offerID;
    static Field _serviceType;
    static Field _propName;
    static Field _propValue;
    static Field _propType;
    static Field _information;
    static Field _objReference;

    /** Holds field definitions */
    static Vector classDescriptor;

    // Initialize field variables
    static {
        
        try {
            rb = ResourceBundle.getBundle( InfraLoggingRb.class.getName() );
            
        } catch( Exception e ) {
            Logger.sysAlarm( Logger.createLogMessageId( Logger.getDefaultLoggerName(),
                                                "Unable to set Logging ResourceBundle({0}).",
                                                "TraderOffer", "" ),
                          new Object[] {InfraLoggingRb.class.getName()} );
        }
        
        try {

            _offerID = TraderOffer.class.getDeclaredField( "offerID" );
            _serviceType = TraderOffer.class.getDeclaredField( "serviceType" );
            _propName = TraderOffer.class.getDeclaredField( "propName" );
            _propValue = TraderOffer.class.getDeclaredField( "propValue" );
            _propType = TraderOffer.class.getDeclaredField( "propType" );
            _information = TraderOffer.class.getDeclaredField( "information" );
            _objReference = TraderOffer.class.getDeclaredField( "objReference" );
        }
        catch(NoSuchFieldException ex) {
            Logger.sysWarn( rb,
                         InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
                         new java.lang.Object[] {"TraderOffer", "" },
                         ex );
            
            //Log.logException(null, ex);  // chanaka
        }
    }

    /**
     * This class is now subclassed for backward and forward compatibility.  A 
     * factory pattern will be used to decide which "version" of the TraderOffer
     * to instantiate.  This is being done to move away from the compressed
     * Reference column to a new ObjReference column that can accomodatee the 
     * uncompressed IOR.
     * 
     * @param traderWithBothReferences
     * @return specialized TraderOffer
     */
    public static TraderOffer createTraderOffer(String traderWithBothIORReferences) {
        Logger.debug("====> inside of createTraderOffer, about to create.....");
        Logger.debug("====> the value of the -D trigger is: " +
                     traderWithBothIORReferences);
        if (traderWithBothIORReferences.compareToIgnoreCase("true") == 0) {
            return new TraderOfferIORReference();
        } 
        else {
            return new TraderOfferIORObjReference();
        }

    }    
    
    /**
    * Describe how this class relates to the relational database.
    */
    public void initDescriptor()
    {
        synchronized( TraderOffer.class ) {
            if ( classDescriptor == null ) {
                Vector tmpClassDescriptor = getSuperDescriptor();
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "offerID", _offerID ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "serviceType", _serviceType ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "propName", _propName ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "propValue", _propValue ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "propType", _propType ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "information", _information ) );
                tmpClassDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "objReference", _objReference ) );
                classDescriptor = tmpClassDescriptor;     
            }
        }
    }

    /**
    * The update method is necessary in order to allow any changes to private instance variables.
    */
    public void update(boolean get, Object[] data, Field[] fields)
    {
        for(int i = 0; i < data.length; i++) {
            try {
                if (get) data[i] = fields[i].get(this);
                else fields[i].set(this, data[i]);
            } 
            catch(IllegalAccessException ex) { System.out.println(ex); }
            catch(IllegalArgumentException ex) { System.out.println(ex); }
        }
    }

    /**
    * Define the tablename and the description of the class.
    */
    public ObjectEditingView initializeObjectEditor()
    {
        final RDBPersistentAdapter result = (RDBPersistentAdapter)super.initializeObjectEditor();
        if ( null == classDescriptor ) {
            initDescriptor();
        }
        result.setTableName( "TraderOffer" );
        result.setClassDescription( classDescriptor );
        return result;
    }

    /**
    * Accessor for the offerID field
    * @return the offerID field
    */
    public String getOfferID()
    {
        return (String)editor.get(_offerID, offerID);
    }

    /**
    * Mutator for the offerID field
    * @param aValue the new offerID
    */
    public void setOfferID(String aValue)
    {
        editor.set(_offerID, aValue, offerID);
    }

    /**
    * Accessor for the serviceType field
    * @return the serviceType field
    */
    public String getServiceType()
    {
        return (String)editor.get(_serviceType, serviceType);
    }

    /**
    * Mutator for the serviceType field
    * @param aValue the new serviceType
    */
    public void setServiceType(String aValue)
    {
        editor.set(_serviceType, aValue, serviceType);
    }

    /**
    * Accessor for the information field
    * @return the information field
    */
    public String getInformation()
    {
        return (String)editor.get(_information, information);
    }

    /**
    * Mutator for the information field
    * @param aValue the new information field
    */
    public void setInformation(String aValue)
    {
        editor.set(_information, aValue, information);
    }

    /**
    * Accessor for the reference field
    * NOTE:  this interface remains, but this accessor will just
    * return the value of the ObjReference column that replaced the 
    * Reference column.  This accessor is being kept for backward 
    * compatibility.
    * @return the reference field
    */
    public String getReference()
    {
        return getObjReference();
    }

    /**
    * Accessor for the uncompressed reference field objReference
    * @return the objReference field
    */
    public String getObjReference()
    {
        try {
// System.out.println("getObjReference start");
            StringWrapper sw = (StringWrapper)editor.get(_objReference, objReference);
//System.out.println("getObjReference with a StringWrapper length of: " + sw.getString().length());
             return sw.getString();
        }
        catch(NullPointerException ne) {
System.out.println("caught NullPointerException...returning NULL string");
            Logger.sysWarn( rb,
                    InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
                    new java.lang.Object[] {"TraderOffer", "" },
                    ne );
            return new String("");
        }
    }    
     
    /**
    * Accessor for the reference field, converted to a CORBA object
    * @return the reference as a CORBA Object
    */
    public org.omg.CORBA.Object getCORBAReference()
    {
        String ior;
        ior = getObjReference();
        return getORB().string_to_object(ior);
    }

    /**
    * Mutator for the reference field.  This accessor is being kept for backward 
    * compatibility.
    * NOTE:  this sets the value for the uncompressed ObjReference column now
    * @param aValue the new reference
    */
    public void setReference(String aValue)
    {
//System.out.println("======> start of setReference");
        setObjReference(aValue);
//System.out.println("======> end of setReference");
    }

    /**
    * Mutator for the objReference field
    * NOTE: This mutator does not compress the value.
    * @param aValue the new reference
    */
    public void setObjReference(String aValue)
    {
//System.out.println("======> start of setObjReference");
        StringWrapper sh = new StringWrapper(aValue);
        editor.set(_objReference, sh, objReference);
//System.out.println("======> end of setObjReference");
    }
    
    
    /**
    * Mutator for the reference field
    * NOTE: This mutator was changed to reflect updating both the compressed (varchar)
    * reference column as well as the uncompressed (long raw) objReference column
    * @param aValue the new reference
    * the parameter is first converted to a String, and then reference is set
    */
    public void setReference(org.omg.CORBA.Object aValue)
    {
        String ior = getORB().object_to_string(aValue);
        setObjReference(ior);
    }

    /**
    * Accessor for the property name array
    * @return the property name array
    */
    public String[] getPropertyNames()
    {
        return (String[])editor.get(_propName, propName);
    }

    /**
    * Mutator for the property name array
    * @param aValue the new property name array
    */
    public void setPropertyNames(String[] aValue)
    {
        editor.set(_propName, aValue, propName);
    }

    /**
    * Accessor for the property value array
    * @return the property value array
    */
    public String[] getPropertyValues()
    {
        return (String[])editor.get(_propValue, propValue);
    }

    /**
    * Mutator for the property value array
    * @param aValue the new property value array
    */
    public void setPropertyValues(String[] aValue)
    {
        editor.set(_propValue, aValue, propValue);
    }

    /**
    * Accessor for the property type array
    * @return the property type array
    */
    public int[] getPropertyTypes()
    {
        return (int[])editor.get(_propType, propType);
    }

    /**
    * Mutator for the property type array
    * @param aValue the new property type array
    */
    public void setPropertyTypes(int[] aValue)
    {
        editor.set(_propType, aValue, propType);
    }

    /**
    * Mutator for the property name, value, and type arrays
    * the Trader Property array is broken down into the three individual
    * parts of this object and then each array is saved separately.
    * @param aValue Trader Property sequence
    */
    public void setProperties(Property[] aValue)
    {
        int len = aValue.length;
        int[] tempType = null;
        String[] tempName = null;
        String[] tempValue = null;
        if (len != 0) {
            tempType = new int[len];
            tempName = new String[len];
            tempValue = new String[len];
            for (int i=0; i<len; i++) {
                tempName[i] = aValue[i].name;
                tempType[i] = getKindFromAny( aValue[i].value );
                tempValue[i] = getValueFromAny( aValue[i].value );
            }
        }

        setPropertyTypes(tempType);
        setPropertyNames(tempName);
        setPropertyValues(tempValue);
    }

    /**
    * Convert this object to a Trader Offer object
    * @return a Trader Offer object
    * @exception PropertyTypeMismatch thrown if the typecode doesn't match a value
    * (i.e., can't convert the Stringified value to its corresponding primitive)
    */
    public Offer toOffer()
    throws PropertyTypeMismatch
    {
        Offer retVal = new Offer(getCORBAReference(), null);
        String[] names = getPropertyNames();
        Property[] props = new Property[0];
        if ( names != null) {
            String[] values = getPropertyValues();
            int[] types = getPropertyTypes();
            int len = names.length;
            props = new Property[len];
            for (int i=0; i<len; i++) {
                props[i] = new Property(names[i], null);
                props[i].value = populateAny(types[i], (values != null ? values[i] : null) );
            }
        }

        retVal.properties = props;
        return retVal;
    }

    /**
    * Convert this object to a Trader OfferInfo object
    * @return a Trader OfferInfo object
    * @exception PropertyTypeMismatch thrown if the typecode doesn't match a value
    * (i.e., can't convert the Stringified value to its corresponding primitive)
    */
    public OfferInfo toOfferInfo()
    throws PropertyTypeMismatch
    {
        OfferInfo retVal = new OfferInfo(getCORBAReference(), getServiceType(), null);
        String[] names = getPropertyNames();
        Property[] props = new Property[0];
        if ( names != null) {
            String[] values = getPropertyValues();
            int[] types = getPropertyTypes();
            int len = names.length;
            props = new Property[len];
            for (int i=0; i<len; i++) {
                props[i] = new Property(names[i], null);
                props[i].value = populateAny(types[i], values[i]);
            }
        }

        retVal.properties = props;
        return retVal;
    }

    /**
    * Accessor for the ORB reference
    * @return the ORB reference
    */
    protected org.omg.CORBA.ORB getORB()
    {
        if ( orb == null ) {
            orb = com.cboe.ORBInfra.ORB.Orb.init();
        }
        return orb;
    }

    /**
    * Populate an Any object with the value parameter, converted according to the TypeCode
    * <b>Note</b>: This method throws PropertyTypeMismatch if the String value
    * conversion (to the appropriate primitive) fails. This is <b>not</b> in the spec.
    * @param type the typecode
    * @param value the value to insert into the Any
    * @return the populated Any object
    */
    public Any populateAny(int type, String value)
    throws PropertyTypeMismatch
    {
        Any anAny = getORB().create_any();
        try {
            switch (type) {
                case TCKind._tk_string:
                    anAny.insert_string( value );
                    break;
                case TCKind._tk_char:
                    anAny.insert_char( value.charAt(0) );
                    break;
                case TCKind._tk_long:
                    anAny.insert_long( Integer.parseInt(value) );
                    break;
                case TCKind._tk_short:
                    anAny.insert_short( Short.parseShort(value) );
                    break;
                case TCKind._tk_ushort:
                    anAny.insert_ushort( Short.parseShort(value) );
                    break;
                case TCKind._tk_ulong:
                    anAny.insert_ulong( Integer.parseInt(value) );
                    break;
                case TCKind._tk_float:
                    anAny.insert_float( Float.parseFloat(value) );
                    break;
                case TCKind._tk_double:
                    anAny.insert_double( Double.parseDouble(value) );
                    break;
                case TCKind._tk_boolean:
                    boolean flag = value.equalsIgnoreCase("true");
                    anAny.insert_boolean( flag );
                    break;
                default:
                    //Log.trace(this, "populateAny: TCKind + " + type + " not Supported"); // TODO chanaka
                    throw new PropertyTypeMismatch();
            }
        }
        catch(NumberFormatException nfe) {
            Logger.sysWarn( rb,
                         InfraLoggingRb.DS_TS_PERSIST_NO_MATCHING_TCKIND,
                         new java.lang.Object[] { "TraderOffer", "populateAny", new Integer( type ) },
                         nfe );
                
            //Log.trace(this, "populateAny: Value " + value + " does not match TCKind " + type);
            //Log.debugException(this, nfe);  // TODO chanaka
            throw new PropertyTypeMismatch();
        }

        return anAny;
    }

    /**
    * Extract the TCKind value from an Any object
    * @param Any the Any object
    * @return the integer TCKind value
    */
    public int getKindFromAny(Any anAny)
    {
        return anAny.type().kind().value();
    }

    /**
    * Extract the value from an Any object, according to the TypeCode
    * and convert it to a String
    * @param Any the populated Any object
    * @return the Any's value formatted as a string
    */
    public String getValueFromAny(Any anAny)
    {
        int type = getKindFromAny(anAny);
        String retVal = null;
        switch (type) {
            case TCKind._tk_string:
                retVal = anAny.extract_string();
                break;
            case TCKind._tk_char:
                retVal = String.valueOf( anAny.extract_char() );
                break;
            case TCKind._tk_long:
                retVal = Integer.toString( anAny.extract_long(), 10 );
                break;
            case TCKind._tk_short:
                retVal = Integer.toString( (int)anAny.extract_short(), 10 );
                break;
            case TCKind._tk_ushort:
                retVal = Integer.toString( (int)anAny.extract_ushort(), 10 );
                break;
            case TCKind._tk_ulong:
                retVal = Integer.toString( anAny.extract_ulong(), 10 );
                break;
            case TCKind._tk_float:
                retVal = Float.toString( anAny.extract_float() );
                break;
            case TCKind._tk_double:
                retVal = Double.toString( anAny.extract_double() );
                break;
            case TCKind._tk_boolean:
                retVal = String.valueOf( anAny.extract_boolean() );
                break;
            default:
                //Log.trace(this, "getValueFromAny: TCKind + " + type + " not Supported"); // TODO chanaka
                Logger.sysWarn( rb,
                             InfraLoggingRb.DS_TS_PERSIST_NO_MATCHING_TCKIND,
                             new java.lang.Object[] { "TraderOffer", "getValueFromAny", new Integer(type) } );
                
                throw new RuntimeException("getValueFromAny: TCKind + " + type + " not Supported");
        }

        return retVal;
    }

}