create or replace function convert_date(sql_time in date) return number as
convert_time number;
ref_time date;
gmt_time date;
begin
        ref_time := to_date('19700101', 'YYYYMMDD');
        gmt_time := new_time(sql_time, get_time_zone(sql_time), 'GMT');
        convert_time := (gmt_time - ref_time) * 24 * 60 * 60 * 1000;
        return convert_time;
end;
/
