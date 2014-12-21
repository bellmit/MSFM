whenever sqlerror continue

drop table session_element_class;

whenever sqlerror exit

create table session_element_class (
    databaseIdentifier  number(20) not null,
    prod_class_key      number(20) not null,
    session_element_key number(20) not null,
    undly_sess_name     varchar2(30),
    class_state         number(3)
)
/* tablespace sbtg_sm_data01 */
;

alter table session_element_class add constraint session_element_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

whenever sqlerror continue
