include /vobs/dte/sbtcommon/build/defs/server.mk
include $(EVENT)/build/event.mk
include $(EVENT_INTERFACES_DEP)


JAVADIR=$(EVENT_INTERFACES_JAVA)
CLASSDIR=$(EVENT_INTERFACES_CLASS)
RELDIR=$(EVENT_REL)
JARNAME=$(EVENT_INTERFACES_JARNAME)
VERSIONSRC=$(EVENT_INTERFACES_VERSIONSRC)

MYCLASSPATH =$(EVENT_INTERFACES_CLASSPATH)


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



