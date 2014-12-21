create or replace function get_time_zone(ref_time in date) return varchar2 as
zone varchar2(3);
month number;
day number;
day_of_week number;
begin
        month := to_number(to_char(ref_time, 'MM'));
        if month <= 2 or month = 12 then
        -- January, February, and December always Central Standard Time (CST)
                 zone := 'CST';
        elsif month >= 4 and month <= 10 then
        -- April to October always Central Daylight Time (CDT)
                zone := 'CDT';
        elsif month = 3 then
        -- March CST until second Sunday (2 am)
                day := to_number(to_char(ref_time, 'DD'));
                day_of_week := to_number(to_char(trunc(ref_time, 'MONTH'), 'D'));
                if day_of_week = 1 then
                -- March first day is Sunday
		    if day < 8 then
			zone := 'CST';
		    else 	
                       	zone := 'CDT';
		    end if;
                else
	        -- When March does not start on Sunday
		    if day < 16 - day_of_week then
                       	zone := 'CST';
               	    else
                       	zone := 'CDT';
		    end if;
                end if;
        else
        -- November CDT until first Sunday (2am)
               day := to_number(to_char(ref_time, 'DD'));
               day_of_week := to_number(to_char(trunc(ref_time, 'MONTH'), 'D'));
               -- November first day is Sunday 
               if day_of_week = 1 then
	                zone := 'CST';
	       -- When November does not start on Sunday  
               elsif day < 9 - day_of_week then
                        zone := 'CDT';
               else
                        zone := 'CST';
               end if;
        end if;
        return zone;
end;
/

