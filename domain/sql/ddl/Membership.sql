whenever sqlerror continue

drop table MEMBERSHIP_REFRESH_TIME_T; 
/*  
drop table FIRM_SBT_AUTH;
drop table BRKR_SBT_AUTH;
drop table SBT_PART_ASSOC;
drop table DPM_CLASS_ASGN;
drop table MM_CLASS_ASGN;
*/
whenever sqlerror exit failure

create table MEMBERSHIP_REFRESH_TIME_T 
(	LAST_REFRESH_FIRM_TIME  date,
        LAST_REFRESH_BROKER_TIME date,
        LAST_REFRESH_JA_TIME date,
        LAST_REFRESH_DPM_TIME date,
        LAST_REFRESH_MM_TIME date
)
/* tablespace sbtg_sm_data01 */
;
/******
create table FIRM_SBT_AUTH
(
	ENT_UID			number(10),
	FIRM_ACR		varchar2(3),
	FULL_NAME		varchar2(196),
	OCC_NBR			number(3),
	STAT_CODE		char(1),
	CLEAR_FIRM_IND		char(1),
	MBR_IND			char(1),
	LMOD_DATE		date,
	APPL_TYPE_CODE		number(10)
);

create table BRKR_SBT_AUTH
(
	ENT_UID			number(10),
	BRKR_ACR		varchar2(3),
	FULL_NAME		varchar2(196),
	STAT_CODE		char(1),
	APPL_TYPE_CODE		number(10),
	CLEAR_FIRM_ENT_UID	number(10),
	MM_IND			char(1),
	FB_IND			char(1),
	LMOD_DATE		date
);

create table SBT_PART_ASSOC
(
	PART_ENT_UID		number(10),
	JADPM_ENT_UID		number(10),
	JADPM_ACR		varchar2(3),
	PRIM_DPM_DGN_IND	char(1),
	DPM_RELAT_ACR		varchar2(3),
	STAT_CODE		char(1),
	LMOD_DATE		date
);

create table DPM_CLASS_ASGN
(
	DPM_ENT_UID		number(10),
	DPM_ACR			varchar2(5),
	CLASS_SYM		varchar2(6),
	LMOD_DATE		date	
);

create table MM_CLASS_ASGN
(
	MM_ENT_UID		number(10),
	MM_ACR			varchar2(5),
	CLASS_SYM		varchar2(6),
	LMOD_DATE		date	
);
****/
commit;
