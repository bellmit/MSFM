include /vobs/dte/commonBuild/defs/common.defs
include /vobs/dte/commonBuild/defs/components.mk
include /vobs/dte/commonBuild/defs/tpt.mk
include /vobs/dte/build/make/defs/old.mk
include /vobs/dte/build/make/defs/components.mk
include /vobs/dte/build/make/defs/tpt.mk
include /vobs/dte/CommonLibraries/MsgCodec/java/MsgCodec.mk
include /vobs/dte/CommonLibraries/MsgCodec/java/MsgCodec_dep.mk


#These must be defined before we can include generic_java.mk
JAVADIR=${MSG_CODEC_JAVA}
CLASSDIR=${MSG_CODEC_CLASS}
RELDIR=/vobs/dte/CommonLibraries/MsgCodec/release
JARNAME=${MSG_CODEC_JARNAME}
VERSIONSRC=${MSG_CODEC_VERSIONSRC}

MYCLASSPATH =$(MSG_CODEC_CLASSPATH)


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



