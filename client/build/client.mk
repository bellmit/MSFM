#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# CLIENT = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
CLIENT_REL      = $(CLIENT)/release
CLIENT_JAVA     = $(CLIENT)/Java
CLIENT_CLASSES  = $(CLIENT)/classes


###########################################################
# Define locations of the build components.
#
###########################################################
CLIENT_INTERFACES           = $(CLIENT_JAVA)/interfaces
CLIENT_INTERCEPTORS         = $(CLIENT_JAVA)/interceptors
CLIENT_IMPLS                = $(CLIENT_JAVA)/impls
CLIENT_TRANSLATOR           = $(CLIENT_JAVA)/translator
CLIENT_INTERMARKETTRANSLATOR= $(CLIENT_JAVA)/intermarketTranslator
CLIENT_INTERNALTRANSLATOR   = $(CLIENT_JAVA)/internalTranslator
CLIENT_IDL                  = $(CLIENT)/idl
CLIENT_INTERNAL_IDL         = $(CLIENT)/internalIDL
CLIENT_COMMON               = $(CLIENT_JAVA)/common
CLIENT_COMMONTRANSLATOR     = $(CLIENT_JAVA)/commonTranslator
CLIENT_INSTRUMENTATIONTRANSLATOR  = $(CLIENT_JAVA)/instrumentationTranslator
CLIENT_MESSAGINGTRANSLATOR  = $(CLIENT_JAVA)/messagingTranslator
CLIENT_FIXTRANSLATOR        = $(CLIENT_JAVA)/fixTranslator
CLIENT_TEST                 = $(CLIENT_JAVA)/test

include $(CLIENT_INTERFACES)/client_interfaces.mk
include $(CLIENT_INTERCEPTORS)/client_interceptors.mk
include $(CLIENT_IMPLS)/client_impls.mk
include $(CLIENT_TRANSLATOR)/client_translator.mk
include $(CLIENT_INTERMARKETTRANSLATOR)/client_intermarketTranslator.mk
include $(CLIENT_INTERNALTRANSLATOR)/client_internalTranslator.mk
include $(CLIENT_IDL)/client_idl.mk
include $(CLIENT_INTERNAL_IDL)/client_internal_idl.mk
include $(CLIENT_COMMON)/client_common.mk
include $(CLIENT_COMMONTRANSLATOR)/client_commonTranslator.mk
include $(CLIENT_INSTRUMENTATIONTRANSLATOR)/client_instrumentationTranslator.mk
include $(CLIENT_MESSAGINGTRANSLATOR)/client_messagingTranslator.mk
include $(CLIENT_FIXTRANSLATOR)/client_fixTranslator.mk
include $(CLIENT_TEST)/client_test.mk

###########################################################
# Define locations of the dependency files for each build 
# component
#
# These are only defined and not included in order to break
# current circular depedencies between release components.
# Once these circular dependencies are removed the contents
# can be moved to the mk file for the build component and
# these defines can be removed.
#
###########################################################
CLIENT_INTERFACES_DEP       = $(CLIENT_INTERFACES)/client_interfaces_dep.mk
CLIENT_INTERCEPTORS_DEP     = $(CLIENT_INTERCEPTORS)/client_interceptors_dep.mk
CLIENT_IMPLS_DEP            = $(CLIENT_IMPLS)/client_impls_dep.mk
CLIENT_TRANSLATOR_DEP       = $(CLIENT_TRANSLATOR)/client_translator_dep.mk
CLIENT_INTERMARKETTRANSLATOR_DEP    = $(CLIENT_INTERMARKETTRANSLATOR)/client_intermarketTranslator_dep.mk
CLIENT_INTERNALTRANSLATOR_DEP       = $(CLIENT_INTERNALTRANSLATOR)/client_internalTranslator_dep.mk
CLIENT_IDL_DEP              = $(CLIENT_IDL)/client_idl_dep.mk
CLIENT_INTERNAL_IDL_DEP     = $(CLIENT_INTERNAL_IDL)/client_internal_idl_dep.mk
CLIENT_COMMON_DEP           = $(CLIENT_COMMON)/client_common_dep.mk
CLIENT_COMMONTRANSLATOR_DEP = $(CLIENT_COMMONTRANSLATOR)/client_commonTranslator_dep.mk
CLIENT_INSTRUMENTATIONTRANSLATOR_DEP = $(CLIENT_INSTRUMENTATIONTRANSLATOR)/client_instrumentationTranslator_dep.mk
CLIENT_MESSAGINGTRANSLATOR_DEP = $(CLIENT_MESSAGINGTRANSLATOR)/client_messagingTranslator_dep.mk
CLIENT_FIXTRANSLATOR_DEP    = $(CLIENT_FIXTRANSLATOR)/client_fixTranslator_dep.mk
CLIENT_TEST_DEP             = $(CLIENT_TEST)/client_test_dep.mk

