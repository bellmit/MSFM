whenever sqlerror continue

drop sequence global_uid_seq;

whenever sqlerror exit failure

create sequence global_uid_seq;
