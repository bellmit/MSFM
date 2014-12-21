include /vobs/dte/sbtcommon/build/defs/server.mk
include $(DOMAIN)/build/domain.mk
include $(DOMAIN_IMPLS_DEP)


JAVADIR=$(DOMAIN_IMPLS_JAVA)
CLASSDIR=$(DOMAIN_IMPLS_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_IMPLS_JARNAME)
VERSIONSRC=$(DOMAIN_IMPLS_VERSIONSRC)

MYCLASSPATH=$(DOMAIN_IMPLS_CLASSPATH)

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



