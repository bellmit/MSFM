#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# EVENT = the directory location for the root of the release
#           component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
EVENT_REL                  =$(EVENT)/release
EVENT_JAVA                 =$(EVENT)/Java
EVENT_CLASSES              =$(EVENT)/classes

###########################################################
# Define locations of the build components.
#
###########################################################
EVENT_INTERFACES    =$(EVENT_JAVA)/interfaces
EVENT_IMPLS         =$(EVENT_JAVA)/impls
EVENT_IDL           =$(EVENT)/idl
EVENT_TEST          =$(EVENT_JAVA)/test

include $(EVENT_INTERFACES)/event_interfaces.mk
include $(EVENT_IMPLS)/event_impls.mk
include $(EVENT_IDL)/event_idl.mk
include $(EVENT_TEST)/event_test.mk

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
EVENT_INTERFACES_DEP      = $(EVENT_INTERFACES)/event_interfaces_dep.mk
EVENT_IMPLS_DEP    = $(EVENT_IMPLS)/event_impls_dep.mk
EVENT_IDL_DEP      = $(EVENT_IDL)/event_idl_dep.mk
EVENT_TEST_DEP    = $(EVENT_TEST)/event_test_dep.mk
