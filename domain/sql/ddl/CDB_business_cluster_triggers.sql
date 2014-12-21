
DROP TABLE   NBBO_AGENT_MAP_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   NBBO_AGENT_MAP_UPD_LOG
(
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE),
  ACTN_CODE           CHAR(1 BYTE),
  ROW_ID              ROWID,
  LAST_MOD_DATE       DATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON NBBO_AGENT_MAP_UPD_LOG TO CDB_LINK;

DROP TABLE   ORDER_MKT_MNTR_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   ORDER_MKT_MNTR_UPD_LOG
(
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  ORDER_MKT_MNTR_UPD_LOG TO CDB_LINK;

DROP TABLE   SBT_AGENT_ASGN_ORDER_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_AGENT_ASGN_ORDER_UPD_LOG
(
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_AGENT_ASGN_ORDER_UPD_LOG TO CDB_LINK;

DROP TABLE   SBT_AUCT_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_AUCT_UPD_LOG
(
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE),
  ACTN_CODE           CHAR(1 BYTE),
  ROW_ID              ROWID,
  LAST_MOD_DATE       DATE                      DEFAULT sysdate
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_AUCT_UPD_LOG TO CDB_LINK;

DROP TABLE   SBT_ORDER_LEG_DTL_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_ORDER_LEG_DTL_LOG
(
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE                      DEFAULT SYSDATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_ORDER_LEG_DTL_LOG TO CDB_LINK;

DROP TABLE   SBT_ORDER_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_ORDER_UPD_LOG
(
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE                      DEFAULT SYSDATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_ORDER_UPD_LOG TO CDB_LINK;

DROP TABLE   SBT_TRADE_RPT_DTL_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_TRADE_RPT_DTL_UPD_LOG
(
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE                      DEFAULT SYSDATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_TRADE_RPT_DTL_UPD_LOG TO CDB_LINK;

DROP TABLE   SBT_TRADE_RPT_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE   SBT_TRADE_RPT_UPD_LOG
(
  REMOTE_TABLE_NAME   VARCHAR2(30 BYTE)         NOT NULL,
  ACTN_CODE           CHAR(1 BYTE)              NOT NULL,
  DATABASEIDENTIFIER  NUMBER(20)                NOT NULL,
  ROW_ID              ROWID                     NOT NULL,
  LAST_MOD_DATE       DATE                      DEFAULT SYSDATE
)
LOGGING 
NOCACHE
NOPARALLEL
MONITORING;
GRANT SELECT, INSERT, UPDATE, DELETE ON  SBT_TRADE_RPT_UPD_LOG TO CDB_LINK;


-- Indexes
CREATE INDEX   NBBO_AGENT_MAP_UPD_LOG_I1 ON   NBBO_AGENT_MAP_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   ORDER_MKT_MNTR_UPD_LOG_I1 ON   ORDER_MKT_MNTR_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_AGENT_ASGN_OR_U_LOG_I1 ON   SBT_AGENT_ASGN_ORDER_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_AUCT_UPD_LOG_I1 ON   SBT_AUCT_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_ORDER_LEG_DTL_LOG_I1 ON   SBT_ORDER_LEG_DTL_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_ORDER_UPD_LOG_I1 ON   SBT_ORDER_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_TRADE_RPT_DTL_UPD_LOG_I1 ON   SBT_TRADE_RPT_DTL_UPD_LOG
(DATABASEIDENTIFIER)
;


CREATE INDEX   SBT_TRADE_RPT_UPD_LOG_I1 ON   SBT_TRADE_RPT_UPD_LOG
(DATABASEIDENTIFIER)
;

--NBBO_AGENT_MAP_TRBU
--ORDER_MKT_MNTR_TRBU
--SBTORDER_TRBDU
--SBT_AGENT_ASGN_ORDER_TRBU
--SBT_AUCT_TRBU
--SBT_ORDER_LEG_DTL_TRBU
--SBT_TRADEREPORTENTRY_TRBU
--SBT_TRADEREPORT_TRBU
--
CREATE OR REPLACE TRIGGER NBBO_AGENT_MAP_TRBU
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



CREATE OR REPLACE TRIGGER ORDER_MKT_MNTR_TRBU
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



CREATE OR REPLACE TRIGGER SBTORDER_TRBDU
 BEFORE
 UPDATE OR DELETE
 ON  SBTORDER
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW declare
     p_actn_code  CHAR(1);

begin

IF UPDATING THEN
   if (:new.state != 8) then
      p_actn_code  := 'U';
      INSERT INTO SBT_ORDER_UPD_LOG
           (remote_table_name,actn_code,row_id,databaseidentifier)
      VALUES ('SBTORDER',p_actn_code,:old.rowid, :old.databaseidentifier) ;

   end if;
END IF;

IF DELETING THEN

   p_actn_code  := 'D';
   INSERT INTO SBT_ORDER_UPD_LOG
   (remote_table_name,actn_code,row_id,databaseidentifier)
   VALUES ('SBTORDER',p_actn_code,:old.rowid, :old.databaseidentifier) ;

END IF;
END;
/




CREATE OR REPLACE TRIGGER SBT_AGENT_ASGN_ORDER_TRBU
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



CREATE OR REPLACE TRIGGER SBT_AUCT_TRBU
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



CREATE OR REPLACE TRIGGER SBT_ORDER_LEG_DTL_TRBU
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



CREATE OR REPLACE TRIGGER SBT_TRADEREPORTENTRY_TRBU
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

GRANT SELECT, INSERT, UPDATE, DELETE ON NBBO_AGENT_MAP_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON ORDER_MKT_MNTR_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_AGENT_ASGN_ORDER_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_AUCT_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_ORDER_LEG_DTL_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_ORDER_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_TRADE_RPT_DTL_UPD_LOG TO CDB0P00_LINK01;
GRANT SELECT, INSERT, UPDATE, DELETE ON SBT_TRADE_RPT_UPD_LOG TO CDB0P00_LINK01;


-- Verification
-- Test, ATG Tables
--select owner,table_name from dba_tables where table_name like '%_LOG' and owner like 'TR%';
-- Triggers
--select owner,trigger_name,status from dba_triggers where  owner like 'TR%';
--select substr(object_name,1,32),status from user_objects where OBJECT_TYPE like 'TR%' ;






