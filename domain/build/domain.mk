#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# DOMAIN = the directory location for the root of the domain release
#           component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
DOMAIN_REL      = $(DOMAIN)/release
DOMAIN_JAVA     = $(DOMAIN)/Java
DOMAIN_CLASSES  = $(DOMAIN)/classes


###########################################################
# Define locations of the build components.
#
###########################################################
DOMAIN_TEST         = $(DOMAIN_JAVA)/test
DOMAIN_INTERFACES   = $(DOMAIN_JAVA)/interfaces
DOMAIN_IMPLS        = $(DOMAIN_JAVA)/impls
DOMAIN_PERSIST      = $(DOMAIN_JAVA)/persist
DOMAIN_IDL          = $(DOMAIN)/idl
DOMAIN_XML          = $(DOMAIN_JAVA)/xml


include $(DOMAIN_TEST)/domain_test.mk
include $(DOMAIN_INTERFACES)/domain_interfaces.mk
include $(DOMAIN_IMPLS)/domain_impls.mk
include $(DOMAIN_PERSIST)/domain_persist.mk
include $(DOMAIN_IDL)/domain_idl.mk
include $(DOMAIN_XML)/domain_xml.mk

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
DOMAIN_INTERFACES_DEP      = $(DOMAIN_INTERFACES)/domain_interfaces_dep.mk
DOMAIN_TEST_DEP     = $(DOMAIN_TEST)/domain_test_dep.mk
DOMAIN_IMPLS_DEP    = $(DOMAIN_IMPLS)/domain_impls_dep.mk
DOMAIN_PERSIST_DEP  = $(DOMAIN_PERSIST)/domain_persist_dep.mk
DOMAIN_IDL_DEP      = $(DOMAIN_IDL)/domain_idl_dep.mk
DOMAIN_XML_DEP      = $(DOMAIN_XML)/domain_xml_dep.mk

