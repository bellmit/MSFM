include /vobs/dte/sbtcommon/build/defs/server.mk
include $(DOMAIN)/build/domain.mk
include $(DOMAIN_INTERFACES_DEP)


JAVADIR=$(DOMAIN_INTERFACES_JAVA)
CLASSDIR=$(DOMAIN_INTERFACES_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_INTERFACES_JARNAME)
VERSIONSRC=$(DOMAIN_INTERFACES_VERSIONSRC)

MYCLASSPATH=$(DOMAIN_INTERFACES_CLASSPATH)


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



