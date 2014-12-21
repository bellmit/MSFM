whenever sqlerror continue 
 
whenever sqlerror exit failure 

alter table recap_reports_archive drop constraint recap_reports_archive_pk;
alter table mkt_hist_reports_archive drop constraint mkt_hist_reports_archive_pk;

create index recap_reports_archive_pk
on recap_reports_archive(dayofweek,databaseIdentifier) local;

create index mkt_hist_reports_archive_pk
on mkt_hist_reports_archive(dayofweek,databaseIdentifier) local;
