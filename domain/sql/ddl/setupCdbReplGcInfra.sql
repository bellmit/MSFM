--Script to set-up LOG tables for corresponding Global tables for Replication to CDB
--Script create date: 11/02/2007
--Created by Srinivas Peesapati
-- conn GLOBAL INFRA

CREATE TABLE sbt_global_table_updt_log (
       table_name           VARCHAR2(30) NOT NULL,
       table_upd_ind        VARCHAR2(1) NOT NULL
)
;
ALTER TABLE sbt_global_table_updt_log
       ADD  ( CONSTRAINT sbt_global_table_updt_log_pk PRIMARY KEY (
              table_name) USING INDEX
              ) nologging;

-- ***********************************


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

create or replace trigger USERHISTORY_TRAIUD
  AFTER DELETE or INSERT or UPDATE
  on USERHISTORY
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'USERHISTORY';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/


--CDB_LINK01
grant select,update,delete,insert on sbt_global_table_updt_log to CDB_LINK01;
grant select on USERHISTORY to CDB_LINK01;
grant select on SBT_ALERT to CDB_LINK01; 
