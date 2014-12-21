
whenever sqlerror continue

drop table sbtAgentAssignedOrder;

whenever sqlerror exit failure

create table sbtAgentAssignedOrder
(
    databaseIdentifier      number(20) not null,
    orderDBId               number(20) not null,
    type                    number(3) not null,
    status                  char(1) not null,
    cancelQuantity          number(10),
    cancelType              number(5),
    userAssignedCancelId    varchar2(256),
    replacementOrderDBId    number(20),
    productClassKey         number(20) not null,
    productKey              number(20),
    receivedTime            number(20) not null,
    lastActivityTime        number(20) not null,
    creat_rec_time          timestamp default systimestamp
)
/* tablespace sbtb_me_data03 */
;

alter table sbtAgentAssignedOrder
add constraint sbtAgentAssignedOrderpk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

create index sbtAgentAssignedOrder_i1
on sbtAgentAssignedOrder(ProductClassKey)
/* tablespace sbtg_me_indx01 */
;
create index sbtAgentAssignedOrder_i2
on sbtAgentAssignedOrder(orderDBId)
/* tablespace sbtg_me_indx01 */
;
create index sbtAgentAssignedOrder_i3
on sbtAgentAssignedOrder(type, status)
/* tablespace sbtg_me_indx01 */
;

CREATE INDEX sbtAgentAssignedOrder_i4 ON 
sbtAgentAssignedOrder(CREAT_REC_TIME);


