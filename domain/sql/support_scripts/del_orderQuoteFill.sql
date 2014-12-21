select PRODUCTKEY,USERID, count(*) from sbt_user_report
where EVENTTYPE in( 102,2)
group by  PRODUCTKEY,USERID;
