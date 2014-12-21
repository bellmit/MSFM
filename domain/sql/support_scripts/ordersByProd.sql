select productkey, count(*) from sbtorder group by productkey order by count(*);
