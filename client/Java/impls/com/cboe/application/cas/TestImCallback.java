/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 16, 2002
 * Time: 2:50:56 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.cas;

import com.cboe.interfaces.intermarketCallback.IntermarketOrderStatusConsumer;
import com.cboe.interfaces.intermarketCallback.NBBOAgentSessionAdminConsumer;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.application.test.ReflectiveStructTester;

public class TestImCallback implements IntermarketOrderStatusConsumer,
        NBBOAgentSessionAdminConsumer
{
    //public void acceptCancelHeldOrderRequest(int classKey, HeldOrderCancelRequestStruct struct)
    public void acceptCancelHeldOrderRequest(ProductKeysStruct productKeys, HeldOrderCancelRequestStruct struct)
    {
        System.out.println();
        System.out.println("TestNullValues HeldOrderCancelRequestStruct: " + ReflectiveStructTester.testNullStruct(struct));

    }

    public void acceptFillRejectReport(OrderFillRejectStruct struct)
    {
        System.out.println();
        System.out.println("TestNullValues acceptFillRejectReport: " + ReflectiveStructTester.testNullStruct(struct));
    }

    public void acceptHeldOrderCanceledReport(HeldOrderCancelReportStruct struct)
    {
        System.out.println();
        System.out.println("TestNullValues acceptHeldOrderCanceledReport: " + ReflectiveStructTester.testNullStruct(struct));
    }
    public void acceptHeldOrderFilledReport(HeldOrderFilledReportStruct struct)
    {
        System.out.println();
        System.out.println("TestNullValues acceptHeldOrderFilledReport: " + ReflectiveStructTester.testNullStruct(struct));
    }

    public void acceptHeldOrderStatus(HeldOrderDetailStruct[] structs)
    {
        System.out.println();
        System.out.println("TestNullValues acceptHeldOrderStatus: " + ReflectiveStructTester.testNullStruct(structs));
    }

    public void acceptNewHeldOrder(HeldOrderDetailStruct struct)
    {
        System.out.println();
        System.out.println("acceptNewHeldOrder " );
    }
    public void acceptForcedOut(String s, int i, String s1)
    {
        System.out.println();
        System.out.println("acceptForcedOut");
    }

    public void acceptReminder(OrderReminderStruct struct, int classKey, String sessionName)
    {
        System.out.println();
        System.out.println("TestNullValues acceptReminder: " + ReflectiveStructTester.testNullStruct(struct));
    }

    public void acceptSatisfactionAlert(com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct struct, int i, String s)
    {
        System.out.println();
        System.out.println("TestNullValues acceptSatisfactionAlert: " + ReflectiveStructTester.testNullStruct(struct));
    }

    public void acceptIntermarketAdminMessage(String s, String s1, ProductKeysStruct productKeysStruct, AdminStruct adminStruct)
    {
        System.out.println("acceptIntermarketAdminMessage");
    }

    public void acceptBroadcastIntermarketAdminMessage(String s, String s1, AdminStruct adminStruct)
    {
        System.out.println("acceptIntermarketAdminMessage");
    }

}
