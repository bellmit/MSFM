whenever sqlerror continue

drop table property;

whenever sqlerror exit failure

create table property
(
	databaseIdentifier	number(20) not null,
	propertyGroup		number(20) not null,
        propertyIndex          number(6) not null,
	propertyName		varchar2(3000) not null,
	propertyValue		varchar2(4000)
)
;

alter table property
add constraint property_pk
primary key (databaseIdentifier)
;
create index property_i1
on property(propertyGroup, propertyIndex)
;

