--Script to set-up LOG tables for corresponding Global tables for Replication to CDB
-- log in Example: conn trade14_07/trade14_07@sbtbd110
--
--Script create date: 11/02/2007
--Created by Srinivas Peesapati

DROP TABLE PROD_CLASS_DISS_LOG CASCADE CONSTRAINTS;

DROP TABLE PRODUCT_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE PRODUCT_UPD_LOG (
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
)
;

DROP TABLE SESSION_ELEMENT_PROD_UPD_LOG CASCADE CONSTRAINTS;

CREATE TABLE SESSION_ELEMENT_PROD_UPD_LOG (
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE DEFAULT SYSDATE NULL
)
;

CREATE TABLE sbt_global_table_updt_log (
       table_name           VARCHAR2(30) NOT NULL,
       table_upd_ind        VARCHAR2(1) NOT NULL
)
;
ALTER TABLE sbt_global_table_updt_log
       ADD  ( CONSTRAINT sbt_global_table_updt_log_pk PRIMARY KEY (
              table_name) USING INDEX
              ) nologging;

--2.2 create stored procedure sp_ins_global_log for complete refresh

CREATE OR REPLACE  PROCEDURE  SP_INS_GLOBAL_LOG
(
	p_table_name 	IN	sbt_global_table_updt_log.table_name%TYPE,
	p_status	OUT	INTEGER
)
AS
	v_table_upd_ind 	sbt_global_table_updt_log.table_upd_ind%TYPE;
	v_table_name	 	sbt_global_table_updt_log.table_name%TYPE;
	g_sqlcode       	NUMBER ;
	g_sqlerrm         	VARCHAR2(120) ;
	g_table_name           	VARCHAR2(30) ;
BEGIN
	v_table_name	:= p_table_name;
	SELECT table_upd_ind
	  INTO v_table_upd_ind
	  FROM sbt_global_table_updt_log
	 WHERE table_name = v_table_name
	;
	IF v_table_upd_ind <> 'Y'
	THEN
		UPDATE sbt_global_table_updt_log
		   SET table_upd_ind = 'Y'
		 WHERE table_name = v_table_name;
	END IF;
	p_status := 0;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		INSERT INTO sbt_global_table_updt_log (table_name, table_upd_ind)
			 VALUES (v_table_name, 'Y');
		p_status  := 0;
	WHEN OTHERS THEN
		p_status  := -1;
                g_sqlcode := sqlcode ;
            	g_sqlerrm := SUBSTR(sqlerrm, 1, 120) ;
            	g_table_name := p_table_name ;
/*		DBMS_OUTPUT.PUT_LINE('g_table_name.....'|| g_table_name) ;
		DBMS_OUTPUT.PUT_LINE('g_sqlcode........'|| TO_CHAR(g_sqlcode)) ;
		DBMS_OUTPUT.PUT_LINE('g_sqlerrm........'|| g_sqlerrm) ;
*/
END ;
/
grant execute on sp_ins_global_log to db_owner;


--2.3 create triggers to invoke the store procedure for complete refresh.

create or replace trigger EXCHANGE_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on EXCHANGE

/* ERwin Builtin Mon Mar 10 15:30:19 2003 */
/* default body for EXCHANGE_TRAUID */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'EXCHANGE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger FIRM_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on FIRM

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'FIRM';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger EXCHANGE_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on EXCHANGE
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'EXCHANGE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger MKT_DATA_SUMMARY_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on MKT_DATA_SUMMARY

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'MKT_DATA_SUMMARY';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger PRODUCT_TRBU
  BEFORE UPDATE
  on PRODUCT

  for each row
/* ERwin Builtin Thu Aug 18 12:49:51 2005 */
/* default body for PRODUCT_TRBU */
declare numrows INTEGER;
p_actn_code  CHAR(1);
v_count INTEGER;
begin
     p_actn_code  := 'U';
     INSERT INTO PRODUCT_UPD_LOG
     (remote_table_name,actn_code,row_id,databaseidentifier)
     VALUES ('PRODUCT',p_actn_code,:old.rowid, :old.databaseidentifier) ;
  end;
/

create or replace trigger PROD_CLASS_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on PROD_CLASS

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger PROD_COMP_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on PROD_COMP



declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_COMP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger PROD_DESC_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on PROD_DESC

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_DESC';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger PROD_TYPE_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on PROD_TYPE

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_TYPE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger RECAP_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on RECAP


/* ERwin Builtin Mon Mar 10 15:37:11 2003 */
/* default body for RECAP_TRAIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'RECAP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger RPT_CLASS_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on RPT_CLASS

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'RPT_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger SBT_ALERT_TRAIUD
  AFTER INSERT OR UPDATE OR DELETE
  on SBT_ALERT

  for each row

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_ALERT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger SBT_ALERT_EXCH_MKT_TRAIUD
  AFTER  INSERT OR UPDATE OR DELETE
  on SBT_ALERT_EXCHANGE_MARKET

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_ALERT_EXCHANGE_MARKET';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger SBT_USER_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on SBT_USER

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_USER';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger SESSION_ELEMENT_CLASS_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on SESSION_ELEMENT_CLASS

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SESSION_ELEMENT_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger TRADE_SESS_EVENT_HIST_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on TRADINGSESSIONEVENTHIST
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADINGSESSIONEVENTHIST';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
create or replace trigger TEMPLATE_CLASS_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on TEMPLATE_CLASS

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TEMPLATE_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger TRADING_PROPERTY_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on TRADING_PROPERTY

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_PROPERTY';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger TRADING_SESSION_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on TRADING_SESSION

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger TRADING_SESS_TEMPL_TRBIUD
  AFTER DELETE or INSERT or UPDATE
  on TRADING_SESSION_TEMPLATE
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION_TEMPLATE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger TRADING_SESSION_ELEMENT_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on TRADING_SESSION_ELEMENT

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION_ELEMENT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


drop TABLE TRADE_THRU_ORDER_LOG;

CREATE TABLE TRADE_THRU_ORDER_LOG (
       DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
       REMOTE_TABLE_NAME    VARCHAR2(30) NOT NULL,
       ACTN_CODE            CHAR(1) NOT NULL,
       ROW_ID               ROWID NOT NULL,
       LAST_MOD_DATE        DATE NULL
);

CREATE INDEX SBT_AGENT_ASGN_ORDER_UPD_LOG_1 ON TRADE_THRU_ORDER_LOG
(
       DATABASEIDENTIFIER             ASC
);

DROP trigger PROD_CLASS_DISS_TRBU;

create or replace trigger LOGIN_USERID_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on LOGIN_USERID

  for each row

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'LOGIN_USERID';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger ASGN_CLASS_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on ASGN_CLASS

  for each row

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'ASGN_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

create or replace trigger USER_FIRM_AFFL_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on USER_FIRM_AFFILIATION

  for each row

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'USER_FIRM_AFFILIATION';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger GROUP_ELEMENT_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on GROUP_ELEMENT
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'GROUP_ELEMENT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


create or replace trigger GROUP_RELATIONSHIP_TRAUID
  AFTER DELETE or INSERT or UPDATE
  on GROUP_RELATIONSHIP
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'GROUP_RELATIONSHIP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

-- 2.4 grant to link_user for global tables

--CDB_LINK01
GRANT SELECT, DELETE, UPDATE  ON PRODUCT_UPD_LOG TO CDB_LINK01;
GRANT SELECT, DELETE, UPDATE, INSERT  ON sbt_global_table_updt_log TO CDB_LINK01;
GRANT SELECT, DELETE, UPDATE  ON PRODUCT_UPD_LOG TO CDB_LINK01;
GRANT SELECT, DELETE, UPDATE, INSERT  ON sbt_global_table_updt_log TO CDB_LINK01;
GRANT SELECT  ON BUS_DAY TO CDB_LINK01;
GRANT SELECT  ON EXCHANGE TO CDB_LINK01;
GRANT SELECT  ON FIRM TO CDB_LINK01;
GRANT SELECT  ON MKT_DATA_SUMMARY TO CDB_LINK01;
GRANT SELECT  ON PROD_CLASS TO CDB_LINK01;
GRANT SELECT  ON PROD_COMP TO CDB_LINK01;
GRANT SELECT  ON PROD_DESC TO CDB_LINK01;
GRANT SELECT  ON PROD_TYPE TO CDB_LINK01;
GRANT SELECT  ON PRODUCT TO CDB_LINK01;
GRANT SELECT  ON RECAP TO CDB_LINK01;
GRANT SELECT  ON RPT_CLASS TO CDB_LINK01;
GRANT SELECT  ON SBT_ALERT TO CDB_LINK01;
GRANT SELECT  ON SBT_ALERT_EXCHANGE_MARKET TO CDB_LINK01;
GRANT SELECT  ON SBT_USER TO CDB_LINK01;
GRANT SELECT  ON SESSION_ELEMENT_CLASS TO CDB_LINK01;
GRANT SELECT  ON SESSION_ELEMENT_PRODUCT TO CDB_LINK01;
GRANT SELECT  ON TEMPLATE_CLASS TO CDB_LINK01;
GRANT SELECT  ON TRADING_PROPERTY TO CDB_LINK01;
GRANT SELECT  ON TRADING_SESSION TO CDB_LINK01;
GRANT SELECT  ON TRADING_SESSION_ELEMENT TO CDB_LINK01;
GRANT SELECT  ON TRADING_SESSION_TEMPLATE TO CDB_LINK01;
GRANT SELECT  ON TRADINGSESSIONEVENTHIST TO CDB_LINK01;
GRANT SELECT  ON LOGIN_USERID TO CDB_LINK01;
GRANT SELECT  ON ASGN_CLASS TO CDB_LINK01;
GRANT SELECT  ON USER_FIRM_AFFILIATION TO CDB_LINK01;
GRANT SELECT  ON TRADEDTHROUGHORDER to CDB_LINK01;
GRANT SELECT  ON GROUP_ELEMENT TO CDB_LINK01;
GRANT SELECT  ON GROUP_RELATIONSHIP TO CDB_LINK01;
GRANT SELECT  ON PROPERTY_GROUP TO CDB_LINK01;
GRANT SELECT  ON PROPERTY TO CDB_LINK01;
GRANT SELECT  ON TRADING_SESSION_TEMPLATE TO CDB_LINK01;
GRANT SELECT  ON   EXCHANGE TO CDB_LINK01;


-- Verification
-- Test, ATG Tables
--select table_name from user_tables where table_name like '%_LOG';
-- Triggers
--select trigger_name,status from user_triggers ;
--select substr(object_name,1,32),status from user_objects where OBJECT_TYPE like 'TR%' or OBJECT_NAME Like '%SP_INS_GLOBAL_LOG%';
