include /vobs/dte/CommonLibraries/CommonTest/build/defs/test.mk
include $(COMMONTEST)/build/commontest.mk
include $(COMMONTEST_UTIL_DEP)


#These must be defined before we can include generic_java.mk
JAVADIR=$(COMMONTEST_UTIL_JAVA)
CLASSDIR=$(COMMONTEST_UTIL_CLASS)
RELDIR=$(COMMONTEST_REL)
JARNAME=$(COMMONTEST_UTIL_JARNAME)

MYCLASSPATH =$(COMMONTEST_UTIL_CLASSPATH)


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
