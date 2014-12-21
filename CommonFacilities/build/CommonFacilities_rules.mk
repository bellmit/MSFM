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
include $(COMMONFACILITIES_DEP)

ALL_COMMONFACILITIES_BUILD_COMPONENTS=$(COMMONFACILITIES_JAR)


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
.PHONY: CommonFacilities $(ALL_COMMONFACILITIES_BUILD_COMPONENTS)

all:: CommonFacilities

all_targets:: CommonFacilities_targets

CommonFacilities: $(ALL_COMMONFACILITIES_BUILD_COMPONENTS)

CommonFacilities_targets:
	@cd $(COMMONFACILITIES_JAVA); $(MAKE) $(ACTION)

$(COMMONFACILITIES_JAR): $(COMMONFACILITIES_DEPENDS)
	@cd $(COMMONFACILITIES_JAVA); $(MAKE) $(COMMONFACILITIES_JAR)


