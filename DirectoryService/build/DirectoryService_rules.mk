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
include $(DIRECTORYSERVICE_DEP)

ALL_DIRECTORYSERVICE_BUILD_COMPONENTS=$(DIRECTORYSERVICE_BUILD_JAR)


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
.PHONY: DirectoryService $(ALL_DIRECTORYSERVICE_BUILD_COMPONENTS)

all:: DirectoryService

all_targets:: DirectoryService_targets

DirectoryService: $(ALL_DIRECTORYSERVICE_BUILD_COMPONENTS)

DirectoryService_targets:
	@cd $(DIRECTORYSERVICE_JAVA); $(MAKE) $(ACTION)

$(DIRECTORYSERVICE_BUILD_JAR): $(DIRECTORYSERVICE_DEPENDS)
	@cd $(DIRECTORYSERVICE_JAVA); $(MAKE) $@


