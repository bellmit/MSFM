##########################################################
# Define the root of the build environment
# This defaults to /vobs/dte but can be redefined
# in the environment prior to building
#
##########################################################
# Insure the definition of BUILD_ENV_ROOT
ifndef (BUILD_ENV_ROOT)
      BUILD_ENV_ROOT = /vobs/dte
endif
ifeq ($(strip $(BUILD_ENV_ROOT)),)
      BUILD_ENV_ROOT = /vobs/dte
endif
export BUILD_ENV_ROOT

##########################################################
# Include the definitions of component locations
# This can be redfined by setting COMPONENTS= to the location
# of an alternative include file
##########################################################
ifdef (COMPONENTS)
    include $(COMPONENTS)
else
    include $(BUILD_ENV_ROOT)/cmi/build/defs/components.mk
endif

##########################################################
# Include definitions of componenets outside this project
#
##########################################################
# Third Party Tools (someday to be part of commonBuild?)
include $(BUILD_ENV_ROOT)/cmi/build/defs/tpt.mk

##########################################################
# Include core definitions for compilers, extentions, etc.
#
##########################################################

include $(IDLBASE)/defs/idlbase.mk
include $(BUILD_ENV_ROOT)/commonBuild/defs/common.defs

JAVA_HOME   =$(JAVAHOME_1.5)
JAVAHOME    =$(JAVAHOME_1.5)
JFLAGS      =$(JFLAGS15)

##########################################################
# Component JARS
#
##########################################################
# release directory
JARPATH      =release/jars

# IDL compiler jar used to build other jars.
IDLCOM                       =IDLCompiler
IDLCOMJAR                    =$(IDLBASE_IDLCOMPILER)/$(JARPATH)/$(IDLCOM).$(JAR_EXT)

