package com.cboe.infrastructureServices.systemsManagementService;


import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


/**
 *  This class is used to query for "global", application-defined, foundation framework properties.
 *  These properties are place in the section
 *  <GlobalFounadationFramework>
 *      <FouandationFramewrok>
 *          <Application>
 *              <ApplicationProperties>
 *
 *
 * @author Matt Sochacki
 */
public class ApplicationPropertyHelper
{

    private ApplicationPropertyHelper()
    {

    }

    /**
     *  Use this method to obtain the an application level  property 
     *  @param propertyName the property being queried for.
     */
    public static String getProperty( String propertyName ) throws NoSuchPropertyException
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        ConfigurationService configService = ff.getConfigService();
        PropertyQuery pq = PropertyQuery.queryFor( propertyName ).from( "Application" ).from( configService.getFullName( ff ) );
        Log.debug("Querying for property " + pq.queryString() );
        return configService.getProperty( pq.queryString() );

        
    }

    /**
     *  Use this method to obtain the an application level  property 
     *  @param propertyName the property being queried for.
     */
    public static String getProperty( String propertyName, String defaultValue )
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        ConfigurationService configService = ff.getConfigService();
        PropertyQuery pq = PropertyQuery.queryFor( propertyName ).from( "Application" ).from( configService.getFullName( ff ) );
        Log.debug("Querying for property " + pq.queryString() );
        return configService.getProperty( pq.queryString(), defaultValue );

        
    }

}


