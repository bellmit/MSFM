

DECLARE 
    
    i NUMBER;
    j NUMBER;
    k NUMBER;
    
BEGIN
    
    -------------------------------------------------------------
    -- Sequence numbers table
    -------------------------------------------------------------
    
    FOR i IN 1..300 
    LOOP
    
    insert into sequences
    ( ID, last, name )
    values
    ( i,  0,    i );

    END LOOP;

    
    -------------------------------------------------------------
    -- Physical Location
    -------------------------------------------------------------
    
    FOR i IN 1..300 
    LOOP
    
        insert into physical_location
        ( ID, name, description )
        values
        ( i,  i,    'test' );

    END LOOP;
    
    
    -------------------------------------------------------------
    -- Destination
    -------------------------------------------------------------
        
    FOR i IN 1..300  -- direct route, one printer (300)
    LOOP
    
        insert into destination
        ( destination_key, name, description )
        values
        ( i,               i,    'direct 1' );

    END LOOP;
  
    
    FOR i IN 301..305  -- direct route, 10 printers (5)
    LOOP
    
        insert into destination
        ( destination_key, name, description )
        values
        ( i,               i,    'direct 10' );

    END LOOP;
    
    
    FOR i IN 306..315  -- round robin, 10 printers (10)
    LOOP
    
        insert into destination
        ( destination_key, name, description )
        values
        ( i,               i,    'round robin 10' );

    END LOOP;
    
    
    FOR i IN 316..325  -- cascade, 5 printers (10)
    LOOP
    
        insert into destination
        ( destination_key, name, description )
        values
        ( i,               i,    'cascade 5' );

    END LOOP;


    -------------------------------------------------------------
    -- Route
    -------------------------------------------------------------
    
    FOR i IN 1..300  -- direct route, one printer (300)
    LOOP
    
        insert into route
        ( destination_key, physical_Location_Id )
        values
        ( i,               i );

    END LOOP;
  
    k := 1;
    
    FOR i IN 301..305  -- 5 groups
    LOOP
        
        FOR j IN 1..10  -- 10 printers each (locs 1-40)
        LOOP
            
            insert into route
            ( destination_key, physical_Location_Id )
            values
            ( i,               k );

            k := k + 1;

        END LOOP;
        
    END LOOP;
  
    
    FOR i IN 306..315  -- round robin, 10 groups (locs 41-140)
    LOOP
    
        FOR j IN 1..10  -- 10 printers each
        LOOP
            
            insert into route
            ( destination_key, physical_Location_Id )
            values
            ( i,               k );
    
            k := k + 1;

        END LOOP;

    END LOOP;
    
    
    FOR i IN 316..325  -- cascade, 10 groups (locs 141-191)
    LOOP
    
        FOR j IN 1..5  -- 5 printers each
        LOOP
            
            insert into route
            ( destination_key, physical_Location_Id )
            values
            ( i,               k );
    
            k := k + 1;

        END LOOP;

    END LOOP;



    /* round robin groups */
    
    k := 41;  -- (locs 41-140)
    
    FOR i IN 306..315  -- round robin, 10 groups of 10 printers each 
    LOOP
    
        insert into round_robin_groups
        ( id, destination_key, modulo, counter, description )
        values
        ( i,  i,               10,     1,       i );


        FOR j IN 1..10  -- 10 printers each 
        LOOP
            
            insert into round_robin_members
            ( group_id, modulo_offset, physical_location_id )
            values
            ( i,        j,             k );
    
            k := k + 1;

        END LOOP;

    END LOOP;
        
    
    /* cascade groups */
    
    k := 141;  -- (locs 141-191)
    
    FOR i IN 316..325  -- cascade, 10 groups
    LOOP
    
        insert into cascade_groups
        ( id, destination_key, threshold, description )
        values
        ( i,  i,               10,        i );


        FOR j IN 1..10  -- 10 printers each 
        LOOP
            
            insert into cascade_members
            ( group_id, position, physical_location_id )
            values
            ( i,        j,        k );
    
            k := k + 1;

        END LOOP;

    END LOOP;

END;
/
commit;
/

select count(*) from sequences;
select count(*) from physical_location;
select count(*) from destination;
select count(*) from route;
select count(*) from round_robin_groups;
select count(*) from round_robin_members;
select count(*) from cascade_groups;
select count(*) from cascade_members;

commit;


exit;
