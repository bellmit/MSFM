#######################################################################
# This files defines the rules for building all build components
# in this release component
#
# It is intended that this file be included in two other makes, one
# for the release component and one for the entire system.

#######################################################################

###########################################################
# Include each build component defintion
#
###########################################################
include $(COMMONTEST_UTIL_DEP)


ALL_COMMONTEST_BUILD_COMPONENTS=$(COMMONTEST_UTIL_BUILD_JAR)


############################################################
# The following are the build rules that will build this
# entire release component.
#
# These are all PHONY rules since each build component has
# the real rules for building the component.  This exists
# so that all build components can be built from a single
# makefile.
#
############################################################
.PHONY: commontest $(ALL_COMMONTEST_BUILD_COMPONENTS)

all:: commontest

all_targets:: commontest_targets

commontest: $(ALL_COMMONTEST_BUILD_COMPONENTS)

commontest_targets: commontest_util

commontest_util:
	@cd $(COMMONTEST_UTIL_JAVA); $(MAKE) $(ACTION)

$(COMMONTEST_UTIL_BUILD_JAR): $(COMMONTEST_UTIL_DEPENDS)
	@cd $(COMMONTEST_UTIL_JAVA); $(MAKE) $@

