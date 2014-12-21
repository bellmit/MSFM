package com.cboe.directoryService;

import java.util.ArrayList;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.portable.ObjectImpl;

import com.cboe.ORBInfra.CDR.CDRBuffer;
import com.cboe.ORBInfra.CDR.CDROutputStream;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IOPImpl.ProfileExists;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;
import com.cboe.ORBInfra.Interceptors.Interceptor_LocalImpl;
import com.cboe.ORBInfra.Interceptors.ServerPostInvokeInterceptor;
import com.cboe.ORBInfra.ORB.BindMediator;
import com.cboe.ORBInfra.ORB.DelegateBase;
import com.cboe.ORBInfra.ORB.DelegateImpl;
import com.cboe.ORBInfra.ORB.OrbAux;
import com.cboe.ORBInfra.ORB.ServerRequestImpl;
import com.cboe.ORBInfra.ORB.SimpleObject;
import com.cboe.ORBInfra.ORB.StreamPurpose;
import com.cboe.ORBInfra.PolicyAdministration.PolicyManager_i;
import com.cboe.ORBInfra.TIOP.LocatorSubjectsComponent;
import com.cboe.ORBInfra.TIOP.PolicyValueComponent;
import com.cboe.ORBInfra.TIOP.TIOPAlternateAddressComponent;
import com.cboe.ORBInfra.TIOP.TIOPAlternateTargetComponent;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.ORBInfra.TIOP.UniqueNameComponent;
import com.cboe.ORBInfra.giopSupport.GIOPDecodeHelper;
import com.cboe.TIOPTransport.TIOPTransport;
import com.cboe.common.log.Logger;
import com.cboe.messagingService.AMIRequestImpl;
import static com.cboe.directoryService.TraderLogBuilder.*;

/** 
* Used to duplicate a message to the "B" side Trader Service
*/
public class TraderTransitionInterceptor
extends Interceptor_LocalImpl
implements ServerPostInvokeInterceptor
{
		/** for logging, common name to give all log messages*/
	private static final String CLASS_ID = TraderTransitionInterceptor.class.getSimpleName();
	
	/** operations that need to be replicated to the "B" side */
	private static String[] forwardMethods = {
		/* ServiceTypeRepository Interface */
		"add_type", "remove_type", "mask_type", "unmask_type",
		/* Register Interface */
		"export", "modify", "withdraw_using_constraint", "withdraw"
	};

	/** indices into the forwardRefs array */
	private static int[] forwardRefPointers = {
		/* ServiceTypeRepository Interface */
		0, 0, 0, 0,
		/* Register Interface */
		1, 1, 1, 1
	};

	private static int forwardLen = forwardMethods.length;
	private static ArrayList forwardList;
	private static SimpleObject[] forwardRefs;

	/** CBOE ORB reference */
	private com.cboe.ORBInfra.ORB.Orb orb;

	/** the Request Impl for the post invoke method */
	private static AMIRequestImpl aRequest;

	/** policy for no rebind */
	private org.omg.CORBA.Policy[] noRebind;

	/** active flag */
	private boolean isActive;

	/** debugging flag */
	private static boolean doDebug;

	/** alternate object host */
	private String altHostString;

	/** alternate object orb name */
	private String altOrbString;

	/** alternate object port */
	private String altPortString;


	static {
		doDebug = System.getProperty("LocalLog.SysNotify") != null;
		forwardList = new ArrayList();
		forwardRefs = new SimpleObject[2];
		aRequest = new AMIRequestImpl();
		for (int i=0; i<forwardLen; i++) {
			forwardList.add(forwardMethods[i]);
		}
	}

	/**
	* Constructor.
	* @param orb CBOE ORB reference
	* @param host alternate object hostname
	* @param orbName alternate object orb name
	* @param portNum alternate object port number
	*/
	public TraderTransitionInterceptor(org.omg.CORBA.ORB orb, String host, String orbName, String portNum)
	{
		this.orb = (com.cboe.ORBInfra.ORB.Orb)orb;

		org.omg.CORBA.Any anAny = orb.create_any();
		anAny.insert_short(org.omg.Messaging.NO_REBIND.value);
		noRebind = new org.omg.CORBA.Policy[1];
		try {
			noRebind[0] = OrbAux.create_policy(org.omg.Messaging.REBIND_POLICY_TYPE.value, anAny);
		}
		catch(org.omg.CORBA.PolicyError pe) {
			Logger.sysAlarm(format(CLASS_ID, "<ctor>", "policy error during construction"));
			throw new org.omg.CORBA.INTERNAL();
		}

		altHostString = host;
		altOrbString = orbName;
		altPortString = portNum;
		isActive = false;
	}

	/**
	* get the active flag
	* @return flag true if active, false is inactive
	*/
	public boolean isActive()
	{
		return isActive;
	}

	/**
	* set the active flag
	* @param flag true if active, false is inactive
	*/
	public void setIsActive(boolean flag)
	{
		isActive = flag;
	}

	/**
	* Make a new CORBA reference based on the current reference.
	* @param oldReference current CORBA object reference
	* @param index index into the forwardRefs array
	* 0 = ServiceTypeRepository Interface
	* 1 = Register Interface
	*/
	public void setAltReference(org.omg.CORBA.Object oldReference, int index)
	{
		final String METHOD_ID = "setAltReference";
		
		try {
			DelegateImpl delegate = (DelegateImpl)((ObjectImpl)oldReference)._get_delegate();
			IORImpl ior = ((DelegateImpl)delegate).getIOR();
			BindMediator mediator = delegate.getRebindMediator();

			// get the current TIOP profile
			TIOPProfileImpl current_tiopProfile = (TIOPProfileImpl)ior.getProfile(TIOPProfileImpl.tag);

			// create a new IOR, but only use binding sequence Tiop
			IORImpl newIOR = new IORImpl(ior.getTypeId(), "Tiop:");

			int altPort = current_tiopProfile.getPort();
			if ( altPortString != null ) {
				try {
					altPort = Short.parseShort(altPortString, 10);
				}
				catch(NumberFormatException nfe) {
					Logger.sysAlarm(format(CLASS_ID, METHOD_ID,"Bad AltPort property %s", altPortString), nfe);
					throw new org.omg.CORBA.INV_OBJREF("bad AltPort property: " + altPortString);
				}
			}

			// build a new TIOP Profile without Locator or Alt Info
			TIOPProfileImpl new_tiopProfile = new TIOPProfileImpl(
					current_tiopProfile.getMajorVersionNo(),
					current_tiopProfile.getMinorVersionNo(),
					altHostString,
					altPort,
					current_tiopProfile.getObjectKey().getPOAObjectKey(),
					new LocatorSubjectsComponent("DeadLetterBox", "DeadLetterBox"),
					new TIOPAlternateAddressComponent("", (short)4000),
					new UniqueNameComponent( altOrbString ),
					new PolicyValueComponent( new Policy[0] ),
					new TIOPAlternateTargetComponent("", (short)4000, "")
				);

			newIOR.addProfile(new_tiopProfile);
			Logger.sysNotify(format(CLASS_ID, METHOD_ID, "setAltReference: new IOR =  %s",newIOR.toString()));

			// create a new Object reference
			SimpleObject newReference = new SimpleObject();

			TIOPTransport tiop = (TIOPTransport)orb.getTransportManager().findTransport(TIOPProfileImpl.tag);

			DelegateImpl dele = (DelegateImpl)tiop.createDelegate(orb, newIOR);
			PolicyManager_i mgr = dele._get_policy_manager();
			mgr.set_policy_overrides(noRebind, SetOverrideType.SET_OVERRIDE);
			Logger.sysNotify("setAltReference: Policy Manager = " + mgr.toString());

			newReference._set_delegate(dele);
			forwardRefs[index] = newReference;
		}
		catch(ProfileExists pe) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Profile already exists throwing back"));
			throw new org.omg.CORBA.INV_OBJREF();
		}
		catch(ProfileNotPresent pnp) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID, "Profile is not present, throwing back"));
			throw new org.omg.CORBA.INV_OBJREF();
		}
		catch(ClassCastException cce) {
			Logger.sysAlarm(format(CLASS_ID, METHOD_ID), cce);
			throw new org.omg.CORBA.INV_OBJREF();
		}
	}

	/**
	* PostInvoke implementation
	* @param orb an ORB reference
	* @param req the Request object
	*/
	public void ServerPostInvoke(ORB omgORB, ServerRequest req)
	{
		if ( !isActive ) {
			return;
		}

		String method = req.operation();
		if ( doDebug ) {
			Logger.sysNotify("ServerPostInvoke: method = " + method);
		}

		// only forward methods that update LDAP
		int idx = forwardList.indexOf(method);
		if ( idx >= 0 ) {
			int refIdx = forwardRefPointers[idx];
			byte[] giopMessage = ((ServerRequestImpl)req).getParameterStream().getCDRBuffer().value;
			StringBuffer opName = new StringBuffer();
			int paramIndex = GIOPDecodeHelper.getHeaderInfo(giopMessage, opName);
			int len = giopMessage.length-paramIndex;
			byte[] params = new byte[len];
			System.arraycopy(giopMessage, paramIndex, params, 0, len);

			if ( doDebug ) {
				Logger.sysNotify("ServerPostInvoke: opName from GIOP stream = " + opName);
				Logger.sysNotify("ServerPostInvoke: GIOP Buffer");
				toHex(giopMessage);
				Logger.sysNotify("ServerPostInvoke: End GIOP Buffer\n");
				Logger.sysNotify("ServerPostInvoke: GIOP Buffer len = " + giopMessage.length);
				Logger.sysNotify("ServerPostInvoke: Param Index = " + paramIndex);
				Logger.sysNotify("ServerPostInvoke: Parameter Buffer");
				toHex(params);
				Logger.sysNotify("ServerPostInvoke: End Parameter Buffer\n");
			}

			CDROutputStream oStream = (CDROutputStream)orb.create_output_stream(StreamPurpose.REQUEST);
			CDRBuffer aBuffer = new CDRBuffer(params);
			oStream.reinit(aBuffer, len);

			// reset the starting position
			// this must be *after* the reinit!
			oStream.getCDRIndex().start = 0;

			aRequest.reinit(
				forwardRefs[refIdx], // target object
				method, // opName
				(DelegateBase)forwardRefs[refIdx]._get_delegate(),
				oStream, // parameterStream
				true // response expected
			);


			try {
				if ( doDebug ) {
					Logger.sysNotify("ServerPostInvoke: Request = " + aRequest);
					Logger.sysNotify("ServerPostInvoke: doing oneWaySend...");
				}
			    aRequest.getDelegate().getRebindMediator().oneWaySend(aRequest);
				if ( doDebug ) {
					Logger.sysNotify("ServerPostInvoke: done with oneWaySend...");
				}
			}
			catch(Throwable t) { // log and continue
			    Logger.sysWarn(format(CLASS_ID, "ServerPostInvoke"), t);
			}
			finally {
				orb.release_output_stream(oStream, StreamPurpose.REQUEST);
			}
		}
	}

	private void toHex(byte[] block)
	{
		String hexits = "0123456789abcdef";
		StringBuffer buf1 = new StringBuffer();
		StringBuffer buf2 = new StringBuffer();
		boolean first = true;
		int len = block.length;
		for (int i = 0; i < len; i++) {
			if ( (i % 40) == 0 && !first ) {
				System.out.println(buf2);
				System.out.println(buf1);
				buf1 = new StringBuffer();
				buf2 = new StringBuffer();
			}
			first = false;
			buf1.append(hexits.charAt(block[i] >>> 4 & 0xf));
			buf1.append(hexits.charAt(block[i] & 0xf));
			buf2.append(' ');
			char c = (char)block[i];
			if ( Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c) ) { buf2.append(c); }
			else { buf2.append('.'); }
		}

		if ( buf1.length() > 0 ) {
			System.out.println(buf2);
			System.out.println(buf1);
		}
	}
}
