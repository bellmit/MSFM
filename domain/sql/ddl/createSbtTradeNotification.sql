whenever sqlerror continue

drop table SBTTRADENOTIFICATION;


CREATE TABLE SBTTRADENOTIFICATION
(
  DATABASEIDENTIFIER        NUMBER(20)         NOT NULL,
  TNIDENTIFIER		     	VARCHAR2(6)        NOT NULL,
  executingBrokerAcronym    VARCHAR2(5),
  executingBrokerExch	    VARCHAR2(5),
  etnBrokerAcronym	    	VARCHAR2(5),
  etnBrokerExch		     	VARCHAR2(5),
  etnFIRM	     	     	VARCHAR2(5),
  etnFIRMEXCH		     	VARCHAR2(5),
  EVENTTIME		     		NUMBER(20),
  PRODUCTKey		     	NUMBER(20),
  PRODUCTTYPE		     	NUMBER(3),
  CLASSKEY		     		NUMBER(20),
  SPREADINDICATOR	     	CHAR(1),
  SESSIONName		     	VARCHAR2(30),
  SIDE			     		CHAR(1),
  QUANTITY                  NUMBER(20),
  ExecutionPRICE            VARCHAR2(12 BYTE),
  EXECUTIONTIME		     	NUMBER(20),
  ENDORSEMENTTIME	     	NUMBER(20),
  STATE			     		NUMBER(3),
  RESENDINDICATOR	     	CHAR(1),  
  BUNDLECOUNT				NUMBER(3),
  BUNDLERECEIVED			NUMBER(3),
  FIRSTBUNDLESEQNUMBER		NUMBER(10),
  WORKSTATIONID				VARCHAR2(10),
  CREAT_REC_TIME            TIMESTAMP(6)   DEFAULT systimestamp   
)
/* tablespace sbtb_me_data03 */
;

alter table SBTTRADENOTIFICATION
add constraint SBTTRADENOTIFICATION_PK
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

alter table SBTTRADENOTIFICATION
add constraint SBTTRADENOTIFICATION_UK
unique (TNIDENTIFIER)
/* using index tablespace sbtb_me_indx03 */
;
