include /vobs/dte/sbtcommon/build/defs/server.mk
include /vobs/dte/commonBuild/defs/tpt.mk
include $(DOMAIN)/build/domain.mk
include $(DOMAIN_TEST_DEP)


#These must be defined before we can include generic_java.mk
JAVADIR=$(DOMAIN_TEST_JAVA)
CLASSDIR=$(DOMAIN_TEST_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_TEST_JARNAME)
VERSIONSRC=$(DOMAIN_TEST_VERSIONSRC)

MYCLASSPATH =$(DOMAIN_TEST_CLASSPATH)


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



