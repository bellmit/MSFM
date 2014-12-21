include /vobs/dte/sbtcommon/build/defs/gui.mk
include $(CLIENT)/build/client.mk

include $(CLIENT_TRANSLATOR_DEP)

JAVADIR=$(CLIENT_TRANSLATOR_JAVA)
CLASSDIR=$(CLIENT_TRANSLATOR_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_TRANSLATOR_JARNAME)
VERSIONSRC=$(CLIENT_TRANSLATOR_VERSIONSRC)

MYCLASSPATH =$(CLIENT_TRANSLATOR_CLASSPATH)

RESOURCES_OUT=$(CLASSDIR)/$(JARNAME)_resources.out

PERMISSION_RESOURCE_DIR=$(JAVADIR)/com/cboe/presentation/resources/permissions

THERESOURCES := $(wildcard $(PERMISSION_RESOURCE_DIR)/*)

include $(GENERIC_JAVA_MK)

##############################################################################################
# Redefine and override basic definitions
##

define copy-resources
	$(clean-resources)
	mkdir -p $(CLASSDIR)/com/cboe/presentation/resources/permissions
	cp $(PERMISSION_RESOURCE_DIR)/* $(CLASSDIR)/com/cboe/presentation/resources/permissions/.
	chmod ug+w $(CLASSDIR)/com/cboe/presentation/resources/permissions/*
	touch $(RESOURCES_OUT)
endef

define clean-resources
	echo "Cleaning resources..."
	rm -rf $(CLASSDIR)/com/cboe/presentation/resources/permissions
	rm -f $(RESOURCES_OUT)
endef

################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(BUILD_VERSION) $(RESOURCES_OUT)
	@$(jar-all)

$(RESOURCES_OUT): $(THERESOURCES) $(JAVA_COMPILE)
	$(copy-resources)

java: $(JAVA_COMPILE)

jars:
	$(jar-all)

clean: gen_clean
	@$(clean-resources)

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall

