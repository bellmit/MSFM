package com.cboe.client.util;

import com.cboe.ORBInfra.ORB.OrbAux;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.rolloutUtil.IORResolver;
import com.cboe.rolloutUtil.IORResolverFactory;
import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;

import java.lang.reflect.Method;

/**
 * A utility class that returns a fully narrowed object reference from the 
 * reference returned by the TraderService.
 *
 *  This class resovles using the IORResolver to handle 
 *
 * @author Matt Sochacki
 */
public class ClientObjectResolver {

    static public org.omg.CORBA.Object resolveObject( org.omg.CORBA.Object anObject, java.lang.String helperClassName )
    {
        IORResolver iorResolver = IORResolverFactory.getIORResolver();

        String ior = FoundationFramework.getInstance().getOrbService().getOrb().object_to_string(anObject);
        if (Log.isDebugOn())
        {
            Log.debug("The Object being resolved has an ior of - " + ior);
        }

        return iorResolver.resolveIOR( ior, helperClassName );
    }
    
    /**
     * This method will set the timeout policy on the client side, so that if process does not respond call will timeout in the
     * timeoutPeriod, and  narrow the object reference.
     * @param anObject
     * @param helperClassName
     * @param timeoutPeriod - Timeout period 
     * @return -narrowed object reference
     * @throws Exception
     */ 
    static public org.omg.CORBA.Object resolveObject(org.omg.CORBA.Object anObject, java.lang.String helperClassName, int timeoutPeriod ) throws Exception
    {
        org.omg.CORBA.Object result = null;
        
        //Set the client side timeout policies
        org.omg.CORBA.ORB orb = FoundationFramework.getInstance().getOrbService().getOrb();
        Any a = orb.create_any();
        Policy[] adapterPolicies = new Policy[ 1 ];
        a.insert_long( timeoutPeriod );
        
        adapterPolicies[ 0 ] = OrbAux.create_policy( com.cboe.ORBInfra.PolicyAdministration.RELATIVE_RT_TIMEOUT_CO_POLICY_TYPE.value, a);            
        result = OrbAux.set_policy_overides( adapterPolicies, SetOverrideType.SET_OVERRIDE, anObject );
        
        //narrow the object reference
        Class helperClass = Class.forName(helperClassName);
        Class[] params = new Class[] { org.omg.CORBA.Object.class };
        
        Method method = helperClass.getMethod("narrow",params);
        
        result = (org.omg.CORBA.Object) method.invoke(null,new java.lang.Object[] {result});

        return result;
    }

}
