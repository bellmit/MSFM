include /vobs/dte/sbtcommon/build/defs/server.mk

include $(EVENT)/build/event.mk
include $(EVENT_IDL_DEP)


##############################################################
#
#  The following definitions are used by generic_idl_java.mk
#
##############################################################

IDLDIR=$(EVENT_IDL)
JAVADIR=$(EVENT_IDL_JAVA)
CLASSDIR=$(EVENT_IDL_CLASS)
RELDIR=$(EVENT_REL)
JARNAME=$(EVENT_IDL_JARNAME)
VERSIONSRC=$(EVENT_IDL_VERSIONSRC)
JAVA_JARNAME=$(EVENT_IDL_JAVA_JARNAME)

ALL_IDL_FILES=$(EVENT_IDL_FILES)

MYCLASSPATH=$(EVENT_IDL_CLASSPATH)

CBOE_PACKAGE=com.cboe
IDL_PACKAGE=$(CBOE_PACKAGE).idl
OMG_PACKAGE=org.omg
PSWITCH= $(IDL_PACKAGE) \
		-P exceptions=$(CBOE_PACKAGE) \
		-P CosEventComm=$(OMG_PACKAGE) \
		-P CosTypedEventComm=$(OMG_PACKAGE) \
		-P policyEditor=$(CBOE_PACKAGE).securityService \
        -P infraUtil=$(CBOE_PACKAGE)


IC_SWITCHES=-IR -I $(SERVICEIDL) -I $(CMI_IDL) -I $(DOMAIN_IDL) -I $(SERVER_IDL) -I $(IVIDL) -P $(PSWITCH) -Tie

NOSOURCE:=TRUE


include $(GENERIC_IDL_JAVA_MK)



########################################################################
# Build rules
########################################################################

all: $(BUILD_JAR) $(RELEASE_JAVA_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(jar-classes)

$(RELEASE_JAVA_JAR): $(BUILD_VERSION)
	@$(jar-java-classes)

java: $(JAVA_COMPILE)

idl: $(ALL_OUTPUT_IDX_FILES)

jars: $(BUILD_JAR) $(RELEASE_JAVA_JAR)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall

