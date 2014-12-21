include /vobs/dte/sbtcommon/build/defs/server.mk

include $(DOMAIN)/build/domain.mk
include $(DOMAIN_IDL_DEP)


##############################################################
#
#  The following definitions are used by generic_idl_java.mk
#
##############################################################

IDLDIR=$(DOMAIN_IDL)
JAVADIR=$(DOMAIN_IDL_JAVA)
CLASSDIR=$(DOMAIN_IDL_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_IDL_JARNAME)
VERSIONSRC=$(DOMAIN_IDL_VERSIONSRC)
JAVA_JARNAME=$(DOMAIN_IDL_JAVA_JARNAME)

ALL_IDL_FILES=$(DOMAIN_IDL_FILES)

MYCLASSPATH=$(DOMAIN_IDL_CLASSPATH)

PACKAGE=com.cboe.idl
EXCEPTION_PACKAGE=com.cboe

PSWITCH= $(PACKAGE)\
        -P exchange=$(PACKAGE) \
        -P firm=$(PACKAGE) \
        -P constants=$(PACKAGE) \
        -P orderBook=$(PACKAGE) \
        -P product=$(PACKAGE) \
        -P quote=$(PACKAGE) \
        -P trade=$(PACKAGE) \
        -P tradingProperty=$(PACKAGE) \
        -P user=$(PACKAGE) \
        -P alert=$(PACKAGE) \
        -P groupElement=$(PACKAGE) \
        -P CosEventComm=org.omg \
        -P CosTypedEventComm=org.omg \
        -P infrastructureServices=$(PACKAGE) \
        -P policyEditor=com.cboe.securityService \
        -P exceptions=$(EXCEPTION_PACKAGE) \
        -P infraUtil=com.cboe



IC_SWITCHES=-IR -I $(CMI_IDL) -I $(IVIDL) -P $(PSWITCH) -Tie

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

