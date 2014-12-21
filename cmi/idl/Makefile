include /vobs/dte/cmi/build/defs/CBOE_market_interface.mk

include $(CMI)/build/cmi.mk
include $(CMI_IDL_DEP)


##############################################################
#
#  The following definitions are used by generic_idl_java.mk
#
##############################################################

IDLDIR=$(CMI_IDL)
JAVADIR=$(CMI_IDL_JAVA)
CLASSDIR=$(CMI_IDL_CLASS)
RELDIR=$(CMI_REL)
JARNAME=$(CMI_IDL_JARNAME)
JAVA_JARNAME=$(CMI_IDL_JAVA_JARNAME)

ALL_IDL_FILES=$(CMI_IDL_FILES)

MYCLASSPATH=$(CMI_IDL_CLASSPATH)


CBOE_PACKAGE=com.cboe
IDL_PACKAGE=$(CBOE_PACKAGE).idl
OMG_PACKAGE=org.omg
PSWITCH= $(IDL_PACKAGE) \
        -P exceptions=$(CBOE_PACKAGE) \
        -P CosEventComm=$(OMG_PACKAGE) \
        -P CosTypedEventComm=$(OMG_PACKAGE) \
        -P policyEditor=$(CBOE_PACKAGE).securityService \
        -P infraUtil=$(CBOE_PACKAGE)


IC_SWITCHES=-IR -I $(CMI_IDL) -I $(SERVICEIDL) -I $(BASEIDL) -P $(PSWITCH) -Tie

MESSAGINGSYSTEM_IDLCOMPILER_JAR=$(IDLBASE_IDLCOMPILER_JAR)

NOSOURCE:=TRUE

include $(GENERIC_IDL_JAVA_MK)

########################################################################
# Build rules
########################################################################

all: $(BUILD_JAR) $(BUILD_JAVA_JAR)

$(BUILD_JAR): $(MANIFEST_FILE)
	@$(jar-classes)

$(BUILD_JAVA_JAR): $(MANIFEST_FILE)
	@$(jar-java-classes)

java: $(JAVA_COMPILE)

idl: $(ALL_OUTPUT_IDX_DIRS)

jars: $(BUILD_JAR) $(BUILD_JAVA_JAR)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall


