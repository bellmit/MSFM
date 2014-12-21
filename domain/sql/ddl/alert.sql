whenever sqlerror continue

drop table sbt_alert;

whenever sqlerror exit failure

create table sbt_alert
(
    databaseIdentifier	    number(20) not null,
    alertCreationTime       number(20) not null,
    alertType               number(3),
    resolution              varchar2(6),
    nbboAgentId             varchar2(15),
    updatedById             varchar2(15),
    cboeMarketableOrder     char(1),
    orderId                 number(20),
    tradeId                 number(20),
    sessionName             varchar2(8),
    productKey              number(20),
    productClass	        number(20),
    reportingClass	        number(20),
    productType		        number(2),
    comments                varchar2(200),
    tradedThroughQuantity   number(10),
    tradedThroughPrice      varchar2(20),
    side                    varchar2(2),
    lastsaleExchange        varchar2(5),
    lastsaleQuantity        number(10),
    lastsalePrice           varchar2(20),
    lastSalePostfix         varchar2(6),
    resentCount             number(10),
    lastResentTime          number(20),
    orsId                   varchar2(6));

     /* tablespace sbtg_sm_data01 */

alter table sbt_alert
add constraint sbt_alert_pk
primary key (databaseIdentifier);

