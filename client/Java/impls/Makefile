include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_IMPLS_DEP)


JAVADIR=$(CLIENT_IMPLS_JAVA)
CLASSDIR=$(CLIENT_IMPLS_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_IMPLS_JARNAME)
VERSIONSRC=$(CLIENT_IMPLS_VERSIONSRC)

MYCLASSPATH =$(CLIENT_IMPLS_CLASSPATH)

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



