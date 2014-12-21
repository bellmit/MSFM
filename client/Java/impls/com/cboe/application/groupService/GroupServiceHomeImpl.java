package com.cboe.application.groupService;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.BOInterceptor;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.internalBusinessServices.GroupService;
import com.cboe.interfaces.internalBusinessServices.GroupServiceHome;

/**
 * An implementation of GroupServiceHome for use in the process that implements
 * the product state service for admin.
 */
public class GroupServiceHomeImpl extends ClientBOHome implements GroupServiceHome {
    /**
     * Reference to implemented user service.
     */
    private GroupService groupService = null;

    /**
     * Create an instance of home.
     */
    public GroupServiceHomeImpl() {
        super();
    }

    /**
     * Creates an instance of the service.
     *
     * @see GroupServiceHomeImpl#create
     */
    public GroupService create() {
        if (groupService == null) {
            GroupServiceImpl bo = new GroupServiceImpl();

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            bo.create(String.valueOf(bo.hashCode()));

            //Every bo object must be added to the container.
            addToContainer(bo);

            //The addToContainer call MUST occur prior to creation of the interceptor.
            BOInterceptor boi = null;
            try {
                boi = this.createInterceptor(bo);
                groupService = (GroupService) boi;
            }
            catch (Exception ex) {
                Log.alarm("GroupServiceInterceptor Failed to create interceptor: " + ex);
                groupService = bo;
            }

        }
        return groupService;
    }

    /**
     * @see GroupServiceHome#find
     */
    public GroupService find() {
        return create();
    }

    public void activate() {
        // Method not used
    }

}
