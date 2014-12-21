include /vobs/dte/sbtcommon/build/defs/server.mk
include $(DOMAIN)/build/domain.mk
include $(DOMAIN_XML_DEP)


JAVADIR=$(DOMAIN_XML_JAVA)
CLASSDIR=$(DOMAIN_XML_CLASS)
RELDIR=$(DOMAIN_REL)
JARNAME=$(DOMAIN_XML_JARNAME)
VERSIONSRC=$(DOMAIN_XML_VERSIONSRC)

XML_DIR=com/cboe/client/xml
SCHEMA_DIR=$(XML_DIR)/schemas
BIND_DIR=$(XML_DIR)/bind
OUTPUT_DIR=$(JAVADIR)
RESOURCE_SRC=$(JAVADIR)/$(BIND_DIR)
RESOURCE_TRG=$(CLASSDIR)/$(BIND_DIR)
XMLBINDING_OUT=$(JAVADIR)/xmlBinding.out

MYCLASSPATH=$(DOMAIN_XML_CLASSPATH)

include $(GENERIC_JAVA_MK)

export JAVA_HOME=$(JAVAHOME)

define build-xmlBinding
	echo "\nGenerating Java XML Bindings
	$(XJC) -readOnly -classpath $(COMMONFACILITIES_JAR) -d $(JAVADIR) $(SCHEMA_DIR)/*.xsd
	touch $(JAVADIR)/xmlBinding.out
endef

define copy-resources
	mkdir -p $(RESOURCE_TRG)
	cp $(RESOURCE_SRC)/*.ser $(RESOURCE_SRC)/*.properties $(RESOURCE_TRG)
	chmod ug+w $(RESOURCE_TRG)/*.ser $(RESOURCE_TRG)/*.properties
endef

define clean-xmlbinding
	echo "Cleaning xmlbinding... $(BIND_DIR)"
	/usr/bin/rm -fr $(BIND_DIR) 
	/usr/bin/rm -f $(XMLBINDING_OUT) 
endef


################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(copy-resources)
	@$(jar-all)

$(JAVA_COMPILE):: $(XMLBINDING_OUT) 

$(XMLBINDING_OUT): $(SCHEMA_DIR)/*.xsd
	@$(clean-xmlbinding)
	@$(build-xmlBinding)

java: $(JAVA_COMPILE)

xmlBinding: $(XMLBINDING_OUT) 

resources:
	@$(copy-resources)

jars:
	$(jar-all)

clean: gen_clean
	@$(clean-xmlbinding)

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall


