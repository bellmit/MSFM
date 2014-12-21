package com.cboe.interfaces.domain.linkageClassGate;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: woltersm
 * Date: Sep 21, 2005
 * Time: 9:19:18 AM
 * To change this template use File | Settings | File Templates.
 *
 * This is the Home for the Linkage class gate.  This home will keep a hashtable of all LinkageClassGates for all
 * classes.  Individual instances of the LinkageClassGates can be retrieved by other services by calling the
 * getGate method of the LinkageClassGateHome.  The individual gates can then be opened, closed, or queried
 * without going through the home.
 *
 */
public interface LinkageClassGateHome {

    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "LinkageClassGateHome";

    /**
     * Returns a reference to the LinkageClassGateImpl for the class passed in.  If no classGate exists
     * for this class, one will be instantiated by this method.
     *
     * @return LinkageClassGate
     * @param classKey
     */
    public LinkageClassGate getGate(int classKey);

    /**
     * Returns true if the gate is currently closed or false if it is open
     * for the given exchange/class combo
     * 
     * @return boolean
     * @param exchange
     * @param classKey
     */
    public boolean isClosed(String Exchange, int classKey);

}


