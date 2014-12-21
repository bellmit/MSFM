include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_INTERNALTRANSLATOR_DEP)

JAVADIR=$(CLIENT_INTERNALTRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_INTERNALTRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_INTERNALTRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_INTERNALTRANSLATOR_VERSIONSRC)

MYCLASSPATH =$(CLIENT_INTERNALTRANSLATOR_CLASSPATH)

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

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall



