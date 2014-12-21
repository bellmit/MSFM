include /vobs/dte/sbtcommon/build/defs/server.mk
include $(EVENT)/build/event.mk
include $(EVENT_TEST_DEP)


JAVADIR=$(EVENT_TEST_JAVA)
CLASSDIR=$(EVENT_TEST_CLASS)
RELDIR=$(EVENT_REL)
JARNAME=$(EVENT_TEST_JARNAME)
VERSIONSRC=$(EVENT_TEST_VERSIONSRC)

MYCLASSPATH=$(EVENT_TEST_CLASSPATH)


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



