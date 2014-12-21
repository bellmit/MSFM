#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# COMMONFACILITIES = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
COMMONFACILITIES_REL      = $(COMMONFACILITIES)/release
COMMONFACILITIES_JAVA     = $(COMMONFACILITIES)/Java
COMMONFACILITIES_CLASSES  = $(COMMONFACILITIES)/classes
COMMONFACILITIES_CLASS    = $(COMMONFACILITIES_CLASSES)
COMMONFACILITIES_JARNAME  = CommonFacilities
COMMONFACILITIES_JAR      = $(COMMONFACILITIES_REL)/$(COMMONFACILITIES_JARNAME).$(JAR_EXT)
COMMONFACILITIES_VERSIONSRC  =$(COMMONFACILITIES_JAVA)/$(COMMONFACILITIES_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    COMMONFACILITIES_BUILD_JAR =$(COMMONFACILITIES_JAR)
else
    COMMONFACILITIES_BUILD_JAR =$(COMMONFACILITIES_CLASS)/$(COMMONFACILITIES_JARNAME).$(JAR_EXT)
endif



###########################################################
# Define locations of the build components.
#
###########################################################


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
COMMONFACILITIES_DEP = $(COMMONFACILITIES_JAVA)/CommonFacilities_dep.mk

