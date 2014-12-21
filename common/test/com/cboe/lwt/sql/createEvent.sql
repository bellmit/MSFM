whenever sqlerror continue

drop table event;

whenever sqlerror exit failure

create table event (DatabaseIdentifier number(20) not null, unitId number(20) not null, 
		    processID number(20) not null, machine number(11) not null, entityId number(20), 
		    timeStamp number(20), extraInfo number(20), flags number(4),timeStampMillis number(20) );
