include /vobs/dte/sbtcommon/build/defs/server.mk
include $(COMMONFACILITIES)/build/CommonFacilities.mk
include $(COMMONFACILITIES_DEP)


#These must be defined before we can include generic_java.mk
JAVADIR=$(COMMONFACILITIES_JAVA)
CLASSDIR=$(COMMONFACILITIES_CLASS)
RELDIR=$(COMMONFACILITIES_REL)
JARNAME=$(COMMONFACILITIES_JARNAME)
VERSIONSRC=$(COMMONFACILITIES_VERSIONSRC)

MYCLASSPATH =$(COMMONFACILITIES_CLASSPATH)


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
