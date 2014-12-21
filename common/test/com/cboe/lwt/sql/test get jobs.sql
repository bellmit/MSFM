            SELECT BDY.DATABASEIDENTIFIER
            FROM route         RTE,
                 print_header  HDR,
                 print_body    BDY
            WHERE RTE.physical_location_id = 10
              AND RTE.destination_key      = HDR.destination_key
              AND HDR.DATABASEIDENTIFIER   = BDY.DATABASEIDENTIFIER
              AND ROWNUM                  <= 20
              AND HDR.DATABASEIDENTIFIER  > ( SELECT MAX( HST.print_job_id ) 
                                              FROM print_history HST
                                              WHERE HST.physical_location_id = 10 )
            ORDER BY HDR.DATABASEIDENTIFIER;      
