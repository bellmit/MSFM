whenever sqlerror continue

drop table SBTRoutedMessage;

whenever sqlerror exit failure

create table SBTRoutedMessage
(
    databaseIdentifier number(20) not null, 
    messageType number(2),   
    creat_rec_time timestamp(6) default systimestamp,
    location varchar(64),
    locationType number(3),
    routeReason number(3),
    routeDescription varchar(64),
    routeCount number(5),
    processedFlag char(1),
    additionalData blob,
    lastRouteTime number(24),
    /* database identifier of order that is the primary subject of this message */
    orderDbId number(20)
)
partition by list (messageType )(partition nonorderrelated values (5,8,9,10) ,partition orderrelated values (1,2,3,4,6,7,11,12)         );
/* tablespace sbtb_me_data03 */

create unique index SBTRoutedMessage_PK on SBTRoutedMessage(messageType,databaseIdentifier) local;

alter table SBTRoutedMessage add primary key (messageType,databaseIdentifier) using index SBTRoutedMessage_PK;

create index SBTRoutedMessage_i1 on SBTRoutedMessage(location) local;

create index SBTRoutedMessage_i2 on SBTRoutedMessage(orderDbId) local;
