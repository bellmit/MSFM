#######################################################################
# This files defines the rules for building all build components
# in this release component
#
# It is intended that this file be included in two other makes, one
# for the release component and one for the entire system.

#######################################################################

###########################################################
# Include each build component defintion
#
###########################################################
include $(DOMAIN_IMPLS_DEP)
include $(DOMAIN_INTERFACES_DEP)
include $(DOMAIN_PERSIST_DEP)
include $(DOMAIN_IDL_DEP)
include $(DOMAIN_XML_DEP)
include $(DOMAIN_TEST_DEP)


ALL_DOMAIN_BUILD_COMPONENTS=$(DOMAIN_IMPLS_JAR) \
                            $(DOMAIN_INTERFACES_JAR) \
                            $(DOMAIN_TEST_JAR) \
                            $(DOMAIN_PERSIST_JAR) \
                            $(DOMAIN_IDL_JAR) \
                            $(DOMAIN_XML_JAR)


############################################################
# The following are the build rules that will build this
# entire release component.
#
# These are all PHONY rules since each build component has
# the real rules for building the component.  This exists
# so that all build components can be built from a single
# makefile.
#
############################################################
.PHONY: domain $(ALL_DOMAIN_BUILD_COMPONENTS)

all:: domain

all_targets:: domain_targets

domain: $(ALL_DOMAIN_BUILD_COMPONENTS)

#used for generic execution of commands accross all targets
domain_targets: domain_interfaces domain_impls domain_persist domain_idl domain_xml domain_test
	

$(DOMAIN_INTERFACES_JAR): $(DOMAIN_INTERFACES_DEPENDS) 
	@cd $(DOMAIN_INTERFACES_JAVA); $(MAKE) $(DOMAIN_INTERFACES_JAR)

domain_interfaces:
	@cd $(DOMAIN_INTERFACES_JAVA); $(MAKE) $(ACTION)

$(DOMAIN_IMPLS_JAR): $(DOMAIN_IMPLS_DEPENDS)
	@cd $(DOMAIN_IMPLS_JAVA); $(MAKE) $(DOMAIN_IMPLS_JAR)

domain_impls:
	@cd $(DOMAIN_IMPLS_JAVA); $(MAKE) $(ACTION)

$(DOMAIN_TEST_JAR): $(DOMAIN_TEST_DEPENDS)
	@cd $(DOMAIN_TEST_JAVA); $(MAKE) $(DOMAIN_TEST_JAR)

domain_test:
	@cd $(DOMAIN_TEST_JAVA); $(MAKE) $(ACTION)

$(DOMAIN_PERSIST_JAR): $(DOMAIN_PERSIST_DEPENDS)
	@cd $(DOMAIN_PERSIST_JAVA); $(MAKE) $(DOMAIN_PERSIST_JAR)

domain_persist:
	@cd $(DOMAIN_PERSIST_JAVA); $(MAKE) $(ACTION)

$(DOMAIN_IDL_JAR): $(DOMAIN_IDL_DEPENDS)
	@cd $(DOMAIN_IDL); $(MAKE) $(DOMAIN_IDL_JAR) $(DOMAIN_IDL_JAVA_JAR)

domain_idl:
	@cd $(DOMAIN_IDL); $(MAKE) $(ACTION)

$(DOMAIN_XML_JAR): $(DOMAIN_XML_DEPENDS)
	@cd $(DOMAIN_XML_JAVA); $(MAKE) $(DOMAIN_XML_JAR)

domain_xml:
	@cd $(DOMAIN_XML_JAVA); $(MAKE) $(ACTION)

