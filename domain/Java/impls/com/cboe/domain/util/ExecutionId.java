package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public final class ExecutionId
{
    public static final char CANCEL_REPORT_STRUCT = 'C';
    public static final char FILLED_REPORT_STRUCT = 'F';

    private String execId;

    private String executingOrGiveupFirm;
    private String branch;
    private int branchSequenceNumber;
    private String correspondentFirm;
    private char type;
    private int transactionSequenceNumber;

    public int hashCode()
    {
        return execId.hashCode();
    }

    public boolean equals(Object theRhs)
    {
        if(hashCode() == theRhs.hashCode() &&
           theRhs instanceof ExecutionId &&
           execId.equals(((ExecutionId) theRhs).execId))
        {
            return true;
        }

        return false;
    }

    // Disable no-arg constructor
    private ExecutionId()
    {
    }

    public ExecutionId(OrderIdStruct orderId, char reportStructType, int aTranSeqNo)
    {
        if(reportStructType != CANCEL_REPORT_STRUCT && reportStructType != FILLED_REPORT_STRUCT)
        {
            throw new IllegalArgumentException("Invalid report struct type.");
        } 

        executingOrGiveupFirm = orderId.executingOrGiveUpFirm.firmNumber;
        branch = orderId.branch;
        branchSequenceNumber = orderId.branchSequenceNumber;
        correspondentFirm = orderId.correspondentFirm;
        type = reportStructType;
        transactionSequenceNumber = aTranSeqNo;
  
        int offset;
        int length;
        byte[] execIdBuffer = new byte[20];

        //    Subfield                   Length (bytes)       Value
        //    --------                   --------------       ------
        //    Firm                             4              Firm from OrderIdStruct
        //    Correspondent Firm               4              Correspondent Firm from OrderIdStruct
        //    Branch                           3              Branch from OrderIdStruct
        //    Branch Sequence Number           4              Branch sequence number from OrderIdStruct
        //    Report Type                      1              'C' = CancelReport, 'F' = FillReport
        //    Transaction Sequence Number      4              Transaction Sequence Number from the event

        offset = 0;

        length = 4;
        writeSpaceFilledString(executingOrGiveupFirm, execIdBuffer, offset, length);                
        offset += length;

        length = 4;
        writeSpaceFilledString(correspondentFirm, execIdBuffer, offset, length);
        offset += length;

        length = 3;
        writeSpaceFilledString(branch, execIdBuffer, offset, length);
        offset += length;

        length = 4;
        writeIntAsString(branchSequenceNumber, execIdBuffer, offset, length);
        offset += length;

        length = 1;
        execIdBuffer[offset] = (byte) type;
        offset += length;

        length = 4;
        writeIntAsString(transactionSequenceNumber, execIdBuffer, offset, length);
        offset += length;

        execId = new String(execIdBuffer);
    }

    public ExecutionId(String anExecId)
    {
        if(anExecId.length() != 20)
        {
            throw new IllegalArgumentException("ExecutionId must be 20 characters long.");
        }        

        execId = anExecId;

        //    Subfield                   Length (bytes)       Value
        //    --------                   --------------       ------
        //    Firm                             4              Firm from OrderIdStruct
        //    Correspondent Firm               4              Correspondent Firm from OrderIdStruct
        //    Branch                           3              Branch from OrderIdStruct
        //    Branch Sequence Number           4              Branch sequence number from OrderIdStruct
        //    Report Type                      1              'C' = CancelReport, 'F' = FillReport
        //    Transaction Sequence Number      4              Transaction Sequence Number from the event

        int offset = 0;

        int length = 4;
        executingOrGiveupFirm = anExecId.substring(offset, offset + length).trim();
        offset += length;

        length = 4;
        correspondentFirm = anExecId.substring(offset, offset + length).trim();
        offset += length;

        length = 3;
        branch = anExecId.substring(offset, offset + length).trim();
        offset += length;

        length = 4;
        branchSequenceNumber = Integer.parseInt(anExecId.substring(offset, offset + length));
        offset += length;

        length = 1;
        type = anExecId.charAt(offset);
        offset += length;

        length = 4;
        transactionSequenceNumber = Integer.parseInt(anExecId.substring(offset, offset + length));
        offset += length;
    }

    public String toString()
    {
        return execId;
    }

    public String getExecId()
    {
        return execId;
    }

    public String getExecutingOrGiveupFirm()
    {
        return executingOrGiveupFirm;
    }

    public String getBranch()
    {
        return branch;
    }

    public int getBranchSequenceNumber()
    {
        return branchSequenceNumber;
    }

    public String getCorrespondentFirm()
    {
        return correspondentFirm;
    }

    public char getReportStructType()
    {
        return type;
    }

    public int getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    /**
     * Copies a number into the specified buffer at the specified offset of
     * specified max length.
     * The byte[] is prefixed with 0's if the length is larger then the value
     * of the number.
     *
     * @param data byte[] array that holds the output integer.
     * @param offset int where the string will be stored in the bave buffer.
     * @param length int length of the field in the output buffer
     * @param value int to be copied in the output buffer.
     */
    public static void writeIntAsString(int value, byte[] data, int offset, int length)
    {
    
        for (int i = length - 1; i >= 0; i--, value /= 10)
        {
            data[offset+i] = (byte)((value == 0) ? '0' : (byte)((value % 10) + '0'));
        }
    }

    /**
     * Copies the string into the specified buffer at the specified offset of
     * specified max length).
     *
     * The output byte[] is left justified and space filled.
     *
     * @author Ravi Vazirani
     *
     * @param data byte[] array that holds the output string.
     * @param offset int where the string will be stored in the bave buffer.
     * @param length int length of the field in the output buffer
     * @param value java.lang.String to be copied in the output buffer.
     */
    public static void writeSpaceFilledString(String value, byte[] data, int offset, int length)
    {
        int valueLength = (value == null) ? 0 : value.length();
    
        for (int i = 0; i < length; i++)
        {
            data[offset + i] = (byte) ((i >= valueLength) ? ' ' : value.charAt(i));
        }
    }

    public static void main(String[] args)
    {
        String executingOrGiveupFirm = "690";
        String branch = "AAA";
        int branchSequenceNumber = 150;
        String correspondentFirm = "LIC";
        char type = 'C';
        int transactionSequenceNumber = 22;

        System.out.println("firm: " + executingOrGiveupFirm);
        System.out.println("corrFirm: " + correspondentFirm);
        System.out.println("branch: " + branch);
        System.out.println("brSeqNo: " + branchSequenceNumber);
        System.out.println("type " + type);
        System.out.println("tranSeqNo " + transactionSequenceNumber);

        OrderIdStruct orderId = new OrderIdStruct();
        orderId.branch = branch;
        orderId.branchSequenceNumber = branchSequenceNumber;
        orderId.correspondentFirm = correspondentFirm;
        orderId.executingOrGiveUpFirm = new ExchangeFirmStruct();
        orderId.executingOrGiveUpFirm.firmNumber = executingOrGiveupFirm;
        orderId.executingOrGiveUpFirm.exchange = "CBOE";
        orderId.orderDate = "12241974";
        orderId.highCboeId = 0;
        orderId.lowCboeId = 0;

        ExecutionId id = new ExecutionId(orderId, ExecutionId.CANCEL_REPORT_STRUCT, transactionSequenceNumber);
        System.out.println("exec Id string: " + id);
        
        ExecutionId newId = new ExecutionId(id.toString());
        System.out.println("new eId string: " + newId);

        String newFirm = newId.getExecutingOrGiveupFirm();
        String newBranch = newId.getBranch();
        int newBranchSeq = newId.getBranchSequenceNumber();
        String newCorr = newId.getCorrespondentFirm();
        char newType = newId.getReportStructType();
        int newTranSeq = newId.getTransactionSequenceNumber();

        assertTrue(newFirm.equals(executingOrGiveupFirm), "firm test: " + executingOrGiveupFirm + " " + newFirm);
        assertTrue(newBranch.equals(branch), "branch test: " + branch + " " + newBranch);
        assertTrue(newBranchSeq == branchSequenceNumber, "branch seq test: " + branchSequenceNumber + " " + newBranchSeq);
        assertTrue(newCorr.equals(correspondentFirm), "correspondent firm test: " + correspondentFirm + " " + newCorr);
        assertTrue(newType == type, "type test: " + type + " " + newType);
        assertTrue(newTranSeq == transactionSequenceNumber, "tran seq test: " + transactionSequenceNumber + " " + newTranSeq);

        orderId.branch = newBranch;
        orderId.branchSequenceNumber = newBranchSeq;
        orderId.correspondentFirm = newCorr;
        orderId.executingOrGiveUpFirm.firmNumber = newFirm;

        ExecutionId anotherId = new ExecutionId(orderId, ExecutionId.CANCEL_REPORT_STRUCT, transactionSequenceNumber);
        executingOrGiveupFirm = anotherId.getExecutingOrGiveupFirm();
        branch = anotherId.getBranch();
        branchSequenceNumber = anotherId.getBranchSequenceNumber();
        correspondentFirm = anotherId.getCorrespondentFirm();
        type = newId.getReportStructType();
        transactionSequenceNumber = newId.getTransactionSequenceNumber();

        assertTrue(newFirm.equals(executingOrGiveupFirm), "firm test: " + executingOrGiveupFirm + " " + newFirm);
        assertTrue(newBranch.equals(branch), "branch test: " + branch + " " + newBranch);
        assertTrue(newBranchSeq == branchSequenceNumber, "branch seq test: " + branchSequenceNumber + " " + newBranchSeq);
        assertTrue(newCorr.equals(correspondentFirm), "correspondent firm test: " + correspondentFirm + " " + newCorr);
        assertTrue(newType == type, "type test: " + type + " " + newType);
        assertTrue(newTranSeq == transactionSequenceNumber, "tran seq test: " + transactionSequenceNumber + " " + newTranSeq);

        assertTrue(newId.equals(anotherId), "equals test: " + newId + " " + anotherId);
        assertTrue(!newId.equals("690 LIC AAA0150F0022"), "equals test 2 :" + newId + "690 LIC AAA0150");
        ExecutionId yetAnotherId = new ExecutionId(orderId, ExecutionId.FILLED_REPORT_STRUCT, transactionSequenceNumber);
        assertTrue(!newId.equals(yetAnotherId), "equals test 3");
        Object o = new Object();
        assertTrue(!newId.equals(o), "equals test 4");
        System.out.println("object hash code: " + o.hashCode() + "id hash code: " + newId.hashCode());
    }

    private static void assertTrue(boolean didTestPass, String test)
    {
        if(!didTestPass)
        {
            System.out.println("Assertion failed!  Test: " + test);
            System.exit(-1);
        }
    }
}
