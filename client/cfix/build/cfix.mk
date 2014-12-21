#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# CFIX = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
CFIX_REL      = $(CFIX)/release
CFIX_JAVA     = $(CFIX)/Java
CFIX_CLASSES  = $(CFIX)/classes


###########################################################
# Define locations of the build components.
#
###########################################################
CFIX_INTERFACES = $(CFIX_JAVA)/interfaces
CFIX_IMPLS      = $(CFIX_JAVA)/impls
CFIX_INTERCEPTORS = $(CFIX_JAVA)/interceptors
CFIX_TEST       = $(CFIX_JAVA)/test


include $(CFIX_INTERFACES)/cfix_interfaces.mk
include $(CFIX_IMPLS)/cfix_impls.mk
include $(CFIX_INTERCEPTORS)/cfix_interceptors.mk
include $(CFIX_TEST)/cfix_test.mk

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
CFIX_INTERFACES_DEP = $(CFIX_INTERFACES)/cfix_interfaces_dep.mk
CFIX_IMPLS_DEP      = $(CFIX_IMPLS)/cfix_impls_dep.mk
CFIX_INTERCEPTORS_DEP = $(CFIX_INTERCEPTORS)/cfix_interceptors_dep.mk
CFIX_TEST_DEP       = $(CFIX_TEST)/cfix_test_dep.mk

