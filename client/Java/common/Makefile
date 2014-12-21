include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_COMMON_DEP)

JAVADIR=$(CLIENT_COMMON_JAVA)
CLASSDIR=$(CLIENT_COMMON_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_COMMON_JARNAME)
VERSIONSRC=$(CLIENT_COMMON_VERSIONSRC)

MYCLASSPATH=$(CLIENT_COMMON_CLASSPATH)

include $(GENERIC_JAVA_MK)

##############################################################################################
# Redefine and override basic definitions
##

################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(jar-all)

java: $(JAVA_COMPILE)

jars:
	$(jar-all)
	@$(jar-all)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall



