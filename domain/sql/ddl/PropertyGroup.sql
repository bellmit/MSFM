whenever sqlerror continue

drop table property_group;

whenever sqlerror exit failure

create table property_group
(
	databaseIdentifier number(20) not null,
	versionNumber number(5),
	category varchar2(40),
	propertyKey varchar2(256)
)
;

alter table property_group
add constraint property_group_pk
primary key (databaseIdentifier)
;
create index property_group_i1
on property_group( category, propertyKey)
;
