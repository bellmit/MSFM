create or replace function convert_java_time(java_time in number) return date as
convert_time date;
begin
	convert_time := to_date('19700101', 'YYYYMMDD');
    -- truncate on conversion from millis so seconds isn't rounded
	convert_time := convert_time + trunc(java_time / 1000) / 60 / 60 / 24;
	convert_time := new_time(convert_time, 'GMT', get_time_zone(convert_time));
	return convert_time;
end;
/

create or replace function java_time(jt in number)
return varchar2
is
begin
    return to_char(convert_java_time(jt), 'yyyy.mm.dd HH24:mi:ss');
end;
/
