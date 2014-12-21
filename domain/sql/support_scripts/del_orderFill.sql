select PRODUCTKEY,USERID, count(*) from sbt_user_report
where EVENTTYPE = 2 
group by  PRODUCTKEY,USERID;
