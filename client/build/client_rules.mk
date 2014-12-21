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
include $(CLIENT)/build/client.mk
include $(CLIENT_INTERFACES_DEP)
include $(CLIENT_INTERCEPTORS_DEP)
include $(CLIENT_IMPLS_DEP)
include $(CLIENT_TEST_DEP)
include $(CLIENT_TRANSLATOR_DEP)
include $(CLIENT_INTERMARKETTRANSLATOR_DEP)
include $(CLIENT_INTERNALTRANSLATOR_DEP)
include $(CLIENT_IDL_DEP)
include $(CLIENT_INTERNAL_IDL_DEP)
include $(CLIENT_COMMON_DEP)
include $(CLIENT_COMMONTRANSLATOR_DEP)
include $(CLIENT_INSTRUMENTATIONTRANSLATOR_DEP)
include $(CLIENT_MESSAGINGTRANSLATOR_DEP)
include $(CLIENT_FIXTRANSLATOR_DEP)


ALL_CLIENT_BUILD_COMPONENTS=$(CLIENT_INTERFACES_JAR) \
                            $(CLIENT_INTERCEPTORS_JAR) \
                            $(CLIENT_IMPLS_JAR) \
                            $(CLIENT_TEST_JAR) \
                            $(CLIENT_TRANSLATOR_JAR) \
                            $(CLIENT_INTERMARKETTRANSLATOR_JAR) \
                            $(CLIENT_INTERNALTRANSLATOR_JAR) \
                            $(CLIENT_IDL_JAR) \
                            $(CLIENT_INTERNAL_IDL_JAR) \
                            $(CLIENT_COMMON_JAR) \
                            $(CLIENT_COMMONTRANSLATOR_JAR) \
                            $(CLIENT_INSTRUMENTATIONTRANSLATOR_JAR) \
                            $(CLIENT_MESSAGINGTRANSLATOR_JAR) \
                            $(CLIENT_FIXTRANSLATOR_JAR)


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
.PHONY: client $(ALL_CLIENT_BUILD_COMPONENTS)

all:: client

all_targets:: client_targets

client: $(ALL_CLIENT_BUILD_COMPONENTS)

client_targets: client_interfaces client_interceptors client_impls client_translator client_intermarketTranslator client_internalTranslator client_idl client_internalIDL client_common client_commonTranslator client_instrumentationTranslator client_messagingTranslator client_fixTranslator client_test

$(CLIENT_INTERFACES_JAR): $(CLIENT_INTERFACES_DEPENDS)
	@cd $(CLIENT_INTERFACES_JAVA); $(MAKE) $(CLIENT_INTERFACES_JAR)

client_interfaces:
	@cd $(CLIENT_INTERFACES_JAVA); $(MAKE) $(ACTION)

$(CLIENT_INTERCEPTORS_JAR): $(CLIENT_INTERCEPTORS_DEPENDS)
	@cd $(CLIENT_INTERCEPTORS_JAVA); $(MAKE) $(CLIENT_INTERCEPTORS_JAR)

client_interceptors:
	@cd $(CLIENT_INTERCEPTORS_JAVA); $(MAKE) $(ACTION)

$(CLIENT_IMPLS_JAR): $(CLIENT_IMPLS_DEPENDS)
	@cd $(CLIENT_IMPLS_JAVA); $(MAKE) $(CLIENT_IMPLS_JAR)

client_impls:
	@cd $(CLIENT_IMPLS_JAVA); $(MAKE) $(ACTION)

$(CLIENT_TEST_JAR): $(CLIENT_TEST_DEPENDS)
	@cd $(CLIENT_TEST_JAVA); $(MAKE) $(CLIENT_TEST_JAR)

client_test:
	@cd $(CLIENT_TEST_JAVA); $(MAKE) $(ACTION)

$(CLIENT_TRANSLATOR_JAR): $(CLIENT_TRANSLATOR_DEPENDS)
	@cd $(CLIENT_TRANSLATOR_JAVA); $(MAKE) $(CLIENT_TRANSLATOR_JAR)

client_translator:
	@cd $(CLIENT_TRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_INTERMARKETTRANSLATOR_JAR): $(CLIENT_INTERMARKETTRANSLATOR_DEPENDS)
	@cd $(CLIENT_INTERMARKETTRANSLATOR_JAVA); $(MAKE) $(CLIENT_INTERMARKETTRANSLATOR_JAR)

client_intermarketTranslator:
	@cd $(CLIENT_INTERMARKETTRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_INTERNALTRANSLATOR_JAR): $(CLIENT_INTERNALTRANSLATOR_DEPENDS)
	@cd $(CLIENT_INTERNALTRANSLATOR_JAVA); $(MAKE) $(CLIENT_INTERNALTRANSLATOR_JAR)

client_internalTranslator:
	@cd $(CLIENT_INTERNALTRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_IDL_JAR): $(CLIENT_IDL_DEPENDS)
	@cd $(CLIENT_IDL); $(MAKE) $(CLIENT_IDL_JAR) $(CLIENT_IDL_JAVA_JAR)

client_idl:
	@cd $(CLIENT_IDL); $(MAKE) $(ACTION)

$(CLIENT_INTERNAL_IDL_JAR): $(CLIENT_INTERNAL_IDL_DEPENDS)
	@cd $(CLIENT_INTERNAL_IDL); $(MAKE) $(CLIENT_INTERNAL_IDL_JAR) $(CLIENT_INTERNAL_IDL_JAVA_JAR)

client_internalIDL:
	@cd $(CLIENT_INTERNAL_IDL); $(MAKE) $(ACTION)

$(CLIENT_COMMON_JAR): $(CLIENT_COMMON_DEPENDS)
	@cd $(CLIENT_COMMON_JAVA); $(MAKE) $(CLIENT_COMMON_JAR)

client_common:
	@cd $(CLIENT_COMMON_JAVA); $(MAKE) $(ACTION)

$(CLIENT_COMMONTRANSLATOR_JAR): $(CLIENT_COMMONTRANSLATOR_DEPENDS)
	@cd $(CLIENT_COMMONTRANSLATOR_JAVA); $(MAKE) $(CLIENT_COMMONTRANSLATOR_JAR)

client_commonTranslator:
	@cd $(CLIENT_COMMONTRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_INSTRUMENTATIONTRANSLATOR_JAR): $(CLIENT_INSTRUMENTATIONTRANSLATOR_DEPENDS)
	@cd $(CLIENT_INSTRUMENTATIONTRANSLATOR_JAVA); $(MAKE) $(CLIENT_INSTRUMENTATIONTRANSLATOR_JAR)

client_instrumentationTranslator:
	@cd $(CLIENT_INSTRUMENTATIONTRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_MESSAGINGTRANSLATOR_JAR): $(CLIENT_MESSAGINGTRANSLATOR_DEPENDS)
	@cd $(CLIENT_MESSAGINGTRANSLATOR_JAVA); $(MAKE) $(CLIENT_MESSAGINGTRANSLATOR_JAR)

client_messagingTranslator:
	@cd $(CLIENT_MESSAGINGTRANSLATOR_JAVA); $(MAKE) $(ACTION)

$(CLIENT_FIXTRANSLATOR_JAR): $(CLIENT_FIXTRANSLATOR_DEPENDS)
	@cd $(CLIENT_FIXTRANSLATOR_JAVA); $(MAKE) $(CLIENT_FIXTRANSLATOR_JAR)

client_fixTranslator:
	@cd $(CLIENT_FIXTRANSLATOR_JAVA); $(MAKE) $(ACTION)

