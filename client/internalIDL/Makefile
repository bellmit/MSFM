include /vobs/dte/sbtcommon/build/defs/sbt.mk

include $(CLIENT)/build/client.mk
include $(CLIENT_INTERNAL_IDL_DEP)


##############################################################
#
#  The following definitions are used by generic_idl_java.mk
#
##############################################################

IDLDIR=$(CLIENT_INTERNAL_IDL)
JAVADIR=$(CLIENT_INTERNAL_IDL_JAVA)
CLASSDIR=$(CLIENT_INTERNAL_IDL_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_INTERNAL_IDL_JARNAME)
VERSIONSRC=$(CLIENT_INTERNAL_IDL_VERSIONSRC)
JAVA_JARNAME=$(CLIENT_INTERNAL_IDL_JAVA_JARNAME)

ALL_IDL_FILES=$(CLIENT_INTERNAL_IDL_FILES)

MYCLASSPATH=$(CLIENT_INTERNAL_IDL_CLASSPATH)


CBOE_PACKAGE=com.cboe
IDL_PACKAGE=$(CBOE_PACKAGE).idl
OMG_PACKAGE=org.omg
PSWITCH= $(IDL_PACKAGE) \
        -P exceptions=$(CBOE_PACKAGE) \
        -P CosEventComm=$(OMG_PACKAGE) \
        -P CosTypedEventComm=$(OMG_PACKAGE) \
        -P policyEditor=$(CBOE_PACKAGE).securityService \
        -P infraUtil=$(CBOE_PACKAGE)


IC_SWITCHES=-IR -I $(CLIENT_INTERNAL_IDL) -I $(DOMAIN_IDL) -I $(EVENT_IDL) -I $(CMI_IDL) -I $(SSIDL) -I $(IVIDL) -P $(PSWITCH) -Tie


NOSOURCE:=TRUE

include $(GENERIC_IDL_JAVA_MK)



########################################################################
# Build rules
########################################################################

all: $(RELEASE_JAR) $(RELEASE_JAVA_JAR)

$(RELEASE_JAR): $(BUILD_VERSION)
	@$(jar-classes)

$(RELEASE_JAVA_JAR): $(BUILD_VERSION)
	@$(jar-java-classes)

java: $(JAVA_COMPILE)

idl: $(ALL_OUTPUT_IDX_FILES)

jars: $(RELEASE_JAR) $(RELEASE_JAVA_JAR)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall


