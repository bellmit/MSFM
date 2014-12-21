include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CFIX)/build/cfix.mk
include $(CFIX_IMPLS_DEP)

JAVADIR=$(CFIX_IMPLS_JAVA)
CLASSDIR=$(CFIX_IMPLS_CLASS)
RELDIR=$(CFIX_REL)
JARNAME=$(CFIX_IMPLS_JARNAME)
VERSIONSRC=$(CFIX_IMPLS_VERSIONSRC)

MYCLASSPATH =$(CFIX_IMPLS_CLASSPATH)

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



