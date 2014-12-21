include /vobs/dte/build/make/defs/inf.mk
include $(DIRECTORYSERVICE)/build/DirectoryService.mk
include $(DIRECTORYSERVICE_DEP)


#These must be defined before we can include generic_java.mk
PRODUCT=$(DIRECTORYSERVICE_JARNAME)
JAVADIR=$(DIRECTORYSERVICE_JAVA)
CLASSDIR=$(DIRECTORYSERVICE_CLASS)
RELDIR=$(DIRECTORYSERVICE_REL)
JARNAME=$(DIRECTORYSERVICE_JARNAME)

MYCLASSPATH =$(DIRECTORYSERVICE_CLASSPATH)


include $(GENERIC_JAVA_MK)


################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(MANIFEST_FILE)
	@$(jar-all)

java: $(JAVA_COMPILE)

jars:
	$(jar-all)

clean: gen_clean

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall
