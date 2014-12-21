#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# DIRECTORYSERVICE = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
DIRECTORYSERVICE_REL      = $(DIRECTORYSERVICE)/release/jars
DIRECTORYSERVICE_JAVA     = $(DIRECTORYSERVICE)/java
DIRECTORYSERVICE_IDL      = $(DIRECTORYSERVICE)/idl
DIRECTORYSERVICE_CLASSES  = $(DIRECTORYSERVICE)/classes
DIRECTORYSERVICE_CLASS    = $(DIRECTORYSERVICE_CLASSES)
DIRECTORYSERVICE_JARNAME  = DirectoryService
DIRECTORYSERVICE_JAR      = $(DIRECTORYSERVICE_REL)/$(DIRECTORYSERVICE_JARNAME).$(JAR_EXT)


ifdef BUILDCHECKOUT
    DIRECTORYSERVICE_BUILD_JAR = $(DIRECTORYSERVICE_JAR)
else
    DIRECTORYSERVICE_BUILD_JAR = $(DIRECTORYSERVICE_CLASS)/$(DIRECTORYSERVICE_JARNAME).$(JAR_EXT)
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
DIRECTORYSERVICE_DEP = $(DIRECTORYSERVICE_JAVA)/DirectoryService_dep.mk

