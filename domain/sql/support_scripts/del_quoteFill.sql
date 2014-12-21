select PRODUCTKEY,USERID, count(*) from sbt_user_report
where EVENTTYPE = 102
group by  PRODUCTKEY,USERID;
