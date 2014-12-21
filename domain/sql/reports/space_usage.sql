column segment_name format a30
column tablespace_name format a20

select segment_name, tablespace_name, count(*), sum(bytes)
from user_extents
group by segment_name, tablespace_name
order by 4 desc;

select tablespace_name, sum(bytes)
from user_extents
group by tablespace_name;

select tablespace_name, sum(bytes)
from user_free_space
group by tablespace_name;
