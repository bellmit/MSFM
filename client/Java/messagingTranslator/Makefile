include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_MESSAGINGTRANSLATOR_DEP)

JAVADIR=$(CLIENT_MESSAGINGTRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_MESSAGINGTRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_MESSAGINGTRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_MESSAGINGTRANSLATOR_VERSIONSRC)

MYCLASSPATH=$(CLIENT_MESSAGINGTRANSLATOR_CLASSPATH)


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


