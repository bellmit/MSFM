include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_FIXTRANSLATOR_DEP)


JAVADIR=$(CLIENT_FIXTRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_FIXTRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_FIXTRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_FIXTRANSLATOR_VERSIONSRC)

MYCLASSPATH =$(CLIENT_FIXTRANSLATOR_CLASSPATH)

include $(GENERIC_JAVA_MK)


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



