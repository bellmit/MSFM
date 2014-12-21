package com.cboe.directoryService.persist;

//import com.cboe.loggingService.Log;
import com.objectwave.persist.AttributeTypeColumn;
import com.objectwave.persist.DomainObject;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.transactionalSupport.ObjectEditingView;
import java.lang.reflect.Field;
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
* NOTE:  This class does not do much.  It became necessary to instantiate
* via a factory while transitioning the Reference column to the ObjReference
* column.  During the transition, both fields were needed and then
* eventually only the ObjReference column would remain.  Due to the
* schema difference, a new class was necessary to satisfy JGrinder. 
* This class represents the new class that only uses the ObjReference 
* column.  The TraderOfferIORReference class is the "rollover" class 
* that was created to work with both columns.
*/
public class TraderOfferIORObjReference extends TraderOffer {

	/**
	* Constructor
	*/
	public TraderOfferIORObjReference()
	{ }

	/**
	* Constructor
	*/
	public TraderOfferIORObjReference(String id)
	{
		this();
		setOfferID(id);
	}

}
