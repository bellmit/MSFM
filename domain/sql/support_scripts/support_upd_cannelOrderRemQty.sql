UPDATE SBTORDER
SET STATE = 8,
 CANCELLEDQUANTITY = ORIGINALQUANTITY + ADDEDQUANTITY - TRADEDQUANTITY
WHERE BRANCH  = '&1'
AND BRANCHSEQUENCENUMBER  = &2 
AND ORDERDATE = '&3'
--'20030918'
/
