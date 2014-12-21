whenever sqlerror exit failure

ALTER TABLE sbtorderhistory 
ADD (
		EXCHANGENAME   VARCHAR2(6)
	);
	
ALTER TABLE sbtorderhistory
MODIFY (
         auctionType VARCHAR2(3)
     	);
