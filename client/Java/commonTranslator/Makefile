include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk
include $(ICS)/build/ics.mk

include $(CLIENT_COMMONTRANSLATOR_DEP)

JAVADIR=$(CLIENT_COMMONTRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_COMMONTRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_COMMONTRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_COMMONTRANSLATOR_VERSIONSRC)

MYCLASSPATH =$(CLIENT_COMMONTRANSLATOR_CLASSPATH)

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



