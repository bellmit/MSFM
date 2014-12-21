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
include $(FOUNDATIONFRAMEWORK_DEP)

ALL_FOUNDATIONFRAMEWORK_BUILD_COMPONENTS=$(FOUNDATIONFRAMEWORK_BUILD_JAR)


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
.PHONY: FoundationFramework $(ALL_FOUNDATIONFRAMEWORK_BUILD_COMPONENTS)

all:: FoundationFramework

all_targets:: FoundationFramework_targets

FoundationFramework: $(ALL_FOUNDATIONFRAMEWORK_BUILD_COMPONENTS)

FoundationFramework_targets:
	@cd $(FOUNDATIONFRAMEWORK_JAVA); $(MAKE) $(ACTION)

$(FOUNDATIONFRAMEWORK_BUILD_JAR): $(FOUNDATIONFRAMEWORK_DEPENDS)
	@cd $(FOUNDATIONFRAMEWORK_JAVA); $(MAKE) $@


