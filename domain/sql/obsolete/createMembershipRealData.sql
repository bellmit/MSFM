whenever sqlerror exit failure

/*
 * HERE IS LIST OF PROD CLASS TO REPORTING CLASS MAPPING
 
CLASS_SYM  CLASS_SYM                                                            
---------- ----------                                                           
AA         AA                                                                   

VJQ        AAIR                                                                 

AVQ        ADVNA                                                                

WXW        AMZN                                                                 
WEW        AMZN                                                                 
YZZ        AMZN                                                                 
YQN        AMZN                                                                 
QZN        AMZN                                                                 
ZCM        AMZN                                                                 
ZWE        AMZN                                                                 
ZCR        AMZN                                                                 
ZQQ        AMZN                                                                 
ZQN        AMZN                                                                 

AOL        AOL                                                                  
WIU        AOL                                                                  
AOK        AOL                                                                  
WAN        AOL                                                                  
ZWO        AOL                                                                  
ZKS        AOL                                                                  
ZKF        AOL                                                                  
AOE        AOL                                                                  
ZOL        AOL                                                                  
AOO        AOL                                                                  
ZAN        AOL                                                                  

IBM        IBM                                                                  
ZVB        IBM                                                                  
WIB        IBM                                                                  
IBZ        IBM                                                                  
ZIB        IBM                                                                  

IRX        IRX                                                                  
LXI        IRX                                                                  

OEX        OEX                                                                  
OEY        OEX                                                                  
OEW        OEX                                                                  
OEZ        OEX                                                                  

ORCL       ORCL                                                                 
ORQ        ORCL                                                                 
WOQ        ORCL                                                                 
ZWM        ORCL                                                                 
ORV        ORCL                                                                 
ZOR        ORCL                                                                 
LRZ        tORCL                                                                 

XRX        XRX                                                                  
ZXR        XRX                                                                  
WXR        XRX                                                                  
*/

/*
 * Insert firm
 */
insert into firm_sbt_auth values(1,'TPF','TEST TPF FIRM',000,'A','Y','Y',SYSDATE,10202);

/*
 * Insert brokers
 */
 insert into brkr_sbt_auth values(2,'XXS','TPF BROKER','A',10201,1,'N','Y',SYSDATE);

/*
 * Insert Joint Account relations
 */

/* Participant=HTP , DPM=ZCT , Q Account = QBC*/ 
 insert into sbt_part_assoc values(7389,80422,'ZCT','Y','QBC','A',SYSDATE);

/* Participant=MMR , DPM=ZZZ , Q Account = QPF*/ 
 insert into sbt_part_assoc values(7392,19001,'ZZZ','Y','QPF','A',SYSDATE);

/* Participant=VSG , DPM=ZGO , Q Account = QIL*/ 
/* Participant=VSG , DPM=ZFI , Q Account = QIL*/ 
/* Participant=JFX , DPM=ZGO , Q Account = QIL*/ 
/* Participant=VTO , DPM=ZGO , Q Account = QIL*/ 
/* Participant=TTW , DPM=ZGO , Q Account = QIL*/ 
 insert into sbt_part_assoc values(7394,13255,'ZGO','Y','QIL','A',SYSDATE);
 insert into sbt_part_assoc values(7394,80479,'ZFI','Y','QIL','A',SYSDATE);
 insert into sbt_part_assoc values(7399,13255,'ZGO','Y','QIL','A',SYSDATE);
 insert into sbt_part_assoc values(7405,13255,'ZGO','Y','QIL','A',SYSDATE);
 insert into sbt_part_assoc values(7407,13255,'ZGO','Y','QIL','A',SYSDATE);

/*
 * Insert DPM assigned Classes
 */
insert into DPM_CLASS_ASGN values(80422,'ZCT','AA',SYSDATE);
insert into DPM_CLASS_ASGN values(80422,'ZCT','VJQ',SYSDATE);
insert into DPM_CLASS_ASGN values(80422,'ZCT','AVQ',SYSDATE);

insert into DPM_CLASS_ASGN values(80479,'ZFI','WXW',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','WEW',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','YZZ',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','QZN',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','ZCM',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','ZWE',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','ZCR',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','ZOQ',SYSDATE);
insert into DPM_CLASS_ASGN values(80479,'ZFI','ZQN',SYSDATE);

/*
 * Insert MM assigned classes
 */
insert into MM_CLASS_ASGN values(7553,'DAC','AOL',SYSDATE);
insert into MM_CLASS_ASGN values(7553,'DAC','WIU',SYSDATE);
insert into MM_CLASS_ASGN values(7553,'DAC','WAN',SYSDATE);
insert into MM_CLASS_ASGN values(7553,'DAC','sss',SYSDATE);

/*
 * Part of ZCT.
 */
insert into MM_CLASS_ASGN values(7643,'DNO','IBM',SYSDATE);
insert into MM_CLASS_ASGN values(7643,'DNO','AA',SYSDATE);
insert into MM_CLASS_ASGN values(7643,'DNO','VJQ',SYSDATE);

/*
 * Part of ZFI.
 */
insert into MM_CLASS_ASGN values(7656,'DVE','IRX',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','LXI',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','OEX',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','OEY',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','OEW',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','OEZ',SYSDATE);
insert into MM_CLASS_ASGN values(7656,'DVE','ORQ',SYSDATE);

commit;
