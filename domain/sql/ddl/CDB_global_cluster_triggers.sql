/***** There are a total of 26 triggers captured from SBT production[DB=SBTGP01A:server=prdgcd2a] as of June 1,2009 */

/**** This file contains changes to below 5 triggers - to change from statement level triggers from row level *********/

1. "GLOBAL_SERVICE_OWNER"."USER_FIRM_AFFL_TRAUID"
2. "GLOBAL_SERVICE_OWNER"."TRADING_SESS_TEMPL_TRAUID"
3. "GLOBAL_SERVICE_OWNER"."TRADE_THRU_ORDER_TRAUID"
4. "GLOBAL_SERVICE_OWNER"."ASGN_CLASS_TRAUID"
5. "GLOBAL_SERVICE_OWNER"."LOGIN_USERID_TRAUID"

/**************************************************************/

/*************** Trigger -1 ***********************/

CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."ASGN_CLASS_TRAUID"
AFTER DELETE or INSERT or UPDATE
on ASGN_CLASS
/* for each row -- changed to statement level trigger-Ravi Babu Jul 31, 2009  */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'ASGN_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."ASGN_CLASS_TRAUID" ENABLE;


/*************** Trigger -2 ***********************/


  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."EXCHANGE_TRAUID"
  AFTER DELETE or INSERT or UPDATE
  on EXCHANGE
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'EXCHANGE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."EXCHANGE_TRAUID" ENABLE;



/*************** Trigger -3 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."FIRM_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on FIRM
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'FIRM';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."FIRM_TRAIUD" ENABLE;


/*************** Trigger -4 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."GROUP_ELEMENT_TRAUID"
  AFTER DELETE or INSERT or UPDATE
  on GROUP_ELEMENT
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'GROUP_ELEMENT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."GROUP_ELEMENT_TRAUID" ENABLE;


/*************** Trigger -5 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."GROUP_RELATIONSHIP"
  AFTER DELETE or INSERT or UPDATE
  on GROUP_RELATIONSHIP
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'GROUP_RELATIONSHIP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."GROUP_RELATIONSHIP" ENABLE;


/*************** Trigger -6 ***********************/


CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."LOGIN_USERID_TRAUID"
AFTER DELETE or INSERT or UPDATE
on LOGIN_USERID
/* for each row -- changed to statement level trigger-Ravi Babu Jul 31, 2009  */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'LOGIN_USERID';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."LOGIN_USERID_TRAUID" ENABLE;




/*************** Trigger -7 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."MKT_DATA_SUMMARY_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on MKT_DATA_SUMMARY
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'MKT_DATA_SUMMARY';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."MKT_DATA_SUMMARY_TRAIUD" ENABLE;




/*************** Trigger -8 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."PRODUCT_TRBU"
  BEFORE UPDATE
  on PRODUCT
  for each row
/* ERwin Builtin Thu Jun 30 13:46:31 2005 */
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
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."PRODUCT_TRBU" ENABLE;




/*************** Trigger -9 ***********************/


  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_CLASS_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on PROD_CLASS
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/

ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_CLASS_TRAIUD" ENABLE;



/*************** Trigger -10 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_COMP_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on PROD_COMP
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_COMP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_COMP_TRAIUD" ENABLE;



/*************** Trigger -11 ***********************/



  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_DESC_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on PROD_DESC
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_DESC';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_DESC_TRAIUD" ENABLE;





/*************** Trigger -12 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_TYPE_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on PROD_TYPE
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'PROD_TYPE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."PROD_TYPE_TRAIUD" ENABLE;


/*************** Trigger -13 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."RECAP_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on RECAP
/* ERwin Builtin Tue Mar 04 14:19:26 2003 */
/* default body for RECAP_TRAIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'RECAP';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."RECAP_TRAIUD" ENABLE;


/*************** Trigger -14 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."RPT_CLASS_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on RPT_CLASS
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'RPT_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."RPT_CLASS_TRAIUD" ENABLE;

/*************** Trigger -15 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_ALERT_EXCH_MKT_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on SBT_ALERT_EXCHANGE_MARKET
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_ALERT_EXCHANGE_MARKET';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_ALERT_EXCH_MKT_TRAIUD" ENABLE

/*************** Trigger -16 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_ALERT_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on SBT_ALERT

declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_ALERT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_ALERT_TRAIUD" ENABLE;

/*************** Trigger -17***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_USER_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on SBT_USER
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SBT_USER';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."SBT_USER_TRAIUD" ENABLE;

/*************** Trigger -18 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."SESSION_ELEMENT_CLASS_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on SESSION_ELEMENT_CLASS
/* ERwin Builtin Tue Mar 04 14:20:24 2003 */
/* default body for TEMPLATE_CLASS_TRAIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'SESSION_ELEMENT_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."SESSION_ELEMENT_CLASS_TRAIUD" ENABLE;


/*************** Trigger -19 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TEMPLATE_CLASS_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on TEMPLATE_CLASS
/* ERwin Builtin Tue Mar 04 14:20:24 2003 */
/* default body for TEMPLATE_CLASS_TRAIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TEMPLATE_CLASS';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TEMPLATE_CLASS_TRAIUD" ENABLE;


/*************** Trigger -20 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADE_SESS_EVENT_HIST_TRAUID"
  AFTER DELETE or INSERT or UPDATE
  on TRADINGSESSIONEVENTHIST


/* ERwin Builtin Wed Dec 22 10:26:28 2004 */
/* default body for TRADE_SESS_EVENT_HIST_TRAUID */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADINGSESSIONEVENTHIST';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADE_SESS_EVENT_HIST_TRAUID" ENABLE;


/*************** Trigger -21 ***********************/


/* DROP TRIGGER "GLOBAL_SERVICE_OWNER"."TRADE_THRU_ORDER_TRBU" */
/* Trigger dropped to change its name */

CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADE_THRU_ORDER_TRAUID"
AFTER DELETE or INSERT or UPDATE
ON TRADEDTHROUGHORDER
/* for each row -- changed to statement level trigger-Ravi Babu Jul 31, 2009  */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADEDTHROUGHORDER';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADE_THRU_ORDER_TRBU" ENABLE;


/*************** Trigger -22 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_PROPERTY_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on TRADING_PROPERTY
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_PROPERTY';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_PROPERTY_TRAIUD" ENABLE

/*************** Trigger -23 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESSION_ELEMENT_TRAIUD"
  AFTER DELETE or INSERT or UPDATE
  on TRADING_SESSION_ELEMENT
/* ERwin Builtin Tue Mar 04 14:18:54 2003 */
/* default body for TRADING_SESSION_ELEMENT_TRAIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION_ELEMENT';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESSION_ELEMENT_TRAIUD" ENABLE;


/*************** Trigger -24 ***********************/

  CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESSION_TRAUID"
  AFTER DELETE or INSERT or UPDATE
  on TRADING_SESSION
/* ERwin Builtin Tue Mar 04 14:18:26 2003 */
/* default body for TRADING_SESSION_TRAUID */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESSION_TRAUID" ENABLE;


/*************** Trigger -25 ***********************/

CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESS_TEMPL_TRAUID"
AFTER DELETE or INSERT or UPDATE
on TRADING_SESSION_TEMPLATE
/* for each row -- changed to statement level trigger-Ravi Babu Jul 31, 2009  */
/* ERwin Builtin Mon Mar 11 12:20:54 2002 */
/* default body for MKT_DATA_SUMMARY_TRBIUD */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'TRADING_SESSION_TEMPLATE';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/ 
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."TRADING_SESS_TEMPL_TRBIUD" ENABLE;


/*************** Trigger -26 ***********************/

CREATE OR REPLACE TRIGGER "GLOBAL_SERVICE_OWNER"."USER_FIRM_AFFL_TRAUID"
AFTER DELETE or INSERT or UPDATE
on USER_FIRM_AFFILIATION
/* for each row -- changed to statement level trigger-Ravi Babu Jul 31, 2009  */
declare
v_table_name sbt_global_table_updt_log.table_name%type := 'USER_FIRM_AFFILIATION';
v_status INTEGER;
begin
SP_INS_GLOBAL_LOG(v_table_name,v_status);
end;
/
ALTER TRIGGER "GLOBAL_SERVICE_OWNER"."USER_FIRM_AFFL_TRAUID" ENABLE;






