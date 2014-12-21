include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CFIX)/build/cfix.mk
include $(CFIX_INTERFACES_DEP)


JAVADIR=$(CFIX_INTERFACES_JAVA)
CLASSDIR=$(CFIX_INTERFACES_CLASS)
RELDIR=$(CFIX_REL)
JARNAME=$(CFIX_INTERFACES_JARNAME)
VERSIONSRC=$(CFIX_INTERFACES_VERSIONSRC)

MYCLASSPATH =$(CFIX_INTERFACES_CLASSPATH)

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




