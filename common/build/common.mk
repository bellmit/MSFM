#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# COMMON = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
COMMON_REL           = $(COMMON)/$(JARPATH)
COMMON_JAVA          = $(COMMON)/java
COMMON_TEST_JAVA     = $(COMMON)/test
COMMON_CLASSES       = $(COMMON)/classes
COMMON_CLASS         = $(COMMON_CLASSES)
COMMON_TEST_CLASS    = $(COMMON_CLASSES)/test

COMMON_JARNAME  =common
COMMON_JAR      =$(COMMON_REL)/$(COMMON_JARNAME).$(JAR_EXT)

COMMON_TEST_JARNAME  =commonTest
COMMON_TEST_JAR      =$(COMMON_REL)/$(COMMON_TEST_JARNAME).$(JAR_EXT)

ifdef BUILDCHECKOUT
    COMMON_BUILD_JAR=$(COMMON_JAR)
    COMMON_TEST_BUILD_JAR=$(COMMON_TEST_JAR)
else
    COMMON_BUILD_JAR=$(COMMON_CLASS)/$(COMMON_JARNAME).$(JAR_EXT)
    COMMON_TESTPBUILD_JAR=$(COMMON_CLASS)/$(COMMON_TEST_JARNAME).$(JAR_EXT)
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
COMMON_DEP          = $(COMMON_JAVA)/common_dep.mk
COMMON_TEST_DEP     = $(COMMON_TEST_JAVA)/common_test_dep.mk

