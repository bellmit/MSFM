# The CHECKEDOUT directive should always be first

element * CHECKEDOUT

#
# Only work on the /main in these vobs
element /vobs/dte/models/... /main/LATEST
element /vobs/dte/documents/... /main/LATEST
element /vobs/dte/quality_assurance/... /main/LATEST
element /vobs/dte/dte_admin/... /main/LATEST
element /vobs/dte/api/... /main/LATEST
element /vobs/dte/web_dte/... /main/LATEST
element /vobs/dte/InfraVerity/documents/... /main/LATEST
element /vobs/dte/scripts/... /main/LATEST

element * COMPONENT
element * JDK_SOLARIS_1.6.0_13 -nocheckout
element * JDK_x86_1.6.0_13     -nocheckout
element * JDK_SOLARIS_1.5.0_18 -nocheckout
element * JDK_x86_1.5.0_18     -nocheckout
element * JRE_WINDOWS_MOD1_1.6.0_10 -nocheckout
element * JRE_WINDOWS_MOD1_1.5.0_08 -nocheckout
element * JRE_WINDOWS_MOD1_1.5.0_03  -nocheckout

include /vobs/dte/dte_admin/config_specs/build/QABUILD_INIT_COMMON_BUILD_0.0.9.unix.cs
include /vobs/dte/dte_admin/config_specs/idlbase/QAIDLBASE_INITIAL_RELEASE_0.1.13.unix.cs
include /vobs/dte/dte_admin/config_specs/cmi/QACMI_RELEASE_ONE_0.13.5.unix.cs
include /vobs/dte/dte_admin/config_specs/infra/QAINFRA_THIRTEEN_ONE_13.0.4.7.unix.cs
include /vobs/dte/dte_admin/config_specs/msgcodec/QAMSGCODEC_RELEASE_ONE_0.0.11.unix.cs


##########################################
#### Third party includes

element * JGL_3.1.0                  -nocheckout
element * COMMONS_NET_1.4.0
element * STYLEREPORTPRO_7.0         -nocheckout
element * JAVAMAIL_1.3.2
element * APACHE_ANT_1.6.2           -nocheckout
element * CRIMSON_1.1.3              -nocheckout
element * COMMONS_NET_1.4.0          -nocheckout
element * COMMONS_DIGESTER_1.7       -nocheckout
element * COMMONS_BEANUTILS_1.7.0    -nocheckout
element * COMMONS_COLLECTIONS_3.1    -nocheckout
element * COMMONS_LOGGING_1.0.4      -nocheckout
element * ITEXT_1.3                  -nocheckout
element * POI_2.5.1                  -nocheckout
element * JFREE_JCOMMON_1.0.0_RC1    -nocheckout
element * JFREE_JFREECHART_1.0.0_RC1 -nocheckout
element * JASPERREPORTS_MOD1_3.0.5   -nocheckout
element * JASPERREPORTS_3.0.5        -nocheckout
element * SQL92_PARSER_1.0.2         -nocheckout
element * INSTALLANYWHERE_MOD1_7.1.3 -nocheckout
element * JCACHE_1.0_DEV_3           -nocheckout
element * EHCACHE_1.2.4              -nocheckout
element * JUNIT_4.7                  -nocheckout


# CFN STOCK NASDAQ
element * QACFNSTOCKIDL_0.0             -nocheckout

##########################################
# APPIA RELEASE TO USE
##########################################

element /vobs/dte/appia/... APPIA_3.2

##########################################
# HAPS - note that it is /vobs/cboe/printing
##########################################
element /vobs/cboe/printing/... QAHAPS_2.5
##########################################
# HAPS - note that it is /vobs/cboe/printing
##########################################

#This is temporary for development, should be above when integrated
element /vobs/dte/appia/... /main/APPIA_1.0.13
element /vobs/dte/appia/... /main/0

#BRANCH
element * .../sbt_8.2_cdx_ff_shared_dev_iteration_2/LATEST

mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2
element * .../sbt_8.2_fast_failover_shared_dev/LATEST
end mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2

mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2
element * .../sbt_8.0_help_desk_enhancements/SBT_HELP_DESK_ENHANCEMENTS_8.2.91.2.6
end mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2

mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2
element * .../sbt_8.1_eight_three_integ/CBOEDIR_EIGHT_THREE_INTEG_8.2.91.2
end mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2

mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2
element * .../sbt_8.2_crit/CBOEDIR_CRIT_8.2.91
end mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2

mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2
element * /main/CBOEDIR_8.2
element /vobs/dte/domain/... /main/0
element /vobs/dte/server/... /main/0
element /vobs/dte/sysAdminClient/... /main/0
element /vobs/dte/simulator/... /main/0
element /vobs/dte/client/... /main/0
element /vobs/dte/gui/... /main/0
element /vobs/dte/linkage/... /main/0
element /vobs/dte/fixclient/... /main/0
element /vobs/dte/coppelia/... /main/0
element /vobs/dte/stock/... /main/0
element /vobs/dte/FixAppia/... /main/0
element /vobs/dte/cfix/... /main/0
element /vobs/dte/sbtcommon/... /main/0
element /vobs/dte/serverptd/... /main/0
element /vobs/dte/ohs/... /main/0
element /vobs/dte/CommonFacilities/... /main/0
element /vobs/dte/appServices/... /main/0
element /vobs/dte/eis/... /main/0
element /vobs/dte/applappl/... /main/0
element /vobs/dte/connectionServer/... /main/0
element /vobs/dte/message/... /main/0
element /vobs/dte/ecMonitor/... /main/0
element /vobs/dte/jcache/... /main/0
end mkbranch sbt_8.2_cdx_ff_shared_dev_iteration_2

