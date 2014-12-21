-- log in Example: conn trade14_04/trade14_04@sbtbd110
--Script to set-up LOG tables for corresponding business tables for Replication to CDB
--1. create log tables
--Script create date: 11/02/2007
--Created by Srinivas Peesapati

DROP TABLE SBT_ORDER_LEG_DTL_LOG CASCADE CONSTRAINTS;

CREATE TABLE SBT_ORDER_LEG_DTL_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
)
;

CREATE INDEX SBT_ORDER_LEG_DTL_LOG_I1 ON SBT_ORDER_LEG_DTL_LOG
(
       DATABASEIDENTIFIER             ASC
)
  ;

DROP TABLE sbt_auct_upd_log;

CREATE TABLE sbt_auct_upd_log (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NULL,
       ACTN_CODE            CHAR(1) NULL,
       ROW_ID               ROWID NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
);

CREATE INDEX SBT_AUCT_UPD_LOG_I1 ON SBT_AUCT_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
);


DROP TABLE SBT_ORDER_UPD_LOG;

CREATE TABLE SBT_ORDER_UPD_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
)
 ;

CREATE INDEX SBT_ORDER_UPD_LOG_I1 ON SBT_ORDER_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
     ;

DROP TABLE SBT_TRADE_RPT_DTL_UPD_LOG;

CREATE TABLE SBT_TRADE_RPT_DTL_UPD_LOG (
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
)
   ;

CREATE INDEX SBT_TRADE_RPT_DTL_UPD_LOG_I1 ON SBT_TRADE_RPT_DTL_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
;


DROP TABLE SBT_TRADE_RPT_UPD_LOG;

CREATE TABLE SBT_TRADE_RPT_UPD_LOG (
       REMOTE_TABLE_NAME     VARCHAR2(30) NOT NULL,
       ACTN_CODE             CHAR(1) NOT NULL,
       DATABASEIDENTIFIER    NUMBER(20) NOT NULL,
       ROW_ID                ROWID NOT NULL,
       LAST_MOD_DATE         DATE DEFAULT SYSDATE NULL
)
   ;

CREATE INDEX SBT_TRADE_RPT_UPD_LOG_I1 ON SBT_TRADE_RPT_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
;

DROP TABLE NBBO_AGENT_MAP_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE NBBO_AGENT_MAP_UPD_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NULL,
       ACTN_CODE            CHAR(1) NULL,
       ROW_ID               ROWID NULL,
       LAST_MOD_DATE        DATE  DEFAULT SYSDATE  NULL
);

CREATE INDEX NBBO_AGENT_MAP_UPD_LOG_I1 ON NBBO_AGENT_MAP_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
;

DROP TABLE SBT_AGENT_ASGN_ORDER_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE SBT_AGENT_ASGN_ORDER_UPD_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE  NULL
);

CREATE INDEX SBT_AGENT_ASGN_OR_U_LOG_I1 ON SBT_AGENT_ASGN_ORDER_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
;

DROP TABLE ORDER_MKT_MNTR_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE ORDER_MKT_MNTR_UPD_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE  NULL
);

CREATE INDEX ORDER_MKT_MNTR_UPD_LOG_I1 ON ORDER_MKT_MNTR_UPD_LOG
(
       DATABASEIDENTIFIER             ASC
)
;

--2. CREATE OR REPLACE TRIGGERs

create or replace trigger NBBO_AGENT_MAP_TRBU
  BEFORE UPDATE
  on NBBOAGENTMAP

  for each row
declare

   p_actn_code  CHAR(1);

begin
       p_actn_code  := 'U';
      INSERT INTO NBBO_AGENT_MAP_UPD_LOG
      (remote_table_name,actn_code,row_id,databaseidentifier)
         VALUES ('NBBOAGENTMAP',p_actn_code,:old.rowid, :old.databaseidentifier) ;
 end;
/

CREATE OR REPLACE TRIGGER SBTORDER_TRBU
 BEFORE
 UPDATE
 ON  SBTORDER
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW declare
     p_actn_code  CHAR(1);

begin

if (:new.state != 8) then
   p_actn_code  := 'U';

   INSERT INTO SBT_ORDER_UPD_LOG
   (remote_table_name,actn_code,row_id,databaseidentifier)
   VALUES ('SBTORDER',p_actn_code,:old.rowid, :old.databaseidentifier) ;
end if;
end;
/

create OR REPLACE trigger SBT_ORDER_LEG_DTL_TRBU
BEFORE UPDATE
  on SBTORDERLEGDETAIL

for each row
declare

    p_actn_code  CHAR(1);
  begin

        p_actn_code  := 'U';
      INSERT INTO SBT_ORDER_LEG_DTL_LOG
      (remote_table_name,actn_code,row_id,databaseidentifier)
      VALUES ('SBTORDERLEGDETAIL',p_actn_code,:old.rowid, :old.databaseidentifier) ;

end;
/

create or replace trigger SBT_TRADEREPORTENTRY_TRBU
  BEFORE UPDATE
   on SBT_TRADEREPORTENTRY

  for each row
declare

   p_actn_code  CHAR(1);
 begin
           p_actn_code  := 'U';
        INSERT INTO SBT_TRADE_RPT_DTL_UPD_LOG
        (remote_table_name,actn_code,row_id,databaseidentifier)
        VALUES ('SBT_TRADEREPORTENTRY',p_actn_code,:old.rowid, :old.databaseidentifier) ;

end;
/

CREATE OR REPLACE TRIGGER SBT_TRADEREPORT_TRBU
      BEFORE UPDATE
       on SBT_TRADEREPORT

      for each row
declare
numrows INTEGER;
p_actn_code  CHAR(1);

begin

            p_actn_code  := 'U';
            INSERT INTO SBT_TRADE_RPT_UPD_LOG
            (remote_table_name,actn_code,row_id,databaseidentifier)
            VALUES ('SBT_TRADEREPORT',p_actn_code,:old.rowid, :old.databaseidentifier) ;

END;
/

create or replace trigger ORDER_MKT_MNTR_TRBU
  BEFORE UPDATE
  on ORDER_MARKET_MONITOR

  for each row
declare

   p_actn_code  CHAR(1);

begin
      p_actn_code  := 'U';
     INSERT INTO ORDER_MKT_MNTR_UPD_LOG
     (remote_table_name,actn_code,row_id,databaseidentifier)
         VALUES ('ORDER_MARKET_MONITOR',p_actn_code,:old.rowid, :old.databaseidentifier) ;

end;
/

create or replace trigger SBT_AGENT_ASGN_ORDER_TRBU
  BEFORE UPDATE
  on SBTAGENTASSIGNEDORDER
  for each row
declare

   p_actn_code  CHAR(1);

begin
        p_actn_code  := 'U';
      INSERT INTO SBT_AGENT_ASGN_ORDER_UPD_LOG
      (remote_table_name,actn_code,row_id,databaseidentifier)
         VALUES ('SBTAGENTASSIGNEDORDER',p_actn_code,:old.rowid, :old.databaseidentifier) ;

end;
/

create or replace trigger SBT_AUCT_TRBU
  BEFORE UPDATE
  on SBTAUCTION
  for each row
declare

   p_actn_code  CHAR(1);

begin
  if (:new.state != 8) then
        p_actn_code  := 'U';
        INSERT INTO sbt_auct_upd_log
        (remote_table_name,actn_code,row_id,databaseidentifier)
        VALUES ('SBTAUCTION',p_actn_code,:old.rowid, :old.databaseidentifier);
  end if;
end;
/

--3.  grant privs to other business tables
--CDB_LINK01
GRANT SELECT, INSERT, UPDATE, DELETE ON NBBO_AGENT_MAP_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON ORDER_MKT_MNTR_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_AGENT_ASGN_ORDER_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_AUCT_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_ORDER_LEG_DTL_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_ORDER_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_TRADE_RPT_DTL_UPD_LOG TO CDB_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_TRADE_RPT_UPD_LOG TO CDB_LINK01;
GRANT SELECT ON MKT_DATA_HIST TO CDB_LINK01;
GRANT SELECT ON NBBOAGENTMAP TO CDB_LINK01;
GRANT SELECT ON ORDER_EXCHANGE_MARKET TO CDB_LINK01;
GRANT SELECT ON ORDER_MARKET_MONITOR TO CDB_LINK01;
GRANT SELECT ON RFQ_HISTORY TO CDB_LINK01;
GRANT SELECT ON SBT_TRADEREPORT TO CDB_LINK01;
GRANT SELECT ON SBT_TRADEREPORTENTRY TO CDB_LINK01;
GRANT SELECT ON SBTAGENTASSIGNEDORDER TO CDB_LINK01;
GRANT SELECT ON SBTORDER TO CDB_LINK01;
GRANT SELECT ON SBTORDERHISTORY TO CDB_LINK01;
GRANT SELECT ON SBTORDERLEGDETAIL TO CDB_LINK01;
GRANT SELECT ON SBTQUOTEHISTORY TO CDB_LINK01;
GRANT SELECT ON SBTAUCTION TO CDB_LINK01;
GRANT SELECT ON INTERMARKETTEXTMESSAGE TO CDB_LINK01;
GRANT SELECT ON ORDERRELATIONSHIP TO CDB_LINK01;
GRANT SELECT ON LINKAGEORDERRELATIONSHIP TO CDB_LINK01;
GRANT SELECT ON BULK_ACTION_DTL TO CDB_LINK01;
GRANT SELECT ON BULK_ACTION_REQUEST TO CDB_LINK01;


-- Verification
-- Test, ATG Tables
--select table_name from user_tables where table_name like '%_LOG';
-- Triggers
--select trigger_name,status from user_triggers ;
--select substr(object_name,1,32),status from user_objects where OBJECT_TYPE like 'TR%' ;
