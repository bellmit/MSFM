#######################################################################
#
# All macros defined in this file should be globally unique across
# all release and build components
# 
# This file makes reference to macros defined elsewhere, please define
# the following when including this file
#
# COMMONTEST = the directory location for the root of this release
#               component
#
#######################################################################

###########################################################
# Define directories global to the release component
# In the future this may become the locations of the
# individual build components
#
###########################################################
COMMONTEST_REL      = $(COMMONTEST)/release
COMMONTEST_JAVA     = $(COMMONTEST)/java
COMMONTEST_CLASSES  = $(COMMONTEST)/classes


###########################################################
# Define locations of the build components.
#
###########################################################
COMMONTEST_UTIL     = $(COMMONTEST_JAVA)/util


include $(COMMONTEST_UTIL)/commontest_util.mk

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
COMMONTEST_UTIL_DEP          = $(COMMONTEST_UTIL)/commontest_util_dep.mk

