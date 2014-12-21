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
include $(COMMON_DEP)


ALL_COMMON_BUILD_COMPONENTS=$(COMMON_BUILD_JAR)


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
.PHONY: common $(ALL_COMMON_BUILD_COMPONENTS)

all:: common

all_targets:: common_targets

common: $(ALL_COMMON_BUILD_COMPONENTS)

common_targets: 
	@cd $(COMMON_JAVA); $(MAKE) $(ACTION)

$(COMMON_BUILD_JAR): $(COMMON_DEPENDS)
	@cd $(COMMON_JAVA); $(MAKE) $@

