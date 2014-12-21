whenever sqlerror continue

drop table SbtORSIDExclusion;

whenever sqlerror exit failure

create table SbtORSIDExclusion
(
        orsid    varchar2(6) not null,
        classkey number(20)  not null,
        orderdbid number(20) not null,
        source VARCHAR2(256 BYTE)        ,
        location VARCHAR2(256 BYTE)                
)
/* tablespace sbtb_me_data03 */
;

create index SbtORSIDExclusion_I1 on SbtORSIDExclusion(orsid,source)

/* using index tablespace sbtb_me_indx03 */
;
/
