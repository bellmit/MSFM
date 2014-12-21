select PRODUCTKEY,USERID, count(*) NBR_OF_FILLS from sbt_user_report where EVENTTYPE = 2
and UserID like 'CME%'
group by PRODUCTKEY,USERID
order by userId;
