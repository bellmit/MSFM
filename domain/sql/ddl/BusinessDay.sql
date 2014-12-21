whenever sqlerror continue

drop table bus_day;

whenever sqlerror exit failure

create table bus_day
(
	databaseIdentifier	    number(20) not null,
    bus_day                 number(20) not null,
    status                  number(2),
    state                   number(2)
)
/* tablespace sbtg_sm_data01 */
;

alter table bus_day
add constraint bus_day_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
