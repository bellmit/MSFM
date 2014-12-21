include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_INTERMARKETTRANSLATOR_DEP)


JAVADIR=$(CLIENT_INTERMARKETTRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_INTERMARKETTRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_INTERMARKETTRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_INTERMARKETTRANSLATOR_VERSIONSRC)

MYCLASSPATH =$(CLIENT_INTERMARKETTRANSLATOR_CLASSPATH)
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



