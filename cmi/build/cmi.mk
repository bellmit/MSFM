#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
#
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# CMI = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Use JDK defined in commonBuild
###########################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
CMI_REL        = $(CMI)/release/jars


###########################################################
# Define locations of the build components.
#
###########################################################
CMI_IDL        = $(CMI)/idl

include $(CMI_IDL)/cmi_idl.mk

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
CMI_IDL_DEP        = $(CMI_IDL)/cmi_idl_dep.mk

