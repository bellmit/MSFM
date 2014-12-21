include /vobs/dte/sbtcommon/build/defs/client.mk

include $(CLIENT)/build/client.mk
include $(CLIENT_IDL_DEP)


##############################################################
#
#  The following definitions are used by generic_idl_java.mk
#
##############################################################

IDLDIR=$(CLIENT_IDL)
JAVADIR=$(CLIENT_IDL_JAVA)
CLASSDIR=$(CLIENT_IDL_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_IDL_JARNAME)
VERSIONSRC=$(CLIENT_IDL_VERSIONSRC)
JAVA_JARNAME=$(CLIENT_IDL_JAVA_JARNAME)

ALL_IDL_FILES=$(CLIENT_IDL_FILES)

MYCLASSPATH=$(CLIENT_IDL_CLASSPATH)


CBOE_PACKAGE=com.cboe
IDL_PACKAGE=$(CBOE_PACKAGE).idl
OMG_PACKAGE=org.omg
PSWITCH= $(IDL_PACKAGE) \
        -P exceptions=$(CBOE_PACKAGE) \
        -P CosEventComm=$(OMG_PACKAGE) \
        -P CosTypedEventComm=$(OMG_PACKAGE) \
        -P policyEditor=$(CBOE_PACKAGE).securityService \
        -P infraUtil=$(CBOE_PACKAGE)


IC_SWITCHES=-IR -I $(CLIENT_IDL) -I $(CMI_IDL) -I $(MSGIDL) -I $(SSIDL) -I $(IVIDL) -P $(PSWITCH) -Tie


NOSOURCE:=TRUE
#JAVAHOME     =$(JAVAHOME_1.4)

include $(GENERIC_IDL_JAVA_MK)



########################################################################
# Build rules
########################################################################

all: $(BUILD_JAR) $(BUILD_JAVA_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(jar-classes)

$(BUILD_JAVA_JAR): $(BUILD_VERSION)
	@$(jar-java-classes)

java: $(JAVA_COMPILE)

idl: $(ALL_OUTPUT_IDX_DIRS)

jars: $(BUILD_JAR) $(BUILD_JAVA_JAR)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall


