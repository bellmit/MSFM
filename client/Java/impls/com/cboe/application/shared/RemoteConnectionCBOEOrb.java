package com.cboe.application.shared;

import com.cboe.loggingService.Log;
import com.cboe.ORBInfra.PortableServer.POA_i;
import com.cboe.ORBInfra.PortableServer.ThreadModelPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.CORBA.PolicyError;
import com.cboe.exceptions.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl;

public class RemoteConnectionCBOEOrb extends RemoteConnectionCORBAHTTP
{
    public static final String MDX_POA_NAME = "MDXPOA";
    public static final String HEARTBEAT_POA_NAME = "HeartbeatPOA";
    public static final String DEFAULT_POA_NAME = "DefaultPOA";

    protected org.omg.PortableServer.POA rootPOA;
    protected org.omg.PortableServer.POA defaultPOA;
    protected org.omg.PortableServer.POA heartbeatPOA;
    protected org.omg.PortableServer.POA mdxPOA;
    private HashMap poas;

    /**
     * SBTConnection constructor comment.
     */
    public RemoteConnectionCBOEOrb(String[] args)
    {
        try
        {
            InetAddress address = InetAddress.getLocalHost();
            String hostname = address.getHostName();
            if (Log.isDebugOn())
            {
                Log.debug(this, "RemoteConnectionCBOEOrb -> Setting IOR hostname = " + hostname);
            }
        } catch (Exception e)
        {
            Log.debugException(this, e);
        }

        orb = com.cboe.ORBInfra.ORB.Orb.init();

        org.omg.CORBA.Object obj;
        try
        {
            obj = orb.resolve_initial_references("RootPOA");
            rootPOA = org.omg.PortableServer.POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();
            System.getProperties().put("POA_DEFAULT_POOL_SIZE", "40");
            defaultPOA = rootPOA.create_POA(DEFAULT_POA_NAME, null, getDefaultPolicies());
            defaultPOA.the_POAManager().activate();

            System.getProperties().put("POA_DEFAULT_POOL_SIZE", "5");
            heartbeatPOA = rootPOA.create_POA(HEARTBEAT_POA_NAME, null, getDefaultPolicies());
            heartbeatPOA.the_POAManager().activate();

            mdxPOA = rootPOA.create_POA(MDX_POA_NAME, null, getMDXPolicies());
            mdxPOA.the_POAManager().activate();
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.debugException(this, e);
        }
    }

    protected org.omg.CORBA.Policy[] getDefaultPolicies() throws PolicyError
    {
        ArrayList tmpList = new ArrayList();
        tmpList.add( rootPOA.create_lifespan_policy( LifespanPolicyValue.TRANSIENT ) );
        tmpList.add( rootPOA.create_id_assignment_policy( IdAssignmentPolicyValue.SYSTEM_ID ) );
        tmpList.add( ((POA_i)rootPOA).create_thread_model_policy(ThreadModelPolicyValue.POOL_PER_POA) );
        org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[ tmpList.size() ];
        tmpList.toArray(policies);
        return policies;
    }

    protected org.omg.CORBA.Policy[] getMDXPolicies() throws PolicyError
    {
        ArrayList<org.omg.CORBA.Policy> tmpList = new ArrayList<org.omg.CORBA.Policy>();
        tmpList.add(rootPOA.create_lifespan_policy(LifespanPolicyValue.TRANSIENT));
        tmpList.add(rootPOA.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID));
        tmpList.add(((POA_i) rootPOA).create_thread_model_policy(ThreadModelPolicyValue.USE_TRANSPORT_THREAD));
        org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[tmpList.size()];
        tmpList.toArray(policies);
        return policies;
    }

    public void cleanupConnection(Object obj)
    {
    }

    public String getHostname(Object obj)
    {
        IORImpl ior = ((com.cboe.ORBInfra.ORB.DelegateImpl)((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate()).getIOR().copy();

        try {
            IIOPProfileImpl iProfile = (IIOPProfileImpl)ior.getProfile( Integer.valueOf( org.omg.IOP.TAG_INTERNET_IOP.value ) );
            return iProfile.getHost();
        }
        catch( com.cboe.ORBInfra.IOPImpl.ProfileNotPresent pnf) {
            if (Log.isDebugOn())
            {
                Log.debug (this, "getHostName returns null");
            }
            return null;
        }
    }

    public String getPort(Object obj)
    {
        IORImpl ior = ((com.cboe.ORBInfra.ORB.DelegateImpl)((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate()).getIOR().copy();

        try {
            IIOPProfileImpl iProfile = (IIOPProfileImpl)ior.getProfile( Integer.valueOf( org.omg.IOP.TAG_INTERNET_IOP.value ) );
            return Integer.toString(iProfile.getPort());
        }
        catch( com.cboe.ORBInfra.IOPImpl.ProfileNotPresent pnf) {
            if (Log.isDebugOn())
            {
                Log.debug (this, "getPort returns null");
            }
            return "unknown";
        }
    }

    private void buildPOAs(POA base)
    {
        poas.put(base.the_name(), base);
        POA[] children = base.the_children();
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                buildPOAs(children[i]);
            }
        }
    }

    private org.omg.PortableServer.POA getPOA(String poaName) throws SystemException
    {
        try
        {
            if (poas == null)
            {
                poas = new HashMap();
                buildPOAs(rootPOA);
            }
            return (POA) poas.get(poaName);
        } catch (Exception e)
        {
            Log.debugException(this, e);
            e.printStackTrace();
            throw new SystemException();
        }
    }

    private org.omg.CORBA.Object connect(String poaName, org.omg.PortableServer.Servant svnt)
            throws SystemException, WrongPolicy
    {
        org.omg.PortableServer.POA targetPoa = rootPOA;
        if (poaName != null && !poaName.equals("RootPOA"))
        {
            targetPoa = getPOA(poaName);
        }
        try
        {
            String servant = svnt.toString();
            String target = targetPoa.toString();
            StringBuilder connecting = new StringBuilder(servant.length()+target.length()+50);
            connecting.append("Connecting the Servant = { ").append(servant).append(" } to the POA = { ").append(target).append(" }");
            System.out.println(connecting.toString());
            targetPoa.activate_object(svnt);
        } catch (WrongPolicy wp)
        {
            throw wp;
        } catch (ServantAlreadyActive at)
        {
        } //Ignore this one

        try
        {
            return targetPoa.servant_to_reference(svnt);
        } catch (WrongPolicy wp)
        {
            throw wp;
        } catch (ServantNotActive at)
        {
        } //Should never happen.
        return null;
    }

    public Object register_object(Object obj, String poaName)
    {
        if (poaName == "")
        {
            poaName = "DefaultPOA";
        }
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Calling connect (" + poaName + ", " + obj + ")");
            }
            return connect(poaName, (Servant) obj);
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.debugException(this, e);
            return null;
        }
    }

    public Object register_object(Object obj)
    {
        return register_object(obj, "");
    }

    public void unregister_object(Object obj)
    {
        com.cboe.ORBInfra.IOPImpl.IORImpl ior =
                ((com.cboe.ORBInfra.ORB.DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate()).getIOR();

                //((com.cboe.ORBInfra.ORB.DelegateImpl) ((com.cboe.ORBInfra.ORB.ObjectImpl) obj)._get_delegate()).getIOR();

        com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl iiopProfile = null;
        try
        {

            iiopProfile = (com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl) ior.getProfile(Integer.valueOf(org.omg.IOP.TAG_INTERNET_IOP.value));
        } catch (com.cboe.ORBInfra.IOPImpl.ProfileNotPresent pe)
        {
        }

        com.cboe.ORBInfra.IOPImpl.BinaryObjectKey objKey = iiopProfile.getObjectKey();
        com.cboe.ORBInfra.PortableServer.POAObjectKey POAKey = objKey.getPOAObjectKey();

        String[] POAname = POAKey.poaName();
        // if no child POAs (ie, only RootPOA), you get a zero length array.

        // printout all the POA names ....
        String objectPOA = "RootPOA";

        if (POAname.length == 0)
        {
            System.out.println("POA Name: RootPOA");
        } else
        {
            StringBuilder name = new StringBuilder(50);
            for (int i = 0; i < POAname.length; ++i)
            {
                name.setLength(0);
                name.append("POA Name: ").append(i).append("   ").append(POAname[i]);
                System.out.println(name.toString());
                objectPOA = POAname[i];
            }
        }

        // once we have our POA name, we can get the POA using find_POA.
        // you can start from RootPOA and go down ..., (i don't know how deep your
        // POA hierachy is going to be ...
        // You can get the RootPOA by doing a "resolve_initial_references".
        try
        {
            POA poa = getPOA(objectPOA);
            // the poa is the POA my servant is registered with.
            // i need to objectId of the servant to do the deactivation.
            byte[] goodDayObjectId = poa.reference_to_id((org.omg.CORBA.Object) obj);

            // deactivate it.
            poa.deactivate_object(goodDayObjectId);
        } catch (Exception e)
        {
            Log.debugException(this, e);
        }

    }

    public Object setRoundTripTimeout(Object obj, int timeout)
    {
        try {
            org.omg.CORBA.Object corbaObj = (org.omg.CORBA.Object) obj;
            org.omg.CORBA.Any timeout_as_any = orb.create_any();
            timeout_as_any.insert_long(timeout);
            org.omg.CORBA.Policy[] policy_list = new org.omg.CORBA.Policy[2];
            policy_list[0] = orb.create_policy(
                    org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value, timeout_as_any);

            org.omg.CORBA.Any rebind = orb.create_any();
            rebind.insert_short(org.omg.Messaging.NO_REBIND.value);
            policy_list[1] = orb.create_policy(
                    org.omg.Messaging.REBIND_POLICY_TYPE.value, rebind);

            org.omg.CORBA.Object relativeRTRef = com.cboe.ORBInfra.ORB.OrbAux.set_policy_overides(
                    policy_list,
                    org.omg.CORBA.SetOverrideType.SET_OVERRIDE,
                    corbaObj);

            return relativeRTRef;
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.debugException(this, e);
            return obj;
        }
    }
}

