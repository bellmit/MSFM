whenever sqlerror continue

drop table intermarketTextMessage;

whenever sqlerror exit failure

create table intermarketTextMessage
(
	databaseIdentifier 	number(20) not null,
	userAssignedID      varchar2(100),
	productKey          number(20),
	sourceExch          varchar2(5),
	destExch            varchar2(5),
	messageTimeStamp    number(38),
	messageKey          number(3),
	originalMessageKey  number(3),
	sender              varchar2(50),
	messageSubject      varchar2(100),
	replyRequested      char(1),
	messageText         varchar2(1000)
)
;
alter table intermarketTextMessage
add constraint intermarketTextMessagePK
primary key (databaseIdentifier)
;
