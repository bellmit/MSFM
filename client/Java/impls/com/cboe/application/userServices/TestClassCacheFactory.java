/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Mar 8, 2002
 * Time: 9:10:03 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.userServices;

import java.util.HashMap;

import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.shared.ServicesHelper;


public class TestClassCacheFactory
{
    private static HashMap testClasses ;

    public TestClassCacheFactory()
    {
        super();
    }

    private static HashMap getTestClasses()
    {
        if (testClasses == null)
        {
            testClasses = new HashMap();
        }
        return testClasses;
    }
    public static void load(ClassStruct[] classes)
        throws SystemException, CommunicationException, AuthorizationException
    {

        testClasses = getTestClasses();

        for ( int i = 0; i < classes.length; i ++)
        {
            if ( classes[i].testClass )
            {
                Integer key = new Integer( classes[i].classKey );
                testClasses.put( key , key );
            }
        }
        if (Log.isDebugOn()) {
            Log.debug("TestClassCacheFactory -> load: classes : " + classes.length +  " test classes : " + testClasses.size());
        }
    }

    public static boolean isTestClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException
    {
        return  getTestClasses().containsKey(new Integer(classKey));
    }

}

