include /vobs/dte/build/make/defs/inf.mk
include $(COMMON)/build/common.mk
include $(COMMON_TEST_DEP)


#These must be defined before we can include generic_java.mk
PRODUCT=$(COMMON_TEST_JARNAME)
JAVADIR=$(COMMON_TEST_JAVA)
CLASSDIR=$(COMMON_TEST_CLASS)
RELDIR=$(COMMON_REL)
JARNAME=$(COMMON_TEST_JARNAME)

MYCLASSPATH =$(COMMON_TEST_CLASSPATH)


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
