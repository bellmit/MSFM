include /vobs/dte/sbtcommon/build/defs/server.mk
include $(DOMAIN)/build/domain.mk
include $(DOMAIN_PERSIST_DEP)


JAVADIR=$(DOMAIN_PERSIST_JAVA)
CLASSDIR=$(DOMAIN_PERSIST_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_PERSIST_JARNAME)
VERSIONSRC=$(DOMAIN_PERSIST_VERSIONSRC)

MYCLASSPATH=$(DOMAIN_PERSIST_CLASSPATH)


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



