DROP PACKAGE "TEST".eventFunc;


DROP TABLE "TEST".cascade_members;
DROP TABLE "TEST".cascade_groups;
DROP TABLE "TEST".round_robin_members;
DROP TABLE "TEST".round_robin_groups;
DROP TABLE "TEST".route;
DROP TABLE "TEST".print_queue;
DROP TABLE "TEST".print_history;
DROP TABLE "TEST".physical_location;
DROP TABLE "TEST".print_header;
DROP TABLE "TEST".destination;
DROP TABLE "TEST".print_body;
DROP TABLE "TEST".sequences;

DROP TABLE "TEST".temp_cc_member_accumulator;
DROP TABLE "TEST".temp_rr_accumulator;


DROP SEQUENCE "TEST".queue_seq;


DROP SEQUENCE "TEST".submit_seq;


/
commit
/

CREATE SEQUENCE "TEST".submit_seq;


/
commit
/

CREATE SEQUENCE "TEST".queue_seq;


/
commit
/

CREATE TABLE "TEST".sequences
(
    id   NUMBER       NOT NULL,
    last NUMBER       NOT NULL,
    name VARCHAR2(32)     NULL,
    CONSTRAINT PK_sequences PRIMARY KEY( id )
)
ORGANIZATION INDEX
STORAGE( FREELIST GROUPS 2   FREELISTS 20 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".print_body
(
    id   NUMBER    NOT NULL,
    body RAW(2000 ) NOT NULL,
    CONSTRAINT PK_print_body PRIMARY KEY( id )
)
PCTFREE 1
PCTUSED 99
STORAGE ( INITIAL 104K NEXT 120K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".physical_location
(
    id          NUMBER       NOT NULL,
    name        VARCHAR2(32)     NULL,
    description VARCHAR2(32)     NULL,
    CONSTRAINT PK_physical_location PRIMARY KEY( id )
)
PCTFREE 10
PCTUSED 80
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".destination  /* potentially partitioned */
(
    destination_key VARCHAR2(32) NOT NULL,
    name            VARCHAR2(32)     NULL,
    description     VARCHAR2(32)     NULL,
    CONSTRAINT PK_destination PRIMARY KEY( destination_key )
)
PCTFREE 10
PCTUSED 80
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".route  /* potentially partitioned */
(
    destination_key      VARCHAR2(32) NOT NULL,
    physical_location_id NUMBER       NOT NULL,
    normally_routed_id   NUMBER           NULL,
    message              VARCHAR2(32)     NULL,
    CONSTRAINT PK_route              PRIMARY KEY( destination_key, physical_location_id ),
    CONSTRAINT FK_route_dest_key     FOREIGN KEY( destination_key )     REFERENCES "TEST".destination(destination_key),
    CONSTRAINT FK_route_loc_id       FOREIGN KEY( physical_location_id )REFERENCES "TEST".physical_location( id ),
    CONSTRAINT FK_route_normal_route FOREIGN KEY( normally_routed_id )  REFERENCES "TEST".physical_location( id )
)
PCTFREE 10
PCTUSED 40
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".round_robin_groups  /* potentially partitioned */
(
    id              NUMBER       NOT NULL,
    destination_key VARCHAR2(32) NOT NULL,
    modulo          NUMBER       NOT NULL,
    counter         NUMBER       NOT NULL,
    description     VARCHAR2(32)     NULL,
    CONSTRAINT PK_rr_groups PRIMARY KEY( id )
)
PCTFREE 10
PCTUSED 40
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".round_robin_members  /* potentially partitioned */
(
    group_id             NUMBER NOT NULL,
    modulo_offset        NUMBER NOT NULL,
    physical_location_id NUMBER NOT NULL,
    CONSTRAINT PK_rr_mbr          PRIMARY KEY( group_id, modulo_offset ),
    CONSTRAINT FK_rr_mbr_group_id FOREIGN KEY( group_id )             REFERENCES "TEST".round_robin_groups( id ),
    CONSTRAINT FK_rr_mbr_loc_id   FOREIGN KEY( physical_location_id ) REFERENCES "TEST".physical_location( id )
)
PCTFREE 10
PCTUSED 40
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".cascade_groups  /* potentially partitioned */
(
    id              NUMBER       NOT NULL,
    destination_key VARCHAR2(32) NOT NULL,
    threshold       NUMBER       NOT NULL,
    description     VARCHAR2(32)     NULL,
    CONSTRAINT PK_cc_groups PRIMARY KEY( id )
)
PCTFREE 10
PCTUSED 40
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".cascade_members  /* potentially partitioned */
(
    group_id             NUMBER NOT NULL,
    position             NUMBER NOT NULL,
    physical_location_id NUMBER NOT NULL,
    CONSTRAINT PK_cc_mbr       PRIMARY KEY( group_id, position ),
    CONSTRAINT FK_cc_mbr_group FOREIGN KEY( group_id )             REFERENCES "TEST".cascade_groups( id ),
    CONSTRAINT FK_cc_mbr_loc   FOREIGN KEY( physical_location_id ) REFERENCES "TEST".physical_location( id )
)
PCTFREE 10
PCTUSED 40
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".print_header
(
    id              NUMBER       NOT NULL,   /* this is the system's global sequence number */
    destination_key VARCHAR2(32) NOT NULL,
    time_created    DATE         NOT NULL,
    CONSTRAINT PK_print_header PRIMARY KEY( id),
    CONSTRAINT FK_header_dest  FOREIGN KEY( destination_key )   REFERENCES "TEST".destination(destination_key ),
    CONSTRAINT FK_header_body  FOREIGN KEY( id)REFERENCES "TEST".print_body( id ) ON DELETE CASCADE
)
PCTFREE 1
PCTUSED 99
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE TABLE "TEST".print_queue  /* potentially partitioned */
(
    physical_location_id NUMBER       NOT NULL,
    printer_sequence     NUMBER       NOT NULL,
    print_job_id         NUMBER       NOT NULL,
    message              VARCHAR2(32)     NULL,
    CONSTRAINT PK_print_queue PRIMARY KEY( physical_location_id, printer_sequence ),
    CONSTRAINT FK_que_job     FOREIGN KEY( print_job_id )        REFERENCES "TEST".print_header( id ),
    CONSTRAINT FK_que_loc     FOREIGN KEY( physical_location_id )REFERENCES "TEST".physical_location( id )
)
PCTFREE 20
PCTUSED 80
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE OR REPLACE TRIGGER "TEST".assign_print_queue_sequence
BEFORE
INSERT ON "TEST".print_queue
FOR EACH ROW
DECLARE
    next NUMBER;

BEGIN

    UPDATE sequences
    SET last = last + 1
    WHERE id = :new.physical_location_id
    RETURNING last
    INTO :new.printer_sequence;

END;


/
commit
/

CREATE TABLE "TEST".print_history  /* potentially partitioned */
(
    print_job_id         NUMBER       NOT NULL,
    physical_location_id NUMBER       NOT NULL,
    printer_sequence     NUMBER       NOT NULL,
    message              VARCHAR2(32)     NULL,
    time_printed         DATE         NOT NULL,
    CONSTRAINT PK_print_history PRIMARY KEY( physical_location_id, printer_sequence ),
    CONSTRAINT FK_hist_job      FOREIGN KEY( print_job_id )        REFERENCES "TEST".print_header( id ),
    CONSTRAINT FK_hist_loc      FOREIGN KEY( physical_location_id )REFERENCES "TEST".physical_location( id )
)
PCTFREE 1
PCTUSED 99
STORAGE ( INITIAL 1504K NEXT 104K MINEXTENTS 1 MAXEXTENTS 121 PCTINCREASE 0 )
TABLESPACE "HAPS_LG_DATA01";


/
commit
/

CREATE GLOBAL TEMPORARY TABLE "TEST".temp_rr_accumulator
(
    group_id      NUMBER NOT NULL,
    modulo_offset NUMBER NOT NULL
) ON COMMIT DELETE ROWS;


/
commit
/

CREATE GLOBAL TEMPORARY TABLE "TEST".temp_cc_member_accumulator
(
    group_id                NUMBER NOT NULL,
    position                NUMBER NOT NULL,
    physical_location_id    NUMBER NOT NULL,
    threshold               NUMBER NOT NULL,
    current_load            NUMBER NOT NULL,
    queue_physical_location NUMBER     NULL
) ON COMMIT DELETE ROWS;


/
commit
/

CREATE OR REPLACE PACKAGE "TEST".eventFunc
IS
    FUNCTION next_rr_counter( p_group_id NUMBER ) RETURN NUMBER;

    FUNCTION next_sequence( p_physical_location_id NUMBER ) RETURN NUMBER;

    PROCEDURE insert_event( p_unitId               IN NUMBER,
                            p_entityId             IN NUMBER,
                            p_extraInfo            IN NUMBER,
                            p_timeStamp            IN NUMBER,
                            p_flags                IN NUMBER,
                            p_machine              IN NUMBER,
                            p_processID            IN NUMBER,
                            p_timeStampMillis      IN NUMBER,
                            p_databaseIdentifier   IN NUMBER );


END eventFunc;


/
commit
/

CREATE OR REPLACE PACKAGE BODY eventFunc
AS
    PROCEDURE insert_event( p_unitId               IN NUMBER,
                            p_entityId             IN NUMBER,
                            p_extraInfo            IN NUMBER,
                            p_timeStamp            IN NUMBER,
                            p_flags                IN NUMBER,
                            p_machine              IN NUMBER,
                            p_processID            IN NUMBER,
                            p_timeStampMillis      IN NUMBER,
                            p_databaseIdentifier   IN NUMBER )
    BEGIN

        "INSERT INTO event                                                                                              "
        "    ( unitId, entityId, extraInfo, timeStamp, flags, machine, processID, timeStampMillis, databaseIdentifier ) "
        "VALUES                                                                                                         "
        "    ( ?,      ?,        ?,         ?,         ?,     ?,       ?,         ?,               ?,                 );"

    END;

END eventFunc;



/
commit
/

exit
    public void execute( int p_unitId,
                         int p_entityId,
                         int p_extraInfo,
                         int p_timeStamp,
                         int p_flags,
                         int p_machine,
                         int p_processID,
                         int p_timeStampMillis,
                         int p_databaseIdentifier )
        throws SQLException
    {
        dbStatement.setInt( 1,  p_unitId             );
        dbStatement.setInt( 2,  p_entityId           );
        dbStatement.setInt( 3,  p_extraInfo          );
        dbStatement.setInt( 4,  p_timeStamp          );
        dbStatement.setInt( 5,  p_flags              );
        dbStatement.setInt( 6,  p_machine            );
        dbStatement.setInt( 7,  p_processID          );
        dbStatement.setInt( 8,  p_timeStampMillis    );
        dbStatement.setInt( 9,  p_databaseIdentifier );
