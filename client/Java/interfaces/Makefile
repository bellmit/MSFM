include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_INTERFACES_DEP)


JAVADIR=$(CLIENT_INTERFACES_JAVA)
CLASSDIR=$(CLIENT_INTERFACES_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_INTERFACES_JARNAME)
VERSIONSRC=$(CLIENT_INTERFACES_VERSIONSRC)

MYCLASSPATH =$(CLIENT_INTERFACES_CLASSPATH)

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



