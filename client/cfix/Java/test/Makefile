include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CFIX)/build/cfix.mk
include $(CFIX_TEST_DEP)

JAVADIR=$(CFIX_TEST_JAVA)
CLASSDIR=$(CFIX_TEST_CLASS)
RELDIR=$(CFIX_REL)
JARNAME=$(CFIX_TEST_JARNAME)
VERSIONSRC=$(CFIX_TEST_VERSIONSRC)

MYCLASSPATH =$(CFIX_TEST_CLASSPATH)

include $(GENERIC_JAVA_MK)


################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(jar-all); \
	ksh '/vobs/tpt/tpt_tools/ant/run_dir/bin/ant UnitTest-Incremental';

java: $(JAVA_COMPILE)

jars:
	$(jar-all)

clean: gen_clean
	ksh '/vobs/tpt/tpt_tools/ant/run_dir/bin/ant Clean'; 

stage_install: gen_stage_install

install: gen_install
	/vobs/dte/dte_admin/tools/junit_email.pl

uninstall: gen_uninstall



