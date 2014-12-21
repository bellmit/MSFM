// -----------------------------------------------------------------------------------
// Source file: AdminServiceProductQueryImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmi.ProductQueryOperations;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.exceptions.*;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AdminServiceClientAsync;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.XmlProductConversionFacade;
import com.cboe.client.xml.bind.GIProductQueryOperationsType;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.util.ExceptionBuilder;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;

public class AdminServiceProductQueryImpl implements ProductQueryOperations
{
    private AdminServiceClientAsync adminService;

    public AdminServiceProductQueryImpl(AdminServiceClientAsync adminService)
    {
        super();
        this.adminService = adminService;
        if (adminService == null)
        {
            throw new IllegalArgumentException("AdminService can not be null");
        }
    }

    // ProductQueryOperations
    public PendingAdjustmentStruct[] getAllPendingAdjustments() throws SystemException, CommunicationException, AuthorizationException
    {
        throw getUnsupportedException("getAllPendingAdjustments():PendingAdjustmentStruct[]");
    }

    public ClassStruct getClassByKey(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createClassByKeyRequest(classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                ClassStruct[] classStructs = XmlProductConversionFacade.getInstance().getClassStructs((GIProductQueryOperationsType)unmarshalledObject);
                if(classStructs.length > 0) // return the first one found (should have been only one anyway).
                {
                    return classStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return null;
    }

    public ClassStruct getClassBySymbol(short i, String s) throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        throw getUnsupportedException("getClassBySymbol(short, String):ClassStruct");
    }

    public PendingNameStruct[] getPendingAdjustmentProducts(int i) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getPendingAdjustmentProducts(int):PendingNameStruct[]");
    }

    public PendingAdjustmentStruct[] getPendingAdjustments(int i, boolean b) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getPendingAdjustments(int, boolean):PendingAdjustmentStruct[]");
    }

    public ProductStruct getProductByKey(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductByKeyRequest(productKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                ProductStruct[] productStructs = XmlProductConversionFacade.getInstance().getProductStructs((GIProductQueryOperationsType)unmarshalledObject);
                if(productStructs.length > 0) // return the first one found (should have been only one anyway).
                {
                    return productStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return null;
    }

    public ProductStruct getProductByName(ProductNameStruct productNameStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw getUnsupportedException("getProductByName(ProductNameStruct):ProductStruct");
    }

    public ClassStruct[] getProductClasses(short productType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductClassesRequest(productType);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getClassStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch(DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return new ClassStruct[0];
    }

    public ProductNameStruct getProductNameStruct(int i) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw getUnsupportedException("getProductNameStruct(int):ProductNameStruct");
    }

    public ProductTypeStruct[] getProductTypes() throws SystemException, CommunicationException, AuthorizationException
    {

        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductTypesRequest();
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getProductTypeStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return new ProductTypeStruct[0];
    }

    public ProductStruct[] getProductsByClass(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductsByClassRequest(classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getProductStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch(DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return new ProductStruct[0];
    }

    public StrategyStruct[] getStrategiesByClass(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createStrategiesByClassRequest(classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getStrategyStructs((GIProductQueryOperationsType) unmarshalledObject);
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        return new StrategyStruct[0];
    }

    public StrategyStruct[] getStrategiesByComponent(int i) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getStrategiesByComponent(String, int):StrategyStruct[]");
    }

    public StrategyStruct getStrategyByKey(int strategyKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createStrategyByKeyRequest(strategyKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                StrategyStruct[] strategyStructs = XmlProductConversionFacade.getInstance().getStrategyStructs((GIProductQueryOperationsType)unmarshalledObject);
                if(strategyStructs.length > 0) // return the first one found (should have been only one anyway).
                {
                    return strategyStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        return null;
    }

    public boolean isValidProductName(ProductNameStruct productNameStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("isValidProductName(ProductNameStruct):boolean");
    }

    // CORBA.Object
    /**
     * Checks whether this object is an instance of a class that
     * implements the given interface.
     *
     * @param repositoryIdentifier the interface to check against
     * @return <code>true</code> if this object reference is an instance
     *         of a class that implements the interface;
     *         <code>false</code> otherwise
     */
    public boolean _is_a(String repositoryIdentifier)
    {
        return false;
    }

    /**
     * Determines whether the two object references are equivalent,
     * so far as the ORB can easily determine. Two object references are equivalent
     * if they are identical. Two distinct object references which in fact refer to
     * the same object are also equivalent. However, ORBs are not required
     * to attempt determination of whether two distinct object references
     * refer to the same object, since such determination could be impractically
     * expensive.
     * @param other the other object reference with which to check for equivalence
     * @return <code>true</code> if this object reference is known to be
     *         equivalent to the given object reference.
     *         Note that <code>false</code> indicates only that the two
     *         object references are distinct, not necessarily that
     *         they reference distinct objects.
     */
    public boolean _is_equivalent(Object other)
    {
        return false;
    }

    /**
     * Determines whether the server object for this object reference has been
     * destroyed.
     * @return <code>true</code> if the ORB knows authoritatively that the
     *         server object does not exist; <code>false</code> otherwise
     */
    public boolean _non_existent()
    {
        return false;
    }

    /**
     * Returns an ORB-internal identifier for this object reference.
     * This is a hash identifier, which does
     * not change during the lifetime of the object reference, and so
     * neither will any hash function of that identifier change. The value returned
     * is not guaranteed to be unique; in other words, another object
     * reference may have the same hash value.
     * If two object references hash differently,
     * then they are distinct object references; however, both may still refer
     * to the same CORBA object.
     *
     * @param maximum the upper bound on the hash value returned by the ORB
     * @return the ORB-internal hash identifier for this object reference
     */
    public int _hash(int maximum)
    {
        return 0;
    }

    /**
     * Returns a duplicate of this CORBA object reference.
     * The server object implementation is not involved in creating
     * the duplicate, and the implementation cannot distinguish whether
     * the original object reference or a duplicate was used to make a request.
     * <P>
     * Note that this method is not very useful in the Java platform,
     * since memory management is handled by the VM.
     * It is included for compliance with the CORBA APIs.
     * <P>
     * The method <code>_duplicate</code> may return this object reference itself.
     *
     * @return a duplicate of this object reference or this object reference
     *         itself
     */
    public Object _duplicate()
    {
        return null;
    }

    /**
     * Signals that the caller is done using this object reference, so
     * internal ORB resources associated with this object reference can be
     * released. Note that the object implementation is not involved in
     * this operation, and other references to the same object are not affected.
     */
    public void _release()
    {
    }

    /**
     * Obtains an <code>InterfaceDef</code> for the object implementation
     * referenced by this object reference.
     * The <code>InterfaceDef</code> object
     * may be used to introspect on the methods, attributes, and other
     * type information for the object referred to by this object reference.
     *
     * @return the <code>InterfaceDef</code> object in the Interface Repository
     *         which provides type information about the object referred to by
     *         this object reference
     */
    public Object _get_interface_def()
    {
        return null;
    }

    /**
     * Creates a <code>Request</code> instance for use in the
     * Dynamic Invocation Interface.
     *
     * @param operation  the name of the method to be invoked using the
     *		              <code>Request</code> instance
     * @return the newly-created <code>Request</code> instance
     */
    public Request _request(String operation)
    {
        return null;
    }

    /**
     * Creates a <code>Request</code> instance initialized with the
     * given context, method name, list of arguments, and container
     * for the method's return value.
     *
     * @param ctx			a <code>Context</code> object containing
     *                     a list of properties
     * @param operation    the name of the method to be invoked
     * @param arg_list		an <code>NVList</code> containing the actual arguments
     *                     to the method being invoked
     * @param result		a <code>NamedValue</code> object to serve as a
     *                     container for the method's return value
     * @return			the newly-created <code>Request</code> object
     *
     * @see Request
     * @see NVList
     * @see NamedValue
     */

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result)
    {
        return null;
    }

    /**
     * Creates a <code>Request</code> instance initialized with the
     * given context, method name, list of arguments, container
     * for the method's return value, list of possible exceptions,
     * and list of context strings needing to be resolved.
     *
     * @param ctx			a <code>Context</code> object containing
     *                     a list of properties
     * @param operation    the name of the method to be invoked
     * @param arg_list		an <code>NVList</code> containing the actual arguments
     *                     to the method being invoked
     * @param result		a <code>NamedValue</code> object to serve as a
     *                     container for the method's return value
     * @param exclist		an <code>ExceptionList</code> object containing a
     *                     list of possible exceptions the method can throw
     * @param ctxlist		a <code>ContextList</code> object containing a list of
     *                     context strings that need to be resolved and sent with the
     * 			     	<code>Request</code> instance
     * @return			the newly-created <code>Request</code> object
     *
     * @see Request
     * @see NVList
     * @see NamedValue
     * @see ExceptionList
     * @see ContextList
     */

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result,
                                   ExceptionList exclist,
                                   ContextList ctxlist)
    {
        return null;
    }

    /**
     * Returns the <code>Policy</code> object of the specified type
     * which applies to this object.
     *
     * @param policy_type the type of policy to be obtained
     * @return A <code>Policy</code> object of the type specified by
     *         the policy_type parameter
     * @exception BAD_PARAM when the value of policy type
     * is not valid either because the specified type is not supported by this
     * ORB or because a policy object of that type is not associated with this
     * Object
     */
    public Policy _get_policy(int policy_type)
    {
        return null;
    }

    /**
     * Retrieves the <code>DomainManagers</code> of this object.
     * This allows administration services (and applications) to retrieve the
     * domain managers, and hence the security and other policies applicable
     * to individual objects that are members of the domain.
     *
     * @return the list of immediately enclosing domain managers of this object.
     *  At least one domain manager is always returned in the list since by
     * default each object is associated with at least one domain manager at
     * creation.
     */
    public DomainManager[] _get_domain_managers()
    {
        return new DomainManager[0];
    }

    /**
     * Returns a new <code>Object</code> with the given policies
     * either replacing any existing policies in this
     * <code>Object</code> or with the given policies added
     * to the existing ones, depending on the value of the
     * given <code>SetOverrideType</code> object.
     *
     * @param policies an array of <code>Policy</code> objects containing
     *                 the policies to be added or to be used as replacements
     * @param set_add either <code>SetOverrideType.SET_OVERRIDE</code>, indicating
     *                that the given policies will replace any existing ones, or
     *                <code>SetOverrideType.ADD_OVERRIDE</code>, indicating that
     *                the given policies should be added to any existing ones
     * @return a new <code>Object</code> with the given policies replacing
     *         or added to those in this <code>Object</code>
     */
    public Object _set_policy_override(Policy[] policies,
                                       SetOverrideType set_add)
    {
        return null;
    }

    private UnsupportedOperationException getUnsupportedException(String methodNotSupported)
    {
        return new UnsupportedOperationException("The method " + methodNotSupported + " is not supported by this API.");
    }


}
