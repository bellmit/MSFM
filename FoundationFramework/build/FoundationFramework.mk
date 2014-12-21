#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# FOUNDATIONFRAMEWORK = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
FOUNDATIONFRAMEWORK_REL      = $(FOUNDATIONFRAMEWORK)/release/jars
FOUNDATIONFRAMEWORK_JAVA     = $(FOUNDATIONFRAMEWORK)/Java
FOUNDATIONFRAMEWORK_CLASSES  = $(FOUNDATIONFRAMEWORK)/classes
FOUNDATIONFRAMEWORK_CLASS    = $(FOUNDATIONFRAMEWORK_CLASSES)
FOUNDATIONFRAMEWORK_JARNAME  = FoundationFramework
FOUNDATIONFRAMEWORK_JAR      = $(FOUNDATIONFRAMEWORK_REL)/$(FOUNDATIONFRAMEWORK_JARNAME).$(JAR_EXT)

ifdef BUILDCHECKOUT
    FOUNDATIONFRAMEWORK_BUILD_JAR = $(FOUNDATIONFRAMEWORK_JAR)
else
    FOUNDATIONFRAMEWORK_BUILD_JAR = $(FOUNDATIONFRAMEWORK_CLASS)/$(FOUNDATIONFRAMEWORK_JARNAME).$(JAR_EXT)
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
FOUNDATIONFRAMEWORK_DEP = $(FOUNDATIONFRAMEWORK_JAVA)/FoundationFramework_dep.mk

