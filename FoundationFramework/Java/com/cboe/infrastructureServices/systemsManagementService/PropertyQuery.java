package com.cboe.infrastructureServices.systemsManagementService;

/**
 * This class is used to build the query string for querying properties from the ConfigurationService
 * Start by specifiying the property to query for with the 'queryFor' method.  Then add 'from' method 
 * calls for each nest scope in which the property exists.
 * Properties can be nested arbitrarily deep within many SMA resources.
 * 
 * @author Matt Sochacki
 */


public class PropertyQuery
{

    private String queryString;

    private PropertyQuery()
    {

    }

    private PropertyQuery( String propertyName )
    {
        queryString=propertyName;
    }


    /**
     *  Use this method to obtain the a query object for the property specified
     *  @param propertyName the property being queried for.
     */
    public static PropertyQuery queryFor( String propertyName )
    {
        return new PropertyQuery( propertyName );
        
    }

    /**
     *  This method is used to specify the resourceType/Name from which a property is retrieved. 
     *  When a property is nested in an SMA Resource it is necessary to qualify the property scope with the 
     *  outer resource type to obtain the property.  If more than one resource of the same type can exist
     *  in a scope then each must be named. See the other from method for cases when only one instance of a 
     *  type exists within a scope.
     *  
     *  @param resourceType the Type (i.e. the tag name in the XML for the ManagedResource)
     *  @param resourceName the Type (i.e. the value of the name attribute in the XML for the ManagedResource)
     */
    public PropertyQuery from( String resourceType , String resourceName )
    {
        queryString=resourceType + "(" + resourceName + ")." + queryString;
        return this;
    }

    /**
     *  This method is used to specify the resourceType from which a property is retrieved. Only use this method
     *  when the resource type from which you are retrieving is a singleton instance within that scope.  See the
     *  other from method when there are multiple named instances of the resource type.
     *  
     *  @param resourceType the Type (i.e. the tag name in the XML for the ManagedResource)
     */
    public PropertyQuery from( String resourceType )
    {
        queryString=resourceType + "." + queryString;
        return this;
    }


    /**
     *  This method will convert the PropertyQuery object into a query string that can be used by the ConfigurationService
     */
    public String queryString()
    {
        return toString();
    }


    public String toString()
    {
        return queryString;
    }


}
