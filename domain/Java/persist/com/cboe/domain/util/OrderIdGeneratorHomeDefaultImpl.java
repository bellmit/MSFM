package com.cboe.domain.util;


import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.OrderIdGeneratorHome;
import com.cboe.util.FormatNotFoundException;
/**
 * This is the default (non-persistent) implementation of OrderIdGenerator.
 * NullOrderIdGenerator provides functionality of incrementing the branch
 * and setting appropriate values to various attributes of OrderId.
 * The functionality to retrieve and save OrderIdSeed to
 * database is provided in subclass OrderIdGenerator.
 * @author Ravi Rade
 * @see OrderIdGenerator
 */

public class OrderIdGeneratorHomeDefaultImpl extends BOHome
    implements OrderIdGeneratorHome {

	public static String DATE_FORMAT_NAME = "DateFormatForOrderIdGenerator";
	public static String DATE_FORMAT_PATTERN = "yyyyMMdd";
    public static int MAX_BRANCH_SEQUENCE_NUMBER = 9999;
    public static final String STARTING_BRANCH = "AAA";

    private String correspondentFirm = new String();
    protected String branch = new String();
    private int branchSequenceNumber = 0;

    public OrderIdGeneratorHomeDefaultImpl(){
    }

    /**
     * Initialize the data formatter and correspondantFirm property.
     */
    public void initialize(){
        //Add date formatter to be used later.
        DateWrapper.addDateFormatter(DATE_FORMAT_NAME,DATE_FORMAT_PATTERN);
        try {
            this.correspondentFirm = 
                getProperty("correspondentFirm");
        }catch (NoSuchPropertyException e) {
            throw new FatalFoundationFrameworkException(e, "correspondentFirm property not found.");
        } 
        //branchSequenceNumber will always be 0 to start with. It will be incremented to 1 before   
        //assigning to branchSequenceNumber of orderIdStruct.
        this.branchSequenceNumber = 0;         
    }


    public synchronized void goMaster(boolean failover){
        try {
            this.branch = getNextBranch();
        }catch (SystemException e) {
            Log.exception(this,"System Exception thrown while reading DB for OrderIdSeed.", e);
        }catch(PersistenceException e) {
            Log.exception("Persistence Exception thrown while reading DB for OrderIdSeed.", e);
       }        
    }

    public OrderIdStruct generateOrderId(OrderStruct anOrder)
    {        
        return generateOrderId(anOrder.orderId.executingOrGiveUpFirm);
    }
    
    public OrderIdStruct generateOrderId(ExchangeFirmStruct executingOrGiveUpFirm)
    {
        OrderIdStruct orderIdStruct = OrderStructBuilder.buildOrderIdStruct();
        //Popluate orderIdStrcut Fields
        
        orderIdStruct.executingOrGiveUpFirm = executingOrGiveUpFirm;

        setBranchAndBranchSequenceNumber(orderIdStruct);

        orderIdStruct.correspondentFirm = this.correspondentFirm;

        try {
            orderIdStruct.orderDate = new DateWrapper().format(DATE_FORMAT_NAME);
        }catch (FormatNotFoundException e){
            Log.exception(this, "Date format is not set", e);
            orderIdStruct.orderDate = new String();
            //This exception will not be raised ever as DATE_FORMAT_NAME format will exist for sure
            // as it is setup in constructor.
        }

        orderIdStruct.highCboeId = 0;
        orderIdStruct.lowCboeId = 0;    

        return orderIdStruct;
    }

    protected synchronized void setBranchAndBranchSequenceNumber(OrderIdStruct orderIdStruct) {

        if (this.branchSequenceNumber >= MAX_BRANCH_SEQUENCE_NUMBER) {
            //increment and save branch.
            this.branch = incrementBranch(this.branch);
            saveBranch(this.branch);
            this.branchSequenceNumber = 0;
        }

        //increment branchSequenceNumber.
        this.branchSequenceNumber++;
        orderIdStruct.branchSequenceNumber = this.branchSequenceNumber;
        orderIdStruct.branch = this.branch;
    }

    /**
     * This method will find next valid Branch.
     * Increment examples 
     * AAA --> AAB   
     * CFR --> CFS   
     * DDD --> DDE   
     * AAZ --> ABA   
    */
    protected synchronized String incrementBranch(String branch) {
        char[] charArray = new char[3];
        charArray[0] = branch.charAt(0);
        charArray[1] = branch.charAt(1);
        charArray[2] = branch.charAt(2);            

        if (branch.equals("ZZZ")){
            Log.alarm("All Branches, upto ZZZ are used, resetting to AAA");
            return getStartingBranch();
        }

        if (branch.charAt(2) == 'Z') {
            if (branch.charAt(1) == 'Z'){
                charArray[0]++;
                charArray[1] = 'A';
            }else{
                charArray[1]++;
            }
            charArray[2] = 'A';
        } else{            
            charArray[2]++;
        }
        return new String(charArray);
        
    }

    protected void saveBranch(String nextBranch){
        //implementation will be provided by OrderGeneratorHomeImpl
    }

    protected String getNextBranch() throws SystemException,PersistenceException   {
        //implementation will be provided by OrderGeneratorHomeImpl
        return getStartingBranch();
    }

    protected String getStartingBranch() {
        return STARTING_BRANCH;
    }

    //For unit testing :
    public static void main(String[] args){

        OrderIdGeneratorHomeDefaultImpl gen = new OrderIdGeneratorHomeDefaultImpl();
        gen.branch = "AAA";
        gen.branchSequenceNumber = 1;
//        gen.goMaster(true);
        gen.MAX_BRANCH_SEQUENCE_NUMBER = 3;

        for (int i = 0 ; i < 20;i++) {
            OrderIdStruct orderId = gen.generateOrderId(new OrderStruct());
            System.out.println("Branch" + orderId.branch + " - " + orderId.branchSequenceNumber);
        }
        int j = 0;
    }

}
