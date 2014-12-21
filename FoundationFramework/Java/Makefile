include /vobs/dte/build/make/defs/inf.mk
include $(FOUNDATIONFRAMEWORK)/build/FoundationFramework.mk
include $(FOUNDATIONFRAMEWORK_DEP)


#These must be defined before we can include generic_java.mk
PRODUCT=$(FOUNDATIONFRAMEWORK_JARNAME)
JAVADIR=$(FOUNDATIONFRAMEWORK_JAVA)
CLASSDIR=$(FOUNDATIONFRAMEWORK_CLASS)
RELDIR=$(FOUNDATIONFRAMEWORK_REL)
JARNAME=$(FOUNDATIONFRAMEWORK_JARNAME)

MYCLASSPATH =$(FOUNDATIONFRAMEWORK_CLASSPATH)


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
