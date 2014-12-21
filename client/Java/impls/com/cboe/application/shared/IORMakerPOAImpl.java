package com.cboe.application.shared;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.application.*;
import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.ORB.DelegateImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.ORBInfra.IIOPImpl.IIOPProfileImpl;
import com.cboe.LocalTransport.LocalProfile;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;



public class IORMakerPOAImpl extends BObject implements IORMaker
{
    public String object_to_string(org.omg.CORBA.Object obj)
    {
        String rtnStr = null;
        int iiopProfileId = 0;
        int localProfileId = 0;
        IORImpl ior = null;
       try
       {
            ior = ((DelegateImpl)((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate()).getIOR();

            iiopProfileId = com.cboe.ORBInfra.IIOPImpl.IIOPProfileImplFactory.Instance().getProfileId();
            localProfileId = com.cboe.LocalTransport.LocalProfileImplFactory.Instance().getProfileId();
            IIOPProfileImpl iiopProfile = (IIOPProfileImpl)ior.getProfile(Integer.valueOf(iiopProfileId));

            if (Character.isDigit((iiopProfile.getHost()).charAt(0)))
            {
                LocalProfile localProfile = (LocalProfile) ior.getProfile(Integer.valueOf(localProfileId));
                iiopProfile.setHost(localProfile.getHost());
            }

        }
        catch (Exception e)
        {
            Log.exception(this, e);
        }
        catch(ProfileNotPresent pnp) {}

        finally
        {
            if (ior == null)
                return "";
            else
            {
                rtnStr = ior.getStringDigest();
                if (Log.isDebugOn())
                {
                    Log.debug(this, "IORMarkerPOAImpl -> ior.getStringDigest() returns " + rtnStr);
                }
                return rtnStr;
            }
        }
    }
}
