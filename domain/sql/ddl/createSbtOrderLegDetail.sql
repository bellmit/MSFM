whenever sqlerror continue

drop table SBTOrderLegDetail;

whenever sqlerror exit failure

create table SBTOrderLegDetail
(
    databaseIdentifier number(20) not null,
    productKey number(20),
    orderDBId number(20),
    side char(1),
    originalQuantity number(10),
    tradedQuantity number(10),
    cancelledQuantity number(10),
    addedQuantity number(10),
    clearingFirmNumber varchar2(5),
    clearingFirmExch varchar2(5),
    mustUsePrice varchar2(20),
    positionEffect char(1),
    coverage char(1),
    totalRejectOrTimeoutQuantity number(10),
    CREAT_REC_TIME timestamp default systimestamp   /* new field, used by CDB application.*/
)
/* tablespace sbtb_me_data03 */
;

alter table SBTOrderLegDetail
add constraint SBTOrderLegDetailpk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

create index SBTOrderLegDetail_i1
on SBTOrderLegDetail(orderDbId)
/* tablespace sbtb_me_indx03 */
;

