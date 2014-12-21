set pagesize 100;
set linesize 400;

REPHEADER PAGE CENTER "TODAY's MOC ORDERS WITH REMAINING QUANTITY > 0"

column RECDTIME format a10;
column EXECUTINGORGIVEUPFIRM heading EFirm format a5;
column BRANCH heading Branch format a6;
column BRANCHSEQUENCENUMBER heading BrSeqNo format 99999;
column ORSID heading OrsId format a6;
column CORRESPONDENTFIRM heading CFirm format a5;
column SIDE heading Side format a4;
column PRICE heading Price format a10;
column EffectivePRICE heading EffPrice format a10;
column timeInForce heading TIF format a3;
column STATE heading State format a13;
column ORIGINALQUANTITY heading OrigQty format 9999999;
column ADDEDQUANTITY heading AddedQty format 9999999;
column TRADEDQUANTITY heading TradedQty format 9999999;
column CANCELLEDQUANTITY heading CxlQty format 9999999;
   
select to_char(convert_java_time(RECEIVEDTIME), 'HH24:MI:SS') RECDTIME, EXECUTINGORGIVEUPFIRM,BRANCH,BRANCHSEQUENCENUMBER,ORSID, CORRESPONDENTFIRM,SIDE,PRICE, EffectivePrice, timeInForce, decode(State,
	1, 'Booked',
	2, 'Cancel',
	3, 'Fill',
	4, 'OutCry',
	5, 'InActive',
	6, 'Active',
	7, 'Expired',
	8, 'Purged',
	9, 'Removed',
	10, 'Waiting',
	-1, 'AssgnToAgent',
	-2, 'IPP Exposing',
	-3, 'InTPF',
	-4, 'Auc_Waiting',
	-5, 'Auc_Exposing',
   State) State, ORIGINALQUANTITY, ADDEDQUANTITY, TRADEDQUANTITY, CANCELLEDQUANTITY from sbtorder where
   orderdate=(select to_char(sysdate, 'YYYYMMDD') curdate from dual) and contingencyType=12 and 
   ( ORIGINALQUANTITY + ADDEDQUANTITY - TRADEDQUANTITY - CANCELLEDQUANTITY > 0 ) order by State, ORSID ASC, RECDTIME;
  
