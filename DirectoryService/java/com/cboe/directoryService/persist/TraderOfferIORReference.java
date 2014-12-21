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
* NOTE:  This class just 'adds' to the TraderOffer super class and
* does not do much.  It became necessary to instantiate
* via a factory while transitioning the Reference column to the ObjReference
* column.  During the transition/rollover, both fields were needed and then
* eventually only the ObjReference column would remain.  Due to the
* schema difference, a new class was necessary to satisfy JGrinder. 
* This class represents the 'rollover' class that uses both
* the Reference column and the ObjReference column. 
* The TraderOfferIORObjReference class is the intended class 
* to instantiate once rollover is complete and the use of the 
* objReference column exclusively.
*/
public class TraderOfferIORReference extends TraderOffer {
    /** 
     * Most variables inherited from the super class 
     */
    
    /** reference is the stringified IOR (possibly compressed) of the CORBA Object */
    String reference;

    // variables to hold reflexive field definitions

    static Field _reference;

    // Initialize field variables
    static {
        
        try {
            _reference = TraderOfferIORReference.class.getDeclaredField( "reference" );
        }
        catch(NoSuchFieldException ex) {
            Logger.sysWarn( rb,
                         InfraLoggingRb.DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
                         new java.lang.Object[] {"TraderOffer", "" },
                         ex );
        }
    }

    /**
    * Constructor
    */
    public TraderOfferIORReference()
    { }

    /**
    * Constructor
    */
    public TraderOfferIORReference(String id)
    {
        this();
        setOfferID(id);
    }

    /**
    * Describe how this class relates to the relational database.
    */
    public void initDescriptor()
    {
        super.initDescriptor();
        synchronized( TraderOfferIORReference.class ) {
 //System.out.println("now executing TraderOfferIORReference.initDescriptor()");
            classDescriptor.addElement( AttributeTypeColumn.getAttributeRelation( "reference", _reference ) );
        }
    }

    /**
    * Accessor for the reference field
    * If it is compressed in the table, inflate the value first
    * @return the reference field
    */
    public String getReference()
    {
        String retVal;
        retVal = getObjReference();
        if ( retVal.length() == 0 ) {
            retVal = getOrigReference();
        }

        return retVal;
    }

    /**
     * Accessor for the original reference field
     * If it is compressed in the table, inflate the value first
     * @return the reference field
     */
     public String getOrigReference()
     {
         String retVal = (String)editor.get(_reference, reference);
         if ( !retVal.startsWith("IOR:") ) {
             retVal = decompress(retVal);
         }

         return retVal;
     }
    
    /**
    * Accessor for the uncompressed reference field objReference
    * @return the objReference field
    */
    public String getObjReference()
    {
        String origReferenceColumn;
        origReferenceColumn = getOrigReference();
        
        try {
//System.out.println("TraderOfferIORReference.getObjReference start");
            StringWrapper sw = (StringWrapper)editor.get(_objReference, objReference);
//System.out.println("getObjReference with a StringWrapper length of: " + sw.getString().length());

            // The original reference column (uncompressed from its compressed
            // form) should equal the new reference column (never compressed).
            // If not, probably indicates a rollout issue. Take the original 
            // as the correct value.
            if (!(origReferenceColumn.equals(sw.getString()))) {
                Logger.debug("NOTE:  Difference between orig Reference column and " +
                             "new objReference column!!!");
// System.out.println("orig length=" + origReferenceColumn.length() + " objReference length=" + sw.getString().length());
// System.out.println("dump of orig reference is: \n" + origReferenceColumn);
                setObjReference(origReferenceColumn);
                sw.setString(origReferenceColumn);
            }
            return sw.getString();
        }
        catch(NullPointerException ne) {
            Logger.debug("caught NullPointerException " +
                         "now setting the objReference to the uncompressed " +
                         "Reference column");
            setObjReference(origReferenceColumn);
            return origReferenceColumn;
        }
    }    
     
    /**
    * Accessor for the reference field, converted to a CORBA object
    * First check to see if the uncompressed reference column (objReference) is
    * populated (i.e. not null).  If it is not null, then use it.  Otherwise, 
    * access from the compressed column (reference).
    * @return the reference as a CORBA Object
    */
    public org.omg.CORBA.Object getCORBAReference()
    {
        String ior;
        ior = getObjReference();
        if (ior.length() == 0) {
            // the uncompressed reference column objReference must be null
            // so, get the reference from the compressed column
            ior = getReference();
        }
        return getORB().string_to_object(ior);
    }

    /**
    * Mutator for the reference field
    * If the string length is greater than Oracle's 4000 char limit,
    * compress the value
    * @param aValue the new reference
    */
    public void setReference(String aValue)
    {
//System.out.println("======> start of TraderOfferIORReference.setReference");
        String tmpVal = new String(aValue);
        if ( tmpVal.length() > 4000 ) { // the VARCHAR2 limit
            tmpVal = compress(tmpVal);
        }

        editor.set(_reference, tmpVal, reference);
        
        // next, set the uncompressed "mirrored" reference column
        setObjReference(aValue);
//System.out.println("======> end of TraderOfferIORReference.setReference");
    }

    /**
    * Mutator for the objReference field
    * NOTE: This mutator does not compress the value.
    * @param aValue the new reference
    */
    public void setObjReference(String aValue)
    {
//System.out.println("======> start of TraderOfferIORReference.setObjReference");
        StringWrapper sh = new StringWrapper(aValue);
        editor.set(_objReference, sh, objReference);
//System.out.println("======> end of TraderOfferIORReference.setObjReference");
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
        setReference(ior);
    }

    private String compress(String inputString)
    {
        //Log.traceEntry(this, "compress"); // TODO chanaka
        //Log.debug(this, "compress: input string", inputString); 
        String retVal = new String();
        try {
            Deflater comp = new Deflater();      
            comp.setInput(inputString.getBytes());
            comp.finish();
            byte[] buf = new byte[4096];
            int clen = comp.deflate(buf);
            //Log.debug(this, "compress: compressed len", clen);
            char[] cbuf = new char[clen];
            for (int i=0; i<clen; i++) {
                cbuf[i] = (char)buf[i];
            }
            retVal = new String(cbuf);
        }
        catch(Exception e) {
            //Log.logException(this, e); //  chanaka
            Logger.sysWarn( rb,
                         InfraLoggingRb.UNCAUGHT_EXCEPTION,
                         new java.lang.Object[] { "TraderOffer", "compress" },
                         e );
        }

        return retVal;
    }

    private String decompress(String compressed)
    {
        //Log.traceEntry(null, "decompress");  

        String retVal = new String();
        char[] cbuf = compressed.toCharArray();
        byte[] bbuf = new byte[cbuf.length];
        for (int i=0; i<cbuf.length; i++) {
            bbuf[i] = (byte)cbuf[i];
        }

        try {
            Inflater decomp = new Inflater();
            decomp.setInput(bbuf);
            byte[] buf = new byte[16384];
            int dlen = decomp.inflate(buf);
            //Log.debug(this, "decompress: decompressed len", dlen);
            retVal = new String(buf, 0, dlen);
        }
        catch(Exception e) {
            Logger.sysWarn( rb,
                         InfraLoggingRb.UNCAUGHT_EXCEPTION,
                         new java.lang.Object[] { "TraderOffer", "decompress" },
                         e );
            
            // Log.logException(this, e); 
        }

        //Log.debug(this, "decompress: output string", retVal);

        return retVal;
    }
}
