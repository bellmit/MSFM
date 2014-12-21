whenever sqlerror continue
drop table MEMBERSHIP_REFRESH_TIME_T; 
whenever sqlerror exit failure

create table MEMBERSHIP_REFRESH_TIME_T 
(       LAST_REFRESH_FIRM_TIME  date,
        LAST_REFRESH_BROKER_TIME date,
        LAST_REFRESH_JA_TIME date,
        LAST_REFRESH_DPM_TIME date,
        LAST_REFRESH_MM_TIME date,
	LAST_REFRESH_EXCH_TIME date
);
/* tablespace sbtg_sm_data01 */

