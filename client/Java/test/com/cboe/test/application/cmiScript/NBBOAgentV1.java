package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiIntermarket.IntermarketManualHandling;
import com.cboe.idl.cmiIntermarket.NBBOAgent;
import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.FillRejectRequestStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.PreOpeningIndicationPriceStruct;
import com.cboe.idl.cmiIntermarketMessages.PreOpeningResponsePriceStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import java.util.Hashtable;
import java.util.Map;

public class NBBOAgentV1
{
    private EngineAccess engineAccess;
    private CasAccess casAccess;
    private NBBOAgent nbboAgentV1;

    private static class ClassKeySession
    {
        public int classKey;
        public String session;
        public ClassKeySession()
        {
            classKey = 0;
            session = "";
        }
        public ClassKeySession(int ck, String s)
        {
            classKey = ck;
            session = s;
        }
        public boolean equals(ClassKeySession other)
        {
            return classKey == other.classKey && session.equals(other.session);
        }
        public int hashCode() // because we changed equals()
        {
            return session.hashCode()+classKey;
        }
    }

    private IntermarketOrderStatusConsumer orderStatusListener;
    private NBBOAgentSessionAdmin nbboAgentSessionAdmin;
    private Map<ClassKeySession, IntermarketManualHandling> intermarketManualHandling;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public NBBOAgentV1(EngineAccess ea, CasAccess ca, NBBOAgent na)
    {
        engineAccess = ea;
        casAccess = ca;
        nbboAgentV1 = na;

        orderStatusListener = new IntermarketOrderStatusConsumer();
        nbboAgentSessionAdmin = new NBBOAgentSessionAdmin();
        intermarketManualHandling =
                new Hashtable<ClassKeySession, IntermarketManualHandling>();
    }

    /** Execute a command on an NBBOAgent object.
     * @param command Words from command line: NBBOAgentV1 function args...
     **/
    public void doCommand(String command[])
    {
        if (command.length < 2)
        {
            Log.message("Command line must have at least object, function");
            return;
        }

        try
        {
            String cmd = command[1];
            // Commands for NBBOAgent
            if (cmd.equalsIgnoreCase("registerAgent"))
            {
                doRegisterAgent(command);
            }
            else if (cmd.equalsIgnoreCase("unregisterAgent"))
            {
                doUnregisterAgent(command);
            }
            // Commands for IntermarketManualHandling
            else if (cmd.equalsIgnoreCase("acceptSatisfactionOrderFill"))
            {
                doAcceptSatisfactionOrderFill(command);
            }
            else if (cmd.equalsIgnoreCase("acceptSatisfactionOrderInCrowdFill"))
            {
                doAcceptSatisfactionOrderInCrowdFill(command);
            }
            else if (cmd.equalsIgnoreCase("acceptSatisfactionOrderReject"))
            {
                doAcceptSatisfactionOrderReject(command);
            }
            else if (cmd.equalsIgnoreCase("acceptCustomerOrderSatisfy"))
            {
                doAcceptCustomerOrderSatisfy(command);
            }
            else if (cmd.equalsIgnoreCase("acceptFillReject"))
            {
                doAcceptFillReject(command);
            }
            else if (cmd.equalsIgnoreCase("acceptHeldOrderReroute"))
            {
                doAcceptHeldOrderReroute(command);
            }
            else if (cmd.equalsIgnoreCase("acceptHeldOrderByClassReroute"))
            {
                doAcceptHeldOrderByClassReroute(command);
            }
            else if (cmd.equalsIgnoreCase("acceptCancelResponse"))
            {
                doAcceptCancelResponse(command);
            }
            else if (cmd.equalsIgnoreCase("acceptHeldOrderFill"))
            {
                doAcceptHeldOrderFill(command);
            }
            else if (cmd.equalsIgnoreCase("getHeldOrderById"))
            {
                doGetHeldOrderById(command);
            }
            else if (cmd.equalsIgnoreCase("getAssociatedOrders"))
            {
                doGetAssociatedOrders(command);
            }
            else if (cmd.equalsIgnoreCase("getOrdersByOrderTypeAndClass"))
            {
                doGetOrdersByOrderTypeAndClass(command);
            }
            else if (cmd.equalsIgnoreCase("getOrdersByOrderTypeAndProduct"))
            {
                doGetOrdersByOrderTypeAndProduct(command);
            }
            else if (cmd.equalsIgnoreCase("acceptPreOpeningIndication"))
            {
                doAcceptPreOpeningIndication(command);
            }
            else if (cmd.equalsIgnoreCase("acceptPreOpeningResponse"))
            {
                doAcceptPreOpeningResponse(command);
            }
            else if (cmd.equalsIgnoreCase("acceptAdminMessage"))
            {
                doAcceptAdminMessage(command);
            }
            else if (cmd.equalsIgnoreCase("lockProduct"))
            {
                doLockProduct(command);
            }
            else if (cmd.equalsIgnoreCase("unlockProduct"))
            {
                doUnlockProduct(command);
            }
            else if (cmd.equalsIgnoreCase("rerouteBookedOrderToHeldOrder"))
            {
                doRerouteBookedOrderToHeldOrder(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOpeningPriceForProduct"))
            {
                doAcceptOpeningPriceForProduct(command);
            }
            else
            {
                Log.message("Unknown function:" + cmd + "  for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    // NBBOAgent

    private void doRegisterAgent(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "session", "forceOverride" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing forceOverride");
            return;
        }
        boolean forceOverride = CommandLine.booleanValue(values[2]);

        NBBOAgentSessionManager agent = nbboAgentV1
                .registerAgent(classKey, session, forceOverride,
                        orderStatusListener._this(),
                        nbboAgentSessionAdmin._this());
        IntermarketManualHandling handling =
                agent.getIntermarketManualHandling();

        // Agent is used only for getting the IntermarketManualHandling object.
        // The IntermarketManualHandling object is used for lots of callbacks
        // and presumably can be revoked when the agent is unregistered, so we
        // keep track of IntermarketManualHandling objects for further use and
        // revocation.
        ClassKeySession key = new ClassKeySession(classKey, session);
        intermarketManualHandling.put(key, handling);
    }

    private void doUnregisterAgent(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "session" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        nbboAgentV1.unregisterAgent(classKey, session,
                orderStatusListener._this(), nbboAgentSessionAdmin._this());

        ClassKeySession key = new ClassKeySession(classKey, session);
        intermarketManualHandling.remove(key);
    }

    // IntermarketManualHandling

    /** Get IntermarketManualHandling object.
     * @param productKey key of product, 0 if classKey is supplied
     * @param classKey key of class, 0 if productKey is supplied
     * @param session trading session name
     * @return object or null. If null, an error message is printed.
     * @throws java.lang.Throwable in case of CAS error.
     */
    private IntermarketManualHandling getHandling(
            int productKey, int classKey, String session) throws Throwable
    {
        if (classKey == 0)
        {
            classKey = casAccess.productKeyToClassKey(productKey);
        }
        ClassKeySession key = new ClassKeySession(classKey, session);
        IntermarketManualHandling handling = intermarketManualHandling.get(key);
        if (handling == null)
        {
            Log.message("No IntermarketManualHandling for classKey:" + classKey
                    + " session:" + session);
        }
        return handling;
    }

    private void doAcceptSatisfactionOrderFill(String command[])
            throws Throwable
    {
        String names[] = { "session", "satisfactionOrderId", "nbboAgentOrder",
                "crowdQuantity", "cancelRemaining", "disposition" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing satisfactionOrderId");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct satisfactionOrderId = (OrderIdStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing nbboAgentOrder");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct nbboAgentOrder = (OrderEntryStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing crowdQuantity");
            return;
        }
        int crowdQuantity = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing cancelRemaining");
            return;
        }
        boolean cancelRemaining = CommandLine.booleanValue(values[4]);

        if (values[5] == null)
        {
            Log.message("Missing disposition");
            return;
        }
        short disposition = Short.parseShort(values[5]);

        IntermarketManualHandling handling =
                getHandling(nbboAgentOrder.productKey, 0, session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptSatisfactionOrderFill(session, satisfactionOrderId,
                nbboAgentOrder, crowdQuantity, cancelRemaining, disposition);
    }

    private void doAcceptSatisfactionOrderInCrowdFill(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "satisfactionOrderId",
                "crowdQuantity", "cancelRemaining", "disposition" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing satisfactionOrderId");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct satisfactionOrderId = (OrderIdStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing crowdQuantity");
            return;
        }
        int crowdQuantity = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing cancelRemaining");
            return;
        }
        boolean cancelRemaining = CommandLine.booleanValue(values[4]);

        if (values[5] == null)
        {
            Log.message("Missing disposition");
            return;
        }
        short disposition = Short.parseShort(values[5]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptSatisfactionOrderInCrowdFill(session, productKey,
                satisfactionOrderId, crowdQuantity, cancelRemaining,
                disposition);
    }

    private void doAcceptSatisfactionOrderReject(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "satisfactionOrderId",
                "resolution" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing satisfactionOrderId");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct satisfactionOrderId = (OrderIdStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing resolution");
            return;
        }
        short resolution = Short.parseShort(values[3]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptSatisfactionOrderReject(session, productKey,
                satisfactionOrderId, resolution);
    }

    private void doAcceptCustomerOrderSatisfy(String command[])
            throws Throwable
    {
        String names[] = { "session",
                "referenceSatisfactonOrderId",  // typo comes from IDL
                "nbboAgentOrder",
                "referenceSatisfactionOrderId"  // allow name without typo
        };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null && values[3] == null)
        {
            Log.message("Missing referenceSatisfactonOrderId");
            return;
        }
        String objName = (values[1] != null) ? values[1] : values[3];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct referenceSatisfactonOrderId = (OrderIdStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing nbboAgentOrder");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct nbboAgentOrder = (OrderEntryStruct) o;

        IntermarketManualHandling handling =
                getHandling(nbboAgentOrder.productKey, 0, session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptCustomerOrderSatisfy(session,
                referenceSatisfactonOrderId, nbboAgentOrder);
    }

    private void doAcceptFillReject(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "fillRejectRequest" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing fillRejectRequest");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof FillRejectRequestStruct))
        {
            Log.message("Not an FillRejectRequestStruct:" + objName);
            return;
        }
        FillRejectRequestStruct fillRejectRequest = (FillRejectRequestStruct) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptFillReject(session, productKey, fillRejectRequest);
    }

    private void doAcceptHeldOrderReroute(String command[])
            throws Throwable
    {
        String names[] = { "heldOrderId", "session", "productKey",
                "nbboProtectionFlag" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing heldOrderId");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct heldOrderId = (OrderIdStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing nbboProtectionFlag");
            return;
        }
        boolean nbboProtectionFlag = CommandLine.booleanValue(values[3]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptHeldOrderReroute(heldOrderId, session, productKey,
                nbboProtectionFlag);
    }

    private void doAcceptHeldOrderByClassReroute(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "session", "nbboProtectionFlag" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing nbboProtectionFlag");
            return;
        }
        boolean nbboProtectionFlag = CommandLine.booleanValue(values[2]);

        IntermarketManualHandling handling = getHandling(0, classKey, session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptHeldOrderByClassReroute(classKey, session,
                nbboProtectionFlag);
    }

    private void doAcceptCancelResponse(String command[])
            throws Throwable
    {
        String names[] = { "orderId", "cancelRequestId", "session",
                "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing orderId");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct orderId = (OrderIdStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing cancelRequestId");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof CboeIdStruct))
        {
            Log.message("Not a CboeIdStruct:" + objName);
            return;
        }
        CboeIdStruct cancelRequestId = (CboeIdStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[2];

        if (values[3] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[3]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptCancelResponse(orderId, cancelRequestId, session,
                productKey);
    }

    private void doAcceptHeldOrderFill(String command[])
            throws Throwable
    {
        String names[] = { "heldOrderId", "session", "nbboAgentOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing heldOrderId");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct heldOrderId = (OrderIdStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing nbboAgentOrder");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct nbboAgentOrder = (OrderEntryStruct) o;

        IntermarketManualHandling handling = 
                getHandling(nbboAgentOrder.productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptHeldOrderFill(heldOrderId, session, nbboAgentOrder);
    }

    private void doGetHeldOrderById(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "orderId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing orderId");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct orderId = (OrderIdStruct) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        HeldOrderDetailStruct hod = handling.getHeldOrderById(session, productKey, orderId);
        Log.message(Struct.toString(hod));
    }

    private void doGetAssociatedOrders(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "orderId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing orderId");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct orderId = (OrderIdStruct) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        OrderStruct oseq[] =
                handling.getAssociatedOrders(session, productKey, orderId);
        Log.message(Struct.toString(oseq));
    }

    private void doGetOrdersByOrderTypeAndClass(String command[])
            throws Throwable
    {
        String names[] = { "session", "classKey", "exchanges", "originTypes",
                "orderFlowDirection" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing exchanges");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof String[]))
        {
            Log.message("Not an ExchangeSequence:" + objName);
            return;
        }
        String exchanges[] = (String[]) o;

        if (values[3] == null)
        {
            Log.message("Missing originTypes");
            return;
        }
        objName = values[3];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof char[]))
        {
            Log.message("Not an OriginTypeSequence:" + objName);
            return;
        }
        char originTypes[] = (char[]) o;

        if (values[4] == null)
        {
            Log.message("Missing orderFlowDirection");
            return;
        }
        short orderFlowDirection = Short.parseShort(values[4]);

        IntermarketManualHandling handling = getHandling(0, classKey, session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        OrderStruct oseq[] = handling.getOrdersByOrderTypeAndClass(
                session, classKey, exchanges, originTypes, orderFlowDirection);

        Log.message(Struct.toString(oseq));
    }

    private void doGetOrdersByOrderTypeAndProduct(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "exchanges", "originTypes",
                "orderFlowDirection" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing exchanges");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof String[]))
        {
            Log.message("Not an ExchangeSequence:" + objName);
            return;
        }
        String exchanges[] = (String[]) o;

        if (values[3] == null)
        {
            Log.message("Missing originTypes");
            return;
        }
        objName = values[3];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof char[]))
        {
            Log.message("Not an OriginTypeSequence:" + objName);
            return;
        }
        char originTypes[] = (char[]) o;

        if (values[4] == null)
        {
            Log.message("Missing orderFlowDirection");
            return;
        }
        short orderFlowDirection = Short.parseShort(values[4]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        OrderStruct oseq[] = handling.getOrdersByOrderTypeAndProduct(
                session, productKey, exchanges, originTypes, orderFlowDirection);

        Log.message(Struct.toString(oseq));
    }

    private void doAcceptPreOpeningIndication(String command[])
            throws Throwable
    {
        String names[] = { "session", "originatingExchange", "productKey",
                "preOpenIndication" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing originatingExchange");
            return;
        }
        String originatingExchange = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing preOpenIndication");
            return;
        }
        String objName = values[3];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof PreOpeningIndicationPriceStruct))
        {
            Log.message("Not a PreOpeningIndicationPriceStruct:" + objName);
            return;
        }
        PreOpeningIndicationPriceStruct preOpenIndication =
                (PreOpeningIndicationPriceStruct) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptPreOpeningIndication(session, originatingExchange,
                productKey, preOpenIndication);
    }

    private void doAcceptPreOpeningResponse(String command[])
            throws Throwable
    {
        String names[] = { "session", "originatingExchange",
                "destinationExchange", "productKey", "preOpenResponses" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing originatingExchange");
            return;
        }
        String originatingExchange = values[1];

        if (values[2] == null)
        {
            Log.message("Missing destinationExchange");
            return;
        }
        String destinationExchange = values[2];

        if (values[3] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing preOpenResponses");
            return;
        }
        String objName = values[4];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof PreOpeningResponsePriceStruct[]))
        {
            Log.message("Not a PreOpeningResponsePriceStructSequence:" + objName);
            return;
        }
        PreOpeningResponsePriceStruct preOpenResponses[] =
                (PreOpeningResponsePriceStruct[]) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptPreOpeningResponse(session, originatingExchange,
                destinationExchange, productKey, preOpenResponses);
    }

    private void doAcceptAdminMessage(String command[])
            throws Throwable
    {
        String names[] = { "session", "destinationExchange", "productKey",
                "adminMessage" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing destinationExchange");
            return;
        }
        String destinationExchange = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing adminMessage");
            return;
        }
        String objName = values[3];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof AdminStruct))
        {
            Log.message("Not an AdminStruct:" + objName);
            return;
        }
        AdminStruct adminMessage = (AdminStruct) o;

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptAdminMessage(session, destinationExchange, productKey,
                adminMessage);
    }

    private void doLockProduct(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        boolean result = handling.lockProduct(session, productKey);
        Log.message(Boolean.valueOf(result).toString());
    }

    private void doUnlockProduct(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        boolean result = handling.unlockProduct(session, productKey);
        Log.message(Boolean.valueOf(result).toString());
    }

    private void doRerouteBookedOrderToHeldOrder(String command[])
            throws Throwable
    {
        String names[] = { "bookedOrderId", "session", "productKey",
                "nbboProtectionFlag" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing bookedOrderId");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct bookedOrderId = (OrderIdStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing nbboProtectionFlag");
            return;
        }
        boolean nbboProtectionFlag = CommandLine.booleanValue(values[3]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        boolean result = handling.rerouteBookedOrderToHeldOrder(
                bookedOrderId, session, productKey, nbboProtectionFlag);
        Log.message(Boolean.valueOf(result).toString());
    }

    private void doAcceptOpeningPriceForProduct(String command[])
            throws Throwable
    {
        String names[] = { "openingPrice", "session", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing openingPrice");
            return;
        }
        PriceStruct openingPrice = Struct.makePriceStruct(values[0]);
        if (openingPrice == null)
        {
            return; // error already reported, leave now.
        }

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        IntermarketManualHandling handling = getHandling(productKey,0,session);
        if (handling == null)
        {
            return; // error already reported, leave now.
        }

        handling.acceptOpeningPriceForProduct(
                openingPrice, session, productKey);
    }
}
