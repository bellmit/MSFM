##########################################################
# Define the root of the build environment
# This defaults to /vobs/dte but can be redefined
# in the environment prior to building
#
##########################################################
# Insure the definition of BUILD_ENV_ROOT
BUILD_ENV_ROOT = /vobs/dte
export BUILD_ENV_ROOT

##########################################################
# Include definitions of componenets outside this project
#
##########################################################
include $(BUILD_ENV_ROOT)/CommonLibraries/CommonTest/build/defs/components.mk


##########################################################
# Include core definitions for compilers, extentions, etc.
#
##########################################################
include $(COMMONTEST)/build/defs/common.defs
include $(COMMONBUILD)/defs/tpt.mk

#################################
# Override SmartSockets version
#################################
SMARTSOCKJAR_VERSION=ss65

JAVAHOME    =$(JAVAHOME_1.6)
JFLAGS      =$(JFLAGS16)

BUILDCHECKOUT           =1 #Tell the build system to checkout jars as part of compile

TOP_LEVEL_MK            =$(COMMONBUILD)/rules/top_level.mk



