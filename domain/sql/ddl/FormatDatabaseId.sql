create or replace function format_database_id(database_id in number)
return varchar2 is
    string_id varchar2(50);
    base_value number;
begin
    base_value := power(2, 32);
    if database_id > base_value then
        string_id := '64bit:';
    else
        string_id := '32bit:';
        base_value := power(2, 16);
    end if;
    string_id := string_id || to_char(trunc(database_id / base_value)) || ':';
    string_id := string_id || to_char(mod(database_id, base_value));
    return string_id;
end;
/
